package fathertoast.crust.api.config.common.value.environment.biome;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.value.ComparatorValue;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.config.common.value.environment.core.CompareFloatEnvironment;
import net.minecraft.world.level.biome.Biome;

import javax.annotation.Nullable;

public class RainfallEnvironment extends CompareFloatEnvironment {
    
    public RainfallEnvironment( ComparatorValue op, float value ) { super( op, value ); }
    
    public RainfallEnvironment( @Nullable IConfigField<?> field, String value ) { super( field, value ); }
    
    /** @return True if this environment matches the provided environment. */
    @Override // Predicate
    public boolean test( EnvironmentContext context ) {
        // Handle the special case of no rainfall
        if( COMPARATOR == ComparatorValue.EQUAL && VALUE == 0.0F )
            return hasNoPrecipitation( context );
        if( COMPARATOR == ComparatorValue.NOT_EQUAL && VALUE == 0.0F )
            return !hasNoPrecipitation( context );
        return super.test( context );
    }
    
    /** @return True if there is no rainfall. */
    protected boolean hasNoPrecipitation( EnvironmentContext context ) {
        return context.getBlockPos() != null && context.getLevel().getBiome( context.getBlockPos() )
                .value().getPrecipitationAt( context.getBlockPos() ) == Biome.Precipitation.NONE;
    }
    
    /** @return Returns the actual value to compare, or null if there isn't enough information. */
    @Override
    @Nullable
    protected Float getActual( EnvironmentContext context ) {
        return context.getBlockPos() == null ? null : context.getLevel()
                .getBiome( context.getBlockPos() ).value().getModifiedClimateSettings().downfall();
    }
}