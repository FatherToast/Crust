package fathertoast.crust.api.config.common.value.environment.core;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.value.ComparatorValue;
import fathertoast.crust.api.config.common.value.collection.value.DoubleValueCodec;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;

import javax.annotation.Nullable;

public abstract class CompareDoubleEnvironment extends ComparableEnvironment<Double> {
    
    public CompareDoubleEnvironment( ComparatorValue op, double value ) { super( op, value ); }
    
    public CompareDoubleEnvironment( @Nullable IConfigField<?> field, String value ) { super( field, value ); }
    
    /** @return The value codec used. */
    @Override
    protected IValueCodec<Double> getValueCodec() { return DoubleValueCodec.ANY; }
}