package fathertoast.crust.api.config.common.value.collection.value;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

/**
 * A boolean value codec. Defines a default value.
 */
@SuppressWarnings( "ClassCanBeRecord" )
@ApiStatus.Experimental
public class BooleanValueCodec implements IValueCodec<Boolean>, IValueCorrector<Boolean> {
    
    /** The standard boolean codec that defaults to false. */
    public static final BooleanValueCodec DEFAULT_FALSE = new BooleanValueCodec( false );
    
    /** The standard boolean codec that defaults to true. */
    public static final BooleanValueCodec DEFAULT_TRUE = new BooleanValueCodec( true );
    
    
    // ---- Instance Methods ---- //
    
    public final boolean defaultValue;
    
    private BooleanValueCodec( boolean def ) { defaultValue = def; }
    
    /** @return The value format (for example, {@literal "<Number (Any Value)>"}). */
    @Override
    public String getFormat() { return "<Boolean>"; }
    
    /**
     * @param field The config field we are loading for, or null if error reporting should be suppressed.
     * @param line  The full line, for error context.
     * @param value The value string to parse from.
     * @return A new value based on the value string. If the parse fails, returns a non-null default value.
     */
    @Override // IValueCodec
    public Boolean parseTomlString( @Nullable AbstractConfigField field, String line, @Nullable String value ) {
        if( value == null ) return defaultValue;
        Object v = TomlHelper.parseStringPrimitive( value );
        if( v instanceof Number numberValue ) {
            boolean newValue = !(numberValue.doubleValue() == 0.0); // 0 is false, anything else is true
            if( field != null ) {
                ConfigUtil.infoFor( field );
                ConfigUtil.LOG.info( "Numerical value given for boolean! Converting value {} to {}. Entry: {}",
                        numberValue, newValue, line );
            }
            return newValue;
        }
        return correctValue( field, line, TomlHelper.asBoolean( v ) );
    }
    
    /**
     * @param field The config field we are loading for, or null if error reporting should be suppressed.
     * @param line  The full line, for error context.
     * @param value The value to correct, or null if the value is missing.
     * @return The same value if it is present and valid. If the value is missing, a default value is quietly returned.
     * If invalid, it reports the problem (unless field is null) and returns the closest valid value.
     */
    @Override // IValueCorrector
    public Boolean correctValue( @Nullable AbstractConfigField field, String line, @Nullable Boolean value ) {
        return value == null ? defaultValue : value;
    }
}