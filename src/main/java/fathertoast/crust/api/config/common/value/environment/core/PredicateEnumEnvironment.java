package fathertoast.crust.api.config.common.value.environment.core;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * Boilerplate environment that adds all necessary support for predicate enum checks.
 * <p>
 * This implementation works well for enum values that need to provide their own logic.
 * For enums that can be mapped directly from context, use {@link EnumEnvironment} instead.
 */
public abstract class PredicateEnumEnvironment<T extends Enum<T> & Predicate<EnvironmentContext>> extends EnumEnvironment<T> {
    
    public PredicateEnumEnvironment( T value, boolean invert ) { super( value, invert ); }
    
    public PredicateEnumEnvironment( @Nullable IConfigField<?> field, String value, T[] validValues ) {
        super( field, value, validValues );
    }
    
    /** @return True if this environment matches the provided environment, ignoring inversion. */
    @Override
    protected boolean cleanTest( EnvironmentContext context ) { return VALUE.test( context ); }
}