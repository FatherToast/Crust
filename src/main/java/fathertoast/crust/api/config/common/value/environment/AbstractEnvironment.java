package fathertoast.crust.api.config.common.value.environment;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class AbstractEnvironment {
    
    private String regName;
    
    AbstractEnvironment setName( String name ) {
        if( regName == null ) regName = name;
        else throw new IllegalStateException( "Do not attempt to set manually! " + name + "=" + getClass().getName() );
        return this;
    }
    
    /** @return The string representation of this environment, as it would appear in a config file. */
    @Override
    public final String toString() { return regName + " " + value(); }
    
    /** @return The string value of this environment, as it would appear in a config file. */
    public abstract String value();
    
    /** @return Returns true if this environment matches the provided environment. */
    public abstract boolean matches( World world, @Nullable BlockPos pos );
}