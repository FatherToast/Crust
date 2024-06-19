package fathertoast.crust.api.config.common.value.environment.biome;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.ComparisonOperator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class BiomeTemperatureEnvironment extends TemperatureEnvironment {
    
    @SuppressWarnings( "unused" )
    public BiomeTemperatureEnvironment( boolean freezing ) { super( freezing ); }
    
    @SuppressWarnings( "unused" )
    public BiomeTemperatureEnvironment( ComparisonOperator op, float value ) { super( op, value ); }
    
    public BiomeTemperatureEnvironment( AbstractConfigField field, String line ) { super( field, line ); }
    
    /** @return Returns the actual value to compare, or Float.NaN if there isn't enough information. */
    @Override
    public float getActual( Level level, @Nullable BlockPos pos ) {
        return pos == null ? Float.NaN : level.getBiome( pos ).value().getBaseTemperature();
    }
}