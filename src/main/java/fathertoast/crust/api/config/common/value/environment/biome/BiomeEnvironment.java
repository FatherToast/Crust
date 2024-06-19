package fathertoast.crust.api.config.common.value.environment.biome;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.DynamicRegistryEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;

import javax.annotation.Nullable;

public class BiomeEnvironment extends DynamicRegistryEnvironment<Biome> {
    
    public BiomeEnvironment(ConfigManager cfgManager, ResourceKey<Biome> biome, boolean invert ) {
        super( cfgManager, biome.location(), invert );
    }
    
    public BiomeEnvironment( AbstractConfigField field, String line ) { super( field, line ); }
    
    /** @return The registry used. */
    @Override
    public ResourceKey<Registry<Biome>> getRegistry() { return Registries.BIOME; }
    
    /** @return Returns true if this environment matches the provided environment. */
    @Override
    public boolean matches( ServerLevel level, @Nullable BlockPos pos ) {
        final Biome entry = getRegistryEntry( level );
        return (entry != null && pos != null && entry.equals( level.getBiome( pos ).value() )) != INVERT;
    }
}