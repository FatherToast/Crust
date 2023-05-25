package fathertoast.crust.api.config.common.value.environment.dimension;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.DynamicRegistryGroupEnvironment;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;

public class DimensionTypeGroupEnvironment extends DynamicRegistryGroupEnvironment<DimensionType> {
    
    @SuppressWarnings( "unused" )
    public DimensionTypeGroupEnvironment( ConfigManager cfgManager, RegistryKey<DimensionType> dimType, boolean invert ) {
        this( cfgManager, dimType.location(), invert );
    }
    
    public DimensionTypeGroupEnvironment( ConfigManager cfgManager, ResourceLocation regKey, boolean invert ) {
        super( cfgManager, regKey, invert );
    }
    
    public DimensionTypeGroupEnvironment( AbstractConfigField field, String line ) { super( field, line ); }
    
    /** @return The registry used. */
    @Override
    public RegistryKey<Registry<DimensionType>> getRegistry() { return Registry.DIMENSION_TYPE_REGISTRY; }
    
    /** @return Returns true if this environment matches the provided environment. */
    @Override
    public final boolean matches( ServerWorld world, @Nullable BlockPos pos ) {
        final DimensionType target = world.dimensionType();
        final List<DimensionType> entries = getRegistryEntries( world );
        for( DimensionType entry : entries ) {
            if( entry.equals( target ) ) return !INVERT;
        }
        return INVERT;
    }
}