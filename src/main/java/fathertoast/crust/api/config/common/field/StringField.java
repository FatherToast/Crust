package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.StringFieldWidgetProvider;
import fathertoast.crust.api.config.common.file.TomlHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

/**
 * Represents a config field with a string value.
 */
@SuppressWarnings( "unused" )
public class StringField extends GenericField<String> {
    
    /** This string field's value validator. */
    @Nullable
    private final Predicate<String> validator;
    
    /** Creates a new field, optionally with a value validator. */
    public StringField( String key, String defaultValue, @Nullable String... description ) {
        this( key, defaultValue, null, description );
    }
    
    /** Creates a new field with no value validator. */
    public StringField( String key, String defaultValue, @Nullable Predicate<String> validator, @Nullable String... description ) {
        super( key, defaultValue, description );
        this.validator = validator;
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoNoHelp( "String", valueDefault ) );
    }
    
    /**
     * Loads this field's value from the given raw toml value. If anything goes wrong, correct it at the lowest level possible.
     * <p>
     * For example, a missing value should be set to the default, while an out-of-range value should be adjusted to the
     * nearest in-range value.
     */
    @Override
    public void load( @Nullable Object raw ) {
        if( raw == null ) {
            value = valueDefault;
            return;
        }
        final String val = raw.toString();
        
        if( validator != null ) {
            value = validator.test( val ) ? val : valueDefault;
        }
        else {
            value = val;
        }
    }
    
    /** @return This string field's value validator. Can be null. */
    @Nullable
    public Predicate<String> getValidator() {
        return validator;
    }
    
    /** @return This field's gui component provider. */
    @Override
    public IConfigFieldWidgetProvider getWidgetProvider() { return new StringFieldWidgetProvider( this ); }
}