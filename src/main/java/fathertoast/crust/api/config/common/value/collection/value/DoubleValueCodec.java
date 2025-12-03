package fathertoast.crust.api.config.common.value.collection.value;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

/**
 * A double value codec. Defines a default value and an allowed value range.
 */
@SuppressWarnings( "ClassCanBeRecord" )
@ApiStatus.Experimental
public class DoubleValueCodec implements IValueCodec<Double> {
    
    /** A double codec that allows any value and defaults to 0.0. */
    public static final DoubleValueCodec ANY = of( 0.0, DoubleField.Range.ANY );
    
    /** The standard codec for percentages (0 to 1). Defaults to 0.0. */
    public static final DoubleValueCodec PERCENT = of( 0.0, DoubleField.Range.PERCENT );
    
    public static DoubleValueCodec of( double defaultValue, DoubleField.Range range ) { return of( defaultValue, range.MIN, range.MAX ); }
    
    public static DoubleValueCodec of( double defaultValue, double min, double max ) { return new DoubleValueCodec( defaultValue, min, max ); }
    
    
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
    
    /** @return The value format (e.g., {@literal "<Number (Any Value)>"}). */
    @Override
    public String getFormat() { return getFormat( "Number" ); }
    
    /** @return The value format (e.g., {@literal "<Number (Any Value)>"}). */
    public String getFormat( String name ) {
        return String.format( "<%s (%s)>", name, TomlHelper.fieldRange( minValue, maxValue ) );
    }
    
    /**
     * @param field The config field we are loading for, or null if error reporting should be suppressed.
     * @param line  The full line, for error context.
     * @param value The value string to parse from.
     * @return A new value based on the value string. If the parse fails, returns a non-null default value.
     */
    @Override
    public Double parseTomlString( @Nullable AbstractConfigField field, String line, @Nullable String value ) {
        if( value == null ) return defaultValue;
        // Try to parse the value
        double v;
        try {
            v = Double.parseDouble( value );
        }
        catch( NumberFormatException ex ) {
            // This is thrown if the string is not a parsable number
            if( field != null ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Entry has invalid value ({})! Falling back to {}. Entry: {}",
                        value, defaultValue, line );
            }
            return defaultValue;
        }
        // Verify value is within range
        if( v < minValue ) {
            if( field != null ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Entry value is below the minimum! Adjusting from {} to {}. Entry: {}",
                        v, minValue, line );
            }
            return minValue;
        }
        else if( v > maxValue ) {
            if( field != null ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Entry value is above the maximum! Adjusting from {} to {}. Entry: {}",
                        v, maxValue, line );
            }
            return maxValue;
        }
        return v;
    }
}