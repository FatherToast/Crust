package fathertoast.crust.api.config.common.value.collection.value;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.field.LongField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

/**
 * A long value codec. Defines a default value and an allowed value range.
 */
@ApiStatus.Experimental
public class LongValueCodec implements IValueCodec<Long>, IValueCorrector<Long> {
    
    /** The standard long codec for any value. Defaults to 0. */
    public static final LongValueCodec ANY = of( 0, LongField.Range.ANY );
    
    /** The standard long codec for positive values (> 0). Defaults to 1. */
    public static final LongValueCodec POSITIVE = of( 1, LongField.Range.POSITIVE );
    
    /** The standard long codec for non-negative values (>= 0). Defaults to 0. */
    public static final LongValueCodec NON_NEGATIVE = of( 0, LongField.Range.NON_NEGATIVE );
    
    /** The standard long codec for non-negative values or -1 (>= -1). Defaults to -1. */
    public static final LongValueCodec TOKEN_NEGATIVE = of( 0, LongField.Range.TOKEN_NEGATIVE );
    
    public static LongValueCodec of( long defaultValue, LongField.Range range ) { return of( defaultValue, range.MIN, range.MAX ); }
    
    public static LongValueCodec of( long defaultValue, long min, long max ) { return new LongValueCodec( defaultValue, min, max ); }
    
    
    // ---- Instance Methods ---- //
    
    public final long defaultValue;
    public final long minValue;
    public final long maxValue;
    
    private LongValueCodec( long def, long min, long max ) {
        if( def < min || max < def ) throw new IllegalArgumentException( "Default value must be within range!" );
        if( max <= min ) throw new IllegalArgumentException( "Maximum value must be greater than minimum value!" );
        defaultValue = def;
        minValue = min;
        maxValue = max;
    }
    
    /** @return The value format (for example, {@literal "<Number (Any Value)>"}). */
    @Override
    public String getFormat() { return getFormat( "Long" ); }
    
    /** @return The value format (for example, {@literal "<Number (Any Value)>"}). */
    public String getFormat( String name ) {
        return String.format( "<%s (%s)>", name, TomlHelper.fieldRange( minValue, maxValue ) );
    }
    
    /**
     * @param field The config field we are loading for, or null if error reporting should be suppressed.
     * @param line  The full line, for error context.
     * @param value The value string to parse from.
     * @return A new value based on the value string. If the parse fails, returns a non-null default value.
     */
    @Override // IValueCodec
    public Long parseTomlString( @Nullable AbstractConfigField field, String line, @Nullable String value ) {
        if( value == null ) return defaultValue;
        Object v = TomlHelper.parseStringPrimitive( value );
        if( v instanceof Number numberValue ) {
            if( field != null && (double) numberValue.longValue() != numberValue.doubleValue() ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Floating point value given for long! Truncating value {} to {}.",
                        numberValue.doubleValue(), numberValue.longValue() );
            }
            return correctValue( field, line, numberValue.longValue() );
        }
        if( field != null ) {
            ConfigUtil.warnFor( field );
            ConfigUtil.LOG.warn( "Invalid long ({})! Falling back to {}. Entry: {}",
                    value, defaultValue, line );
        }
        return defaultValue;
    }
    
    /**
     * @param field The config field we are loading for, or null if error reporting should be suppressed.
     * @param line  The full line, for error context.
     * @param value The value to correct, or null if the value is missing.
     * @return The same value if it is present and valid. If the value is missing, a default value is quietly returned.
     * If invalid, it reports the problem (unless field is null) and returns the closest valid value.
     */
    @Override // IValueCorrector
    public Long correctValue( @Nullable AbstractConfigField field, String line, @Nullable Long value ) {
        if( value == null ) return defaultValue;
        // Verify value is within range
        if( value < minValue ) {
            if( field != null ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Entry value is below the minimum! Adjusting from {} to {}. Entry: {}",
                        value, minValue, line );
            }
            return minValue;
        }
        else if( value > maxValue ) {
            if( field != null ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Entry value is above the maximum! Adjusting from {} to {}. Entry: {}",
                        value, maxValue, line );
            }
            return maxValue;
        }
        return value;
    }
}
