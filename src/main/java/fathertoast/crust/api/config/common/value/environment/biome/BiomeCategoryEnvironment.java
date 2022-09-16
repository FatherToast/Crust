package fathertoast.crust.api.config.common.value.environment.biome;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.field.EnvironmentListField;
import fathertoast.crust.api.config.common.value.environment.EnumEnvironment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BiomeCategoryEnvironment extends EnumEnvironment<BiomeCategory> {
    
    public BiomeCategoryEnvironment( BiomeCategory value, boolean invert ) { super( value, invert ); }
    
    public BiomeCategoryEnvironment( AbstractConfigField field, String line ) { super( field, line, BiomeCategory.values() ); }
    
    /** @return The string name of this environment, as it would appear in a config file. */
    @Override
    public String name() { return EnvironmentListField.ENV_BIOME_CATEGORY; }
    
    /** @return Returns true if this environment matches the provided environment. */
    @Override
    public boolean matches( World world, @Nullable BlockPos pos ) {
        return (pos != null && VALUE.BASE.equals( world.getBiome( pos ).getBiomeCategory() )) != INVERT;
    }
}