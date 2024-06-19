package fathertoast.crust.api.config.common.value.environment.position;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.CompareIntEnvironment;
import fathertoast.crust.api.config.common.value.environment.ComparisonOperator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class YEnvironment extends CompareIntEnvironment {
    
    public YEnvironment( ComparisonOperator op, int value ) { super( op, value ); }
    
    public YEnvironment( AbstractConfigField field, String line ) { super( field, line ); }
    
    /** @return Returns the actual value to compare, or null if there isn't enough information. */
    @Override
    public Integer getActual( Level level, @Nullable BlockPos pos ) { return pos == null ? null : pos.getY(); }
}