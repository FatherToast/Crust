package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.collection.FuzzyMap;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.value.FuzzyEntry;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Boilerplate for fuzzy map fields, but can also be used directly with generic fuzzy maps.
 *
 * @param <T> The type to match against.
 * @param <V> The value type.
 * @see fathertoast.crust.api.config.common.value.collection.key.IFuzzyKeyParser
 * @see fathertoast.crust.api.config.common.value.collection.value.IValueCodec
 * @see FuzzySetField
 */
@ApiStatus.Experimental
public class FuzzyMapField<T, V, F extends FuzzyMap<T, V>> extends FuzzySetField<T, F> {
    
    /** Creates a new field. */
    public FuzzyMapField( String key, F defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoNoDefault( valueDefault.getTypeName() + " Map",
                "[ \"" + FuzzyKey.keyWithValue( "key_1", "value_1" ) + "\", \"" +
                        FuzzyKey.keyWithValue( "key_2", FuzzyKey.BLACKLIST_VALUE ) + "\", \"" +
                        FuzzyKey.keyWithValue( "key_3", "value_3" ) + "\", ... ]" ) );
        comment.add( "Key Patterns: " + valueDefault.getKeyPatterns() );
        comment.add( "Value Format: " + valueDefault.getValueFormat() );
        comment.add( TomlHelper.fieldInfoOnlyDefault( valueDefault ) );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /** @return The value for the given target, or null if the target is not contained in this map. */
    @Nullable
    public V get( T target ) { return get().get( target ); }
    
    /** @return The first matching entry, or null if no match was found or the match was a blacklist entry. */
    @Nullable
    public FuzzyEntry<T, V> getEntry( T target ) { return get().getEntry( target ); }
}