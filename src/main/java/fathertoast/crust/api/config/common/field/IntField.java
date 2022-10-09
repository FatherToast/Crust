package fathertoast.crust.api.config.common.field;

import com.electronwill.nightconfig.core.io.CharacterOutput;
import fathertoast.crust.api.config.client.gui.widget.field.HexIntFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.field.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.field.NumberFieldWidgetProvider;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.CrustTomlWriter;
import fathertoast.crust.api.config.common.file.TomlHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Represents a config field with an integer value.
 */
@SuppressWarnings( "unused" )
public class IntField extends AbstractConfigField {
    
    /** The default field value. */
    private final int valueDefault;
    /** The minimum field value. */
    private final int valueMin;
    /** The maximum field value. */
    private final int valueMax;
    
    /** The underlying field value. */
    private int value;
    
    /** Creates a new field that accepts a common range of values. */
    public IntField( String key, int defaultValue, Range range, @Nullable String... description ) {
        this( key, defaultValue, range.MIN, range.MAX, description );
    }
    
    /** Creates a new field that accepts a specialized range of values. */
    public IntField( String key, int defaultValue, int min, int max, @Nullable String... description ) {
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
    public int get() { return value; }
    
    /** @return Returns the config field's value cast down to a short. */
    public short getShort() { return (short) get(); }
    
    /** @return Returns the config field's value cast down to a byte. */
    public byte getByte() { return (byte) get(); }
    
    /** @return Treats the config field's value as a one-in-X chance and returns the result of a single roll. */
    public boolean rollChance( Random random ) { return get() > 0 && random.nextInt( get() ) == 0; }
    
    /** @return Returns the minimum value allowed by this field. */
    public int minValue() { return valueMin; }
    
    /** @return Returns the maximum value allowed by this field. */
    public int maxValue() { return valueMax; }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoRange( valueDefault, valueMin, valueMax ) );
    }
    
    /**
     * Loads this field's value from the given raw toml value. If anything goes wrong, correct it at the lowest level possible.
     * <p>
     * For example, a missing value should be set to the default, while an out-of-range value should be adjusted to the
     * nearest in-range value
     */
    @Override
    public void load( @Nullable Object raw ) {
        Number newValue;
        if( raw instanceof String ) {
            ConfigUtil.LOG.info( "Unboxing string value for {} \"{}\" to a different primitive.",
                    getClass(), getKey() );
            newValue = TomlHelper.parseNumber( (String) raw );
        }
        else {
            newValue = TomlHelper.asNumber( raw );
        }
        
        if( newValue == null ) {
            if( raw != null ) {
                ConfigUtil.LOG.warn( "Invalid value for {} \"{}\"! Falling back to default. Invalid value: {}",
                        getClass(), getKey(), raw );
            }
            value = valueDefault;
        }
        else {
            int castValue = newValue.intValue();
            if( castValue < valueMin ) {
                ConfigUtil.LOG.warn( "Value for {} \"{}\" is below the minimum ({})! Clamping value. Invalid value: {}",
                        getClass(), getKey(), valueMin, raw );
                value = valueMin;
            }
            else if( castValue > valueMax ) {
                ConfigUtil.LOG.warn( "Value for {} \"{}\" is above the maximum ({})! Clamping value. Invalid value: {}",
                        getClass(), getKey(), valueMax, raw );
                value = valueMax;
            }
            else {
                if( (double) castValue != newValue.doubleValue() ) {
                    ConfigUtil.LOG.warn( "Value for {} \"{}\" is not an integer! Truncating value. Invalid value: {}",
                            getClass(), getKey(), raw );
                }
                value = castValue;
            }
        }
    }
    
    /** @return The raw toml value that should be assigned to this field in the config file. */
    @Override
    public Object getRaw() { return value; }
    
    /** @return The default raw toml value of this field. */
    @Override
    public Object getRawDefault() { return valueDefault; }
    
    /** @return This field's gui component provider. */
    @Override
    public IConfigFieldWidgetProvider getWidgetProvider() {
        return new NumberFieldWidgetProvider( this, Number::intValue,
                ( number ) -> valueMin <= number.intValue() && number.intValue() <= valueMax );
    }
    
    
    /** A set of commonly used ranges for this field type. */
    public enum Range {
        
        /** Accepts any value. */
        ANY( Integer.MIN_VALUE, Integer.MAX_VALUE ),
        /** Accepts any positive value (> 0). */
        POSITIVE( 1, Integer.MAX_VALUE ),
        /** Accepts any non-negative value (>= 0). */
        NON_NEGATIVE( 0, Integer.MAX_VALUE ),
        /** Accepts any non-negative value and -1 (>= -1). */
        TOKEN_NEGATIVE( -1, Integer.MAX_VALUE );
        
        public final int MIN;
        public final int MAX;
        
        Range( int min, int max ) {
            MIN = min;
            MAX = max;
        }
    }
    
    
    /**
     * Represents a config field with an integer value that displays values in hexadecimal.
     */
    public static class Hex extends IntField {
        
        /** Minimum hex digits to output. */
        private final int minDigits;
        
        /**
         * Creates a new field that accepts a specialized range of values and prints a minimum number of digits.
         * Since hex is unsigned, negatives are not supported unless using Range.ANY.
         */
        public Hex( String key, int defaultValue, int digitsMin, int min, int max, @Nullable String... description ) {
            super( key, defaultValue, min, max, description );
            minDigits = digitsMin;
            if( (min < 0 || max < 0) && (min != Range.ANY.MIN || max != Range.ANY.MAX) ) {
                throw new IllegalArgumentException( "Negatives are unsupported by hex int unless allowing any value!" );
            }
        }
        
        /**
         * Creates a new field that accepts a specialized range of values.
         * Since hex is unsigned, negatives are not supported unless using Range.ANY.
         */
        public Hex( String key, int defaultValue, int min, int max, @Nullable String... description ) {
            this( key, defaultValue, 1, min, max, description );
        }
        
        /** @return The minimum number of digits this field prints. */
        public int getMinDigits() { return minDigits; }
        
        /** Adds info about the field type, format, and bounds to the end of a field's description. */
        @Override
        public void appendFieldInfo( List<String> comment ) {
            TomlHelper.HEX_MODE = minDigits;
            super.appendFieldInfo( comment );
            TomlHelper.HEX_MODE = 0;
        }
        
        /** Writes this field's value to file. */
        @Override
        public void writeValue( CrustTomlWriter writer, CharacterOutput output ) {
            TomlHelper.HEX_MODE = minDigits;
            super.writeValue( writer, output );
            TomlHelper.HEX_MODE = 0;
        }
        
        /** @return This field's gui component provider. */
        @Override
        public IConfigFieldWidgetProvider getWidgetProvider() {
            return new HexIntFieldWidgetProvider( this, ( number ) -> minValue() <= number && number <= maxValue() );
        }
    }
    
    
    /**
     * Represents two number fields, a minimum and a maximum, combined into one.
     * This has convenience methods for returning a random value between the min and the max (inclusive).
     */
    public static class RandomRange {
        
        /** The minimum. Defines the lower limit of the range (inclusive). */
        private final IntField MINIMUM;
        /** The maximum. Defines the upper limit of the range (inclusive). */
        private final IntField MAXIMUM;
        
        /** Links two values together as minimum and maximum. */
        public RandomRange( IntField minimum, IntField maximum ) {
            MINIMUM = minimum;
            MAXIMUM = maximum;
            if( minimum.valueDefault > maximum.valueDefault ) {
                throw new IllegalArgumentException( String.format( "Random range has inverted default values! (%s > %s) See: (%s, %s)",
                        minimum.valueDefault, maximum.valueDefault, minimum.getKey(), maximum.getKey() ) );
            }
        }
        
        /** @return Returns the minimum value of this range. */
        public int getMin() { return MINIMUM.get(); }
        
        /** @return Returns the maximum value of this range. */
        public int getMax() { return MAXIMUM.get(); }
        
        /** @return Returns a random value between the minimum and the maximum (inclusive). */
        public int next( Random random ) {
            final int delta = getMax() - getMin();
            if( delta > 0 ) {
                return getMin() + random.nextInt( delta + 1 );
            }
            if( delta < 0 ) {
                ConfigUtil.LOG.warn( "Value for range \"({},{})\" is invalid ({} > {})! Ignoring maximum value.",
                        MINIMUM.getKey(), MAXIMUM.getKey(), getMin(), getMax() );
            }
            return getMin();
        }
    }
}