package fathertoast.crust.api.config.common.value.environment;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.file.TomlHelper;

import java.util.Locale;

public abstract class EnumEnvironment<T extends Enum<T>> extends AbstractEnvironment {
    
    /** If true, the condition is inverted. */
    protected final boolean INVERT;
    /** The enum value for this environment. */
    protected final T VALUE;
    
    public EnumEnvironment( T value, boolean invert ) {
        INVERT = invert;
        VALUE = value;
    }
    
    public EnumEnvironment( AbstractConfigField field, String line, T[] validValues ) {
        INVERT = line.startsWith( "!" );
        VALUE = parseValue( field, line, validValues, INVERT ? line.substring( 1 ) : line );
    }
    
    /** @return Attempts to parse the string literal as one of the valid values and returns it, or null if invalid. */
    private T parseValue( AbstractConfigField field, String line, T[] validValues, String name ) {
        for( T value : validValues ) {
            if( value.name().equalsIgnoreCase( name ) ) return value;
        }
        // Value cannot be parsed
        ConfigUtil.LOG.warn( "Invalid entry for {} \"{}\"! Value not defined (must be in the set [ {} ]). Defaulting to {}. Invalid entry: {}",
                field.getClass(), field.getKey(), TomlHelper.toLiteralList( (Object[]) validValues ),
                TomlHelper.toLiteral( validValues[0] ), line );
        return validValues[0];
    }
    
    /** @return The string value of this environment, as it would appear in a config file. */
    @Override
    public final String value() { return (INVERT ? "!" : "") + VALUE.name().toLowerCase( Locale.ROOT ); }
}