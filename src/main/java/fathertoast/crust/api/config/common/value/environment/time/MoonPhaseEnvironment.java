package fathertoast.crust.api.config.common.value.environment.time;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.config.common.value.environment.core.EnumEnvironment;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class MoonPhaseEnvironment extends EnumEnvironment<MoonPhaseEnvironment.Value> {
    
    /** Values match up to the vanilla weather command. */
    public enum Value implements Predicate<EnvironmentContext> {
        FULL( 0 ), WANING_GIBBOUS( 1 ), LAST_QUARTER( 2 ), WANING_CRESCENT( 3 ),
        NEW( 4 ), WAXING_CRESCENT( 5 ), FIRST_QUARTER( 6 ), WAXING_GIBBOUS( 7 );
        
        public final int INDEX;
        
        Value( int i ) { INDEX = i; }
        
        @Override // Predicate
        public boolean test( EnvironmentContext context ) { return INDEX == context.getLevel().getMoonPhase(); }
    }
    
    public MoonPhaseEnvironment( Value value, boolean invert ) { super( value, invert ); }
    
    public MoonPhaseEnvironment( @Nullable IConfigField<?> field, String value ) { super( field, value, Value.values() ); }
}