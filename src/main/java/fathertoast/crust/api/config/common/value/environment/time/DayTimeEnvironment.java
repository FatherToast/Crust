package fathertoast.crust.api.config.common.value.environment.time;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.EnumEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class DayTimeEnvironment extends EnumEnvironment<DayTimeEnvironment.Value> {
    
    /** Values match up to the vanilla set time command. */
    public enum Value {
        DAY( 1_000, 13_000 ), SUNSET( 12_000, 13_000 ),
        NIGHT( 13_000, 1_000 ), SUNRISE( 23_000, 1_000 );
        
        private final int START, END;
        
        Value( int start, int end ) {
            START = start;
            END = end;
        }
        
        public boolean matches( int dayTime ) {
            if( START < END ) return START <= dayTime && dayTime < END;
            return START <= dayTime || dayTime < END; // Handle day wrapping
        }
    }
    
    public DayTimeEnvironment( Value value, boolean invert ) { super( value, invert ); }
    
    public DayTimeEnvironment( AbstractConfigField field, String line ) { super( field, line, Value.values() ); }
    
    /** @return Returns true if this environment matches the provided environment. */
    @Override
    public boolean matches( Level level, @Nullable BlockPos pos ) {
        return (VALUE.matches( (int) (level.dayTime() / 24_000L) )) != INVERT;
    }
}