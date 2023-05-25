package fathertoast.crust.api.config.common.value.environment;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.field.EnvironmentListField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.environment.time.WorldTimeEnvironment;

import java.util.*;

public final class CrustEnvironmentRegistry {
    
    /**
     * Call this to register an environment that can be used in {@link EnvironmentListField}s.
     * This links a unique identifier (environment name) to a parsing function so the environment can be
     * read from and written to files.
     *
     * @param name        The unique environment name. Not case-sensitive.
     * @param factory     A bi-function that parses a file line to load the environment from file. Often just a reference to the constructor.
     * @param format      The data format expected by the factory.
     *                    Use (!) to show where the 'invert operator' can be placed. Use op to show where a 'comparison operator' is used.
     * @param description A description that details how to use the environment condition.
     *                    See {@link EnvironmentListField#environmentDescriptions()}
     * @return The environment name registered, for convenience.
     * @throws IllegalStateException If the environment name has already been registered.
     */
    public static String register( String name, IFactory factory, String format, String... description ) {
        name = name.toLowerCase( Locale.ROOT ); // Not case sensitive
        if( NAME_TO_FACTORY_MAP.containsKey( name ) )
            throw new IllegalStateException( "Duplicate environment names cannot be registered! Duplicate name: " + name );
        
        // Link unique identifier to data parser
        NAME_TO_FACTORY_MAP.put( name, factory );
        
        // Add info to overall environment 'how-to' comment
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
        return factory.parse( field, value ).setName( name );
    }
    
    public static Set<String> getNames() { return NAME_SET; }
    
    public static List<String> getDescriptions() { return Collections.unmodifiableList( DESCRIPTIONS ); }
    
    public interface IFactory {
        AbstractEnvironment parse( AbstractConfigField field, String value );
    }
    
    
    private static final Map<String, IFactory> NAME_TO_FACTORY_MAP = new HashMap<>();
    private static final Set<String> NAME_SET = Collections.unmodifiableSet( NAME_TO_FACTORY_MAP.keySet() );
    
    private static final List<String> DESCRIPTIONS;
    
    static {
        // Initialize environment descriptions comment
        List<String> comment = new ArrayList<>();
        comment.add( "Environment conditions (for Environment List entries):" );
        comment.add( "  Many environment conditions can be inverted by using \"!\"; these are shown with (!) in the " +
                "appropriate location." );
        comment.add( "  Other environment conditions are numerical comparisons; these use the operators (shown as op) " +
                "<, >, =, <=, >=, or != to compare value." );
        comment.add( "Valid environment conditions are:" );
        DESCRIPTIONS = comment;
    }
}