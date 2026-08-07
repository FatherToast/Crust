package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.client.gui.widget.provider.EnumFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.util.OnClient;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents a config field with an enum value.
 * <p>
 * Just like {@link fathertoast.crust.api.config.common.value.collection.value.EnumValueCodec},
 * this uses {@link Enum#name()} for its string representations and is not case-sensitive. Therefore,
 * you should avoid vanilla enums (due to obfuscation) and any enums with constants that share the
 * same name when ignoring case.
 */
@SuppressWarnings( "unused" )
public class EnumField<T extends Enum<T>> extends AbstractConfigField<T> {
    
    /** Valid field values. */
    private final T[] valuesValid;
    
    /** Creates a new field that accepts any enum value. */
    public EnumField( String key, T defaultValue, @Nullable String... description ) {
        this( key, defaultValue, defaultValue.getDeclaringClass().getEnumConstants(), description );
    }
    
    /** Creates a new field that accepts the specified set of enum values. */
    public EnumField( String key, T defaultValue, T[] validValues, @Nullable String... description ) {
        super( key, defaultValue, description );
        if( validValues.length == 0 )
            throw new IllegalArgumentException( "Cannot create field with no valid values! Invalid field: " + key );
        valuesValid = validValues;
    }
    
    /** @return An array of all values allowed by this field. */
    public T[] validValues() { return valuesValid; }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoValidValues( "Enum", getDefaultValue(), (Object[]) validValues() ) );
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
        // Try to directly assign the value
        try { //noinspection unchecked
            return (T) raw;
        }
        catch( ClassCastException ex ) {
            // Not directly assignable, try to parse
        }
        
        // Parse the value
        T value = raw instanceof String s ? parseName( s ) : null;
        if( value == null ) {
            // Value cannot be parsed to this field
            ConfigUtil.warnFor( this );
            ConfigUtil.LOG.warn( "Invalid enum value! Falling back to default ({}). Invalid value: {}",
                    getDefaultValue(), raw );
            return getDefaultValue();
        }
        return value;
    }
    
    /** @return Attempts to parse the string literal as one of the valid values for this field and returns it, or null if invalid. */
    @Nullable
    public T parseName( String name ) {
        for( T val : valuesValid ) {
            if( val.name().equalsIgnoreCase( name ) ) return val;
        }
        return null;
    }
    
    /** Writes a field value to the byte buffer; should be the inverse of {@link #deserialize}. */
    @Override
    public void serialize( T value, FriendlyByteBuf buffer ) { buffer.writeEnum( value ); }
    
    /** Reads a new field value from the byte buffer; should be the inverse of {@link #serialize}. */
    @Override
    public T deserialize( FriendlyByteBuf buffer ) { return buffer.readEnum( getDefaultValue().getDeclaringClass() ); }
    
    /** @return This field's gui component provider. */
    @Override
    @OnClient
    public IConfigFieldWidgetProvider<T> getWidgetProvider() { return new EnumFieldWidgetProvider<>( this ); }
}