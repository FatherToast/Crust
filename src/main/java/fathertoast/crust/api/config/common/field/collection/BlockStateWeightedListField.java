package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.value.collection.BlockStateWeightedList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

/**
 * Represents a config field with a block state weighted list value.
 * Use {@link #next(RandomSource)} to draw a random block state, or null if empty or nothing is drawn.
 */
@ApiStatus.Experimental
public class BlockStateWeightedListField extends FuzzyWeightedListField<BlockState, BlockStateWeightedList> {
    
    /** Creates a new field. */
    public BlockStateWeightedListField( String key, BlockStateWeightedList defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
}