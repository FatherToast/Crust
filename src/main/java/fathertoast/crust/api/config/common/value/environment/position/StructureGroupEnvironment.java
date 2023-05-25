package fathertoast.crust.api.config.common.value.environment.position;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.RegistryGroupEnvironment;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class StructureGroupEnvironment extends RegistryGroupEnvironment<Structure<?>> {
    
    @SuppressWarnings( "unused" )
    public StructureGroupEnvironment( Structure<?> structure, boolean invert ) { super( structure, invert ); }
    
    @SuppressWarnings( "unused" )
    public StructureGroupEnvironment( ResourceLocation regKey, boolean invert ) { super( regKey, invert ); }
    
    public StructureGroupEnvironment( AbstractConfigField field, String line ) { super( field, line ); }
    
    /** @return The registry used. */
    @Override
    public IForgeRegistry<Structure<?>> getRegistry() { return ForgeRegistries.STRUCTURE_FEATURES; }
    
    /** @return Returns true if this environment matches the provided environment. */
    @Override
    public final boolean matches( World world, @Nullable BlockPos pos ) {
        final StructureManager structureManager = pos != null && world instanceof ServerWorld ?
                ((ServerWorld) world).structureFeatureManager() : null;
        if( structureManager != null ) {
            final List<Structure<?>> entries = getRegistryEntries();
            for( Structure<?> entry : entries ) {
                if( structureManager.getStructureAt( pos, false, entry ).isValid() ) return !INVERT;
            }
        }
        return INVERT;
    }
}