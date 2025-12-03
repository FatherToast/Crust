package fathertoast.crust.api.config.common.value.collection.key;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

/**
 * Implemented by fuzzy keys that can supply more than one matching object.
 *
 * @param <T> The type to match against and to supply.
 * @see FuzzyKey
 * @see fathertoast.crust.api.config.common.value.collection.FuzzyList
 * @see fathertoast.crust.api.config.common.value.collection.FuzzyValueList
 */
@ApiStatus.Experimental
public interface IMultiKey<T> extends IRandomKey<T> {
    
    /** @return An iterator over all values that match this key, or null if anything goes wrong. */
    @Nullable
    Iterator<T> getValueIterator();
}