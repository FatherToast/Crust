package fathertoast.crust.api.config.common.value.environment.core;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.value.ComparatorValue;
import fathertoast.crust.api.config.common.value.collection.value.FloatValueCodec;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;

import javax.annotation.Nullable;

public abstract class CompareFloatEnvironment extends ComparableEnvironment<Float> {
    
    public CompareFloatEnvironment( ComparatorValue op, float value ) { super( op, value ); }
    
    public CompareFloatEnvironment( @Nullable IConfigField<?> field, String value ) { super( field, value ); }
    
    /** @return The value codec used. */
    @Override
    protected IValueCodec<Float> getValueCodec() { return FloatValueCodec.ANY; }
}