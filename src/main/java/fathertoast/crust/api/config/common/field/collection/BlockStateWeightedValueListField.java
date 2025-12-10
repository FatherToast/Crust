package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.value.collection.BlockStateWeightedValueList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

/**
 * Represents a config field with a block state weighted value list value.
 * Use {@link #next(RandomSource)} to draw a random key-value pair, or null if empty or nothing is drawn.
 * <p>
 * Allows any value type that has a codec.
 *
 * @param <V> The value type.
 * @see fathertoast.crust.api.config.common.value.collection.value.IValueCodec
 */
@ApiStatus.Experimental
public class BlockStateWeightedValueListField<V> extends FuzzyWeightedValueListField<BlockState, V, BlockStateWeightedValueList<V>> {
    
    /** Creates a new field. */
    public BlockStateWeightedValueListField( String key, BlockStateWeightedValueList<V> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
}