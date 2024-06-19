package fathertoast.crust.api.config.common.value.environment.dimension;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.DynamicRegistryEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;


import javax.annotation.Nullable;

public class DimensionTypeEnvironment extends DynamicRegistryEnvironment<Level> {
    
    public DimensionTypeEnvironment( ConfigManager cfgManager, ResourceKey<Level> dimType, boolean invert ) {
        super( cfgManager, dimType.location(), invert );
    }
    
    public DimensionTypeEnvironment( AbstractConfigField field, String line ) { super( field, line ); }
    
    /** @return The registry used. */
    @Override
    public ResourceKey<Registry<Level>> getRegistry() { return Registries.DIMENSION; }
    
    /** @return Returns true if this environment matches the provided environment. */
    @Override
    public boolean matches( ServerLevel level, @Nullable BlockPos pos ) {
        final Level entry = getRegistryEntry( level );
        return (entry != null && entry.dimension().equals( level.dimension() )) != INVERT;
    }
}