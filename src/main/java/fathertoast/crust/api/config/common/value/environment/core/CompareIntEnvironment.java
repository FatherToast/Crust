package fathertoast.crust.api.config.common.value.environment.core;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.value.ComparatorValue;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import fathertoast.crust.api.config.common.value.collection.value.IntValueCodec;

import javax.annotation.Nullable;

public abstract class CompareIntEnvironment extends ComparableEnvironment<Integer> {
    
    public CompareIntEnvironment( ComparatorValue op, int value ) { super( op, value ); }
    
    public CompareIntEnvironment( @Nullable IConfigField<?> field, String value ) { super( field, value ); }
    
    /** @return The value codec used. */
    @Override
    protected IValueCodec<Integer> getValueCodec() { return IntValueCodec.ANY; }
}