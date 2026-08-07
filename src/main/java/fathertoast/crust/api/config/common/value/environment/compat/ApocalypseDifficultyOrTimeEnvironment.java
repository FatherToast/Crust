package fathertoast.crust.api.config.common.value.environment.compat;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.value.ComparatorValue;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.config.common.value.environment.time.WorldTimeEnvironment;

import javax.annotation.Nullable;

/**
 * This condition is the same as {@link ApocalypseDifficultyEnvironment}, except that when
 * Apocalypse is NOT installed, this condition is treated as {@link WorldTimeEnvironment}
 * instead of always evaluating as false.
 */
public class ApocalypseDifficultyOrTimeEnvironment extends ApocalypseDifficultyEnvironment {
    
    public ApocalypseDifficultyOrTimeEnvironment( ComparatorValue op, long value ) { super( op, value ); }
    
    public ApocalypseDifficultyOrTimeEnvironment( @Nullable IConfigField<?> field, String value ) { super( field, value ); }
    
    /** @return Returns the actual value to compare, or null if there isn't enough information. */
    @Override
    @Nullable
    protected Long getActual( EnvironmentContext context ) {
        return isApocalypseInstalled() ? super.getActual( context ) : (Long) context.getLevel().dayTime();
    }
}