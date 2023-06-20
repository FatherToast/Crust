package fathertoast.crust.api.config.common.value.environment.compat;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.ComparisonOperator;
import fathertoast.crust.api.config.common.value.environment.time.WorldTimeEnvironment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * This condition is the same as {@link ApocalypseDifficultyEnvironment}, except that when
 * Apocalypse Rebooted is NOT installed, this condition is treated as {@link WorldTimeEnvironment}
 * instead of always evaluating as false.
 */
public class ApocalypseDifficultyOrTimeEnvironment extends ApocalypseDifficultyEnvironment {
    
    public ApocalypseDifficultyOrTimeEnvironment( ComparisonOperator op, long value ) { super( op, value ); }
    
    public ApocalypseDifficultyOrTimeEnvironment( AbstractConfigField field, String line ) { super( field, line ); }
    
    /** @return Returns the actual value to compare, or null if there isn't enough information. */
    @Override
    public Long getActual( World world, @Nullable BlockPos pos ) {
        return isApocalypseInstalled() ? super.getActual( world, pos ) : (Long) world.dayTime();
    }
}