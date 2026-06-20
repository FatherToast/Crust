package fathertoast.crust.api.config.common.field;

import com.electronwill.nightconfig.core.io.CharacterOutput;
import fathertoast.crust.api.config.common.file.CrustTomlWriter;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.List;

/**
 * A generic string field implementation that allows use of a value codec to handle its value.
 * The automatic field info provided by value codecs is a little different format and less
 * descriptive than that of a typical field.
 */
@ApiStatus.Experimental
public class ValueCodecField<T> extends GenericField<T> {
    
    protected final IValueCodec<T> valueCodec;
    
    /** Creates a new field. */
    public ValueCodecField( String key, IValueCodec<T> codec, @Nullable String... description ) {
        super( key, codec.getDefaultValue(), description );
        valueCodec = codec;
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoNoHelp( valueCodec.getFormat(), valueDefault ) );
    }
    
    /**
     * Loads this field's value from the given value or raw toml. If anything goes wrong, correct it at the lowest level possible.
     * <p>
     * For example, a missing value should be set to the default, while an out-of-range value should be adjusted to the
     * nearest in-range value and print a warning explaining the change.
     */
    @Override
    public void load( @Nullable Object raw ) {
        if( raw == null ) {
            value = valueDefault;
            return;
        }
        final String val = raw.toString();
        
        value = valueCodec.parseTomlString( this, val, val );
    }
    
    /** Writes this field's value to file. */
    @Override
    public void writeValue( CrustTomlWriter writer, CharacterOutput output ) {
        writer.writeLine( TomlHelper.toLiteral( valueCodec.toTomlString( value ) ), output );
    }
    
    //    /** @return This field's gui component provider. */ TODO
    //    @Override
    //    public IConfigFieldWidgetProvider getWidgetProvider() { return new StringFieldWidgetProvider( this ); }
}