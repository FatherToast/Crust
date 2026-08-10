package fathertoast.crust.api.config.common.field;

import com.electronwill.nightconfig.core.io.CharacterOutput;
import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.StringFieldWidgetProvider;
import fathertoast.crust.api.config.common.file.CrustTomlWriter;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;
import java.util.List;

/**
 * A generic string field implementation that allows use of a value codec to handle its value.
 * The automatic field info provided by value codecs is a little different format and less
 * descriptive than that of a typical field.
 */
@SuppressWarnings( "unused" )
public class ValueCodecField<T> extends AbstractConfigField<T> {
    
    protected final IValueCodec<T> valueCodec;
    
    /** Creates a new field. */
    public ValueCodecField( String key, IValueCodec<T> codec, @Nullable String... description ) {
        super( key, codec.getDefaultValue(), description );
        valueCodec = codec;
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoNoHelp( valueCodec.getFormat(), getDefaultValue() ) );
    }
    
    /**
     * @return Reads a value from the given value or raw toml. If anything goes wrong, correct it at the lowest level
     * possible.
     * <p>
     * For example, a missing or unreadable value should return the default value, while an out-of-range value should be
     * adjusted to the nearest in-range value. If any value correction is applied, print a warning to explain the change.
     */
    @Override
    public T parse( Object raw ) {
        String s = raw.toString();
        return valueCodec.parseTomlString( this, s, s );
    }
    
    /** Writes this field's value to file. */
    @Override
    public void writeValue( CrustTomlWriter writer, CharacterOutput output ) {
        writer.writeLine( TomlHelper.toLiteral( valueCodec.toTomlString( getLocalValue() ) ), output );
    }
    
    /** Writes a field value to the byte buffer; should be the inverse of {@link #deserialize}. */
    @Override
    public void serialize( T value, FriendlyByteBuf buffer ) { buffer.writeUtf( valueCodec.toTomlString( value ) ); }
    
    /** Reads a new field value from the byte buffer; should be the inverse of {@link #serialize}. */
    @Override
    public T deserialize( FriendlyByteBuf buffer ) { return parse( buffer.readUtf() ); }
    
    /** @return This field's gui component provider. */
    @Override
    public IConfigFieldWidgetProvider<T> getWidgetProvider() {
        return new StringFieldWidgetProvider<>( valueCodec::toTomlString, text ->
                text.equalsIgnoreCase( valueCodec.toTomlString(
                        valueCodec.parseTomlString( null, text, text ) ) ) );
    }
}