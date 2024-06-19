package fathertoast.crust.api.config.common.value.environment.time;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.EnumEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class WeatherEnvironment extends EnumEnvironment<WeatherEnvironment.Value> {
    
    /** Values match up to the vanilla weather command. */
    public enum Value { CLEAR, RAIN, THUNDER }
    
    public WeatherEnvironment( Value value, boolean invert ) { super( value, invert ); }
    
    public WeatherEnvironment( AbstractConfigField field, String line ) { super( field, line, Value.values() ); }
    
    /** @return Returns true if this environment matches the provided environment. */
    @Override
    public boolean matches( Level level, @Nullable BlockPos pos ) {
        if( level.getLevelData().isThundering() ) return (VALUE == Value.CLEAR) == INVERT; // Thunder implies rain
        if( level.getLevelData().isRaining() ) return (VALUE == Value.RAIN) != INVERT;
        return (VALUE == Value.CLEAR) != INVERT;
    }
}