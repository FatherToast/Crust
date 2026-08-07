package fathertoast.crust.api.config.common.value.environment.biome;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.value.ComparatorValue;
import fathertoast.crust.api.config.common.value.collection.value.FloatValueCodec;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.config.common.value.environment.core.CompareFloatEnvironment;
import fathertoast.crust.api.config.common.value.environment.core.InvertibleEnvironment;

import javax.annotation.Nullable;

public class TemperatureEnvironment extends CompareFloatEnvironment {
    
    public static final String FREEZING = "freezing";
    public static final float FREEZING_POINT = 0.15F;
    
    public static String handleTempInput( String value ) {
        if( value.equalsIgnoreCase( FREEZING ) )
            return ComparatorValue.LESS.toTomlString() + " " + FloatValueCodec.ANY.toTomlString( FREEZING_POINT );
        if( value.equalsIgnoreCase( InvertibleEnvironment.CODE + FREEZING ) )
            return ComparatorValue.LESS.invert().toTomlString() + " " + FloatValueCodec.ANY.toTomlString( FREEZING_POINT );
        return value;
    }
    
    public TemperatureEnvironment( boolean freezing ) {
        this( freezing ? ComparatorValue.LESS : ComparatorValue.LESS.invert(), FREEZING_POINT );
    }
    
    public TemperatureEnvironment( ComparatorValue op, float value ) { super( op, value ); }
    
    public TemperatureEnvironment( @Nullable IConfigField<?> field, String value ) { super( field, handleTempInput( value ) ); }
    
    /** @return The string value of this environment, as it would appear in a config file. */
    @Override
    public String value() {
        if( COMPARATOR == ComparatorValue.LESS && VALUE == FREEZING_POINT ) return FREEZING;
        if( COMPARATOR == ComparatorValue.LESS.invert() && VALUE == FREEZING_POINT )
            return InvertibleEnvironment.CODE + FREEZING;
        return super.value();
    }
    
    /** @return Returns the actual value to compare, or null if there isn't enough information. */
    @Override
    @Nullable
    protected Float getActual( EnvironmentContext context ) {
        //noinspection deprecation
        return context.getBlockPos() == null ? null : context.getLevel()
                .getBiome( context.getBlockPos() ).value().getTemperature( context.getBlockPos() );
    }
}