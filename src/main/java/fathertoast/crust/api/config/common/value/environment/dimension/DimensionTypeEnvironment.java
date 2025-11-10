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

import javax.annotation.Nullable;

public class DimensionTypeEnvironment extends DynamicRegistryEnvironment<Level> {
    
    public DimensionTypeEnvironment( ConfigManager cfgManager, ResourceKey<Level> dimType, boolean invert ) {
        super( cfgManager, dimType, invert );
    }
    
    public DimensionTypeEnvironment( AbstractConfigField field, String value ) { super( field, value ); }
    
    /** @return The registry used. */
    @Override
    public ResourceKey<Registry<Level>> getRegistry() { return Registries.DIMENSION; }
    
    /** @return Returns true if this environment matches the provided environment. */
    @Override
    public boolean matches( Level level, @Nullable BlockPos pos ) {
        try {
            return REGISTRY_KEY.equals( level.dimension().location() ) != INVERT;
        }
        catch( NullPointerException ex ) {
            return false; // Shouldn't be possible, but who knows man
        }
    }
    
    // We override the method that calls this, so really it should not get called
    @Override
    public boolean matches( ServerLevel level, @Nullable BlockPos pos ) {
        return matches( (Level) level, pos );
    }
}