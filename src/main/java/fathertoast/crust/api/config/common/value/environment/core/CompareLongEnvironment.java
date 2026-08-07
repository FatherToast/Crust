package fathertoast.crust.api.config.common.value.environment.core;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.value.ComparatorValue;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import fathertoast.crust.api.config.common.value.collection.value.LongValueCodec;

import javax.annotation.Nullable;

public abstract class CompareLongEnvironment extends ComparableEnvironment<Long> {
    
    public CompareLongEnvironment( ComparatorValue op, long value ) { super( op, value ); }
    
    public CompareLongEnvironment( @Nullable IConfigField<?> field, String value ) { super( field, value ); }
    
    /** @return The value codec used. */
    @Override
    protected IValueCodec<Long> getValueCodec() { return LongValueCodec.ANY; }
}