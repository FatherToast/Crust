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
    
    public CompareIntEnvironment( AbstractConfigField field, String value ) {
        final String line = name() + " " + value;
        if( value.isEmpty() ) {
            COMPARATOR = ComparisonOperator.LESS_THAN;
            VALUE = 0;
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
    private int parseValue( AbstractConfigField field, String line, String arg ) {
        // Try to parse the value
        int value;
        try {
            value = Integer.parseInt( arg );//TODO allow floating points and cast to int w/ warning
        }
        catch( NumberFormatException ex ) {
            ConfigUtil.warnFor( field );
            ConfigUtil.LOG.warn( "Environment entry has invalid integer value {}! Falling back to 0. Entry: {}",
                    arg, line );
            value = 0;
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