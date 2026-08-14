package fathertoast.crust.api.config.common.value.collection.value;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.file.TomlHelper;

import javax.annotation.Nullable;

/**
 * A float value codec. Defines a default value and an allowed value range.
 */
@SuppressWarnings( "ClassCanBeRecord" )
public class FloatValueCodec implements IValueCodec<Float>, IValueCorrector<Float> {
    
    /** The standard float codec for any value. Defaults to 0.0. */
    public static final FloatValueCodec ANY = of( 0.0F, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY );
    
    /** The standard float codec for percent values (>= 0). Defaults to 0.0. */
    public static final FloatValueCodec NON_NEGATIVE = of( 0.0F, 0.0F, Float.POSITIVE_INFINITY );
    
    /** The standard float codec for percent values (0 to 1). Defaults to 0.0. */
    public static final FloatValueCodec PERCENT = of( 0.0F, 0.0F, 1.0F );
    
    /** The standard float codec for signed percent values (-1 to 1). Defaults to 0.0. */
    public static final FloatValueCodec SIGNED_PERCENT = of( 0.0F, -1.0F, 1.0F );
    
    public static FloatValueCodec of( float defaultValue, float min, float max ) { return new FloatValueCodec( defaultValue, min, max ); }
    
    
    // ---- Instance Methods ---- //
    
    public final float defaultValue;
    public final float minValue;
    public final float maxValue;
    
    protected FloatValueCodec( float def, float min, float max ) {
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
    public Float parseTomlString( @Nullable IConfigField<?> field, String line, @Nullable String value ) {
        if( value == null ) return defaultValue;
        Object v = TomlHelper.parseStringPrimitive( value );
        if( v instanceof Number numberValue ) {
            return correctValue( field, line, numberValue.floatValue() );
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
    public Float correctValue( @Nullable IConfigField<?> field, String line, @Nullable Float value ) {
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