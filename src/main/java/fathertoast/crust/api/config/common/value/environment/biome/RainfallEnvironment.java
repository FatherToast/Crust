package fathertoast.crust.api.config.common.value.environment.biome;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.CompareFloatEnvironment;
import fathertoast.crust.api.config.common.value.environment.ComparisonOperator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import javax.annotation.Nullable;

public class RainfallEnvironment extends CompareFloatEnvironment {
    
    public RainfallEnvironment( ComparisonOperator op, float value ) { super( op, value ); }
    
    public RainfallEnvironment( AbstractConfigField field, String line ) { super( field, line ); }
    
    /** @return Returns true if this environment matches the provided environment. */
    @Override
    public boolean matches( Level level, @Nullable BlockPos pos ) {
        // Handle the special case of no rainfall
        if( COMPARATOR == ComparisonOperator.EQUAL_TO && VALUE == 0.0F )
            return pos != null && level.getBiome( pos ).value().getPrecipitationAt( pos ) == Biome.Precipitation.NONE;
        if( COMPARATOR == ComparisonOperator.NOT_EQUAL_TO && VALUE == 0.0F )
            return pos != null && level.getBiome( pos ).value().getPrecipitationAt( pos ) != Biome.Precipitation.NONE;
        return super.matches( level, pos );
    }
    
    /** @return Returns the actual value to compare, or Float.NaN if there isn't enough information. */
    @Override
    public float getActual( Level level, @Nullable BlockPos pos ) {
        return pos == null ? Float.NaN : level.getBiome( pos ).value().getModifiedClimateSettings().downfall();
    }
}