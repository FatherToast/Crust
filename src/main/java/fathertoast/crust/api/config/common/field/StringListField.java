package fathertoast.crust.api.config.common.field;

import com.electronwill.nightconfig.core.io.CharacterOutput;
import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.StringListFieldWidgetProvider;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.CrustTomlWriter;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.util.OnClient;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

/**
 * Represents a config field with a string list value.
 */
@SuppressWarnings( "unused" )
public class StringListField extends AbstractConfigField<List<String>> {
    
    /** The field info type to display; if null we default to "String". */
    @Nullable
    private final String type;
    /** This string field's value validator. */
    @Nullable
    private final Predicate<String> valueValidator;
    
    /** Creates a new field. */
    public StringListField( String key, List<String> defaultValue, @Nullable String... description ) {
        this( key, null, defaultValue, null, description );
    }
    
    /** Creates a new field. */
    public StringListField( String key, List<String> defaultValue, @Nullable Predicate<String> validator, @Nullable String... description ) {
        this( key, null, defaultValue, validator, description );
    }
    
    /** Creates a new field. */
    public StringListField( String key, @Nullable String typeName, List<String> defaultValue, @Nullable String... description ) {
        this( key, typeName, defaultValue, null, description );
    }
    
    /** Creates a new field referred to as something other than a "String" list. */
    public StringListField( String key, @Nullable String typeName, List<String> defaultValue, @Nullable Predicate<String> validator, @Nullable String... description ) {
        super( key, defaultValue, description );
        type = typeName;
        valueValidator = validator;
    }
    
    /** @return The type name to use for this field. */
    public String getType() { return type == null ? "String" : type; }
    
    /**
     * @return This string field's value validator. Strings that the validator test returns 'true' for are valid.
     * If the validator is null, all strings are valid.
     */
    @Nullable
    public Predicate<String> getValidator() { return valueValidator; }
    
    /** @return Returns true if there are no entries in this string list. */
    public boolean isEmpty() { return get().isEmpty(); }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoFormat( getType() + " List", getDefaultValue(),
                "[ \"" + ConfigUtil.toLowerCaseNoSpaces( getType() ) + "1\", \"" +
                        ConfigUtil.toLowerCaseNoSpaces( getType() ) + "2\", ... ]" ) );
    }
    
    /**
     * @return Reads a value from the given value or raw toml. If anything goes wrong, correct it at the lowest level
     * possible.
     * <p>
     * For example, a missing or unreadable value should return the default value, while an out-of-range value should be
     * adjusted to the nearest in-range value. If any value correction is applied, print a warning to explain the change.
     */
    @Override
    public List<String> parse( Object raw ) { return TomlHelper.parseStringList( raw ); }
    
    /** Writes this field's value to file. */
    @Override
    public void writeValue( CrustTomlWriter writer, CharacterOutput output ) { writer.writeArray( getLocalValue(), output ); }
    
    /** Writes a field value to the byte buffer; should be the inverse of {@link #deserialize}. */
    @Override
    public void serialize( List<String> value, FriendlyByteBuf buffer ) { buffer.writeCollection( value, FriendlyByteBuf::writeUtf ); }
    
    /** Reads a new field value from the byte buffer; should be the inverse of {@link #serialize}. */
    @Override
    public List<String> deserialize( FriendlyByteBuf buffer ) { return buffer.readList( FriendlyByteBuf::readUtf ); }
    
    /** @return This field's gui component provider. */
    @Override
    @OnClient
    public IConfigFieldWidgetProvider<List<String>> getWidgetProvider() {
        return new StringListFieldWidgetProvider<>( stringList -> stringList, getValidator() );
    }
}