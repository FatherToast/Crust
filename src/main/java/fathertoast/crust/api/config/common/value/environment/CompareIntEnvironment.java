package fathertoast.crust.api.config.common.value.environment;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public abstract class CompareIntEnvironment extends AbstractEnvironment {
    
    /** How the actual value is compared to this environment's value. */
    public final ComparisonOperator COMPARATOR;
    /** The value for this environment. */
    public final int VALUE;
    
    public CompareIntEnvironment( ComparisonOperator op, int value ) {
        COMPARATOR = op;
        VALUE = value;
    }
    
    public CompareIntEnvironment( AbstractConfigField field, String line ) {
        if( line.isEmpty() ) {
            COMPARATOR = ComparisonOperator.LESS_THAN;
            VALUE = 0;
            ConfigUtil.LOG.warn( "Invalid entry for {} \"{}\"! Not defined. Defaulting to \"{}\". Invalid entry: {}",
                    field.getClass(), field.getKey(), value(), line );
        }
        else {
            final ComparisonOperator op = ComparisonOperator.parse( line );
            if( op == null ) {
                COMPARATOR = ComparisonOperator.LESS_THAN;
                ConfigUtil.LOG.warn( "Invalid entry for {} \"{}\"! Comparison not defined (must be in the set [ {} ]). Defaulting to \"{}\". Invalid entry: {}",
                        field.getClass(), field.getKey(), TomlHelper.toLiteralList( (Object[]) ComparisonOperator.values() ), COMPARATOR, line );
            }
            else COMPARATOR = op;
            VALUE = parseValue( field, line, line.substring( COMPARATOR.toString().length() ).trim() );
        }
    }
    
    /** @return Parses the value and returns a valid result. */
    private int parseValue( AbstractConfigField field, String line, String arg ) {
        // Try to parse the value
        int value;
        try {
            value = Integer.parseInt( arg );
        }
        catch( NumberFormatException ex ) {
            ConfigUtil.LOG.warn( "Invalid entry for {} \"{}\"! Value not defined (must be an integer). Defaulting to '0'. Invalid entry: {}",
                    field.getClass(), field.getKey(), line );
            value = 0;
        }
        // Verify value is within range
        if( value < getMinValue() ) {
            ConfigUtil.LOG.warn( "Value for {} \"{}\" is below the minimum ({})! Clamping value. Invalid value: {}",
                    field.getClass(), field.getKey(), getMinValue(), value );
            value = getMinValue();
        }
        else if( value > getMaxValue() ) {
            ConfigUtil.LOG.warn( "Value for {} \"{}\" is above the maximum ({})! Clamping value. Invalid value: {}",
                    field.getClass(), field.getKey(), getMaxValue(), value );
            value = getMaxValue();
        }
        return value;
    }
    
    /** @return The minimum value that can be given to the value. */
    protected int getMinValue() { return Integer.MIN_VALUE; }
    
    /** @return The maximum value that can be given to the value. */
    protected int getMaxValue() { return Integer.MAX_VALUE; }
    
    /** @return The string value of this environment, as it would appear in a config file. */
    @Override
    public String value() { return COMPARATOR + " " + VALUE; }
    
    /** @return Returns true if this environment matches the provided environment. */
    @Override
    public boolean matches( Level level, @Nullable BlockPos pos ) {
        final Integer actual = getActual( level, pos );
        return actual != null && COMPARATOR.apply( actual, VALUE );
    }
    
    /** @return Returns the actual value to compare, or null if there isn't enough information. */
    @Nullable
    public abstract Integer getActual( Level level, @Nullable BlockPos pos );
}