package fathertoast.crust.api.config.common.value.environment;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public abstract class CompareFloatEnvironment extends AbstractEnvironment {
    
    /** How the actual value is compared to this environment's value. */
    public final ComparisonOperator COMPARATOR;
    /** The value for this environment. */
    public final float VALUE;
    
    public CompareFloatEnvironment( ComparisonOperator op, float value ) {
        COMPARATOR = op;
        VALUE = value;
    }
    
    public CompareFloatEnvironment( AbstractConfigField field, String value ) {
        final String line = name() + " " + value;
        if( value.isEmpty() ) {
            COMPARATOR = ComparisonOperator.LESS_THAN;
            VALUE = 0.0F;
            ConfigUtil.warnFor( field );
            ConfigUtil.LOG.warn( "Environment entry missing operator and value! Defaulting to \"{}\". Entry: {}",
                    value(), line );
        }
        else {
            final ComparisonOperator op = ComparisonOperator.parse( value );
            if( op == null ) {
                COMPARATOR = ComparisonOperator.LESS_THAN;
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Environment entry has missing or invalid operator! Must be in the set [ {} ]. Defaulting to \"{}\". Entry: {}",
                        TomlHelper.toLiteralList( (Object[]) ComparisonOperator.values() ), COMPARATOR, line );
            }
            else COMPARATOR = op;
            VALUE = parseValue( field, line, value.substring( COMPARATOR.toString().length() ).trim() );
        }
    }
    
    /** @return Parses the value and returns a valid result. */
    private float parseValue( AbstractConfigField field, String line, String arg ) {
        // Try to parse the value
        float value;
        try {
            value = Float.parseFloat( arg );
        }
        catch( NumberFormatException ex ) {
            ConfigUtil.warnFor( field );
            ConfigUtil.LOG.warn( "Environment entry has invalid floating point value {}! Falling back to 0. Entry: {}",
                    arg, line );
            value = 0.0F;
        }
        // Verify value is within range
        if( value < getMinValue() ) {
            ConfigUtil.warnFor( field );
            ConfigUtil.LOG.warn( "Environment entry value is below the minimum! Adjusting from {} to {}. Entry: {}",
                    value, getMinValue(), line );
            value = getMinValue();
        }
        else if( value > getMaxValue() ) {
            ConfigUtil.warnFor( field );
            ConfigUtil.LOG.warn( "Entity entry value is above the maximum! Adjusting from {} to {}. Entry: {}",
                    value, getMaxValue(), line );
            value = getMaxValue();
        }
        return value;
    }
    
    /** @return The minimum value that can be given to the value. */
    protected float getMinValue() { return Float.NEGATIVE_INFINITY; }
    
    /** @return The maximum value that can be given to the value. */
    protected float getMaxValue() { return Float.POSITIVE_INFINITY; }
    
    /** @return The string value of this environment, as it would appear in a config file. */
    @Override
    public String value() { return COMPARATOR + " " + VALUE; }
    
    /** @return Returns true if this environment matches the provided environment. */
    @Override
    public boolean matches( Level level, @Nullable BlockPos pos ) {
        final float actual = getActual( level, pos );
        return !Float.isNaN( actual ) && COMPARATOR.apply( actual, VALUE );
    }
    
    /** @return Returns the actual value to compare, or Float.NaN if there isn't enough information. */
    public abstract float getActual( Level level, @Nullable BlockPos pos );
    
}