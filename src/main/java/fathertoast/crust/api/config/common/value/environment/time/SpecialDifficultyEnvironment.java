package fathertoast.crust.api.config.common.value.environment.time;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.value.ComparatorValue;
import fathertoast.crust.api.config.common.value.collection.value.FloatValueCodec;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.config.common.value.environment.core.CompareFloatEnvironment;

import javax.annotation.Nullable;

/**
 * Notes on special multiplier difficulty:
 * This is 0 while regional difficulty is <= 2 and this is 1 while regional difficulty is >= 4 (linearly scales between).
 * <p>
 * In Peaceful and Easy, this is always 0. In Normal, this only maxes out at the absolute peak regional difficulty.
 * In Hard, this starts out as 0.125 and reaches 1 during new moons with only ~50 days in the area.
 */
public class SpecialDifficultyEnvironment extends CompareFloatEnvironment {
    
    public SpecialDifficultyEnvironment( ComparatorValue op, float value ) { super( op, value ); }
    
    public SpecialDifficultyEnvironment( @Nullable IConfigField<?> field, String value ) { super( field, value ); }
    
    /** @return The value codec used. */
    @Override
    protected IValueCodec<Float> getValueCodec() { return FloatValueCodec.PERCENT; }
    
    /** @return Returns the actual value to compare, or null if there isn't enough information. */
    @Override
    @Nullable
    protected Float getActual( EnvironmentContext context ) {
        return context.getBlockPos() == null ? null :
                context.getLevel().getCurrentDifficultyAt( context.getBlockPos() ).getSpecialMultiplier();
    }
}