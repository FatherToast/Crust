package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.EnvironmentEntry;
import fathertoast.crust.api.config.common.value.EnvironmentList;
import fathertoast.crust.api.config.common.value.environment.AbstractEnvironment;
import fathertoast.crust.api.config.common.value.environment.CrustEnvironmentRegistry;
import fathertoast.crust.api.config.common.value.environment.biome.*;
import fathertoast.crust.api.config.common.value.environment.compat.ApocalypseDifficultyEnvironment;
import fathertoast.crust.api.config.common.value.environment.dimension.DimensionPropertyEnvironment;
import fathertoast.crust.api.config.common.value.environment.dimension.DimensionTypeEnvironment;
import fathertoast.crust.api.config.common.value.environment.dimension.DimensionTypeGroupEnvironment;
import fathertoast.crust.api.config.common.value.environment.position.*;
import fathertoast.crust.api.config.common.value.environment.time.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a config field with an environment list value.
 */
@SuppressWarnings( "unused" )
public class EnvironmentListField extends GenericField<EnvironmentList> {
    
    /**
     * Provides a description of how to use environment lists. Recommended to put at the top of any file using environment lists.
     * Always use put the environment condition descriptions at the bottom of the file if this is used!
     */
    public static List<String> verboseDescription() {
        List<String> comment = new ArrayList<>();
        comment.add( "Environment List fields: General format =" );
        comment.add( "    [ \"value environment1 condition1 & environment2 condition2 & ...\", ... ]" );
        comment.add( "  Environment lists are arrays of environment entries. Each entry is a value followed by the " +
                "environment conditions that must be satisfied for the value to be chosen. The environments are tested " +
                "in the order listed, and the first matching entry is chosen." );
        comment.add( "  See the bottom of this file for an explanation on each environment condition available." );
        return comment;
    }
    
    
    /** Provides a detailed description of how to use each environment condition. Recommended to put at the bottom of any file using environment lists. */
    public static List<String> environmentDescriptions() { return CrustEnvironmentRegistry.getDescriptions(); }
    
    /** Creates a new field. */
    public EnvironmentListField( String key, EnvironmentList defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoFormat( "Environment List", valueDefault,
                "[ \"value condition1 state1 & condition2 state2 & ...\", ... ]" ) );
        comment.add( "   Range for Values: " + TomlHelper.fieldRange( valueDefault.getMinValue(), valueDefault.getMaxValue() ) );
    }
    
    /**
     * Loads this field's value from the given raw toml value. If anything goes wrong, correct it at the lowest level possible.
     * <p>
     * For example, a missing value should be set to the default, while an out-of-range value should be adjusted to the
     * nearest in-range value
     */
    @Override
    public void load( @Nullable Object raw ) {
        if( raw == null ) {
            value = valueDefault;
            return;
        }
        List<String> list = TomlHelper.parseStringList( raw );
        List<EnvironmentEntry> entryList = new ArrayList<>();
        for( String line : list ) {
            entryList.add( parseEntry( line ) );
        }
        value = new EnvironmentList( entryList );
    }
    
    /** Parses a single entry line and returns the result. */
    private EnvironmentEntry parseEntry( final String line ) {
        // Parse the value out of the conditions
        final String[] args = line.split( " ", 2 );
        final double value = parseValue( args[0], line );
        
        final List<AbstractEnvironment> conditions = new ArrayList<>();
        if( args.length > 1 ) {
            final String[] condArgs = args[1].split( "&" );
            for( String condArg : condArgs ) {
                conditions.add( parseCondition( condArg.trim(), line ) );
            }
        }
        if( conditions.isEmpty() ) {
            ConfigUtil.LOG.warn( "No environments defined in entry for {} \"{}\"! Invalid entry: {}",
                    getClass(), getKey(), line );
        }
        
        return new EnvironmentEntry( value, conditions );
    }
    
    /** Parses a single value argument and returns a valid result. */
    private double parseValue( final String arg, final String line ) {
        // Try to parse the value
        double value;
        try {
            value = Double.parseDouble( arg );
        }
        catch( NumberFormatException ex ) {
            // This is thrown if the string is not a parsable number
            ConfigUtil.LOG.warn( "Invalid value for {} \"{}\"! Falling back to 0. Invalid entry: {}",
                    getClass(), getKey(), line );
            value = 0.0;
        }
        // Verify value is within range
        if( value < valueDefault.getMinValue() ) {
            ConfigUtil.LOG.warn( "Value for {} \"{}\" is below the minimum ({})! Clamping value. Invalid value: {}",
                    getClass(), getKey(), valueDefault.getMinValue(), value );
            value = valueDefault.getMinValue();
        }
        else if( value > valueDefault.getMaxValue() ) {
            ConfigUtil.LOG.warn( "Value for {} \"{}\" is above the maximum ({})! Clamping value. Invalid value: {}",
                    getClass(), getKey(), valueDefault.getMaxValue(), value );
            value = valueDefault.getMaxValue();
        }
        return value;
    }
    
    /** Parses a single environment condition argument and returns a valid result. */
    private AbstractEnvironment parseCondition( final String arg, final String line ) {
        // First parse the environment name, since it defines the format for the rest
        final String[] args = arg.split( " ", 2 );
        
        final String value;
        if( args.length < 2 ) value = "";
        else value = args[1].trim();
        
        return CrustEnvironmentRegistry.parse( this, args[0], value );
    }
    
    
    // Convenience methods
    
    /** @return The value matching the given environment, or the default value if no matching environment is defined. */
    public double getOrElse( World world, @Nullable BlockPos pos, DoubleField defaultValue ) { return get().getOrElse( world, pos, defaultValue ); }
    
    /** @return The value matching the given environment, or the default value if no matching environment is defined. */
    public double getOrElse( World world, @Nullable BlockPos pos, double defaultValue ) { return get().getOrElse( world, pos, defaultValue ); }
    
    /** @return The value matching the given environment, or null if no matching environment is defined. */
    @Nullable
    public Double get( World world, @Nullable BlockPos pos ) { return get().get( world, pos ); }
    
    
    /* Note: when adding new environment conditions:
     *  - Create the environment class
     *  - Register and describe it here
     *  - Add any applicable builder methods in EnvironmentEntry.Builder
     */
    static {
        // Dimension-based
        CrustEnvironmentRegistry.register( "dimension_property", DimensionPropertyEnvironment::new,
                "(!)property",
                "Valid property values: " + TomlHelper.toLiteralList( (Object[]) DimensionPropertyEnvironment.Value.values() ),
                "Dimension properties are the true/false values available to dimension types in data packs. " +
                        "See the wiki for more info: [https://minecraft.fandom.com/wiki/Custom_dimension#Syntax]." );
        CrustEnvironmentRegistry.register( "dimension_type", ( field, value ) -> value.endsWith( "*" ) ?
                        new DimensionTypeGroupEnvironment( field, value ) : new DimensionTypeEnvironment( field, value ),
                "(!)namespace:dimension_type_name",
                "The world's dimension type. In vanilla, these are only \"minecraft:overworld\", " +
                        "\"minecraft:the_nether\", or \"minecraft:the_end\"." );
        
        // Biome-based
        CrustEnvironmentRegistry.register( "terrain_depth", TerrainDepthEnvironment::new,
                "op value",//TODO see if this changes in MC 1.18
                "Biome's depth parameter. A measure of how high the terrain generates; depth < 0 makes a " +
                        "watery biome. For reference, generally vanilla plateaus are 1.5, mountains are 1, plains are " +
                        "0.125, swamps are -0.2, rivers are -0.5, oceans are -1, and deep oceans are -1.8." );
        CrustEnvironmentRegistry.register( "terrain_scale", TerrainScaleEnvironment::new,
                "op value",
                "Biome's scale parameter. A measure of how 'wavy' the terrain generates. For reference, " +
                        "generally vanilla mountains are 0.5 and plains are 0.05." );
        CrustEnvironmentRegistry.register( "rainfall", RainfallEnvironment::new,
                "op value",
                "Biome's rainfall parameter. If this is \"= 0\", it checks that rain is disabled. For " +
                        "reference, rainfall > 0.85 suppresses fire." );
        CrustEnvironmentRegistry.register( "biome_temp", BiomeTemperatureEnvironment::new,
                "op value OR (!)" + TemperatureEnvironment.FREEZING,
                "Biome's temperature parameter. For reference, freezing is < 0.15 and hot is generally " +
                        "considered > 0.95." );
        CrustEnvironmentRegistry.register( "temp", TemperatureEnvironment::new,
                "op value OR (!)" + TemperatureEnvironment.FREEZING,
                "Height-adjusted temperature. For reference, freezing is < 0.15 and hot is generally " +
                        "considered > 0.95." );
        CrustEnvironmentRegistry.register( "biome_category", BiomeCategoryEnvironment::new,
                "(!)category",
                "Valid category values: " + TomlHelper.toLiteralList( (Object[]) BiomeCategory.values() ) );
        CrustEnvironmentRegistry.register( "biome", ( field, value ) -> value.endsWith( "*" ) ?
                        new BiomeGroupEnvironment( field, value ) : new BiomeEnvironment( field, value ),
                "(!)namespace:biome_name",
                "The biome. See the wiki for vanilla biome names (resource locations) " +
                        "[https://minecraft.fandom.com/wiki/Biome#Biome_IDs]." );
        
        // Position-based
        CrustEnvironmentRegistry.register( "structure", ( field, value ) -> value.endsWith( "*" ) ?
                        new StructureGroupEnvironment( field, value ) : new StructureEnvironment( field, value ),
                "(!)namespace:structure_name",
                "The structure. See the wiki for vanilla structure names " +
                        "[https://minecraft.fandom.com/wiki/Generated_structures#Locating]." );
        CrustEnvironmentRegistry.register( "y", YEnvironment::new,
                "op value",//TODO change lava level to -54 for MC 1.18
                "The y-value. For reference, sea level is normally 63 and lava level is normally 10." );
        CrustEnvironmentRegistry.register( "y_from_sea", YFromSeaEnvironment::new,
                "op value",
                "The y-value from sea level. Expect the only air <= 0 to be in caves/ravines (which may " +
                        "still have direct view of the sky)." );
        CrustEnvironmentRegistry.register( "position", PositionEnvironment::new,
                "(!)state",
                "Valid state values: " + TomlHelper.toLiteralList( (Object[]) PositionEnvironment.Value.values() ),
                "Miscellaneous conditions that generally do what you expect. For reference, 'near' a village is ~3 " +
                        "chunks, and redstone checks weak power." );
        
        // Time-based
        CrustEnvironmentRegistry.register( "difficulty", DifficultyEnvironment::new,
                "op value",
                "The regional difficulty (0 to 6.75). This is based on many factors such as difficulty " +
                        "setting, moon brightness, chunk inhabited time, and world time.",
                "For reference, this scales up to the max after 63 days in the world and 150 days in a particular " +
                        "chunk, and peaks during full moons. On Peaceful this is always 0, on Easy this is 0.75 to " +
                        "1.5, on Normal this is 1.5 to 4.0, and on Hard this is 2.25 to 6.75." );
        CrustEnvironmentRegistry.register( "special_difficulty", SpecialDifficultyEnvironment::new,
                "op value",
                "The 'special multiplier' for regional difficulty (0 to 1). For reference, this is 0 when " +
                        "difficulty <= 2 and 1 when difficulty >= 4.",
                "This is always 0 in Easy and below. In Normal, it maxes at absolute peak regional difficulty. " +
                        "In Hard, it starts at 0.125 and maxes out in ~50 days." );
        CrustEnvironmentRegistry.register( "weather", WeatherEnvironment::new,
                "(!)type",
                "Valid type values: " + TomlHelper.toLiteralList( (Object[]) WeatherEnvironment.Value.values() ) );
        CrustEnvironmentRegistry.register( "moon_brightness", MoonBrightnessEnvironment::new,
                "op value",
                "The moon brightness (0 to 1). New moon has 0 brightness, full moon has 1 brightness. " +
                        "Intermediate phases are 0.25, 0.5, or 0.75." );
        CrustEnvironmentRegistry.register( "moon_phase", MoonPhaseEnvironment::new,
                "(!)phase",
                "Valid phase values: " + TomlHelper.toLiteralList( (Object[]) MoonPhaseEnvironment.Value.values() ),
                "For reference, the first day in a new world is always a full moon." );
        CrustEnvironmentRegistry.register( "day_time", DayTimeEnvironment::new,
                "(!)time",
                "Valid time values: " + TomlHelper.toLiteralList( (Object[]) DayTimeEnvironment.Value.values() ),
                "Note that the transition periods, sunset & sunrise, are considered as part of day & night, respectively." );
        CrustEnvironmentRegistry.register( "time_from_midnight", TimeFromMidnightEnvironment::new,
                "op value",
                "The absolute time in ticks away from midnight. Value must be 0 to 12000." );
        CrustEnvironmentRegistry.register( "world_time", WorldTimeEnvironment::new,
                "op value",
                "The total time the world has existed, in ticks. For reference, each day cycle is 24000 " +
                        "ticks and each lunar cycle is 192000 ticks." );
        CrustEnvironmentRegistry.register( "chunk_time", ChunkTimeEnvironment::new,
                "op value",
                "The total time the chunk has been loaded, in ticks. For reference, each day cycle is 24000 " +
                        "ticks and each lunar cycle is 192000 ticks." );
        
        // Mod-based
        CrustEnvironmentRegistry.register( "apocalypse_difficulty", ApocalypseDifficultyEnvironment::new,
                " op value",
                "The Apocalypse Rebooted mod's difficulty (scale depends on your config). This is based on " +
                        "the nearest player's current difficulty level. If no player exists, it assumes 0 difficulty." );
    }
}