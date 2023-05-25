package fathertoast.crust.api.config.common.value.environment.dimension;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.DynamicRegistryEnvironment;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class DimensionTypeEnvironment extends DynamicRegistryEnvironment<DimensionType> {
    
    public DimensionTypeEnvironment( ConfigManager cfgManager, RegistryKey<DimensionType> dimType, boolean invert ) {
        super( cfgManager, dimType.location(), invert );
    }
    
    public DimensionTypeEnvironment( AbstractConfigField field, String line ) { super( field, line ); }
    
    /** @return The registry used. */
    @Override
    public RegistryKey<Registry<DimensionType>> getRegistry() { return Registry.DIMENSION_TYPE_REGISTRY; }
    
    /** @return Returns true if this environment matches the provided environment. */
    @Override
    public boolean matches( ServerWorld world, @Nullable BlockPos pos ) {
        final DimensionType entry = getRegistryEntry( world );
        return (entry != null && entry.equals( world.dimensionType() )) != INVERT;
    }
}