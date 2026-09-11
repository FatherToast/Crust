package fathertoast.crust.api.config.common.value.environment.position;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.value.ComparatorValue;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import fathertoast.crust.api.config.common.value.collection.value.IntValueCodec;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.config.common.value.environment.core.CompareIntEnvironment;
import org.jetbrains.annotations.Nullable;

/**
 * This environment checks both block light and skylight at the context's position
 * and evaluates against the greater of the two.
 *
 * @see SkylightEnvironment
 * @see BlockLightEnvironment
 */
public class BrightnessEnvironment extends CompareIntEnvironment {
    
    /** An integer value codec with light level range. */
    protected static final IValueCodec<Integer> LIGHT_LEVEL = IntValueCodec.of( 0, 0, 15 );
    
    
    public BrightnessEnvironment( ComparatorValue op, int value ) { super( op, value ); }
    
    public BrightnessEnvironment( @Nullable IConfigField<?> field, String value ) { super( field, value ); }
    
    /** @return Returns the actual value to compare, or null if there isn't enough information. */
    @Override
    @Nullable
    protected Integer getActual( EnvironmentContext context ) {
        if( context.getBlockPos() == null ) return null;
        return context.getLevel().getRawBrightness( context.getBlockPos(), 0 );
    }
    
    /** @return The value codec used. */
    @Override
    protected IValueCodec<Integer> getValueCodec() { return LIGHT_LEVEL; }
}
