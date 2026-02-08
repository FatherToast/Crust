package fathertoast.crust.api.config.common.value.collection.key;

import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Implemented by fuzzy keys that can also be mapped directly to the object they match (one to one).
 *
 * @param <T> The type to match against and to supply.
 * @see FuzzyKey
 * @see fathertoast.crust.api.config.common.value.collection.FuzzyList
 * @see fathertoast.crust.api.config.common.value.collection.FuzzyValueList
 */
@ApiStatus.Experimental
public interface IReverseKey<T> extends IRandomKey<T> {
    
    /** @return The value that matches this key, or null if anything goes wrong. */
    @Nullable
    T asValue();
    
    /** @return A value that matches this key, or null if anything goes wrong. */
    @Nullable
    default T nextValue( RandomSource random ) { return asValue(); }
}