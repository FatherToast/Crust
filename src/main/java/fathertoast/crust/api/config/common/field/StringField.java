package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.StringFieldWidgetProvider;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.util.OnClient;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

/**
 * Represents a config field with a string value.
 */
@SuppressWarnings( "unused" )
public class StringField extends AbstractConfigField<String> {
    
    /** The field info type to display; if null we default to "String". */
    @Nullable
    private final String type;
    /** This string field's value validator. */
    @Nullable
    private final Predicate<String> valueValidator;
    
    /** Creates a new field, optionally with a value validator. */
    public StringField( String key, String defaultValue, @Nullable String... description ) {
        this( key, null, defaultValue, null, description );
    }
    
    /** Creates a new field, optionally with a value validator. */
    public StringField( String key, String defaultValue, @Nullable Predicate<String> validator, @Nullable String... description ) {
        this( key, null, defaultValue, validator, description );
    }
    
    /** Creates a new field with no value validator. */
    public StringField( String key, @Nullable String typeName, String defaultValue, @Nullable Predicate<String> validator, @Nullable String... description ) {
        super( key, defaultValue, description );
        type = typeName;
        valueValidator = validator;
        
        // Sanity checks
        if( validator != null && !validator.test( defaultValue ) ) {
            throw new IllegalArgumentException( "Default value is invalid! Invalid field: " + getKey() );
        }
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
        comment.add( TomlHelper.fieldInfoNoHelp( getType(), getDefaultValue() ) );
    }
    
    /**
     * @return Reads a value from the given value or raw toml. If anything goes wrong, correct it at the lowest level
     * possible.
     * <p>
     * For example, a missing or unreadable value should return the default value, while an out-of-range value should be
     * adjusted to the nearest in-range value. If any value correction is applied, print a warning to explain the change.
     */
    @Override
    public String parse( Object raw ) {
        String value = raw.toString();
        if( getValidator() != null && !getValidator().test( value ) ) {
            ConfigUtil.warnFor( this );
            ConfigUtil.LOG.warn( "Invalid string! Falling back to default ({}). Invalid value: {}",
                    getDefaultValue(), raw );
            return getDefaultValue();
        }
        return value;
    }
    
    /** Writes a field value to the byte buffer; should be the inverse of {@link #deserialize}. */
    @Override
    public void serialize( String value, FriendlyByteBuf buffer ) { buffer.writeUtf( value ); }
    
    /** Reads a new field value from the byte buffer; should be the inverse of {@link #serialize}. */
    @Override
    public String deserialize( FriendlyByteBuf buffer ) { return buffer.readUtf(); }
    
    /** @return This field's gui component provider. */
    @Override
    @OnClient
    public IConfigFieldWidgetProvider<String> getWidgetProvider() { return new StringFieldWidgetProvider<>( getValidator() ); }
}