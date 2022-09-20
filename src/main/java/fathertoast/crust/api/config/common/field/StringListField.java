package fathertoast.crust.api.config.common.field;

import com.electronwill.nightconfig.core.io.CharacterOutput;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.CrustTomlWriter;
import fathertoast.crust.api.config.common.file.TomlHelper;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents a config field with a string list value.
 */
@SuppressWarnings( "unused" )
public class StringListField extends GenericField<List<String>> {
    
    private final String type;
    
    /** Creates a new field. */
    public StringListField( String key, List<String> defaultValue, @Nullable String... description ) {
        this( "String", key, defaultValue, description );
    }
    
    /** Creates a new field referred to as something other than a "String" list. */
    public StringListField( String key, String typeName, List<String> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
        type = typeName;
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoFormat( type + " List", valueDefault,
                "[ \"" + ConfigUtil.toLowerCaseNoSpaces( type ) + "1\", \"" +
                        ConfigUtil.toLowerCaseNoSpaces( type ) + "2\", ... ]" ) );
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
        value = TomlHelper.parseStringList( raw );
    }
    
    /** Writes this field's value to file. */
    @Override
    public void writeValue( CrustTomlWriter writer, CharacterOutput output ) {
        writer.writeStringArray( get(), output );
    }
    
    
    // Convenience methods
    
    /** @return Returns true if there are no entries in this string list. */
    public boolean isEmpty() { return get().isEmpty(); }
}