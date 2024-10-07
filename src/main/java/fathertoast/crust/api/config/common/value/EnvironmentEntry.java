package fathertoast.crust.api.config.common.value;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.value.environment.AbstractEnvironment;
import fathertoast.crust.api.config.common.value.environment.ComparisonOperator;
import fathertoast.crust.api.config.common.value.environment.biome.*;
import fathertoast.crust.api.config.common.value.environment.compat.ApocalypseDifficultyEnvironment;
import fathertoast.crust.api.config.common.value.environment.compat.ApocalypseDifficultyOrTimeEnvironment;
import fathertoast.crust.api.config.common.value.environment.dimension.DimensionPropertyEnvironment;
import fathertoast.crust.api.config.common.value.environment.dimension.DimensionTypeEnvironment;
import fathertoast.crust.api.config.common.value.environment.dimension.DimensionTypeGroupEnvironment;
import fathertoast.crust.api.config.common.value.environment.position.PositionEnvironment;
import fathertoast.crust.api.config.common.value.environment.position.StructureEnvironment;
import fathertoast.crust.api.config.common.value.environment.position.YEnvironment;
import fathertoast.crust.api.config.common.value.environment.position.YFromSeaEnvironment;
import fathertoast.crust.api.config.common.value.environment.time.*;
import fathertoast.crust.api.lib.EnvironmentHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * One condition-value entry in an environment list. Uses a 'lazy' implementation so any needed registries are
 * not polled until this entry is actually used.
 */
@SuppressWarnings( "unused" )
public class EnvironmentEntry {
    
    /** The value given to this entry. */
    public final double VALUE;
    /** The conditions that define this entry's environment. */
    private final AbstractEnvironment[] CONDITIONS;
    
    /** Creates an entry with the specified values. */
    public EnvironmentEntry( double value, List<AbstractEnvironment> conditions ) { this( value, conditions.toArray( new AbstractEnvironment[0] ) ); }
    
    /** Creates an entry with the specified values. */
    public EnvironmentEntry( double value, AbstractEnvironment... conditions ) {
        VALUE = value;
        CONDITIONS = conditions;
    }
    
    /**
     * @return Returns true if all this entry's conditions match the provided environment.
     * @throws IllegalStateException If the position is not in a fully loaded chunk.
     * @see EnvironmentHelper#isLoaded(net.minecraft.world.level.LevelAccessor, BlockPos)
     */
    public boolean matches( Level world, BlockPos pos ) {
        if( !EnvironmentHelper.isLoaded( world, pos ) ) {
            throw new IllegalStateException( "Attempted to query world data in an unloaded chunk. This is bad!" );
        }
        return unsafeMatches( world, pos );
    }
    
    /** @return Returns true if all this entry's conditions match the provided environment. */
    public boolean matches( Level world ) { return unsafeMatches( world, null ); }
    
    /**
     * @return Returns true if all this entry's conditions match the provided environment.
     * May cause a world loading deadlock if the position is not in a fully loaded chunk.
     */
    boolean unsafeMatches( Level world, @Nullable BlockPos pos ) {
        for( AbstractEnvironment condition : CONDITIONS ) {
            if( !condition.matches( world, pos ) ) return false;
        }
        return true;
    }
    
    /**
     * @return The string representation of this environment entry, as it would appear in a config file.
     * <p>
     * Format is "value condition1 state1 & condition2 state2 & ...".
     */
    @Override
    public String toString() {
        // Start with the value
        final StringBuilder str = new StringBuilder().append( VALUE );
        if( CONDITIONS.length > 0 ) {
            // List all conditions
            str.append( ' ' );
            boolean first = true;
            for( AbstractEnvironment condition : CONDITIONS ) {
                if( first ) first = false;
                else str.append( " & " );
                str.append( condition );
            }
        }
        return str.toString();
    }
    
    
    // ---- Builder Implementation ---- //
    
    /** Creates a new entry builder. The value is rounded to 2 decimal place precision. */
    public static Builder builder( CrustConfigSpec spec, float value ) { return builder( spec.MANAGER, value ); }
    
    /** Creates a new entry builder. The value is rounded to 2 decimal place precision. */
    public static Builder builder( ConfigManager cfgManager, float value ) { return new Builder( cfgManager, Math.round( value * 100.0 ) / 100.0 ); }
    
    /** Creates a new entry builder. */
    public static Builder builder( CrustConfigSpec spec, double value ) { return builder( spec.MANAGER, value ); }
    
    /** Creates a new entry builder. */
    public static Builder builder( ConfigManager cfgManager, double value ) { return new Builder( cfgManager, value ); }
    
    /**
     * Builder class used to simplify creation of environment entries for default configs,
     * with shortcuts for the most commonly used environments.
     * <p>
     * Keep in mind that ALL conditions in an entry must be satisfied for it to be chosen.
     */
    public static class Builder {
        
        private final ConfigManager MANAGER;
        private final double VALUE;
        private final ArrayList<AbstractEnvironment> CONDITIONS = new ArrayList<>();
        
        private Builder( ConfigManager cfgManager, double value ) {
            MANAGER = cfgManager;
            VALUE = value;
        }
        
        public EnvironmentEntry build() { return new EnvironmentEntry( VALUE, CONDITIONS ); }
        
        /** General-use adder. Use this when the standardized convenience methods below don't give what you need. */
        public Builder in( AbstractEnvironment condition ) {
            CONDITIONS.add( condition );
            return this;
        }
        
        
        // ---- Dimension-based ---- //
        
        /** Check if the dimension type causes water to instantly vaporize and has faster lava flow. */
        public Builder inUltraWarmDimension() { return inDimensionWithProperty( DimensionPropertyEnvironment.Value.ULTRAWARM, false ); }
        
        /** Check if the dimension type causes water to instantly vaporize and has faster lava flow. */
        public Builder notInUltraWarmDimension() { return inDimensionWithProperty( DimensionPropertyEnvironment.Value.ULTRAWARM, true ); }
        
        /** Check if the dimension type allows clocks, compasses, and beds to work. */
        public Builder inNaturalDimension() { return inDimensionWithProperty( DimensionPropertyEnvironment.Value.NATURAL, false ); }
        
        /** Check if the dimension type allows clocks, compasses, and beds to work. */
        public Builder notInNaturalDimension() { return inDimensionWithProperty( DimensionPropertyEnvironment.Value.NATURAL, true ); }
        
        private Builder inDimensionWithProperty( DimensionPropertyEnvironment.Value property, boolean invert ) {
            return in( new DimensionPropertyEnvironment( property, invert ) );
        }
        
        public Builder inOverworld() { return inDimensionType( Level.OVERWORLD, false ); }
        
        public Builder notInOverworld() { return inDimensionType( Level.OVERWORLD, true ); }
        
        public Builder inNether() { return inDimensionType( Level.NETHER, false ); }
        
        public Builder notInNether() { return inDimensionType( Level.NETHER, true ); }
        
        public Builder inTheEnd() { return inDimensionType( Level.END, false ); }
        
        public Builder notInTheEnd() { return inDimensionType( Level.END, true ); }
        
        private Builder inDimensionType( ResourceKey<Level> dimType, boolean invert ) {
            return in( new DimensionTypeEnvironment( MANAGER, dimType, invert ) );
        }
        
        /** Check if the dimension type is vanilla (registered with the "minecraft" namespace). */
        public Builder inVanillaDimension() {
            return in( new DimensionTypeGroupEnvironment( MANAGER, new ResourceLocation( "" ), false ) );
        }
        
        /** Check if the dimension type is vanilla (registered with the "minecraft" namespace). */
        public Builder notInVanillaDimension() {
            return in( new DimensionTypeGroupEnvironment( MANAGER, new ResourceLocation( "" ), true ) );
        }
        
        
        // ---- Biome-based ---- //
        
        /** @see TerrainDepthEnvironment */
        @Deprecated // TODO Reimplement as biome tag
        public Builder inWaterBiome() { return isDepth( ComparisonOperator.LESS_OR_EQUAL, 0.0F ); }
        
        /** @see TerrainDepthEnvironment */
        @Deprecated // TODO Reimplement as biome tag
        public Builder notInWaterBiome() { return isDepth( ComparisonOperator.LESS_OR_EQUAL.invert(), 0.0F ); }
        
        /** @see TerrainDepthEnvironment */
        @Deprecated // TODO Reimplement as biome tag
        public Builder inMountainBiome() { return isDepth( ComparisonOperator.GREATER_OR_EQUAL, 0.4F ); }
        
        /** @see TerrainDepthEnvironment */
        @Deprecated // TODO Reimplement as biome tag
        public Builder notInMountainBiome() { return isDepth( ComparisonOperator.GREATER_OR_EQUAL.invert(), 0.4F ); }
        
        /** @see TerrainDepthEnvironment */
        @Deprecated
        private Builder isDepth( ComparisonOperator op, float value ) { return this;/*in( new TerrainDepthEnvironment( op, value ) );*/ }
        
        /** @see TerrainScaleEnvironment */
        @Deprecated // TODO Reimplement as biome tag
        public Builder inFlatBiome() { return isScale( ComparisonOperator.LESS_OR_EQUAL, 0.1F ); }
        
        /** @see TerrainScaleEnvironment */
        @Deprecated // TODO Reimplement as biome tag
        public Builder notInFlatBiome() { return isScale( ComparisonOperator.LESS_OR_EQUAL.invert(), 0.1F ); }
        
        /** @see TerrainScaleEnvironment */
        @Deprecated // TODO Reimplement as biome tag
        public Builder inHillyBiome() { return isScale( ComparisonOperator.GREATER_OR_EQUAL, 0.3F ); }
        
        /** @see TerrainScaleEnvironment */
        @Deprecated // TODO Reimplement as biome tag
        public Builder notInHillyBiome() { return isScale( ComparisonOperator.GREATER_OR_EQUAL.invert(), 0.3F ); }
        
        /** @see TerrainScaleEnvironment */
        @Deprecated
        private Builder isScale( ComparisonOperator op, float value ) { return this;/*in( new TerrainScaleEnvironment( op, value ) );*/ }
        
        /** Check if the biome has rain disabled. */
        public Builder inDryBiome() { return inAvgRainfall( ComparisonOperator.EQUAL_TO, 0.0F ); }
        
        /** Check if the biome has rain disabled. */
        public Builder notInDryBiome() { return inAvgRainfall( ComparisonOperator.EQUAL_TO.invert(), 0.0F ); }
        
        /** Check if the biome's humidity hinders fire spread. */
        public Builder inHumidBiome() { return inAvgRainfall( ComparisonOperator.GREATER_THAN, 0.85F ); }
        
        /** Check if the biome's humidity hinders fire spread. */
        public Builder notInHumidBiome() { return inAvgRainfall( ComparisonOperator.GREATER_THAN.invert(), 0.85F ); }
        
        private Builder inAvgRainfall( ComparisonOperator op, float value ) { return in( new RainfallEnvironment( op, value ) ); }
        
        /** Check if the temperature is freezing. */
        public Builder isFreezing() { return in( new TemperatureEnvironment( true ) ); }
        
        /** Check if the temperature is freezing. */
        public Builder isNotFreezing() { return in( new TemperatureEnvironment( false ) ); }
        
        /** Check if the temperature is warm (disables snow golem trails). */
        public Builder isWarm() { return isTemperature( ComparisonOperator.GREATER_OR_EQUAL, 0.8F ); }
        
        /** Check if the temperature is warm (disables snow golem trails). */
        public Builder isNotWarm() { return isTemperature( ComparisonOperator.GREATER_OR_EQUAL.invert(), 0.8F ); }
        
        /** Check if the temperature is hot (causes snow golems to die). */
        public Builder isHot() { return isTemperature( ComparisonOperator.GREATER_THAN, 1.0F ); }
        
        /** Check if the temperature is hot (causes snow golems to die). */
        public Builder isNotHot() { return isTemperature( ComparisonOperator.GREATER_THAN.invert(), 1.0F ); }
        
        private Builder isTemperature( ComparisonOperator op, float value ) { return in( new TemperatureEnvironment( op, value ) ); }
        
        /** Check if the biome belongs to a specific category. */ // TODO Reimplement as biome tag
        public Builder inBiomeCategory( BiomeCategory category ) { return in( new BiomeCategoryEnvironment( category, false ) ); }
        
        /** Check if the biome belongs to a specific category. */ // TODO Reimplement as biome tag
        public Builder notInBiomeCategory( BiomeCategory category ) { return in( new BiomeCategoryEnvironment( category, true ) ); }
        
        /** Check if the biome is a specific one. */
        public Builder inBiome( ResourceKey<Biome> biome ) { return in( new BiomeEnvironment( MANAGER, biome, false ) ); }
        
        /** Check if the biome is a specific one. */
        public Builder notInBiome( ResourceKey<Biome> biome ) { return in( new BiomeEnvironment( MANAGER, biome, true ) ); }
        
        
        // ---- Position-based ---- //
        
        /** Check if the position is inside a particular structure. See {@link Structure}. */
        public Builder inStructure( ResourceKey<Structure> structure ) { return in( new StructureEnvironment( MANAGER, structure, false ) ); }
        
        /** Check if the position is inside a particular structure. See {@link Structure}. */
        public Builder notInStructure( ResourceKey<Structure> structure ) { return in( new StructureEnvironment( MANAGER, structure, true ) ); }
        
        /** Check if diamond/redstone ore can generate at the position. */
        public Builder belowDiamondLevel() { return belowY( 17 ); }
        
        /** Check if diamond/redstone ore can generate at the position. */
        public Builder aboveDiamondLevel() { return aboveY( 17 ); }
        
        /** Check if gold/lapis ore can generate at the position. */
        public Builder belowGoldLevel() { return belowY( 33 ); }
        
        /** Check if gold/lapis ore can generate at the position. */
        public Builder aboveGoldLevel() { return aboveY( 33 ); }
        
        private Builder belowY( int y ) { return in( new YEnvironment( ComparisonOperator.LESS_THAN, y ) ); }
        
        private Builder aboveY( int y ) { return in( new YEnvironment( ComparisonOperator.LESS_THAN.invert(), y ) ); }
        
        /** Check if the position is above/below sea level. */
        public Builder belowSeaLevel() { return belowSeaLevel( 0 ); }
        
        /** Check if the position is above/below sea level. */
        public Builder aboveSeaLevel() { return aboveSeaLevel( 0 ); }
        
        /** Check if the position is above/below the average sea floor. */
        public Builder belowSeaDepths() { return belowSeaLevel( -17 ); }
        
        /** Check if the position is above/below the average sea floor. */
        public Builder aboveSeaDepths() { return aboveSeaLevel( -17 ); }
        
        /** Check if the position is above/below the average sea floor. */
        public Builder belowSeaFloor() { return belowSeaLevel( -27 ); }
        
        /** Check if the position is above/below the average sea floor. */
        public Builder aboveSeaFloor() { return aboveSeaLevel( -27 ); }
        
        /** Check if the position is above/below 'mountain level' - that is, high enough to die from falling to sea level. */
        public Builder belowMountainLevel() { return belowSeaLevel( 25 ); }
        
        /** Check if the position is above/below 'mountain level' - that is, high enough to die from falling to sea level. */
        public Builder aboveMountainLevel() { return aboveSeaLevel( 25 ); }
        
        private Builder belowSeaLevel( int dY ) { return in( new YFromSeaEnvironment( ComparisonOperator.LESS_THAN, dY ) ); }
        
        private Builder aboveSeaLevel( int dY ) { return in( new YFromSeaEnvironment( ComparisonOperator.LESS_THAN.invert(), dY ) ); }
        
        public Builder canSeeSky() { return inPositionWithState( PositionEnvironment.Value.CAN_SEE_SKY, false ); }
        
        public Builder cannotSeeSky() { return inPositionWithState( PositionEnvironment.Value.CAN_SEE_SKY, true ); }
        
        public Builder isNearVillage() { return inPositionWithState( PositionEnvironment.Value.IS_NEAR_VILLAGE, false ); }
        
        public Builder isNotNearVillage() { return inPositionWithState( PositionEnvironment.Value.IS_NEAR_VILLAGE, true ); }
        
        public Builder isNearRaid() { return inPositionWithState( PositionEnvironment.Value.IS_NEAR_RAID, false ); }
        
        public Builder isNotNearRaid() { return inPositionWithState( PositionEnvironment.Value.IS_NEAR_RAID, true ); }
        
        private Builder inPositionWithState( PositionEnvironment.Value state, boolean invert ) { return in( new PositionEnvironment( state, invert ) ); }
        
        
        // ---- Time-based ---- //
        
        /** Check if the special difficulty multiplier is above a threshold (0 - 1). */
        public Builder aboveDifficulty( float percent ) { return in( new SpecialDifficultyEnvironment( ComparisonOperator.GREATER_OR_EQUAL, percent ) ); }
        
        /** Check if the special difficulty multiplier is above a threshold (0 - 1). */
        public Builder belowDifficulty( float percent ) { return in( new SpecialDifficultyEnvironment( ComparisonOperator.GREATER_OR_EQUAL.invert(), percent ) ); }
        
        public Builder isRaining() { return inWeather( WeatherEnvironment.Value.RAIN, false ); } // same as "is not clear"
        
        public Builder isNotRaining() { return inWeather( WeatherEnvironment.Value.RAIN, true ); } // same as "is clear"
        
        public Builder isThundering() { return inWeather( WeatherEnvironment.Value.THUNDER, false ); }
        
        public Builder isNotThundering() { return inWeather( WeatherEnvironment.Value.THUNDER, true ); }
        
        private Builder inWeather( WeatherEnvironment.Value weather, boolean invert ) { return in( new WeatherEnvironment( weather, invert ) ); }
        
        public Builder atMaxMoonLight() { return in( new MoonPhaseEnvironment( MoonPhaseEnvironment.Value.FULL, false ) ); }
        
        public Builder aboveHalfMoonLight() { return fromHalfMoonLight( ComparisonOperator.GREATER_THAN ); }
        
        public Builder atHalfMoonLight() { return fromHalfMoonLight( ComparisonOperator.EQUAL_TO ); }
        
        public Builder belowHalfMoonLight() { return fromHalfMoonLight( ComparisonOperator.LESS_THAN ); }
        
        public Builder atNoMoonLight() { return in( new MoonPhaseEnvironment( MoonPhaseEnvironment.Value.NEW, false ) ); }
        
        private Builder fromHalfMoonLight( ComparisonOperator op ) { return in( new MoonBrightnessEnvironment( op, 0.5F ) ); }
        
        public Builder isNight() { return in( new DayTimeEnvironment( DayTimeEnvironment.Value.NIGHT, false ) ); }
        
        public Builder isDay() { return in( new DayTimeEnvironment( DayTimeEnvironment.Value.DAY, false ) ); }
        
        /** Check if the time is during a quarter of the night centered on midnight. */
        public Builder isNearMidnight() { return in( new TimeFromMidnightEnvironment( ComparisonOperator.LESS_OR_EQUAL, 1_500 ) ); }
        
        /** Check if the time is during a quarter of the night centered on midnight. */
        public Builder isNotNearMidnight() { return in( new TimeFromMidnightEnvironment( ComparisonOperator.LESS_OR_EQUAL.invert(), 1_500 ) ); }
        
        /**
         * Check if the world time is after a certain number of days. Should use
         * {@link #afterDaysOrApocalypseDifficulty(int)} instead for options that make the game harder.
         */
        public Builder afterDays( int days ) { return in( new WorldTimeEnvironment( ComparisonOperator.GREATER_OR_EQUAL, 24_000L * days ) ); }
        
        /**
         * Check if the world time is after a certain number of days. Should use
         * {@link #beforeDaysOrApocalypseDifficulty(int)} instead for options that make the game harder.
         */
        public Builder beforeDays( int days ) { return in( new WorldTimeEnvironment( ComparisonOperator.GREATER_OR_EQUAL.invert(), 24_000L * days ) ); }
        
        /**
         * Check if the world time is after a certain number of months. One month is eight days. Should use
         * {@link #afterMonthsOrApocalypseDifficulty(int)} instead for options that make the game harder.
         */
        public Builder afterMonths( int months ) { return afterDays( months * 8 ); }
        
        /**
         * Check if the world time is after a certain number of months. One month is eight days. Should use
         * {@link #beforeMonthsOrApocalypseDifficulty(int)} instead for options that make the game harder.
         */
        public Builder beforeMonths( int months ) { return beforeDays( months * 8 ); }
        
        /** Check if the chunk inhabited time is after a certain number of days. */
        public Builder afterDaysInChunk( int days ) { return in( new ChunkTimeEnvironment( ComparisonOperator.GREATER_OR_EQUAL, 24_000L * days ) ); }
        
        /** Check if the chunk inhabited time is after a certain number of days. */
        public Builder beforeDaysInChunk( int days ) { return in( new ChunkTimeEnvironment( ComparisonOperator.GREATER_OR_EQUAL.invert(), 24_000L * days ) ); }
        
        
        // ---- Mod-based ---- //
        
        /**
         * If Apocalypse Rebooted is installed, check if the difficulty is above a threshold;
         * otherwise, check if the world time is after a certain number of days.
         */
        public Builder afterDaysOrApocalypseDifficulty( int days ) { return in( new ApocalypseDifficultyOrTimeEnvironment( ComparisonOperator.GREATER_OR_EQUAL, 24_000L * days ) ); }
        
        /**
         * If Apocalypse Rebooted is installed, check if the difficulty is above a threshold;
         * otherwise, check if the world time is after a certain number of days.
         */
        public Builder beforeDaysOrApocalypseDifficulty( int days ) { return in( new ApocalypseDifficultyOrTimeEnvironment( ComparisonOperator.GREATER_OR_EQUAL.invert(), 24_000L * days ) ); }
        
        /**
         * If Apocalypse Rebooted is installed, check if the difficulty is above a threshold;
         * otherwise, check if the world time is after a certain number of months. One month is eight days.
         */
        public Builder afterMonthsOrApocalypseDifficulty( int months ) { return afterDaysOrApocalypseDifficulty( months * 8 ); }
        
        /**
         * If Apocalypse Rebooted is installed, check if the difficulty is above a threshold;
         * otherwise, check if the world time is after a certain number of months. One month is eight days.
         */
        public Builder beforeMonthsOrApocalypseDifficulty( int months ) { return beforeDaysOrApocalypseDifficulty( months * 8 ); }
        
        /** Check if the Apocalypse Rebooted difficulty is above a threshold. Always false if the mod is not installed. */
        public Builder aboveApocalypseDifficulty( int days ) { return in( new ApocalypseDifficultyEnvironment( ComparisonOperator.GREATER_OR_EQUAL, 24_000L * days ) ); }
        
        /** Check if the Apocalypse Rebooted difficulty is above a threshold. Always false if the mod is not installed. */
        public Builder belowApocalypseDifficulty( int days ) { return in( new ApocalypseDifficultyEnvironment( ComparisonOperator.GREATER_OR_EQUAL.invert(), 24_000L * days ) ); }
    }
}