package fathertoast.crust.api.config.common.value.environment.time;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.CompareLongEnvironment;
import fathertoast.crust.api.config.common.value.environment.ComparisonOperator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class WorldTimeEnvironment extends CompareLongEnvironment {
    
    public WorldTimeEnvironment( ComparisonOperator op, long value ) { super( op, value ); }
    
    public WorldTimeEnvironment( AbstractConfigField field, String line ) { super( field, line ); }
    
    /** @return The minimum value that can be given to the value. */
    @Override
    protected long getMinValue() { return 0L; }
    
    /** @return Returns the actual value to compare, or null if there isn't enough information. */
    @Override
    public Long getActual( World world, @Nullable BlockPos pos ) { return world.dayTime(); }
}