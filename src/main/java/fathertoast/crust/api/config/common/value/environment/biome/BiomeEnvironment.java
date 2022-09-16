package fathertoast.crust.api.config.common.value.environment.biome;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.field.EnvironmentListField;
import fathertoast.crust.api.config.common.value.environment.DynamicRegistryEnvironment;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class BiomeEnvironment extends DynamicRegistryEnvironment<Biome> {
    
    public BiomeEnvironment( RegistryKey<Biome> biome, boolean invert ) { super( biome.location(), invert ); }
    
    public BiomeEnvironment( AbstractConfigField field, String line ) { super( field, line ); }
    
    /** @return The string name of this environment, as it would appear in a config file. */
    @Override
    public String name() { return EnvironmentListField.ENV_BIOME; }
    
    /** @return The registry used. */
    @Override
    public RegistryKey<Registry<Biome>> getRegistry() { return Registry.BIOME_REGISTRY; }
    
    /** @return Returns true if this environment matches the provided environment. */
    @Override
    public boolean matches( ServerWorld world, @Nullable BlockPos pos ) {
        final Biome entry = getRegistryEntry( world );
        return (entry != null && pos != null && entry.equals( world.getBiome( pos ) )) != INVERT;
    }
}