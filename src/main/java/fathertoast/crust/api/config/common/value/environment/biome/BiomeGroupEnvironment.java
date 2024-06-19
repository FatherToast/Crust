package fathertoast.crust.api.config.common.value.environment.biome;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.DynamicRegistryGroupEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;

import javax.annotation.Nullable;
import java.util.List;

public class BiomeGroupEnvironment extends DynamicRegistryGroupEnvironment<Biome> {
    
    @SuppressWarnings( "unused" )
    public BiomeGroupEnvironment( ConfigManager cfgManager, ResourceKey<Biome> biome, boolean invert ) {
        this( cfgManager, biome.location(), invert );
    }
    
    public BiomeGroupEnvironment( ConfigManager cfgManager, ResourceLocation regKey, boolean invert ) {
        super( cfgManager, regKey, invert );
    }
    
    public BiomeGroupEnvironment( AbstractConfigField field, String line ) { super( field, line ); }
    
    /** @return The registry used. */
    @Override
    public ResourceKey<Registry<Biome>> getRegistry() { return Registries.BIOME; }
    
    /** @return Returns true if this environment matches the provided environment. */
    @Override
    public final boolean matches( ServerLevel level, @Nullable BlockPos pos ) {
        final Holder<Biome> target = pos == null ? null : level.getBiome( pos );
        if( target != null ) {
            final List<Biome> entries = getRegistryEntries( level );
            for( Biome entry : entries ) {
                if( entry.equals( target.value() ) ) return !INVERT;
            }
        }
        return INVERT;
    }
}