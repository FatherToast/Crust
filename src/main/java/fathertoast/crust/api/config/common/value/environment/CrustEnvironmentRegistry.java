package fathertoast.crust.api.config.common.value.environment;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.field.EnvironmentListField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.environment.biome.*;
import fathertoast.crust.api.config.common.value.environment.compat.ApocalypseDifficultyEnvironment;
import fathertoast.crust.api.config.common.value.environment.compat.ApocalypseDifficultyOrTimeEnvironment;
import fathertoast.crust.api.config.common.value.environment.dimension.DimensionPropertyEnvironment;
import fathertoast.crust.api.config.common.value.environment.dimension.DimensionTypeEnvironment;
import fathertoast.crust.api.config.common.value.environment.dimension.DimensionTypeGroupEnvironment;
import fathertoast.crust.api.config.common.value.environment.position.*;
import fathertoast.crust.api.config.common.value.environment.time.*;

import javax.annotation.Nullable;
import java.util.*;

public final class CrustEnvironmentRegistry {
    
    /**
     * Call this to register an environment that can be used in {@link EnvironmentListField}s.
     * This links a unique identifier (environment name) to a parsing function so the environment can be read from files.
     * <p>
     * NOTE: If the factory is capable of generating more than one class type, you must list them all with
     * {@link #register(String, IFactory, List, String, String...)}.
     *
     * @param name        The unique environment name. Not case-sensitive.
     * @param factory     A bi-function that parses a file line to load the environment from file. Often just a reference to the constructor.
     * @param type        The environment class created by the factory.
     * @param format      The data format expected by the factory.
     *                    Use (!) to show where the 'invert operator' can be placed. Use op to show where a 'comparison operator' is used.
     * @param description A description that details how to use the environment condition.
     * @return The environment name registered, for convenience.
     * @throws IllegalStateException If the environment name or class have already been registered.
     */
    public static String register( String name, IFactory factory, Class<? extends AbstractEnvironment> type,
                                   String format, String... description ) {
        return register( name, factory, Collections.singletonList( type ), format, description );
    }
    
    /**
     * Call this to register an environment that can be used in {@link EnvironmentListField}s.
     * This links a unique identifier (environment name) to a parsing function so the environment can be read from files.
     *
     * @param name        The unique environment name. Not case-sensitive.
     * @param factory     A bi-function that parses a file line to load the environment from file. Often just a reference to the constructor.
     * @param types       The environment classes that can be created by the factory.
     * @param format      The data format expected by the factory.
     *                    Use (!) to show where the 'invert operator' can be placed. Use op to show where a 'comparison operator' is used.
     * @param description A description that details how to use the environment condition.
     * @return The environment name registered, for convenience.
     * @throws IllegalStateException If the environment name or any of the classes have already been registered.
     */
    public static String register( String name, IFactory factory, List<Class<? extends AbstractEnvironment>> types,
                                   String format, String... description ) {
        name = name.toLowerCase( Locale.ROOT ).replace( ' ', '_' ); // Not case sensitive, cannot have spaces
        if( NAME_TO_FACTORY_MAP.containsKey( name ) )
            throw new IllegalStateException( "Duplicate environment names cannot be registered! Duplicate name: " + name );
        if( name.equals( "null" ) )
            throw new IllegalArgumentException( "Invalid environment name: " + name );
        
        // Link unique identifier to data parser
        NAME_TO_FACTORY_MAP.put( name, factory );
        
        // Link classes to unique identifier
        for( Class<? extends AbstractEnvironment> type : types ) {
            if( CLASS_TO_NAME_MAP.containsKey( type ) )
                throw new IllegalStateException( "Classes cannot be registered to multiple environment names! Class: " + type );
            CLASS_TO_NAME_MAP.put( type, name );
        }
        
        // Add info to overall environment 'how-to' comment
        DESCRIPTIONS.add( "" );
        if( description.length > 0 ) {
            DESCRIPTIONS.add( "  \"" + name + " " + format + "\":" );
            for( String line : description ) DESCRIPTIONS.add( "    " + line );
        }
        else {
            DESCRIPTIONS.add( "  \"" + name + " " + format + "\"" );
        }
        
        return name;
    }
    
    /**
     * @param field The loading field.
     * @param name  The unique environment name.
     * @param value Environment data; empty string if none was provided.
     * @return A new environment instance parsed from the provided data.
     */
    public static AbstractEnvironment parse( AbstractConfigField field, String name, String value ) {
        IFactory factory = NAME_TO_FACTORY_MAP.get( name.toLowerCase( Locale.ROOT ) );
        
        if( factory == null ) {
            // The environment name was not recognized; try to provide some good feedback because this field is complicated
            final AbstractEnvironment fallback = new WorldTimeEnvironment( ComparisonOperator.LESS_THAN, 0 );
            ConfigUtil.LOG.warn( "Invalid environment '{}' for {} \"{}\"! Falling back to \"{}\". Environment name must be in the set [ {} ]. Invalid environment: {}",
                    name, field.getClass(), field.getKey(), fallback,
                    TomlHelper.toLiteralList( (Object[]) getNames().toArray( new String[0] ) ), value );
            return fallback;
        }
        
        // Allow the registered factory to decide how to load the rest
        return factory.parse( field, value );
    }
    
    /** @return A read-only set of all registered environment names. */
    public static Set<String> getNames() { return NAME_SET; }
    
    /** @return The registered name for the environment. */
    @Nullable
    public static String getName( AbstractEnvironment environment ) { return getName( environment.getClass() ); }
    
    /** @return The registered name for the environment class. */
    @Nullable
    public static String getName( Class<? extends AbstractEnvironment> type ) { return CLASS_TO_NAME_MAP.get( type ); }
    
    /** @return The full descriptions of all registered environments, plus header for use in config files. */
    public static List<String> getDescriptions() { return Collections.unmodifiableList( DESCRIPTIONS ); }
    
    /** A bi-function that parses a file line to load the environment from file. Often just a reference to the constructor. */
    public interface IFactory {
        /**
         * Constructs a new environment instance based on the condition value.
         * <p>
         * For example, if the environment condition is "special_difficulty >= 0.5", then the condition value passed
         * to this parse method is ">= 0.5". If only the environment name was given, the value is an empty string.
         *
         * @param field The field currently being parsed. This should be used only for reporting problems.
         * @param value Additional information provided with the environment.
         * @return A newly constructed environment.
         */
        AbstractEnvironment parse( AbstractConfigField field, String value );
    }
    
    
    private static final Map<String, IFactory> NAME_TO_FACTORY_MAP = new HashMap<>();
    private static final Set<String> NAME_SET = Collections.unmodifiableSet( NAME_TO_FACTORY_MAP.keySet() );
    private static final Map<Class<? extends AbstractEnvironment>, String> CLASS_TO_NAME_MAP = new HashMap<>();
    
    private static final List<String> DESCRIPTIONS;
    
    static {
        // Initialize environment descriptions comment
        List<String> comment = new ArrayList<>();
        comment.add( "Environment conditions (for Environment List entries):" );
        comment.add( "  Many environment conditions can be inverted by using \"!\"; these are shown with (!) in the " +
                "appropriate location." );
        comment.add( "  Other environment conditions are numerical comparisons; these use the operators (shown as op) " +
                "<, >, =, <=, >=, or != to compare value." );
        comment.add( "" );
        comment.add( "Valid environment conditions are:" );
        DESCRIPTIONS = comment;
        
        
        // Register built-in conditions
        /* Note: when adding new environment conditions:
         *  - Create the environment class
         *  - Register and describe it here
         *  - Add any applicable builder methods in EnvironmentEntry.Builder
         *  - Create a test case in TestConfigFile using the builder method
         */
        // Dimension-based
        register( "dimension_property", DimensionPropertyEnvironment::new, DimensionPropertyEnvironment.class,
                "(!)property",
                "Valid property values: " + TomlHelper.toLiteralList( (Object[]) DimensionPropertyEnvironment.Value.values() ),
                "Dimension properties are the true/false values available to dimension types in data packs. " +
                        "See the wiki for more info: [https://minecraft.fandom.com/wiki/Custom_dimension#Syntax]." );
        register( "dimension_type", ( field, value ) -> value.endsWith( "*" ) ?
                        new DimensionTypeGroupEnvironment( field, value ) : new DimensionTypeEnvironment( field, value ),
                Arrays.asList( DimensionTypeGroupEnvironment.class, DimensionTypeEnvironment.class ),
                "(!)namespace:dimension_type_name",
                "The world's dimension type. In vanilla, these are only \"minecraft:overworld\", " +
                        "\"minecraft:the_nether\", or \"minecraft:the_end\"." );
        
        // Biome-based
        //        register( "terrain_depth", TerrainDepthEnvironment::new, TerrainDepthEnvironment.class,
        //                "op value",
        //                "Biome's depth parameter. A measure of how high the terrain generates; depth < 0 makes a " +
        //                        "watery biome. For reference, generally vanilla plateaus are 1.5, mountains are 1, plains are " +
        //                        "0.125, swamps are -0.2, rivers are -0.5, oceans are -1, and deep oceans are -1.8." );
        //        register( "terrain_scale", TerrainScaleEnvironment::new, TerrainScaleEnvironment.class,
        //                "op value",
        //                "Biome's scale parameter. A measure of how 'wavy' the terrain generates. For reference, " +
        //                        "generally vanilla mountains are 0.5 and plains are 0.05." );
        register( "rainfall", RainfallEnvironment::new, RainfallEnvironment.class,
                "op value",
                "Biome's rainfall parameter. If this is \"= 0\", it checks that rain is disabled. For " +
                        "reference, rainfall > 0.85 suppresses fire." );
        register( "biome_temp", BiomeTemperatureEnvironment::new, BiomeTemperatureEnvironment.class,
                "op value OR (!)" + TemperatureEnvironment.FREEZING,
                "Biome's temperature parameter. For reference, freezing is < 0.15 and hot is generally " +
                        "considered > 0.95." );
        register( "temp", TemperatureEnvironment::new, TemperatureEnvironment.class,
                "op value OR (!)" + TemperatureEnvironment.FREEZING,
                "Height-adjusted temperature. For reference, freezing is < 0.15 and hot is generally " +
                        "considered > 0.95." );
        register( "biome_category", BiomeCategoryEnvironment::new, BiomeCategoryEnvironment.class,
                "(!)category", // TODO Remove when updating beyond MC 1.20
                "Valid category values: " + TomlHelper.toLiteralList( (Object[]) BiomeCategory.values() ) );
        // TODO biome_tag
        register( "biome", ( field, value ) -> value.endsWith( "*" ) ?
                        new BiomeGroupEnvironment( field, value ) : new BiomeEnvironment( field, value ),
                Arrays.asList( BiomeGroupEnvironment.class, BiomeEnvironment.class ),
                "(!)namespace:biome_name",
                "The biome. See the wiki for vanilla biome names (resource locations) " +
                        "[https://minecraft.fandom.com/wiki/Biome#Biome_IDs]." );
        
        // Position-based
        register( "structure", ( field, value ) -> value.endsWith( "*" ) ?
                        new StructureGroupEnvironment( field, value ) : new StructureEnvironment( field, value ),
                Arrays.asList( StructureGroupEnvironment.class, StructureEnvironment.class ),
                "(!)namespace:structure_name",
                "The structure. See the wiki for vanilla structure names " +
                        "[https://minecraft.fandom.com/wiki/Structure#ID]." );
        register( "y", YEnvironment::new, YEnvironment.class,
                "op value",
                "The y-value. For reference, sea level is normally 63 and lava level is normally -54." );
        register( "y_from_sea", YFromSeaEnvironment::new, YFromSeaEnvironment.class,
                "op value",
                "The y-value from sea level. Expect the only air < 0 to be in caves/ravines (which may " +
                        "still have direct view of the sky)." );
        register( "position", PositionEnvironment::new, PositionEnvironment.class,
                "(!)state",
                "Valid state values: " + TomlHelper.toLiteralList( (Object[]) PositionEnvironment.Value.values() ),
                "Miscellaneous conditions that generally do what you expect. For reference, 'near' a village is ~3 " +
                        "chunks, and redstone checks weak power." );
        
        // Time-based
        register( "difficulty", DifficultyEnvironment::new, DifficultyEnvironment.class,
                "op value",
                "The regional difficulty (0 to 6.75). This is based on many factors such as difficulty " +
                        "setting, moon brightness, chunk inhabited time, and world time.",
                "For reference, this scales up to the max after 63 days in the world and 150 days in a particular " +
                        "chunk, and peaks during full moons. On Peaceful this is always 0, on Easy this is 0.75 to " +
                        "1.5, on Normal this is 1.5 to 4.0, and on Hard this is 2.25 to 6.75." );
        register( "special_difficulty", SpecialDifficultyEnvironment::new, SpecialDifficultyEnvironment.class,
                "op value",
                "The 'special multiplier' for regional difficulty (0 to 1). For reference, this is 0 when " +
                        "difficulty <= 2 and 1 when difficulty >= 4.",
                "This is always 0 in Easy and below. In Normal, it maxes at absolute peak regional difficulty. " +
                        "In Hard, it starts at 0.125 and maxes out in ~50 days." );
        register( "weather", WeatherEnvironment::new, WeatherEnvironment.class,
                "(!)type",
                "Valid type values: " + TomlHelper.toLiteralList( (Object[]) WeatherEnvironment.Value.values() ) );
        register( "moon_brightness", MoonBrightnessEnvironment::new, MoonBrightnessEnvironment.class,
                "op value",
                "The moon brightness (0 to 1). New moon has 0 brightness, full moon has 1 brightness. " +
                        "Intermediate phases are 0.25, 0.5, or 0.75." );
        register( "moon_phase", MoonPhaseEnvironment::new, MoonPhaseEnvironment.class,
                "(!)phase",
                "Valid phase values: " + TomlHelper.toLiteralList( (Object[]) MoonPhaseEnvironment.Value.values() ),
                "For reference, the first day in a new world is always a full moon." );
        register( "day_time", DayTimeEnvironment::new, DayTimeEnvironment.class,
                "(!)time",
                "Valid time values: " + TomlHelper.toLiteralList( (Object[]) DayTimeEnvironment.Value.values() ),
                "Note that the transition periods, sunset & sunrise, are considered as part of day & night, respectively." );
        register( "time_from_midnight", TimeFromMidnightEnvironment::new, TimeFromMidnightEnvironment.class,
                "op value",
                "The absolute time in ticks away from midnight. Value must be 0 to 12000." );
        register( "world_time", WorldTimeEnvironment::new, WorldTimeEnvironment.class,
                "op value",
                "The total time the world has existed, in ticks. For reference, each day cycle is 24000 " +
                        "ticks and each lunar cycle is 192000 ticks." );
        register( "chunk_time", ChunkTimeEnvironment::new, ChunkTimeEnvironment.class,
                "op value",
                "The total time the chunk has been loaded, in ticks. For reference, each day cycle is 24000 " +
                        "ticks and each lunar cycle is 192000 ticks." );
        
        // Mod-based
        register( "apocalypse_difficulty", ApocalypseDifficultyEnvironment::new, ApocalypseDifficultyEnvironment.class,
                "op value",
                "The Apocalypse Rebooted mod's difficulty (scale depends on your config). This is based on " +
                        "the nearest player's current difficulty level. If no player exists, or the mod is not installed, " +
                        "it will never match any condition." );
        register( "apocalypse_difficulty_or_time", ApocalypseDifficultyOrTimeEnvironment::new, ApocalypseDifficultyOrTimeEnvironment.class,
                "op value",
                "The Apocalypse Rebooted mod's difficulty (scale depends on your config). If the mod is not " +
                        "installed, this is instead treated like a \"" + getName( WorldTimeEnvironment.class ) +
                        "\" condition." );
    }
}