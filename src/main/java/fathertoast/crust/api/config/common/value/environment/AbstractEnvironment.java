package fathertoast.crust.api.config.common.value.environment;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Objects;

public abstract class AbstractEnvironment {
    /** @return The string representation of this environment, as it would appear in a config file. */
    @Override
    public final String toString() {
        String value = value();
        return value == null ? name() : name() + " " + value;
    }
    
    /**
     * @return The string name of this environment, as it would appear in a config file.
     * @throws NullPointerException if not registered.
     */
    public final String name() { return Objects.requireNonNull( CrustEnvironmentRegistry.getName( this ) ); }
    
    /** @return The string value of this environment, as it would appear in a config file. Null if not used. */
    @Nullable
    public abstract String value();
    
    /** @return Returns true if this environment matches the provided environment. */
    public abstract boolean matches( Level level, @Nullable BlockPos pos );
}