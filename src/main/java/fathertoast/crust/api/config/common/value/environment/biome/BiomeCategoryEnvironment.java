package fathertoast.crust.api.config.common.value.environment.biome;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.config.common.value.environment.core.EnumEnvironment;

import javax.annotation.Nullable;

@Deprecated( forRemoval = true ) // TODO Remove when updating beyond 1.20.1
public class BiomeCategoryEnvironment extends EnumEnvironment<BiomeCategory> {
    
    @Deprecated( forRemoval = true )
    public BiomeCategoryEnvironment( BiomeCategory value, boolean invert ) { super( value, invert ); }
    
    @Deprecated( forRemoval = true )
    public BiomeCategoryEnvironment( @Nullable IConfigField<?> field, String value ) {
        super( field, value, BiomeCategory.values() );
        if( field != null ) {
            ConfigUtil.warnFor( field );
            ConfigUtil.LOG.warn( "Deprecated environment entry! The \"{}\" environment condition will be removed in a future version. Deprecated entry: {}",
                    name(), name() + " " + value );
        }
    }
    
    /** @return True if this environment matches the provided environment, ignoring inversion. */
    @Override
    protected boolean cleanTest( EnvironmentContext context ) {
        return context.getBlockPos() != null && VALUE != BiomeCategory.NONE &&
                context.getLevel().getBiome( context.getBlockPos() ).is( VALUE.BIOME_TAG );
    }
}