package fathertoast.crust.api.config.common.field;

import com.electronwill.nightconfig.core.io.CharacterOutput;
import fathertoast.crust.api.config.client.gui.widget.provider.HexIntFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.NumberFieldWidgetProvider;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.file.CrustTomlWriter;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.HexIntWrapper;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.util.JavaRandomSource;
import fathertoast.crust.api.util.OnClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Represents a config field with an integer value.
 */
@SuppressWarnings( "unused" )
public class IntField extends AbstractConfigField<Integer> {
    
    /** The minimum field value. */
    private final int valueMin;
    /** The maximum field value. */
    private final int valueMax;
    
    /** Creates a new field that accepts a common range of values. */
    public IntField( String key, int defaultValue, Range range, @Nullable String... description ) {
        this( key, defaultValue, range.MIN, range.MAX, description );
    }
    
    /** Creates a new field that accepts a specialized range of values. */
    public IntField( String key, int defaultValue, int min, int max, @Nullable String... description ) {
        super( key, defaultValue, description );
        valueMin = min;
        valueMax = max;
        
        // Sanity checks
        if( min >= max ) {
            throw new IllegalArgumentException( "Maximum value must be greater than the minimum! Invalid field: " + getKey() );
        }
        if( defaultValue < min || defaultValue > max ) {
            throw new IllegalArgumentException( "Default value is outside of allowed range! Invalid field: " + getKey() );
        }
    }
    
    /** @return Returns the config field's value. */
    public int getInt() { return get(); }
    
    /** @return Returns the config field's value cast down to a short. */
    public short getShort() { return (short) getInt(); }
    
    /** @return Returns the config field's value cast down to a byte. */
    public byte getByte() { return (byte) getInt(); }
    
    /** @return Treats the config field's value as a 1-in-X chance and returns the result of a single roll. */
    public boolean rollChance( Random random ) { return rollChance( JavaRandomSource.of( random ) ); }
    
    /** @return Treats the config field's value as a 1-in-X chance and returns the result of a single roll. */
    public boolean rollChance( RandomSource random ) { return getInt() > 0 && random.nextInt( getInt() ) == 0; }
    
    /** @return Returns the minimum value allowed by this field. */
    public int minValue() { return valueMin; }
    
    /** @return Returns the maximum value allowed by this field. */
    public int maxValue() { return valueMax; }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoRange( getDefaultValue(), minValue(), maxValue() ) );
    }
    
    /**
     * @return Reads a value from the given value or raw toml. If anything goes wrong, correct it at the lowest level
     * possible.
     * <p>
     * For example, a missing or unreadable value should return the default value, while an out-of-range value should be
     * adjusted to the nearest in-range value. If any value correction is applied, print a warning to explain the change.
     */
    @Override
    public Integer parse( Object raw ) {
        Number value = TomlHelper.readAsNumber( this, raw );
        if( value == null ) {
            ConfigUtil.warnFor( this );
            ConfigUtil.LOG.warn( "Invalid integer! Falling back to default ({}). Invalid value: {}",
                    getDefaultValue(), raw );
            return getDefaultValue();
        }
        int castValue = value.intValue();
        if( castValue < minValue() ) {
            ConfigUtil.warnFor( this );
            ConfigUtil.LOG.warn( "Value is below the minimum! Adjusting from {} to {}.", raw, minValue() );
            return minValue();
        }
        else if( castValue > maxValue() ) {
            ConfigUtil.warnFor( this );
            ConfigUtil.LOG.warn( "Value is above the maximum! Adjusting from {} to {}.", raw, maxValue() );
            return maxValue();
        }
        if( (double) castValue != value.doubleValue() ) {
            ConfigUtil.warnFor( this );
            ConfigUtil.LOG.warn( "Floating point value given for integer! Truncating value {} to {}.",
                    raw, castValue );
        }
        return castValue;
    }
    
    /** Writes a field value to the byte buffer; should be the inverse of {@link #deserialize}. */
    @Override
    public void serialize( Integer value, FriendlyByteBuf buffer ) { buffer.writeInt( value ); }
    
    /** Reads a new field value from the byte buffer; should be the inverse of {@link #serialize}. */
    @Override
    public Integer deserialize( FriendlyByteBuf buffer ) { return buffer.readInt(); }
    
    /** @return This field's gui component provider. */
    @Override
    public IConfigFieldWidgetProvider<Integer> getWidgetProvider() {
        return new NumberFieldWidgetProvider<>( this, Number::intValue,
                number -> valueMin <= number.intValue() && number.intValue() <= valueMax );
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
        
        /** @return The value in an appropriate hex wrapper. */
        public HexIntWrapper wrap( int value ) { return new HexIntWrapper( value, getMinDigits() ); }
        
        /** Adds info about the field type, format, and bounds to the end of a field's description. */
        @Override
        public void appendFieldInfo( List<String> comment ) {
            comment.add( TomlHelper.fieldInfoRange( wrap( getDefaultValue() ),
                    wrap( minValue() ), wrap( maxValue() ) ) );
        }
        
        /** Writes this field's value to file. */
        @Override
        public void writeValue( CrustTomlWriter writer, CharacterOutput output ) {
            writer.writeValue( wrap( getInt() ), output );
        }
        
        /** @return This field's gui component provider. */
        @Override
        @OnClient
        public IConfigFieldWidgetProvider<Integer> getWidgetProvider() {
            return new HexIntFieldWidgetProvider( this,
                    ( number ) -> minValue() <= number && number <= maxValue() );
        }
    }
    
    
    /**
     * Represents two number fields, a minimum and a maximum, combined into one.
     * This has convenience methods for returning a random value between the min and the max (inclusive).
     */
    @SuppressWarnings( "ClassCanBeRecord" )
    public static class RandomRange {
        
        /** The minimum. Defines the lower limit of the range (inclusive). */
        private final IntField MINIMUM;
        /** The maximum. Defines the upper limit of the range (inclusive). */
        private final IntField MAXIMUM;
        
        /**
         * Links two values together as minimum and maximum.
         * <p>
         * Helper method to automatically generate the minimum and maximum int fields and define them in the spec.
         * Appends ".min" and ".max" to the provided key, and only supports comments on the first field.
         */
        public RandomRange( CrustConfigSpec spec, String keyBase, int defaultMinValue, int defaultMaxValue, Range range, @Nullable String... description ) {
            this( spec, keyBase, defaultMinValue, defaultMaxValue, range.MIN, range.MAX, description );
        }
        
        /**
         * Links two values together as minimum and maximum.
         * <p>
         * Helper method to automatically generate the minimum and maximum int fields and define them in the spec.
         * Appends ".min" and ".max" to the provided key, and only supports comments on the first field.
         */
        public RandomRange( CrustConfigSpec spec, String keyBase, int defaultMinValue, int defaultMaxValue, int min, int max, @Nullable String... description ) {
            this(
                    spec.define( new IntField( keyBase + ".min", defaultMinValue, min, max, description ) ),
                    spec.define( new IntField( keyBase + ".max", defaultMaxValue, min, max ) )
            );
        }
        
        /** Links two values together as minimum and maximum. */
        public RandomRange( IntField minimum, IntField maximum ) {
            MINIMUM = minimum;
            MAXIMUM = maximum;
            if( minimum.getDefaultValue() > maximum.getDefaultValue() ) {
                throw new IllegalArgumentException( String.format( "Random range has inverted default values! (%s > %s) See: (%s, %s)",
                        minimum.getDefaultValue(), maximum.getDefaultValue(), minimum.getKey(), maximum.getKey() ) );
            }
        }
        
        /** @return The minimum value of this range. */
        public int getMin() { return MINIMUM.getInt(); }
        
        /** @return The maximum value of this range. */
        public int getMax() { return MAXIMUM.getInt(); }
        
        /** @return The minimum value field. */
        public IntField getMinField() { return MINIMUM; }
        
        /** @return The maximum value field. */
        public IntField getMaxField() { return MAXIMUM; }
        
        
        /** @return A random value between the minimum and the maximum (inclusive). */
        public int next( Random random ) { return next( JavaRandomSource.of( random ) ); }
        
        /** @return A random value between the minimum and the maximum (inclusive). */
        public int next( RandomSource random ) {
            try {
                return random.nextIntBetweenInclusive( getMin(), getMax() );
            }
            catch( IllegalArgumentException ex ) {
                ConfigUtil.warnFor( MAXIMUM );
                ConfigUtil.LOG.warn( "Values for range are invalid; min ({}) is greater than max ({})! Ignoring maximum value.",
                        getMin(), getMax() );
                return getMin();
            }
        }
    }
    
    /**
     * Represents a double field and an environment exception list, combined into one.
     * This has convenience methods for returning the value that should be used based on the environment.
     *
     * @param base       The base value.
     * @param exceptions The environment exceptions list.
     */
    public record EnvironmentSensitive( IntField base, EnvironmentListField<Integer> exceptions ) {
        
        /** @return Returns the config field's value. */
        public int getInt( EnvironmentContext context ) { return exceptions().getOrElse( context, base() ); }
        
        /** @return Returns the config field's value cast down to a short. */
        public short getShort( EnvironmentContext context ) { return (short) getInt( context ); }
        
        /** @return Returns the config field's value cast down to a byte. */
        public byte getByte( EnvironmentContext context ) { return (byte) getInt( context ); }
        
        /** @return Treats the config field's value as a 1-in-X chance and returns the result of a single roll. */
        public boolean rollChance( Random random, EnvironmentContext context ) { return rollChance( JavaRandomSource.of( random ), context ); }
        
        /** @return Treats the config field's value as a 1-in-X chance and returns the result of a single roll. */
        public boolean rollChance( RandomSource random, EnvironmentContext context ) {
            int i = getInt( context );
            return i > 0 && random.nextInt( i ) == 0;
        }
    }
}