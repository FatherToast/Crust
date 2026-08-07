package fathertoast.crust.api.config.common.value.environment.biome;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.value.ComparatorValue;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;

import javax.annotation.Nullable;

public class BiomeTemperatureEnvironment extends TemperatureEnvironment {
    
    public BiomeTemperatureEnvironment( boolean freezing ) { super( freezing ); }
    
    public BiomeTemperatureEnvironment( ComparatorValue op, float value ) { super( op, value ); }
    
    public BiomeTemperatureEnvironment( @Nullable IConfigField<?> field, String value ) { super( field, value ); }
    
    /** @return Returns the actual value to compare, or null if there isn't enough information. */
    @Override
    @Nullable
    protected Float getActual( EnvironmentContext context ) {
        return context.getBlockPos() == null ? null : context.getLevel()
                .getBiome( context.getBlockPos() ).value().getBaseTemperature();
    }
}