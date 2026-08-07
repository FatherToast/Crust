package fathertoast.crust.api.config.common.value.collection.value;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.file.TomlHelper;

import javax.annotation.Nullable;

/**
 * A double value codec. Defines a default value and an allowed value range.
 */
@SuppressWarnings( "ClassCanBeRecord" )
public class DoubleValueCodec implements IValueCodec<Double>, IValueCorrector<Double> {
    
    /** The standard double codec for any value. Defaults to 0.0. */
    public static final DoubleValueCodec ANY = of( 0.0, DoubleField.Range.ANY );
    
    /** The standard double codec for percent values (>= 0). Defaults to 0.0. */
    public static final DoubleValueCodec NON_NEGATIVE = of( 0.0, DoubleField.Range.NON_NEGATIVE );
    
    /** The standard double codec for percent values (0 to 1). Defaults to 0.0. */
    public static final DoubleValueCodec PERCENT = of( 0.0, DoubleField.Range.PERCENT );
    
    /** The standard double codec for signed percent values (-1 to 1). Defaults to 0.0. */
    public static final DoubleValueCodec SIGNED_PERCENT = of( 0.0, DoubleField.Range.SIGNED_PERCENT );
    
    /** The standard double codec for equipment drop chance values (-1 to 2). Defaults to 0.085. */
    public static final DoubleValueCodec DROP_CHANCE = of( 0.085, DoubleField.Range.DROP_CHANCE );
    
    public static DoubleValueCodec of( double defaultValue, DoubleField.Range range ) { return of( defaultValue, range.MIN, range.MAX ); }
    
    public static DoubleValueCodec of( double defaultValue, double min, double max ) { return new DoubleValueCodec( defaultValue, min, max ); }
    
    
    // ---- Instance Methods ---- //
    
    public final double defaultValue;
    public final double minValue;
    public final double maxValue;
    
    private DoubleValueCodec( double def, double min, double max ) {
        if( def < min || max < def ) throw new IllegalArgumentException( "Default value must be within range!" );
        if( max <= min ) throw new IllegalArgumentException( "Maximum value must be greater than minimum value!" );
        defaultValue = def;
        minValue = min;
        maxValue = max;
    }
    
    /** @return The value format (for example, {@literal "<Number (Any Value)>"}). */
    @Override
    public String getFormat() { return getFormat( "Number" ); }
    
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
    public Double parseTomlString( @Nullable IConfigField<?> field, String line, @Nullable String value ) {
        if( value == null ) return defaultValue;
        Object v = TomlHelper.parseStringPrimitive( value );
        if( v instanceof Number numberValue ) {
            return correctValue( field, line, numberValue.doubleValue() );
        }
        if( field != null ) {
            ConfigUtil.warnFor( field );
            ConfigUtil.LOG.warn( "Invalid floating point number ({})! Falling back to {}. Entry: {}",
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
    public Double correctValue( @Nullable IConfigField<?> field, String line, @Nullable Double value ) {
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