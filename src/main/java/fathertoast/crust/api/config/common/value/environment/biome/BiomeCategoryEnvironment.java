package fathertoast.crust.api.config.common.value.environment.biome;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.EnumEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

@Deprecated( forRemoval = true ) // TODO Remove when updating beyond 1.20.1
public class BiomeCategoryEnvironment extends EnumEnvironment<BiomeCategory> {
    
    @Deprecated( forRemoval = true )
    public BiomeCategoryEnvironment( BiomeCategory value, boolean invert ) { super( value, invert ); }
    
    @Deprecated( forRemoval = true )
    public BiomeCategoryEnvironment( AbstractConfigField field, String value ) {
        super( field, value, BiomeCategory.values() );
        ConfigUtil.warnFor( field );
        ConfigUtil.LOG.warn( "Deprecated environment entry! The \"biome_category\" environment condition will be removed in a future version. Deprecated entry: {}",
                name() + " " + value );
    }
    
    /** @return Returns true if this environment matches the provided environment. */
    @Override
    public boolean matches( Level level, @Nullable BlockPos pos ) {
        return (pos != null && (VALUE == BiomeCategory.NONE || level.getBiome( pos ).is( VALUE.BIOME_TAG ))) != INVERT;
    }
}