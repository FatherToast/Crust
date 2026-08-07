package fathertoast.crust.api.config.common.value.environment;

import fathertoast.crust.api.config.common.value.ITomlStringValue;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Predicate;

public abstract class AbstractEnvironment implements ITomlStringValue, Predicate<EnvironmentContext> {
    
    /**
     * @return The string name of this environment, as it would appear in a config file.
     * @throws NullPointerException if not registered.
     */
    public final String name() { return Objects.requireNonNull( CrustEnvironmentRegistry.getName( this ) ); }
    
    /** @return The string value of this environment, as it would appear in a config file. Null if not used. */
    @Nullable
    public abstract String value();
    
    /** @return True if this environment matches the provided environment. */
    @Override // Predicate
    public abstract boolean test( EnvironmentContext context );
    
    
    /** @return This value, converted to a single-line string. */
    @Override
    public final String toTomlString() {
        String value = value();
        return value == null ? name() : name() + " " + value;
    }
    
    @Override
    public final String toString() { return toTomlString(); }
    
    @Override
    public final boolean equals( Object obj ) {
        return obj instanceof ITomlStringValue toml && toTomlString().equalsIgnoreCase( toml.toTomlString() );
    }
    
    @Override
    public final int hashCode() { return toTomlString().hashCode(); }
}