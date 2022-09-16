package fathertoast.crust.api.config.common.value.environment.biome;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.field.EnvironmentListField;
import fathertoast.crust.api.config.common.value.environment.ComparisonOperator;
import fathertoast.crust.api.config.common.value.environment.CompareFloatEnvironment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;

public class RainfallEnvironment extends CompareFloatEnvironment {
    
    public RainfallEnvironment( ComparisonOperator op, float value ) { super( op, value ); }
    
    public RainfallEnvironment( AbstractConfigField field, String line ) { super( field, line ); }
    
    /** @return The string name of this environment, as it would appear in a config file. */
    @Override
    public String name() { return EnvironmentListField.ENV_RAINFALL; }
    
    /** @return Returns true if this environment matches the provided environment. */
    @Override
    public boolean matches( World world, @Nullable BlockPos pos ) {
        // Handle the special case of no rainfall
        if( COMPARATOR == ComparisonOperator.EQUAL_TO && VALUE == 0.0F )
            return pos != null && world.getBiome( pos ).getPrecipitation() == Biome.RainType.NONE;
        if( COMPARATOR == ComparisonOperator.NOT_EQUAL_TO && VALUE == 0.0F )
            return pos != null && world.getBiome( pos ).getPrecipitation() != Biome.RainType.NONE;
        return super.matches( world, pos );
    }
    
    /** @return Returns the actual value to compare, or Float.NaN if there isn't enough information. */
    @Override
    public float getActual( World world, @Nullable BlockPos pos ) {
        return pos == null ? Float.NaN : world.getBiome( pos ).getDownfall();
    }
}