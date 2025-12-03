package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.collection.FuzzyList;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Boilerplate for fuzzy list fields, but can also be used directly with generic fuzzy lists.
 * Use {@link #entries()} to iterate through the defined list.
 *
 * @param <T> The type of list.
 * @see fathertoast.crust.api.config.common.value.collection.key.IFuzzyKeyParser
 * @see FuzzyValueListField FuzzyValueListField - A similar collection that allows values
 */
@ApiStatus.Experimental
public class FuzzyListField<T, C extends FuzzyList<T>> extends AbstractFuzzyCollectionField<T, FuzzyKey<T>, C> {
    
    /** Creates a new field. */
    public FuzzyListField( String key, C defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoNoDefault( valueDefault.getTypeName() + " List",
                "[ \"key_1\", \"key_2\", \"key_3\", ... ]" ) );
        comment.add( "Key Patterns: " + valueDefault.getKeyPatterns() );
        comment.add( TomlHelper.fieldInfoOnlyDefault( valueDefault ) );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /**
     * @return An iterator over the objects represented by the keys in this list that can be used in an
     * enhanced for loop. The iterator skips over null objects, but it may still return null in some cases.
     */
    public FuzzyList.KeyIterator<T> entries() { return get().entries(); }
}