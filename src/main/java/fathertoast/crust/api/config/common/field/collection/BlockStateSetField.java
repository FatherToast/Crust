package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.value.collection.BlockStateSet;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

/**
 * Represents a config field with a block state set value.
 * Use {@link #contains(BlockState)} to check if a target block state is in the set.
 */
@ApiStatus.Experimental
public class BlockStateSetField extends FuzzySetField<BlockState, BlockStateSet> {
    
    /** Creates a new field. */
    public BlockStateSetField( String key, BlockStateSet defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
}