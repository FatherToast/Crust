package fathertoast.crust.api.config.common.value.environment;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.ITomlStringValue;
import fathertoast.crust.api.config.common.value.collection.value.ComparatorValue;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import fathertoast.crust.api.config.common.value.collection.value.MultiValueCodec;
import fathertoast.crust.api.config.common.value.environment.biome.*;
import fathertoast.crust.api.config.common.value.environment.compat.ApocalypseDifficultyEnvironment;
import fathertoast.crust.api.config.common.value.environment.compat.ApocalypseDifficultyOrTimeEnvironment;
import fathertoast.crust.api.config.common.value.environment.dimension.DimensionEnvironment;
import fathertoast.crust.api.config.common.value.environment.dimension.DimensionPropertyEnvironment;
import fathertoast.crust.api.config.common.value.environment.position.PositionEnvironment;
import fathertoast.crust.api.config.common.value.environment.position.StructureEnvironment;
import fathertoast.crust.api.config.common.value.environment.position.YEnvironment;
import fathertoast.crust.api.config.common.value.environment.position.YFromSeaEnvironment;
import fathertoast.crust.api.config.common.value.environment.time.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * One condition-value entry in an environment list.
 *
 * @see EnvironmentList
 * @see EnvironmentContext
 * @see EnvironmentConditionParser
 */
@SuppressWarnings( "unused" )
public class EnvironmentEntry<V> implements ITomlStringValue, Predicate<EnvironmentContext> {
    
    /** String version of this entry (as "value condition") for writing back to disk. */
    private final String asString;
    
    /** The value given to this entry. */
    private final V entryValue;
    /** The environments that define this entry's condition. */
    private final Predicate<EnvironmentContext> entryCondition;
    
    /** Creates an entry with the specified value and condition. */
    public EnvironmentEntry( @Nullable IConfigField<?> field, IValueCodec<V> codec, String line ) {
        this( field, codec, codec instanceof MultiValueCodec<?> mvc ? mvc.arguments() : 1, line );
    }
    
    /** Creates an entry with the specified value and condition. */
    public EnvironmentEntry( @Nullable IConfigField<?> field, IValueCodec<V> codec, int args, String line ) {
        int splitIndex = nthIndexOf( line, ' ', args );
        if( splitIndex < 0 ) {
            entryValue = codec.parseTomlString( field, line, line );
            entryCondition = CrustEnvironmentRegistry.NEVER;
            asString = line + " " + CrustEnvironmentRegistry.NEVER.toTomlString();
            if( field != null ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Environment entry has too few arguments! Falling back to \"{}\". Invalid entry: {}",
                        asString, line );
            }
        }
        else {
            String stringValue = line.substring( 0, splitIndex );
            entryValue = codec.parseTomlString( field, line, stringValue );
            EnvironmentConditionParser parser = EnvironmentConditionParser.of( field, line, line.substring( splitIndex + 1 ) );
            entryCondition = parser.parse();
            asString = stringValue + " " + parser.getCorrectedConditionString();
        }
    }
    
    /** @return The index of the nth repeat of a character in the string, or -1 if no such index exists. */
    private static int nthIndexOf( String s, char c, int n ) {
        int i = -1;
        while( n-- > 0 ) {
            i = s.indexOf( c, i + 1 );
        }
        return i;
    }
    
    public V getValue() { return entryValue; }
    
    /** @return True if this environment matches the provided environment. */
    @Override // Predicate
    public boolean test( EnvironmentContext context ) { return entryCondition.test( context ); }
    
    //    /**
    //     * @return Returns true if all this entry's conditions match the provided environment.
    //     * @throws IllegalStateException If the position is not in a fully loaded chunk.
    //     * @see EnvironmentHelper#isLoaded(net.minecraft.world.level.LevelAccessor, BlockPos)
    //     */
    //    public boolean matches( Level world, BlockPos pos ) {
    //        if( !EnvironmentHelper.isLoaded( world, pos ) ) {
    //            throw new IllegalStateException( "Attempted to query world data in an unloaded chunk. This is bad!" );
    //        }
    //        return unsafeMatches( world, pos );
    //    }
    
    /** @return This value, converted to a single-line string. */
    @Override // ITomlStringValue
    public String toTomlString() { return asString; }
    
    
    // ---- Builder Implementation ---- //
    
    /**
     * Builder class used to simplify creation of environment entries for default configs,
     * with shortcuts for the most commonly used environments.
     * <p>
     * Create these through the {@link EnvironmentList.Builder#entryBuilder(Object)} method
     * while building an environment list.
     *
     * @see EnvironmentList#builder(IValueCodec)
     * @see EnvironmentList#builder(IValueCodec, int)
     * @see EnvironmentList.Builder
     */
    public static class Builder<V> extends EnvBuilder<V> implements OpBuilder<V> {
        
        private final EnvironmentList.Builder<V> listBuilder;
        
        Builder( EnvironmentList.Builder<V> builder, String value ) {
            super( value );
            listBuilder = builder;
        }
        
        /**
         * Generates an environment entry reflecting the current state of this builder,
         * adds it to the list builder, and then returns that list builder.
         */
        @Override
        public EnvironmentList.Builder<V> build() {
            while( groupingLevel > 0 ) close();
            return listBuilder.add( condition.toString() );
        }
        
        /** Appends "&", the AND operator. Note that ANDs are done before ORs in the order of operations. */
        @Override
        public EnvBuilder<V> and() {
            condition.append( " & " );
            return this;
        }
        
        /** Appends "|", the OR operator. Note that ANDs are done before ORs in the order of operations. */
        @Override
        public EnvBuilder<V> or() {
            condition.append( " | " );
            return this;
        }
        
        /**
         * Appends ")", to close one level of parentheses.
         *
         * @throws IllegalStateException when the level of parentheses is zero (no open levels).
         */
        @Override
        public OpBuilder<V> close() {
            if( groupingLevel <= 0 )
                throw new IllegalStateException( "Cannot close parentheses with no matching open!" );
            groupingLevel--;
            condition.append( ")" );
            return this;
        }
    }
    
    /** View of the {@link Builder} when an operation is expected. */
    public interface OpBuilder<V> {
        /**
         * Generates an environment entry reflecting the current state of this builder,
         * adds it to the list builder, and then returns that list builder.
         */
        EnvironmentList.Builder<V> build();
        
        /** Appends "&", the AND operator. Note that ANDs are done before ORs in the order of operations. */
        EnvBuilder<V> and();
        
        /** Appends "|", the OR operator. Note that ANDs are done before ORs in the order of operations. */
        EnvBuilder<V> or();
        
        /**
         * Appends ")", to close one level of parentheses.
         *
         * @throws IllegalStateException when the level of parentheses is zero (no open levels).
         */
        OpBuilder<V> close();
    }
    
    /** View of the {@link Builder} when an environment is expected. */
    public static abstract class EnvBuilder<V> {
        
        protected final StringBuilder condition;
        
        protected int groupingLevel;
        
        protected EnvBuilder( String value ) { condition = new StringBuilder( value ).append( " " ); }
        
        /** Appends "(", to open one level of parentheses. */
        public EnvBuilder<V> open() {
            groupingLevel++;
            condition.append( "(" );
            return this;
        }
        
        /** General-use adder. Use this when the standardized convenience methods below don't give what you need. */
        public OpBuilder<V> in( AbstractEnvironment environment ) {
            condition.append( environment.toTomlString() );
            //noinspection unchecked
            return (OpBuilder<V>) this;
        }
        
        
        // ---- Dimension-based ---- //
        
        /** Check if the dimension type causes water to instantly vaporize and has faster lava flow. */
        public OpBuilder<V> inUltraWarmDimension() { return inDimensionWithProperty( DimensionPropertyEnvironment.Value.ULTRAWARM, false ); }
        
        /** Check if the dimension type causes water to instantly vaporize and has faster lava flow. */
        public OpBuilder<V> notInUltraWarmDimension() { return inDimensionWithProperty( DimensionPropertyEnvironment.Value.ULTRAWARM, true ); }
        
        /** Check if the dimension type allows clocks, compasses, and beds to work. */
        public OpBuilder<V> inNaturalDimension() { return inDimensionWithProperty( DimensionPropertyEnvironment.Value.NATURAL, false ); }
        
        /** Check if the dimension type allows clocks, compasses, and beds to work. */
        public OpBuilder<V> notInNaturalDimension() { return inDimensionWithProperty( DimensionPropertyEnvironment.Value.NATURAL, true ); }
        
        private OpBuilder<V> inDimensionWithProperty( DimensionPropertyEnvironment.Value property, boolean invert ) {
            return in( new DimensionPropertyEnvironment( property, invert ) );
        }
        
        public OpBuilder<V> inOverworld() { return inDimension( Level.OVERWORLD, false ); }
        
        public OpBuilder<V> notInOverworld() { return inDimension( Level.OVERWORLD, true ); }
        
        public OpBuilder<V> inNether() { return inDimension( Level.NETHER, false ); }
        
        public OpBuilder<V> notInNether() { return inDimension( Level.NETHER, true ); }
        
        public OpBuilder<V> inTheEnd() { return inDimension( Level.END, false ); }
        
        public OpBuilder<V> notInTheEnd() { return inDimension( Level.END, true ); }
        
        private OpBuilder<V> inDimension( ResourceKey<Level> dimType, boolean invert ) {
            return in( DimensionEnvironment.of( dimType, invert ) );
        }
        
        /** Check if the dimension type is vanilla (registered with the "minecraft" namespace). */
        public OpBuilder<V> inVanillaDimension() {
            return in( DimensionEnvironment.ofWildcard( "minecraft", false ) );
        }
        
        /** Check if the dimension type is vanilla (registered with the "minecraft" namespace). */
        public OpBuilder<V> notInVanillaDimension() {
            return in( DimensionEnvironment.ofWildcard( "minecraft", true ) );
        }
        
        
        // ---- Biome-based ---- //
        
        /** Check if the biome has rain disabled. */
        public OpBuilder<V> inDryBiome() { return inAvgRainfall( ComparatorValue.EQUAL, 0.0F ); }
        
        /** Check if the biome has rain disabled. */
        public OpBuilder<V> notInDryBiome() { return inAvgRainfall( ComparatorValue.EQUAL.invert(), 0.0F ); }
        
        /** Check if the biome's humidity hinders fire spread. */
        public OpBuilder<V> inHumidBiome() { return inAvgRainfall( ComparatorValue.GREATER, 0.85F ); }
        
        /** Check if the biome's humidity hinders fire spread. */
        public OpBuilder<V> notInHumidBiome() { return inAvgRainfall( ComparatorValue.GREATER.invert(), 0.85F ); }
        
        private OpBuilder<V> inAvgRainfall( ComparatorValue op, float value ) { return in( new RainfallEnvironment( op, value ) ); }
        
        /** Check if the temperature is freezing. */
        public OpBuilder<V> isFreezing() { return in( new TemperatureEnvironment( true ) ); }
        
        /** Check if the temperature is freezing. */
        public OpBuilder<V> isNotFreezing() { return in( new TemperatureEnvironment( false ) ); }
        
        /** Check if the temperature is warm (disables snow golem trails). */
        public OpBuilder<V> isWarm() { return isTemperature( ComparatorValue.GREATER_OR_EQUAL, 0.8F ); }
        
        /** Check if the temperature is warm (disables snow golem trails). */
        public OpBuilder<V> isNotWarm() { return isTemperature( ComparatorValue.GREATER_OR_EQUAL.invert(), 0.8F ); }
        
        /** Check if the temperature is hot (causes snow golems to die). */
        public OpBuilder<V> isHot() { return isTemperature( ComparatorValue.GREATER, 1.0F ); }
        
        /** Check if the temperature is hot (causes snow golems to die). */
        public OpBuilder<V> isNotHot() { return isTemperature( ComparatorValue.GREATER.invert(), 1.0F ); }
        
        private OpBuilder<V> isTemperature( ComparatorValue op, float value ) { return in( new TemperatureEnvironment( op, value ) ); }
        
        /** Check if the biome is contained in the water biome tag. */
        public OpBuilder<V> inWaterBiome() { return inBiome( Tags.Biomes.IS_WATER ); }
        
        /** Check if the biome is contained in the water biome tag. */
        public OpBuilder<V> notInWaterBiome() { return notInBiome( Tags.Biomes.IS_WATER ); }
        
        /** Check if the biome is contained in the mountain biome tag. */
        public OpBuilder<V> inMountainBiome() { return inBiome( Tags.Biomes.IS_MOUNTAIN ); }
        
        /** Check if the biome is contained in the mountain biome tag. */
        public OpBuilder<V> notInMountainBiome() { return notInBiome( Tags.Biomes.IS_MOUNTAIN ); }
        
        /** Check if the biome is contained in the plains biome tag. */
        public OpBuilder<V> inFlatBiome() { return inBiome( Tags.Biomes.IS_PLAINS ); }
        
        /** Check if the biome is contained in the plains biome tag. */
        public OpBuilder<V> notInFlatBiome() { return notInBiome( Tags.Biomes.IS_PLAINS ); }
        
        /** Check if the biome is contained in the spooky biome tag. */
        public OpBuilder<V> inSpookyBiome() { return inBiome( Tags.Biomes.IS_SPOOKY ); }
        
        /** Check if the biome is contained in the spooky biome tag. */
        public OpBuilder<V> notInSpookyBiome() { return notInBiome( Tags.Biomes.IS_SPOOKY ); }
        
        /** Check if the biome is contained in the rare biome tag. */
        public OpBuilder<V> inRareBiome() { return inBiome( Tags.Biomes.IS_RARE ); }
        
        /** Check if the biome is contained in the rare biome tag. */
        public OpBuilder<V> notInRareBiome() { return notInBiome( Tags.Biomes.IS_RARE ); }
        
        /** Check if the biome is contained in a biome tag. See {@link BiomeTags} and {@link Tags.Biomes}. */
        public OpBuilder<V> inBiome( TagKey<Biome> biome ) {
            return in( BiomeEnvironment.ofTag( biome, false ) );
        }
        
        /** Check if the biome is contained in a biome tag. See {@link BiomeTags} and {@link Tags.Biomes}. */
        public OpBuilder<V> notInBiome( TagKey<Biome> biome ) {
            return in( BiomeEnvironment.ofTag( biome, true ) );
        }
        
        /** Check if the biome is a specific one. */
        public OpBuilder<V> inBiome( ResourceKey<Biome> biome ) {
            return in( BiomeEnvironment.of( biome, false ) );
        }
        
        /** Check if the biome is a specific one. */
        public OpBuilder<V> notInBiome( ResourceKey<Biome> biome ) {
            return in( BiomeEnvironment.of( biome, true ) );
        }
        
        /** Check if the biome belongs to a specific category. Please use {@link #inBiome(TagKey)} instead! */
        @SuppressWarnings( "removal" )
        @Deprecated( forRemoval = true ) // TODO Remove when updating beyond 1.20.1
        public OpBuilder<V> inBiomeCategory( BiomeCategory category ) { //noinspection removal
            return in( new BiomeCategoryEnvironment( category, false ) );
        }
        
        /** Check if the biome belongs to a specific category. Please use {@link #notInBiome(TagKey)} instead! */
        @SuppressWarnings( "removal" )
        @Deprecated( forRemoval = true ) // TODO Remove when updating beyond 1.20.1
        public OpBuilder<V> notInBiomeCategory( BiomeCategory category ) { //noinspection removal
            return in( new BiomeCategoryEnvironment( category, true ) );
        }
        
        
        // ---- Position-based ---- //
        
        /** Check if the position is inside a particular structure. See {@link BuiltinStructures}. */
        public OpBuilder<V> inStructure( ResourceKey<Structure> structure ) {
            return in( StructureEnvironment.of( structure, false ) );
        }
        
        /** Check if the position is inside a particular structure. See {@link BuiltinStructures}. */
        public OpBuilder<V> notInStructure( ResourceKey<Structure> structure ) {
            return in( StructureEnvironment.of( structure, true ) );
        }
        
        /** Check if diamond/redstone ore can generate at the position. */
        public OpBuilder<V> belowDiamondLevel() { return belowY( 17 ); }
        
        /** Check if diamond/redstone ore can generate at the position. */
        public OpBuilder<V> aboveDiamondLevel() { return aboveY( 17 ); }
        
        /** Check if gold/lapis ore can generate at the position. */
        public OpBuilder<V> belowGoldLevel() { return belowY( 33 ); }
        
        /** Check if gold/lapis ore can generate at the position. */
        public OpBuilder<V> aboveGoldLevel() { return aboveY( 33 ); }
        
        private OpBuilder<V> belowY( int y ) { return in( new YEnvironment( ComparatorValue.LESS, y ) ); }
        
        private OpBuilder<V> aboveY( int y ) { return in( new YEnvironment( ComparatorValue.LESS.invert(), y ) ); }
        
        /** Check if the position is above/below sea level. */
        public OpBuilder<V> belowSeaLevel() { return belowSeaLevel( 0 ); }
        
        /** Check if the position is above/below sea level. */
        public OpBuilder<V> aboveSeaLevel() { return aboveSeaLevel( 0 ); }
        
        /** Check if the position is above/below the average sea floor. */
        public OpBuilder<V> belowSeaDepths() { return belowSeaLevel( -17 ); }
        
        /** Check if the position is above/below the average sea floor. */
        public OpBuilder<V> aboveSeaDepths() { return aboveSeaLevel( -17 ); }
        
        /** Check if the position is above/below the average sea floor. */
        public OpBuilder<V> belowSeaFloor() { return belowSeaLevel( -27 ); }
        
        /** Check if the position is above/below the average sea floor. */
        public OpBuilder<V> aboveSeaFloor() { return aboveSeaLevel( -27 ); }
        
        /** Check if the position is above/below 'mountain level' - that is, high enough to die from falling to sea level. */
        public OpBuilder<V> belowMountainLevel() { return belowSeaLevel( 25 ); }
        
        /** Check if the position is above/below 'mountain level' - that is, high enough to die from falling to sea level. */
        public OpBuilder<V> aboveMountainLevel() { return aboveSeaLevel( 25 ); }
        
        private OpBuilder<V> belowSeaLevel( int dY ) { return in( new YFromSeaEnvironment( ComparatorValue.LESS, dY ) ); }
        
        private OpBuilder<V> aboveSeaLevel( int dY ) { return in( new YFromSeaEnvironment( ComparatorValue.LESS.invert(), dY ) ); }
        
        public OpBuilder<V> canSeeSky() { return inPositionWithState( PositionEnvironment.Value.CAN_SEE_SKY, false ); }
        
        public OpBuilder<V> cannotSeeSky() { return inPositionWithState( PositionEnvironment.Value.CAN_SEE_SKY, true ); }
        
        public OpBuilder<V> isNearVillage() { return inPositionWithState( PositionEnvironment.Value.IS_NEAR_VILLAGE, false ); }
        
        public OpBuilder<V> isNotNearVillage() { return inPositionWithState( PositionEnvironment.Value.IS_NEAR_VILLAGE, true ); }
        
        public OpBuilder<V> isNearRaid() { return inPositionWithState( PositionEnvironment.Value.IS_NEAR_RAID, false ); }
        
        public OpBuilder<V> isNotNearRaid() { return inPositionWithState( PositionEnvironment.Value.IS_NEAR_RAID, true ); }
        
        private OpBuilder<V> inPositionWithState( PositionEnvironment.Value state, boolean invert ) { return in( new PositionEnvironment( state, invert ) ); }
        
        
        // ---- Time-based ---- //
        
        /** Check if the special difficulty multiplier is above a threshold (0 - 1). */
        public OpBuilder<V> aboveDifficulty( float percent ) { return in( new SpecialDifficultyEnvironment( ComparatorValue.GREATER_OR_EQUAL, percent ) ); }
        
        /** Check if the special difficulty multiplier is above a threshold (0 - 1). */
        public OpBuilder<V> belowDifficulty( float percent ) { return in( new SpecialDifficultyEnvironment( ComparatorValue.GREATER_OR_EQUAL.invert(), percent ) ); }
        
        public OpBuilder<V> isRaining() { return inWeather( WeatherEnvironment.Value.RAIN, false ); } // same as "is not clear"
        
        public OpBuilder<V> isNotRaining() { return inWeather( WeatherEnvironment.Value.RAIN, true ); } // same as "is clear"
        
        public OpBuilder<V> isThundering() { return inWeather( WeatherEnvironment.Value.THUNDER, false ); }
        
        public OpBuilder<V> isNotThundering() { return inWeather( WeatherEnvironment.Value.THUNDER, true ); }
        
        private OpBuilder<V> inWeather( WeatherEnvironment.Value weather, boolean invert ) { return in( new WeatherEnvironment( weather, invert ) ); }
        
        public OpBuilder<V> atMaxMoonLight() { return in( new MoonPhaseEnvironment( MoonPhaseEnvironment.Value.FULL, false ) ); }
        
        public OpBuilder<V> aboveHalfMoonLight() { return fromHalfMoonLight( ComparatorValue.GREATER ); }
        
        public OpBuilder<V> atHalfMoonLight() { return fromHalfMoonLight( ComparatorValue.EQUAL ); }
        
        public OpBuilder<V> belowHalfMoonLight() { return fromHalfMoonLight( ComparatorValue.LESS ); }
        
        public OpBuilder<V> atNoMoonLight() { return in( new MoonPhaseEnvironment( MoonPhaseEnvironment.Value.NEW, false ) ); }
        
        private OpBuilder<V> fromHalfMoonLight( ComparatorValue op ) { return in( new MoonBrightnessEnvironment( op, 0.5F ) ); }
        
        public OpBuilder<V> isNight() { return in( new DayTimeEnvironment( DayTimeEnvironment.Value.NIGHT, false ) ); }
        
        public OpBuilder<V> isDay() { return in( new DayTimeEnvironment( DayTimeEnvironment.Value.DAY, false ) ); }
        
        /** Check if the time is during a quarter of the night centered on midnight. */
        public OpBuilder<V> isNearMidnight() { return in( new TimeFromMidnightEnvironment( ComparatorValue.LESS_OR_EQUAL, 1_500 ) ); }
        
        /** Check if the time is during a quarter of the night centered on midnight. */
        public OpBuilder<V> isNotNearMidnight() { return in( new TimeFromMidnightEnvironment( ComparatorValue.LESS_OR_EQUAL.invert(), 1_500 ) ); }
        
        /**
         * Check if the world time is after a certain number of days. Should use
         * {@link #afterDaysOrApocalypseDifficulty(int)} instead for options that make the game harder.
         */
        public OpBuilder<V> afterDays( int days ) { return in( new WorldTimeEnvironment( ComparatorValue.GREATER_OR_EQUAL, 24_000L * days ) ); }
        
        /**
         * Check if the world time is after a certain number of days. Should use
         * {@link #beforeDaysOrApocalypseDifficulty(int)} instead for options that make the game harder.
         */
        public OpBuilder<V> beforeDays( int days ) { return in( new WorldTimeEnvironment( ComparatorValue.GREATER_OR_EQUAL.invert(), 24_000L * days ) ); }
        
        /**
         * Check if the world time is after a certain number of months. One month is eight days. Should use
         * {@link #afterMonthsOrApocalypseDifficulty(int)} instead for options that make the game harder.
         */
        public OpBuilder<V> afterMonths( int months ) { return afterDays( months * 8 ); }
        
        /**
         * Check if the world time is after a certain number of months. One month is eight days. Should use
         * {@link #beforeMonthsOrApocalypseDifficulty(int)} instead for options that make the game harder.
         */
        public OpBuilder<V> beforeMonths( int months ) { return beforeDays( months * 8 ); }
        
        /** Check if the chunk inhabited time is after a certain number of days. */
        public OpBuilder<V> afterDaysInChunk( int days ) { return in( new ChunkTimeEnvironment( ComparatorValue.GREATER_OR_EQUAL, 24_000L * days ) ); }
        
        /** Check if the chunk inhabited time is after a certain number of days. */
        public OpBuilder<V> beforeDaysInChunk( int days ) { return in( new ChunkTimeEnvironment( ComparatorValue.GREATER_OR_EQUAL.invert(), 24_000L * days ) ); }
        
        
        // ---- Mod-based ---- //
        
        /**
         * If Apocalypse Rebooted is installed, check if the difficulty is above a threshold;
         * otherwise, check if the world time is after a certain number of days.
         */
        public OpBuilder<V> afterDaysOrApocalypseDifficulty( int days ) { return in( new ApocalypseDifficultyOrTimeEnvironment( ComparatorValue.GREATER_OR_EQUAL, 24_000L * days ) ); }
        
        /**
         * If Apocalypse Rebooted is installed, check if the difficulty is above a threshold;
         * otherwise, check if the world time is after a certain number of days.
         */
        public OpBuilder<V> beforeDaysOrApocalypseDifficulty( int days ) { return in( new ApocalypseDifficultyOrTimeEnvironment( ComparatorValue.GREATER_OR_EQUAL.invert(), 24_000L * days ) ); }
        
        /**
         * If Apocalypse Rebooted is installed, check if the difficulty is above a threshold;
         * otherwise, check if the world time is after a certain number of months. One month is eight days.
         */
        public OpBuilder<V> afterMonthsOrApocalypseDifficulty( int months ) { return afterDaysOrApocalypseDifficulty( months * 8 ); }
        
        /**
         * If Apocalypse Rebooted is installed, check if the difficulty is above a threshold;
         * otherwise, check if the world time is after a certain number of months. One month is eight days.
         */
        public OpBuilder<V> beforeMonthsOrApocalypseDifficulty( int months ) { return beforeDaysOrApocalypseDifficulty( months * 8 ); }
        
        /** Check if the Apocalypse Rebooted difficulty is above a threshold. Always false if the mod is not installed. */
        public OpBuilder<V> aboveApocalypseDifficulty( int days ) { return in( new ApocalypseDifficultyEnvironment( ComparatorValue.GREATER_OR_EQUAL, 24_000L * days ) ); }
        
        /** Check if the Apocalypse Rebooted difficulty is above a threshold. Always false if the mod is not installed. */
        public OpBuilder<V> belowApocalypseDifficulty( int days ) { return in( new ApocalypseDifficultyEnvironment( ComparatorValue.GREATER_OR_EQUAL.invert(), 24_000L * days ) ); }
    }
}