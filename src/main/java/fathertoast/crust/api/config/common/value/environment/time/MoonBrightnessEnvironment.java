package fathertoast.crust.api.config.common.value.environment.time;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.field.EnvironmentListField;
import fathertoast.crust.api.config.common.value.environment.CompareFloatEnvironment;
import fathertoast.crust.api.config.common.value.environment.ComparisonOperator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class MoonBrightnessEnvironment extends CompareFloatEnvironment {
    
    public MoonBrightnessEnvironment( ComparisonOperator op, float value ) { super( op, value ); }
    
    public MoonBrightnessEnvironment( AbstractConfigField field, String line ) { super( field, line ); }
    
    /** @return The minimum value that can be given to the value. */
    @Override
    protected float getMinValue() { return 0.0F; }
    
    /** @return The maximum value that can be given to the value. */
    @Override
    protected float getMaxValue() { return 1.0F; }
    
    /** @return The string name of this environment, as it would appear in a config file. */
    @Override
    public String name() { return EnvironmentListField.ENV_MOON_BRIGHTNESS; }
    
    /** @return Returns the actual value to compare, or Float.NaN if there isn't enough information. */
    @Override
    public float getActual( World world, @Nullable BlockPos pos ) {
        return pos == null ? Float.NaN : DimensionType.MOON_BRIGHTNESS_PER_PHASE[world.dimensionType().moonPhase( world.dayTime() )];
    }
}