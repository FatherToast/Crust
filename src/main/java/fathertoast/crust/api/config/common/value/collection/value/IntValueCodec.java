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
public class IntValueCodec implements IValueCodec<Integer> {
    
    /** An integer codec that allows any value and defaults to 0. */
    public static final IntValueCodec ANY = of( 0, IntField.Range.ANY );
    
    /** The standard codec for weights to make weighted random lists. */
    public static final IntValueCodec WEIGHT = of( 0, IntField.Range.NON_NEGATIVE );
    
    public static IntValueCodec of( int defaultValue, IntField.Range range ) { return of( defaultValue, range.MIN, range.MAX ); }
    
    public static IntValueCodec of( int defaultValue, int min, int max ) { return new IntValueCodec( defaultValue, min, max ); }
    
    
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
    
    /** @return The value format (e.g., {@literal "<Number (Any Value)>"}). */
    @Override
    public String getFormat() { return getFormat( "Integer" ); }
    
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
    public Integer parseTomlString( @Nullable AbstractConfigField field, String line, @Nullable String value ) {
        if( value == null ) return defaultValue;
        // Try to parse the value
        int v;
        try {
            v = Integer.parseInt( value );
        }
        catch( NumberFormatException ignored ) {
            try {
                v = (int) Double.parseDouble( value );
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