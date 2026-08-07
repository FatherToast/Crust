package fathertoast.crust.api.config.common.value.environment.time;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.value.ComparatorValue;
import fathertoast.crust.api.config.common.value.collection.value.FloatValueCodec;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.config.common.value.environment.core.CompareFloatEnvironment;

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
    
    public DifficultyEnvironment( ComparatorValue op, float value ) { super( op, value ); }
    
    public DifficultyEnvironment( @Nullable IConfigField<?> field, String value ) { super( field, value ); }
    
    /** @return The value codec used. */
    @Override
    protected IValueCodec<Float> getValueCodec() { return FloatValueCodec.NON_NEGATIVE; }
    
    /** @return Returns the actual value to compare, or null if there isn't enough information. */
    @Override
    @Nullable
    protected Float getActual( EnvironmentContext context ) {
        return context.getBlockPos() == null ? null :
                context.getLevel().getCurrentDifficultyAt( context.getBlockPos() ).getEffectiveDifficulty();
    }
}