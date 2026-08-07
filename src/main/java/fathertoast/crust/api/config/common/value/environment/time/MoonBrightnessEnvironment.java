package fathertoast.crust.api.config.common.value.environment.time;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.value.ComparatorValue;
import fathertoast.crust.api.config.common.value.collection.value.FloatValueCodec;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.config.common.value.environment.core.CompareFloatEnvironment;

import javax.annotation.Nullable;

public class MoonBrightnessEnvironment extends CompareFloatEnvironment {
    
    public MoonBrightnessEnvironment( ComparatorValue op, float value ) { super( op, value ); }
    
    public MoonBrightnessEnvironment( @Nullable IConfigField<?> field, String value ) { super( field, value ); }
    
    /** @return The value codec used. */
    @Override
    protected IValueCodec<Float> getValueCodec() { return FloatValueCodec.PERCENT; }
    
    /** @return Returns the actual value to compare, or null if there isn't enough information. */
    @Override
    @Nullable
    protected Float getActual( EnvironmentContext context ) { return context.getLevel().getMoonBrightness(); }
}