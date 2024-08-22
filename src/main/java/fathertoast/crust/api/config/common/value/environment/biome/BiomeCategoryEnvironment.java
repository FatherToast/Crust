package fathertoast.crust.api.config.common.value.environment.biome;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.EnumEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

// TODO Replace with biome tags
public class BiomeCategoryEnvironment extends EnumEnvironment<BiomeCategory> {
    
    public BiomeCategoryEnvironment( BiomeCategory value, boolean invert ) { super( value, invert ); }
    
    public BiomeCategoryEnvironment( AbstractConfigField field, String line ) { super( field, line, BiomeCategory.values() ); }
    
    /** @return Returns true if this environment matches the provided environment. */
    @Override
    public boolean matches( Level level, @Nullable BlockPos pos ) {
        return (pos != null && (VALUE == BiomeCategory.NONE || level.getBiome( pos ).is( VALUE.BIOME_TAG ))) != INVERT;
    }
}