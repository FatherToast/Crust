package fathertoast.crust.api.config.common.value.environment.biome;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.TagEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import javax.annotation.Nullable;

public class BiomeTagEnvironment extends TagEnvironment<Biome> {
    
    public BiomeTagEnvironment( TagKey<Biome> biomeTag, boolean invert ) { super( biomeTag, invert ); }
    
    public BiomeTagEnvironment( AbstractConfigField field, String value ) { super( field, value ); }
    
    /** @return The registry used. */
    @Override
    public ResourceKey<Registry<Biome>> getRegistry() { return Registries.BIOME; }
    
    /** @return Returns the actual environment to compare, or null if there isn't enough information. */
    @Override
    @Nullable
    public Holder<Biome> getActual( Level level, @Nullable BlockPos pos ) {
        return pos == null ? null : level.getBiome( pos );
    }
}