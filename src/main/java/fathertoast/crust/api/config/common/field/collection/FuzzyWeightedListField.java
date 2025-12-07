package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.collection.FuzzyWeightedList;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.WeightedKey;
import fathertoast.crust.api.config.common.value.collection.value.IntValueCodec;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Boilerplate for weighted list fields, but can also be used directly with generic weighted lists.
 * Use {@link #next(RandomSource)} to draw a random object, or null if empty or nothing is drawn.
 *
 * @param <T> The type of list.
 * @see fathertoast.crust.api.config.common.value.collection.key.IFuzzyKeyParser
 * @see FuzzyWeightedValueListField WeightedValueListField - A similar collection that allows values
 */
@ApiStatus.Experimental
public class FuzzyWeightedListField<T, C extends FuzzyWeightedList<T>> extends AbstractFuzzyCollectionField<T, WeightedKey<T>, C> {
    
    /** A simple implementation for using generic weighted lists without the extra type parameter. */
    @SuppressWarnings( "unused" )
    @ApiStatus.Experimental
    public static class Generic<T> extends FuzzyWeightedListField<T, FuzzyWeightedList<T>> {
        /** Creates a new field. */
        public Generic( String key, FuzzyWeightedList<T> defaultValue, @Nullable String... description ) {
            super( key, defaultValue, description );
        }
    }
    
    /** Creates a new field. */
    public FuzzyWeightedListField( String key, C defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoNoDefault( valueDefault.getTypeName() + " Weighted List",
                "[ \"weight_1 key_1\", \"weight_2 " + FuzzyKey.NULL_KEY + "\", \"weight_3 key_3\", ... ]" ) );
        comment.add( "Weight Format: " + IntValueCodec.NON_NEGATIVE.getFormat() );
        comment.add( "Key Patterns: \"" + FuzzyKey.NULL_KEY + "\", " + valueDefault.getKeyPatterns() );
        comment.add( TomlHelper.fieldInfoOnlyDefault( valueDefault ) );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /** @return True if this weighted list is enabled (it is non-empty and its total weight is positive). */
    public boolean isEnabled() { return get().isEnabled(); }
    
    /**
     * @return A randomly chosen element from this list, or null if a null entry is selected
     * or if the list is empty or disabled (all weights are 0).
     */
    @Nullable
    public T next( Random random ) { return get().next( random ); }
    
    /**
     * @return A randomly chosen element from this list, or null if a null entry is selected
     * or if the list is empty or disabled (all weights are 0).
     */
    @Nullable
    public T next( RandomSource random ) { return get().next( random ); }
}