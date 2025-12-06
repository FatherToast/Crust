package fathertoast.crust.api.config.common.value.collection.key;

import fathertoast.crust.api.config.common.value.ITomlStringValue;
import fathertoast.crust.api.config.common.value.collection.FuzzyWeightedList;
import fathertoast.crust.api.config.common.value.collection.FuzzyWeightedValueList;
import fathertoast.crust.api.util.JavaRandomSource;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * Implemented by fuzzy keys that can also supply a matching object.
 * If the key can match multiple objects, it should return one at random.
 *
 * @param <T> The type to match against and to supply.
 * @see FuzzyKey
 * @see FuzzyWeightedList
 * @see FuzzyWeightedValueList
 */
@ApiStatus.Experimental
public interface IRandomKey<T> extends ITomlStringValue {
    
    /** @return A value that matches this key, or null if anything goes wrong. */
    @Nullable
    T nextValue( RandomSource random );
    
    /** @return A value that matches this key, or null if anything goes wrong. */
    @Nullable
    default T nextValue( Random random ) { return nextValue( JavaRandomSource.of( random ) ); }
}