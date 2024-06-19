package fathertoast.crust.api.config.common.value.environment.position;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.DynamicRegistryGroupEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

import javax.annotation.Nullable;
import java.util.List;

public class StructureGroupEnvironment extends DynamicRegistryGroupEnvironment<Structure> {
    
    @SuppressWarnings( "unused" )
    public StructureGroupEnvironment( ConfigManager cfgManager, ResourceKey<Structure> structure, boolean invert ) { super( cfgManager, structure.location(), invert ); }
    
    @SuppressWarnings( "unused" )
    public StructureGroupEnvironment( ConfigManager cfgManager, ResourceLocation regKey, boolean invert ) { super( cfgManager, regKey, invert ); }
    
    public StructureGroupEnvironment( AbstractConfigField field, String line ) { super( field, line ); }

    
    /** @return The registry used. */
    @Override
    public ResourceKey<Registry<Structure>> getRegistry() { return Registries.STRUCTURE; }
    
    /** @return Returns true if this environment matches the provided environment. */
    @Override
    public final boolean matches( ServerLevel level, @Nullable BlockPos pos ) {
        final StructureManager structureManager = pos != null ?
                level.structureManager() : null;
        if( structureManager != null ) {
            final List<Structure> entries = getRegistryEntries( level );
            for( Structure entry : entries ) {
                if( structureManager.getStructureAt( pos, entry ).isValid() ) return !INVERT;
            }
        }
        return INVERT;
    }
}