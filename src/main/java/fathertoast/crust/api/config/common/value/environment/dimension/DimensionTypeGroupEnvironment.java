package fathertoast.crust.api.config.common.value.environment.dimension;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.DynamicRegistryGroupEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.DimensionType;

import javax.annotation.Nullable;
import java.util.List;

public class DimensionTypeGroupEnvironment extends DynamicRegistryGroupEnvironment<DimensionType> {
    
    @SuppressWarnings( "unused" )
    public DimensionTypeGroupEnvironment( ConfigManager cfgManager, ResourceKey<DimensionType> dimType, boolean invert ) {
        this( cfgManager, dimType.location(), invert );
    }
    
    public DimensionTypeGroupEnvironment( ConfigManager cfgManager, ResourceLocation regKey, boolean invert ) {
        super( cfgManager, regKey, invert );
    }
    
    public DimensionTypeGroupEnvironment( AbstractConfigField field, String line ) { super( field, line ); }
    
    /** @return The registry used. */
    @Override
    public ResourceKey<Registry<DimensionType>> getRegistry() { return Registries.DIMENSION_TYPE; }
    
    /** @return Returns true if this environment matches the provided environment. */
    @Override
    public final boolean matches( ServerLevel level, @Nullable BlockPos pos ) {
        final DimensionType target = level.dimensionType();
        final List<DimensionType> entries = getRegistryEntries( level );
        for( DimensionType entry : entries ) {
            if( entry.equals( target ) ) return !INVERT;
        }
        return INVERT;
    }
}