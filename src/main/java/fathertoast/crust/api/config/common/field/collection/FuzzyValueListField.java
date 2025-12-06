package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.collection.FuzzyValueList;
import fathertoast.crust.api.config.common.value.collection.value.FuzzyEntry;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Boilerplate for fuzzy value list fields, but can also be used directly with generic fuzzy value lists.
 * Use {@link #entries()} to iterate through the defined list of key-value pairs.
 *
 * @param <T> The type of list.
 * @param <V> The value type.
 * @see fathertoast.crust.api.config.common.value.collection.key.IFuzzyKeyParser
 * @see fathertoast.crust.api.config.common.value.collection.value.IValueCodec
 * @see FuzzyListField FuzzyListField - A similar collection that does not allow values
 */
@ApiStatus.Experimental
public class FuzzyValueListField<T, V, C extends FuzzyValueList<T, V>> extends AbstractFuzzyCollectionField<T, FuzzyEntry<T, V>, C> {
    
    /** A simple implementation for using generic fuzzy value lists without the extra type parameter. */
    @SuppressWarnings( "unused" )
    @ApiStatus.Experimental
    public static class Generic<T, V> extends FuzzyValueListField<T, V, FuzzyValueList<T, V>> {
        /** Creates a new field. */
        public Generic( String key, FuzzyValueList<T, V> defaultValue, @Nullable String... description ) {
            super( key, defaultValue, description );
        }
    }
    
    /** Creates a new field. */
    public FuzzyValueListField( String key, C defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoNoDefault( valueDefault.getTypeName() + " Value List",
                "[ \"key_1 value_1\", \"key_2 value_2\", ... ]" ) );
        comment.add( "Key Patterns: " + valueDefault.getKeyPatterns() );
        comment.add( "Value Format: " + valueDefault.getValueFormat() );
        comment.add( TomlHelper.fieldInfoOnlyDefault( valueDefault ) );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /**
     * @return An iterator over the key-value pairs represented by the keys in this list that can be used in
     * an enhanced for loop. The iterator skips over null objects, but it may still return null in some cases.
     */
    public FuzzyValueList.KeyValueIterator<T, V> entries() { return get().entries(); }
}