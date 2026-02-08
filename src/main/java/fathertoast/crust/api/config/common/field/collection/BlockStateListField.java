package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.value.collection.BlockStateList;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

/**
 * Represents a config field with a block state list value.
 * Use {@link #entries()} to iterate through the defined list.
 */
@ApiStatus.Experimental
public class BlockStateListField extends FuzzyListField<BlockState, BlockStateList> {
    
    /** Creates a new field. */
    public BlockStateListField( String key, BlockStateList defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
}