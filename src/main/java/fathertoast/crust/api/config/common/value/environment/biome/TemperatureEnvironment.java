package fathertoast.crust.api.config.common.value.environment.biome;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.environment.ComparisonOperator;
import fathertoast.crust.api.config.common.field.EnvironmentListField;
import fathertoast.crust.api.config.common.value.environment.CompareFloatEnvironment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class TemperatureEnvironment extends CompareFloatEnvironment {
    
    public static final String FREEZING = "freezing";
    public static final float FREEZING_POINT = 0.15F;
    
    public static String handleTempInput( String line ) {
        if( line.equalsIgnoreCase( FREEZING ) )
            return ComparisonOperator.LESS_THAN + " " + FREEZING_POINT;
        if( line.equalsIgnoreCase( "!" + FREEZING ) )
            return ComparisonOperator.LESS_THAN.invert() + " " + FREEZING_POINT;
        return line;
    }
    
    public TemperatureEnvironment( boolean freezing ) {
        this( ComparisonOperator.LESS_THAN.invert( !freezing ), FREEZING_POINT );
    }
    
    public TemperatureEnvironment( ComparisonOperator op, float value ) { super( op, value ); }
    
    public TemperatureEnvironment( AbstractConfigField field, String line ) { super( field, handleTempInput( line ) ); }
    
    /** @return The string name of this environment, as it would appear in a config file. */
    @Override
    public String name() { return EnvironmentListField.ENV_TEMPERATURE; }
    
    /** @return The string value of this environment, as it would appear in a config file. */
    @Override
    public String value() {
        if( COMPARATOR == ComparisonOperator.LESS_THAN && VALUE == FREEZING_POINT ) return FREEZING;
        if( COMPARATOR == ComparisonOperator.LESS_THAN.invert() && VALUE == FREEZING_POINT ) return "!" + FREEZING;
        return super.value();
    }
    
    /** @return Returns the actual value to compare, or Float.NaN if there isn't enough information. */
    @Override
    public float getActual( World world, @Nullable BlockPos pos ) {
        return pos == null ? Float.NaN : world.getBiome( pos ).getTemperature( pos );
    }
}