package fathertoast.crust.api.config.common.value.environment.position;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.RegistryEnvironment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;

public class StructureEnvironment extends RegistryEnvironment<Structure<?>> {
    
    public StructureEnvironment( Structure<?> structure, boolean invert ) { super( structure, invert ); }
    
    public StructureEnvironment( AbstractConfigField field, String line ) { super( field, line ); }
    
    /** @return The registry used. */
    @Override
    public IForgeRegistry<Structure<?>> getRegistry() { return ForgeRegistries.STRUCTURE_FEATURES; }
    
    /** @return Returns true if this environment matches the provided environment. */
    @Override
    public boolean matches( World world, @Nullable BlockPos pos ) {
        final Structure<?> entry = getRegistryEntry();
        return (entry != null && pos != null && world instanceof ServerWorld &&
                ((ServerWorld) world).structureFeatureManager().getStructureAt( pos, false, entry ).isValid()) != INVERT;
    }
}