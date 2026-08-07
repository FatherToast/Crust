package fathertoast.crust.api.config.common.value.environment.time;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.config.common.value.environment.core.PredicateEnumEnvironment;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class DayTimeEnvironment extends PredicateEnumEnvironment<DayTimeEnvironment.Value> {
    
    /** Values match up to the vanilla set time command. */
    public enum Value implements Predicate<EnvironmentContext> {
        DAY( 1_000, 13_000 ), SUNSET( 12_000, 13_000 ),
        NIGHT( 13_000, 1_000 ), SUNRISE( 23_000, 1_000 );
        
        private final int START, END;
        
        Value( int start, int end ) {
            START = start;
            END = end;
        }
        
        @Override // Predicate
        public boolean test( EnvironmentContext context ) {
            int t = (int) (context.getLevel().dayTime() % 24_000L);
            // If time interval does not cross midnight, simply check if day time is within the interval
            if( START < END ) return START <= t && t < END;
            // Otherwise, check if the day time is outside the interval instead
            return t < END || START <= t;
        }
    }
    
    public DayTimeEnvironment( Value value, boolean invert ) { super( value, invert ); }
    
    public DayTimeEnvironment( @Nullable IConfigField<?> field, String value ) { super( field, value, Value.values() ); }
}