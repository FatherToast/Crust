package fathertoast.crust.api.config.common.value.environment.time;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.CompareIntEnvironment;
import fathertoast.crust.api.config.common.value.environment.ComparisonOperator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class TimeFromMidnightEnvironment extends CompareIntEnvironment {
    
    public TimeFromMidnightEnvironment( ComparisonOperator op, int value ) { super( op, value ); }
    
    public TimeFromMidnightEnvironment( AbstractConfigField field, String line ) { super( field, line ); }
    
    /** @return The minimum value that can be given to the value. */
    @Override
    protected int getMinValue() { return 0; }
    
    /** @return The maximum value that can be given to the value. */
    @Override
    protected int getMaxValue() { return 12_000; }
    
    /** @return Returns the actual value to compare, or null if there isn't enough information. */
    @Override
    public Integer getActual( Level level, @Nullable BlockPos pos ) {
        int dayTime = (int) (level.dayTime() / 24_000L);
        if( dayTime < 18_000 ) dayTime += 24_000;
        return dayTime - 18_000;
    }
}