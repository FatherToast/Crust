package fathertoast.crust.api.config.common.value.environment.core;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;

import javax.annotation.Nullable;

/**
 * Boilerplate environment that adds all necessary support for enum checks.
 * <p>
 * This implementation works well for enum values that can be mapped directly from context.
 * For enums that need to provide their own logic, use {@link PredicateEnumEnvironment} instead.
 */
public abstract class EnumEnvironment<T extends Enum<T>> extends InvertibleEnvironment {
    
    /** The enum value for this environment. */
    protected final T VALUE;
    
    public EnumEnvironment( T value, boolean invert ) {
        super( invert );
        VALUE = value;
    }
    
    public EnumEnvironment( @Nullable IConfigField<?> field, String value, T[] validValues ) {
        super( value );
        VALUE = parseValue( field, name() + " " + value, validValues, valueCleaned( value ) );
    }
    
    /** @return Attempts to parse the string literal as one of the valid values and returns it, or null if invalid. */
    private T parseValue( @Nullable IConfigField<?> field, String line, T[] validValues, String name ) {
        for( T value : validValues ) {
            if( value.name().equalsIgnoreCase( name ) ) return value;
        }
        // Value cannot be parsed
        if( field != null ) {
            ConfigUtil.warnFor( field );
            ConfigUtil.LOG.warn( "Environment entry has undefined value! Must be in the set [ {} ]. Defaulting to {}. Entry: {}",
                    TomlHelper.toLiteralList( (Object[]) validValues ), TomlHelper.toLiteral( validValues[0] ), line );
        }
        return validValues[0];
    }
    
    /** @return The string value of this environment, without the invert code. */
    @Override
    protected String cleanValue() { return TomlHelper.enumToString( VALUE ); }
    
    /** @return True if this environment matches the provided environment, ignoring inversion. */
    @Override
    protected boolean cleanTest( EnvironmentContext context ) {
        T actual = getActual( context );
        return actual != null && actual.equals( VALUE );
    }
    
    /**
     * @return Returns the actual environment to compare, or null if there isn't enough information.
     * If you can directly map the enum from context, you may simply override this method.
     * Otherwise, override {@link #cleanTest(EnvironmentContext)} and ignore this one.
     */
    @Nullable
    protected T getActual( EnvironmentContext context ) { return null; }
}