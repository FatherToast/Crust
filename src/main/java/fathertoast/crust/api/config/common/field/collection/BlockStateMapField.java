package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.value.collection.BlockStateMap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

/**
 * Represents a config field with a block state map value.
 * Use {@link #get(BlockState)} to retrieve to value for a target block state (or null if the block state is
 * not mapped). If the value type is a number, you may use {@link #rollChance(BlockState, RandomSource)} to
 * retrieve the value, roll it like a percentage or 1-in-X chance, and get back a pass/fail boolean instead.
 * <p>
 * Allows any value type that has a codec.
 *
 * @param <V> The value type.
 * @see fathertoast.crust.api.config.common.value.collection.value.IValueCodec
 */
@ApiStatus.Experimental
public class BlockStateMapField<V> extends FuzzyMapField<BlockState, V, BlockStateMap<V>> {
    
    /** Creates a new field. */
    public BlockStateMapField( String key, BlockStateMap<V> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
}