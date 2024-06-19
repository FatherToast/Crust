package fathertoast.crust.api.config.common.value.environment.position;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.DynamicRegistryEnvironment;
import fathertoast.crust.api.config.common.value.environment.RegistryEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.Structures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class StructureEnvironment extends DynamicRegistryEnvironment<Structure> {
    
    public StructureEnvironment( ConfigManager cfgManager, ResourceKey<Structure> structure, boolean invert ) { super( cfgManager, structure.location(), invert ); }

    public StructureEnvironment( AbstractConfigField field, String line ) { super( field, line ); }
    
    /** @return The registry used. */
    @Override
    public ResourceKey<Registry<Structure>> getRegistry() { return Registries.STRUCTURE; }
    
    /** @return Returns true if this environment matches the provided environment. */
    @Override
    public boolean matches( ServerLevel level, @Nullable BlockPos pos ) {
        final Structure entry = getRegistryEntry( level );
        return (entry != null && pos != null &&
                level.structureManager().getStructureAt( pos, entry ).isValid()) != INVERT;
    }
}