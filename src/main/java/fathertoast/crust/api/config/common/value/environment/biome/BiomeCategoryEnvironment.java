package fathertoast.crust.api.config.common.value.environment.biome;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.EnumEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

@Deprecated( forRemoval = true )
public class BiomeCategoryEnvironment extends EnumEnvironment<BiomeCategory> {
    
    @Deprecated( forRemoval = true )
    public BiomeCategoryEnvironment( BiomeCategory value, boolean invert ) { super( value, invert ); }
    
    @Deprecated( forRemoval = true )
    public BiomeCategoryEnvironment( AbstractConfigField field, String line ) {
        super( field, line, BiomeCategory.values() );
        ConfigUtil.LOG.warn( "Deprecated entry for {} \"{}\"! The \"biome_category\" environment condition will " +
                        "be removed in a future version. Deprecated entry: {}",
                field.getClass(), field.getKey(), line );
    }
    
    /** @return Returns true if this environment matches the provided environment. */
    @Override
    public boolean matches( Level level, @Nullable BlockPos pos ) {
        return (pos != null && (VALUE == BiomeCategory.NONE || level.getBiome( pos ).is( VALUE.BIOME_TAG ))) != INVERT;
    }
}