package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.NumberFieldWidgetProvider;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.lib.CrustMath;
import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Represents a config field with a long value.
 */
public class LongField extends AbstractConfigField implements Supplier<Long> {
    
    /** The default field value. */
    private final long valueDefault;
    /** The minimum field value. */
    private final long valueMin;
    /** The maximum field value. */
    private final long valueMax;
    
    /** The underlying field value. */
    private long value;
    
    /** Creates a new field that accepts a common range of values. */
    public LongField( String key, long defaultValue, LongField.Range range, @Nullable String... description ) {
        this( key, defaultValue, range.MIN, range.MAX, description );
    }
    
    /** Creates a new field that accepts a specialized range of values. */
    public LongField( String key, long defaultValue, long min, long max, @Nullable String... description ) {
        super( key, description );
        valueDefault = defaultValue;
        valueMin = min;
        valueMax = max;
        
        // Sanity checks
        if( valueMin >= valueMax ) {
            throw new IllegalArgumentException( "Maximum value must be greater than the minimum! Invalid field: " + getKey() );
        }
        if( valueDefault < valueMin || valueDefault > valueMax ) {
            throw new IllegalArgumentException( "Default value is outside of allowed range! Invalid field: " + getKey() );
        }
    }
    
    /** @return Returns the config field's value. */
    @Override
    public Long get() { return value; }
    
    /** @return Returns the config field's value cast down to a 32-bit integer. */
    public int getInt() { return get().intValue(); }
    
    /** @return Treats the config field's value as a 1-in-X chance and returns the result of a single roll. */
    public boolean rollChance( Random random ) { return get() > 0 && random.nextLong( get() ) == 0; }
    
    /** @return Returns the minimum value allowed by this field. */
    public long minValue() { return valueMin; }
    
    /** @return Returns the maximum value allowed by this field. */
    public long maxValue() { return valueMax; }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoRange( valueDefault, valueMin, valueMax ) );
    }
    
    /**
     * Loads this field's value from the given value or raw toml. If anything goes wrong, correct it at the lowest level possible.
     * <p>
     * For example, a missing value should be set to the default, while an out-of-range value should be adjusted to the
     * nearest in-range value and print a warning explaining the change.
     */
    @Override
    public void load( @Nullable Object raw ) {
        Number newValue = TomlHelper.readAsNumber( this, raw );
        if( newValue == null ) {
            if( raw != null ) {
                ConfigUtil.warnFor( this );
                ConfigUtil.LOG.warn( "Invalid long! Falling back to default ({}). Invalid value: {}",
                        valueDefault, raw );
            }
            value = valueDefault;
        }
        else {
            long castValue = newValue.longValue();
            if( castValue < valueMin ) {
                ConfigUtil.warnFor( this );
                ConfigUtil.LOG.warn( "Value is below the minimum! Adjusting from {} to {}.", raw, valueMin );
                value = valueMin;
            }
            else if( castValue > valueMax ) {
                ConfigUtil.warnFor( this );
                ConfigUtil.LOG.warn( "Value is above the maximum! Adjusting from {} to {}.", raw, valueMax );
                value = valueMax;
            }
            else {
                if( (double) castValue != newValue.doubleValue() ) {
                    ConfigUtil.warnFor( this );
                    ConfigUtil.LOG.warn( "Floating point value given for integer! Truncating value {} to {}.",
                            raw, castValue );
                }
                value = castValue;
            }
        }
    }
    
    /** @return The value that should be assigned to this field in the config file. */
    @Override
    public Long getValue() { return value; }
    
    /** @return The default value of this field. */
    @Override
    public Long getDefaultValue() { return valueDefault; }
    
    /** @return This field's gui component provider. */
    @Override
    public IConfigFieldWidgetProvider getWidgetProvider() {
        return new NumberFieldWidgetProvider( this, Number::longValue,
                ( number ) -> number.longValue() >= valueMin && number.longValue() <= valueMax );
    }
    
    
    /** A set of commonly used ranges for this field type. */
    public enum Range {
        
        /** Accepts any value. */
        ANY( Long.MIN_VALUE, Long.MAX_VALUE ),
        /** Accepts any positive value (> 0). */
        POSITIVE( 1, Long.MAX_VALUE ),
        /** Accepts any non-negative value (>= 0). */
        NON_NEGATIVE( 0, Long.MAX_VALUE ),
        /** Accepts any non-negative value and -1 (>= -1). */
        TOKEN_NEGATIVE( -1, Long.MAX_VALUE );
        
        public final long MIN;
        public final long MAX;
        
        Range( long min, long max ) {
            MIN = min;
            MAX = max;
        }
    }
    
    /**
     * Represents two number fields, a minimum and a maximum, combined into one.
     * This has convenience methods for returning a random value between the min and the max (inclusive).
     */
    public static class RandomRange {
        
        /** The minimum. Defines the lower limit of the range (inclusive). */
        private final LongField MINIMUM;
        /** The maximum. Defines the upper limit of the range (inclusive). */
        private final LongField MAXIMUM;
        
        /**
         * Links two values together as minimum and maximum.
         * <p>
         * Helper method to automatically generate the minimum and maximum long fields and define them in the spec.
         * Appends ".min" and ".max" to the provided key, and only supports comments on the first field.
         */
        public RandomRange( CrustConfigSpec spec, String keyBase, long defaultMinValue, long defaultMaxValue, LongField.Range range, @Nullable String... description ) {
            this( spec, keyBase, defaultMinValue, defaultMaxValue, range.MIN, range.MAX, description );
        }
        
        /**
         * Links two values together as minimum and maximum.
         * <p>
         * Helper method to automatically generate the minimum and maximum long fields and define them in the spec.
         * Appends ".min" and ".max" to the provided key, and only supports comments on the first field.
         */
        public RandomRange( CrustConfigSpec spec, String keyBase, long defaultMinValue, long defaultMaxValue, long min, long max, @Nullable String... description ) {
            this(
                    spec.define( new LongField( keyBase + ".min", defaultMinValue, min, max, description ) ),
                    spec.define( new LongField( keyBase + ".max", defaultMaxValue, min, max ) )
            );
        }
        
        /** Links two values together as minimum and maximum. */
        public RandomRange( LongField minimum, LongField maximum ) {
            MINIMUM = minimum;
            MAXIMUM = maximum;
            if( minimum.valueDefault > maximum.valueDefault ) {
                throw new IllegalArgumentException( String.format( "Random range has inverted default values! (%s > %s) See: (%s, %s)",
                        minimum.valueDefault, maximum.valueDefault, minimum.getKey(), maximum.getKey() ) );
            }
        }
        
        /** @return The minimum value of this range. */
        public long getMin() { return MINIMUM.get(); }
        
        /** @return The maximum value of this range. */
        public long getMax() { return MAXIMUM.get(); }
        
        /** @return The minimum value field. */
        public LongField getMinField() { return MINIMUM; }
        
        /** @return The maximum value field. */
        public LongField getMaxField() { return MAXIMUM; }
        
        /**
         * @return A random value between the minimum and the maximum (inclusive).
         * <br><br>
         * All this method does is try and grab the seed from the given RandomSource instance,
         * such that it can be used to instantiate a new {@link Random} object
         * that we can call {@link LongField.RandomRange#next(Random)} with.
         * <br><br>
         * If you end up needing to call this often, it is likely better to call the below method
         * that takes a {@link Random} instance instead when possible, as {@link RandomSource} does not
         * provide a method for generating a bounded random long.
         * @see CrustMath#getRandomSourceSeed(RandomSource)
         */
        public long next( RandomSource random ) {
            return next( new Random( CrustMath.getRandomSourceSeed( random ) ) );
        }
        
        /** @return A random value between the minimum and the maximum (inclusive). */
        public long next( Random random ) {
            try {
                return random.nextLong( getMin(), getMax() );
            }
            catch( IllegalArgumentException ex ) {
                ConfigUtil.warnFor( MAXIMUM );
                ConfigUtil.LOG.warn( "Values for range are invalid; min ({}) is greater than max ({})! Ignoring maximum value.",
                        getMin(), getMax() );
                return getMin();
            }
        }
    }
}
