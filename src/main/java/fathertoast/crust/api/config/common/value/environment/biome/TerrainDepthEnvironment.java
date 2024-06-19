package fathertoast.crust.api.config.common.value.environment.biome;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.CompareFloatEnvironment;
import fathertoast.crust.api.config.common.value.environment.ComparisonOperator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

// TODO - Reconsider what to do with this. Biomes no longer decide the noise of the terrain
public class TerrainDepthEnvironment extends CompareFloatEnvironment {
    
    public TerrainDepthEnvironment( ComparisonOperator op, float value ) { super( op, value ); }
    
    public TerrainDepthEnvironment( AbstractConfigField field, String line ) { super( field, line ); }
    
    /** @return Returns the actual value to compare, or Float.NaN if there isn't enough information. */
    @Override
    public float getActual( Level level, @Nullable BlockPos pos ) {
        return 0.0F;
        //return pos == null ? Float.NaN : level.getBiome( pos ).value().getDepth();
    }
}