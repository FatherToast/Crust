package fathertoast.crust.api.config.common.value.environment.time;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.ComparisonOperator;
import fathertoast.crust.api.config.common.field.EnvironmentListField;
import fathertoast.crust.api.config.common.value.environment.CompareFloatEnvironment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Notes on regional difficulty:
 * Maxes out over 63 days in the world and 150 days the in the chunk (effectively, time the chunk has been loaded).
 * Peaks during the full moon and dramatically scaled by difficulty setting.
 * <p>
 * Peaceful: 0
 * Easy:   0.75 to 1.5  (0.25 from world time, 0.375 from chunk time, and 0.125 from moon brightness)
 * Normal: 1.5  to 4.0  (0.5  from world time, 1.5   from chunk time, and 0.5   from moon brightness)
 * Hard:   2.25 to 6.75 (0.75 from world time, 3.0   from chunk time, and 0.75  from moon brightness)
 */
public class DifficultyEnvironment extends CompareFloatEnvironment {
    
    public DifficultyEnvironment( ComparisonOperator op, float value ) { super( op, value ); }
    
    public DifficultyEnvironment( AbstractConfigField field, String line ) { super( field, line ); }
    
    /** @return The minimum value that can be given to the value. */
    @Override
    protected float getMinValue() { return 0.0F; }
    
    // Don't specify the max value, just in case a mod changes it.
    
    /** @return The string name of this environment, as it would appear in a config file. */
    @Override
    public String name() { return EnvironmentListField.ENV_DIFFICULTY; }
    
    /** @return Returns the actual value to compare, or Float.NaN if there isn't enough information. */
    @Override
    public float getActual( World world, @Nullable BlockPos pos ) {
        return pos == null ? Float.NaN : world.getCurrentDifficultyAt( pos ).getEffectiveDifficulty();
    }
}