package fathertoast.crust.api.config.common.value.environment.time;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.config.common.value.environment.core.PredicateEnumEnvironment;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class WeatherEnvironment extends PredicateEnumEnvironment<WeatherEnvironment.Value> {
    
    /** Values match up to the vanilla weather command. */
    public enum Value implements Predicate<EnvironmentContext> {
        CLEAR, RAIN, THUNDER;
        
        @Override // Predicate
        public boolean test( EnvironmentContext context ) {
            if( context.getLevel().getLevelData().isRaining() ) {
                return switch( this ) {
                    case CLEAR -> false;
                    case RAIN -> true;
                    case THUNDER -> context.getLevel().getLevelData().isThundering();
                };
            }
            return this == Value.CLEAR;
        }
    }
    
    public WeatherEnvironment( Value value, boolean invert ) { super( value, invert ); }
    
    public WeatherEnvironment( @Nullable IConfigField<?> field, String value ) { super( field, value, Value.values() ); }
}