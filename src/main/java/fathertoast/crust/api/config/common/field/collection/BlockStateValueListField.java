package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.value.collection.BlockStateValueList;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

/**
 * Represents a config field with a block state value list value.
 * Use {@link #entries()} to iterate through the defined list of key-value pairs.
 * <p>
 * Allows any value type that has a codec.
 *
 * @param <V> The value type.
 * @see fathertoast.crust.api.config.common.value.collection.value.IValueCodec
 */
@ApiStatus.Experimental
public class BlockStateValueListField<V> extends FuzzyValueListField<BlockState, V, BlockStateValueList<V>> {
    
    /** Creates a new field. */
    public BlockStateValueListField( String key, BlockStateValueList<V> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
}