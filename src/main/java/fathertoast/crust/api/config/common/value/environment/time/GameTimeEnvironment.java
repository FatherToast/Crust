package fathertoast.crust.api.config.common.value.environment.time;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.value.ComparatorValue;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import fathertoast.crust.api.config.common.value.collection.value.LongValueCodec;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.config.common.value.environment.core.CompareLongEnvironment;

import javax.annotation.Nullable;

public class GameTimeEnvironment extends CompareLongEnvironment {
    
    public GameTimeEnvironment( ComparatorValue op, long value ) { super( op, value ); }
    
    public GameTimeEnvironment( @Nullable IConfigField<?> field, String value ) { super( field, value ); }
    
    /** @return The value codec used. */
    @Override
    protected IValueCodec<Long> getValueCodec() { return LongValueCodec.NON_NEGATIVE; }
    
    /** @return Returns the actual value to compare, or null if there isn't enough information. */
    @Override
    @Nullable
    protected Long getActual( EnvironmentContext context ) { return context.getLevel().getLevelData().getGameTime(); }
}