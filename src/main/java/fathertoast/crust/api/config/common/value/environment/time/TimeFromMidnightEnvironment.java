package fathertoast.crust.api.config.common.value.environment.time;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.value.ComparatorValue;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import fathertoast.crust.api.config.common.value.collection.value.IntValueCodec;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.config.common.value.environment.core.CompareIntEnvironment;

import javax.annotation.Nullable;

public class TimeFromMidnightEnvironment extends CompareIntEnvironment {
    
    public static final IValueCodec<Integer> CODEC = IntValueCodec.of( 0, 0, 12_000 );
    
    public TimeFromMidnightEnvironment( ComparatorValue op, int value ) { super( op, value ); }
    
    public TimeFromMidnightEnvironment( @Nullable IConfigField<?> field, String value ) { super( field, value ); }
    
    /** @return The value codec used. */
    @Override
    protected IValueCodec<Integer> getValueCodec() { return CODEC; }
    
    /** @return Returns the actual value to compare, or null if there isn't enough information. */
    @Override
    @Nullable
    protected Integer getActual( EnvironmentContext context ) {
        int dayTime = (int) (context.getLevel().dayTime() % 24_000L);
        if( dayTime > 18_000 ) return dayTime - 18_000;
        if( dayTime > 6_000 ) return 18_000 - dayTime;
        return dayTime + 6_000;
    }
}