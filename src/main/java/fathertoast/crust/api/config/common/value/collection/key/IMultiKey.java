package fathertoast.crust.api.config.common.value.collection.key;

import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.function.Function;

/**
 * Implemented by fuzzy keys that can supply more than one matching object.
 *
 * @param <T> The type to match against and to supply.
 * @see FuzzyKey
 * @see fathertoast.crust.api.config.common.value.collection.FuzzyList
 * @see fathertoast.crust.api.config.common.value.collection.FuzzyValueList
 */
public interface IMultiKey<T> extends IRandomKey<T> {
    
    /** @return An iterator over all values that match this key, or null if anything goes wrong. */
    @Nullable
    Iterator<T> getValueIterator();
    
    
    /** A simple iterator implementation that converts an iterator by performing a function for each element. */
    record ConverterIterator<F, T>( Iterator<F> iterator, Function<F, T> converter ) implements Iterator<T> {
        @Override
        public boolean hasNext() { return iterator().hasNext(); }
        
        @Override
        public T next() { return converter().apply( iterator().next() ); }
    }
}