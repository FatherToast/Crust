package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.collection.FuzzyValueList;
import fathertoast.crust.api.config.common.value.collection.FuzzyWeightedValueList;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.value.IntValueCodec;
import fathertoast.crust.api.config.common.value.collection.value.WeightedEntry;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Boilerplate for weighted value list fields, but can also be used directly with generic weighted value lists.
 * Use {@link #next(RandomSource)} to draw a random key-value pair, or null if empty or nothing is drawn.
 *
 * @param <T> The type of list.
 * @param <V> The value type.
 * @see fathertoast.crust.api.config.common.value.collection.key.IFuzzyKeyParser
 * @see fathertoast.crust.api.config.common.value.collection.value.IValueCodec
 * @see FuzzyWeightedListField WeightedListField - A similar collection that does not allow values
 */
@ApiStatus.Experimental
public class FuzzyWeightedValueListField<T, V, C extends FuzzyWeightedValueList<T, V>> extends AbstractFuzzyCollectionField<T, WeightedEntry<T, V>, C> {
    
    /** A simple implementation for using generic weighted value lists without the extra type parameter. */
    @SuppressWarnings( "unused" )
    @ApiStatus.Experimental
    public static class Generic<T, V> extends FuzzyWeightedValueListField<T, V, FuzzyWeightedValueList<T, V>> {
        /** Creates a new field. */
        public Generic( String key, FuzzyWeightedValueList<T, V> defaultValue, @Nullable String... description ) {
            super( key, defaultValue, description );
        }
    }
    
    /** Creates a new field. */
    public FuzzyWeightedValueListField( String key, C defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoNoDefault( valueDefault.getTypeName() + " Map",
                "[ \"weight_1 key_1 value_1\", \"weight_2 " + FuzzyKey.NULL_KEY + "\", \"weight_3 key_3 value_3\", ... ]" ) );
        comment.add( "Weight Format: " + IntValueCodec.WEIGHT.getFormat() );
        comment.add( "Key Patterns: \"" + FuzzyKey.NULL_KEY + "\", " + valueDefault.getKeyPatterns() );
        comment.add( "Value Format: " + valueDefault.getValueFormat() );
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
    public FuzzyValueList.Pair<T, V> next( Random random ) { return get().next( random ); }
    
    /**
     * @return A randomly chosen element from this list, or null if a null entry is selected
     * or if the list is empty or disabled (all weights are 0).
     */
    @Nullable
    public FuzzyValueList.Pair<T, V> next( RandomSource random ) { return get().next( random ); }
}