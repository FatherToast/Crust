package fathertoast.crust.api.config.common.field;

import com.electronwill.nightconfig.core.io.CharacterOutput;
import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.StringFieldWidgetProvider;
import fathertoast.crust.api.config.common.file.CrustTomlWriter;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.collection.KeyUsage;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.IFuzzyKeyParser;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents a config field with a single fuzzy key value.
 * Very simple field, just to allow making use of any fuzzy logic you want to.
 * Also, 50% cuter than comparable fields.
 */
@SuppressWarnings( "unused" )
public class FuzzyKeyField<T> extends AbstractConfigField<FuzzyKey<T>> {
    
    protected final IFuzzyKeyParser<T> keyParser;
    protected final KeyUsage keyUsage;
    
    /** Creates a new field. */
    public FuzzyKeyField( String key, FuzzyKey<T> defaultValue, IFuzzyKeyParser<T> parser, @Nullable String... description ) {
        this( key, defaultValue, parser, KeyUsage.MATCH, description );
    }
    
    /** Creates a new field. */
    public FuzzyKeyField( String key, FuzzyKey<T> defaultValue, IFuzzyKeyParser<T> parser, KeyUsage usage, @Nullable String... description ) {
        super( key, defaultValue, description );
        keyParser = parser;
        keyUsage = usage;
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( String.format( "<%s Key> Key Patterns: { %s }, Default: %s",
                keyParser.getTypeName(), keyParser.getPatterns( keyUsage ), getDefaultValue().toTomlLiteral( true ) ) );
    }
    
    /**
     * @return Reads a value from the given value or raw toml. If anything goes wrong, correct it at the lowest level
     * possible.
     * <p>
     * For example, a missing or unreadable value should return the default value, while an out-of-range value should be
     * adjusted to the nearest in-range value. If any value correction is applied, print a warning to explain the change.
     */
    @Override
    public FuzzyKey<T> parse( Object raw ) {
        String s = raw.toString();
        FuzzyKey<T> loadedKey = keyUsage.ifAllowed( keyParser.parseKeyString( this, s, s, false ) );
        return loadedKey == null ? getDefaultValue() : // Warnings handled decently enough by parser
                loadedKey;
    }
    
    /** Writes this field's value to file. */
    @Override
    public void writeValue( CrustTomlWriter writer, CharacterOutput output ) {
        writer.writeLine( TomlHelper.toLiteral( keyParser.toTomlString( getLocalValue() ) ), output );
    }
    
    /** Writes a field value to the byte buffer; should be the inverse of {@link #deserialize}. */
    @Override
    public void serialize( FuzzyKey<T> value, FriendlyByteBuf buffer ) { buffer.writeUtf( keyParser.toTomlString( value ) ); }
    
    /** Reads a new field value from the byte buffer; should be the inverse of {@link #serialize}. */
    @Override
    public FuzzyKey<T> deserialize( FriendlyByteBuf buffer ) { return parse( buffer.readUtf() ); }
    
    /** @return This field's gui component provider. */
    @Override
    public IConfigFieldWidgetProvider<FuzzyKey<T>> getWidgetProvider() {
        return new StringFieldWidgetProvider<>(
                text -> keyUsage.ifAllowed( keyParser.parseKeyString(
                        null, text, text, false ) ) != null );
    }
}