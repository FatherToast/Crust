package fathertoast.crust.api.config.common.value.environment.time;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.EnumEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class MoonPhaseEnvironment extends EnumEnvironment<MoonPhaseEnvironment.Value> {
    
    /** Values match up to the vanilla weather command. */
    public enum Value {
        FULL( 0 ), WANING_GIBBOUS( 1 ), LAST_QUARTER( 2 ), WANING_CRESCENT( 3 ),
        NEW( 4 ), WAXING_CRESCENT( 5 ), FIRST_QUARTER( 6 ), WAXING_GIBBOUS( 7 );
        
        public final int INDEX;
        
        Value( int i ) { INDEX = i; }
    }
    
    public MoonPhaseEnvironment( Value value, boolean invert ) { super( value, invert ); }
    
    public MoonPhaseEnvironment( AbstractConfigField field, String line ) { super( field, line, Value.values() ); }
    
    /** @return Returns true if this environment matches the provided environment. */
    @Override
    public boolean matches( Level level, @Nullable BlockPos pos ) {
        final int phase = level.dimensionType().moonPhase( level.dayTime() );
        return (VALUE.INDEX == phase) != INVERT;
    }
}