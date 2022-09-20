package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.common.file.TomlHelper;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents a config field with a string value.
 */
@SuppressWarnings( "unused" )
public class StringField extends GenericField<String> {
    
    /** Creates a new field. */
    public StringField( String key, String defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
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
        value = raw.toString();
    }
}