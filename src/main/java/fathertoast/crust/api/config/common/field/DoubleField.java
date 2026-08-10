package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.NumberFieldWidgetProvider;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.util.JavaRandomSource;
import fathertoast.crust.api.util.OnClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Represents a config field with a double value.
 */
@SuppressWarnings( "unused" )
public class DoubleField extends AbstractConfigField<Double> {
    
    /** The minimum field value. */
    private final double valueMin;
    /** The maximum field value. */
    private final double valueMax;
    
    /** Creates a new field that accepts a common range of values. */
    public DoubleField( String key, double defaultValue, Range range, @Nullable String... description ) {
        this( key, defaultValue, range.MIN, range.MAX, description );
    }
    
    /** Creates a new field that accepts a specialized range of values. */
    public DoubleField( String key, double defaultValue, double min, double max, @Nullable String... description ) {
        super( key, defaultValue, description );
        valueMin = min;
        valueMax = max;
        
        // Sanity checks
        if( min >= max ) {
            throw new IllegalArgumentException( "Maximum value cannot be less than the minimum! Invalid field: " + getKey() );
        }
        if( defaultValue < min || defaultValue > max ) {
            throw new IllegalArgumentException( "Default value is outside of allowed range! Invalid field: " + getKey() );
        }
    }
    
    /** @return Returns the config field's value. */
    public double getDouble() { return get(); }
    
    /** @return Returns the config field's value cast to a float. */
    public float getFloat() { return (float) getDouble(); }
    
    /** @return Treats the config field's value as a percent chance (from 0 to 1) and returns the result of a single roll. */
    public boolean rollChance( Random random ) { return rollChance( JavaRandomSource.of( random ) ); }
    
    /** @return Treats the config field's value as a percent chance (from 0 to 1) and returns the result of a single roll. */
    public boolean rollChance( RandomSource random ) { return random.nextDouble() < getDouble(); }
    
    /** @return Returns the minimum value allowed by this field. */
    public double minValue() { return valueMin; }
    
    /** @return Returns the maximum value allowed by this field. */
    public double maxValue() { return valueMax; }
    
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
    public Double parse( Object raw ) {
        Number value = TomlHelper.readAsNumber( this, raw );
        if( value == null ) {
            ConfigUtil.warnFor( this );
            ConfigUtil.LOG.warn( "Invalid floating point number! Falling back to default ({}). Invalid value: {}",
                    getDefaultValue(), raw );
            return getDefaultValue();
        }
        double castValue = value.doubleValue();
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
        return castValue;
    }
    
    /** Writes a field value to the byte buffer; should be the inverse of {@link #deserialize}. */
    @Override
    public void serialize( Double value, FriendlyByteBuf buffer ) { buffer.writeDouble( value ); }
    
    /** Reads a new field value from the byte buffer; should be the inverse of {@link #serialize}. */
    @Override
    public Double deserialize( FriendlyByteBuf buffer ) { return buffer.readDouble(); }
    
    /** @return This field's gui component provider. */
    @Override
    @OnClient
    public IConfigFieldWidgetProvider<Double> getWidgetProvider() {
        return new NumberFieldWidgetProvider<>( Number::doubleValue,
                number -> minValue() <= number.doubleValue() && number.doubleValue() <= maxValue() );
    }
    
    
    /** A set of commonly used ranges for this field type. */
    public enum Range {
        
        /** Accepts any value. */
        ANY( Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY ),
        /** Accepts any non-negative value (>= 0). */
        NON_NEGATIVE( 0.0, Double.POSITIVE_INFINITY ),
        /** Accepts any value between 0 and 1. */
        PERCENT( 0.0, 1.0 ),
        /** Accepts any value between -1 and 1. */
        SIGNED_PERCENT( -1.0, 1.0 ),
        /**
         * Accepts any value between -1 and 2. This is generally a percent.
         * A value over 1 guarantees the equipment drop and prevents it from being damaged, while
         * a negative value should prevent the item from being equipped at all (implementation-specific).
         */
        DROP_CHANCE( -1.0, 2.0 );
        
        public final double MIN;
        public final double MAX;
        
        Range( double min, double max ) {
            MIN = min;
            MAX = max;
        }
    }
    
    
    /**
     * Represents two number fields, a minimum and a maximum, combined into one.
     * This has convenience methods for returning a random value between the min (inclusive) and the max (exclusive).
     */
    @SuppressWarnings( "ClassCanBeRecord" )
    public static class RandomRange {
        
        /** The minimum. Defines the lower limit of the range (inclusive). */
        private final DoubleField MINIMUM;
        /** The maximum. Defines the upper limit of the range (exclusive). */
        private final DoubleField MAXIMUM;
        
        /**
         * Links two values together as minimum and maximum.
         * <p>
         * Helper method to automatically generate the minimum and maximum double fields and define them in the spec.
         * Appends ".min" and ".max" to the provided key, and only supports comments on the first field.
         */
        public RandomRange( CrustConfigSpec spec, String keyBase, double defaultMinValue, double defaultMaxValue, Range range, @Nullable String... description ) {
            this( spec, keyBase, defaultMinValue, defaultMaxValue, range.MIN, range.MAX, description );
        }
        
        /**
         * Links two values together as minimum and maximum.
         * <p>
         * Helper method to automatically generate the minimum and maximum double fields and define them in the spec.
         * Appends ".min" and ".max" to the provided key, and only supports comments on the first field.
         */
        public RandomRange( CrustConfigSpec spec, String keyBase, double defaultMinValue, double defaultMaxValue, double min, double max, @Nullable String... description ) {
            this(
                    spec.define( new DoubleField( keyBase + ".min", defaultMinValue, min, max, description ) ),
                    spec.define( new DoubleField( keyBase + ".max", defaultMaxValue, min, max ) )
            );
        }
        
        /** Links two values together as minimum and maximum. */
        public RandomRange( DoubleField minimum, DoubleField maximum ) {
            MINIMUM = minimum;
            MAXIMUM = maximum;
            if( minimum.getDefaultValue() > maximum.getDefaultValue() ) {
                throw new IllegalArgumentException( String.format( "Random range has inverted default values! (%s > %s) See: (%s, %s)",
                        minimum.getDefaultValue(), maximum.getDefaultValue(), minimum.getKey(), maximum.getKey() ) );
            }
        }
        
        /** @return The minimum value of this range. */
        public double getMin() { return MINIMUM.getDouble(); }
        
        /** @return The maximum value of this range. */
        public double getMax() { return MAXIMUM.getDouble(); }
        
        /** @return The minimum value field. */
        public DoubleField getMinField() { return MINIMUM; }
        
        /** @return The maximum value field. */
        public DoubleField getMaxField() { return MAXIMUM; }
        
        
        /** @return A random value between the minimum (inclusive) and the maximum (exclusive). */
        public double next( Random random ) { return next( JavaRandomSource.of( random ) ); }
        
        /** @return A random value between the minimum (inclusive) and the maximum (exclusive). */
        public double next( RandomSource random ) {
            final double delta = getMax() - getMin();
            if( delta > 1.0e-4 ) {
                return getMin() + random.nextDouble() * delta;
            }
            if( delta < 0.0 ) {
                ConfigUtil.warnFor( MAXIMUM );
                ConfigUtil.LOG.warn( "Values for range are invalid; min ({}) is greater than max ({})! Ignoring maximum value.",
                        getMin(), getMax() );
            }
            return getMin();
        }
    }
    
    /**
     * Represents a double field and an environment exception list, combined into one.
     * This has convenience methods for returning the value that should be used based on the environment.
     *
     * @param base       The base value.
     * @param exceptions The environment exceptions list.
     */
    public record EnvironmentSensitive( DoubleField base, EnvironmentListField<Double> exceptions ) {
        
        /** @return Returns the config field's value. */
        public double getDouble( EnvironmentContext context ) { return exceptions().getOrElse( context, base() ); }
        
        /** @return Returns the config field's value cast to a float. */
        public float getFloat( EnvironmentContext context ) { return (float) getDouble( context ); }
        
        /** @return Treats the config field's value as a percent chance (from 0 to 1) and returns the result of a single roll. */
        public boolean rollChance( Random random, EnvironmentContext context ) { return rollChance( JavaRandomSource.of( random ), context ); }
        
        /** @return Treats the config field's value as a percent chance (from 0 to 1) and returns the result of a single roll. */
        public boolean rollChance( RandomSource random, EnvironmentContext context ) { return random.nextDouble() < getDouble( context ); }
    }
}