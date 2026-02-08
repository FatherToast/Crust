package fathertoast.crust.api.config.common.value.collection.value;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

/**
 * An integer value codec. Defines a default value and an allowed value range.
 */
@SuppressWarnings( "ClassCanBeRecord" )
@ApiStatus.Experimental
public class IntValueCodec implements IValueCodec<Integer>, IValueCorrector<Integer> {
    
    /** The standard integer codec for any value. Defaults to 0. */
    public static final IntValueCodec ANY = of( 0, IntField.Range.ANY );
    
    /** The standard integer codec for positive values (> 0). Defaults to 1. */
    public static final IntValueCodec POSITIVE = of( 1, IntField.Range.POSITIVE );
    
    /** The standard integer codec for non-negative values (>= 0). Defaults to 0. */
    public static final IntValueCodec NON_NEGATIVE = of( 0, IntField.Range.NON_NEGATIVE );
    
    /** The standard integer codec for non-negative values or -1 (>= -1). Defaults to -1. */
    public static final IntValueCodec TOKEN_NEGATIVE = of( 0, IntField.Range.TOKEN_NEGATIVE );
    
    public static IntValueCodec of( int defaultValue, IntField.Range range ) { return of( defaultValue, range.MIN, range.MAX ); }
    
    public static IntValueCodec of( int defaultValue, int min, int max ) { return new IntValueCodec( defaultValue, min, max ); }
    
    
    // ---- Instance Methods ---- //
    
    public final int defaultValue;
    public final int minValue;
    public final int maxValue;
    
    private IntValueCodec( int def, int min, int max ) {
        if( def < min || max < def ) throw new IllegalArgumentException( "Default value must be within range!" );
        if( max <= min ) throw new IllegalArgumentException( "Maximum value must be greater than minimum value!" );
        defaultValue = def;
        minValue = min;
        maxValue = max;
    }
    
    /** @return The value format (for example, {@literal "<Number (Any Value)>"}). */
    @Override
    public String getFormat() { return getFormat( "Integer" ); }
    
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
    public Integer parseTomlString( @Nullable AbstractConfigField field, String line, @Nullable String value ) {
        if( value == null ) return defaultValue;
        Object v = TomlHelper.parseStringPrimitive( value );
        if( v instanceof Number numberValue ) {
            if( field != null && (double) numberValue.intValue() != numberValue.doubleValue() ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Floating point value given for integer! Truncating value {} to {}.",
                        numberValue.doubleValue(), numberValue.intValue() );
            }
            return correctValue( field, line, numberValue.intValue() );
        }
        if( field != null ) {
            ConfigUtil.warnFor( field );
            ConfigUtil.LOG.warn( "Invalid integer ({})! Falling back to {}. Entry: {}",
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
    public Integer correctValue( @Nullable AbstractConfigField field, String line, @Nullable Integer value ) {
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