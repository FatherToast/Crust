package fathertoast.crust.api.config.common.value.environment.biome;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.CompareFloatEnvironment;
import fathertoast.crust.api.config.common.value.environment.ComparisonOperator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class TemperatureEnvironment extends CompareFloatEnvironment {
    
    public static final String FREEZING = "freezing";
    public static final float FREEZING_POINT = 0.15F;
    
    public static String handleTempInput( String value ) {
        if( value.equalsIgnoreCase( FREEZING ) )
            return ComparisonOperator.LESS_THAN + " " + FREEZING_POINT;
        if( value.equalsIgnoreCase( "!" + FREEZING ) )
            return ComparisonOperator.LESS_THAN.invert() + " " + FREEZING_POINT;
        return value;
    }
    
    public TemperatureEnvironment( boolean freezing ) {
        this( ComparisonOperator.LESS_THAN.invert( !freezing ), FREEZING_POINT );
    }
    
    public TemperatureEnvironment( ComparisonOperator op, float value ) { super( op, value ); }
    
    public TemperatureEnvironment( AbstractConfigField field, String value ) { super( field, handleTempInput( value ) ); }
    
    /** @return The string value of this environment, as it would appear in a config file. */
    @Override
    public String value() {
        if( COMPARATOR == ComparisonOperator.LESS_THAN && VALUE == FREEZING_POINT ) return FREEZING;
        if( COMPARATOR == ComparisonOperator.LESS_THAN.invert() && VALUE == FREEZING_POINT ) return "!" + FREEZING;
        return super.value();
    }
    
    /** @return Returns the actual value to compare, or Float.NaN if there isn't enough information. */
    @Override
    public float getActual( Level level, @Nullable BlockPos pos ) {
        //noinspection deprecation
        return pos == null ? Float.NaN : level.getBiome( pos ).value().getTemperature( pos );
    }
}