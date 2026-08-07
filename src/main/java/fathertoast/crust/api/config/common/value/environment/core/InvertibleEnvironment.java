package fathertoast.crust.api.config.common.value.environment.core;

import fathertoast.crust.api.config.common.value.environment.AbstractEnvironment;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;

/**
 * Boilerplate environment that adds all necessary support for unary inversion ("!" prefix).
 */
public abstract class InvertibleEnvironment extends AbstractEnvironment {
    
    public static final String CODE = "!";
    
    /** If true, the condition is inverted. */
    protected final boolean INVERT;
    
    public InvertibleEnvironment( boolean invert ) { INVERT = invert; }
    
    public InvertibleEnvironment( String value ) { INVERT = value.startsWith( CODE ); }
    
    /** @return The value, with the invert code removed. */
    protected String valueCleaned( String value ) { return INVERT ? value.substring( CODE.length() ) : value; }
    
    /** @return The string value of this environment, as it would appear in a config file. */
    @Override
    public final String value() { return INVERT ? CODE + cleanValue() : cleanValue(); }
    
    /** @return The string value of this environment, without the invert code. */
    protected abstract String cleanValue();
    
    /** @return True if this environment matches the provided environment. */
    @Override // Predicate
    public final boolean test( EnvironmentContext context ) { return cleanTest( context ) != INVERT; }
    
    /** @return True if this environment matches the provided environment, ignoring inversion. */
    protected abstract boolean cleanTest( EnvironmentContext context );
}