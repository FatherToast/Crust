package fathertoast.crust.api.config.common.field;

import com.electronwill.nightconfig.core.io.CharacterOutput;
import fathertoast.crust.api.config.common.file.CrustTomlWriter;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.collection.KeyUsage;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.IFuzzyKeyParser;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents a config field with a single fuzzy key value.
 * Very simple field, just to allow making use of any fuzzy logic you want to.
 * Also, 50% cuter than comparable fields.
 */
@ApiStatus.Experimental
public class FuzzyKeyField<T> extends GenericField<FuzzyKey<T>> {
    
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
                keyParser.getTypeName(), keyParser.getPatterns( keyUsage ), valueDefault.toTomlLiteral( true ) ) );
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
        
        FuzzyKey<T> loadedKey = keyUsage.ifAllowed( keyParser.parseKeyString( this, val, val, false ) );
        value = loadedKey == null ? valueDefault : loadedKey;
    }
    
    /** Writes this field's value to file. */
    @Override
    public void writeValue( CrustTomlWriter writer, CharacterOutput output ) {
        writer.writeLine( TomlHelper.toLiteral( keyParser.toTomlString( value ) ), output );
    }
    
    //    /** @return This field's gui component provider. */ TODO
    //    @Override
    //    public IConfigFieldWidgetProvider getWidgetProvider() { return new StringFieldWidgetProvider( this ); }
}