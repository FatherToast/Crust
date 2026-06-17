package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.value.collection.BlockStateWeightedValueList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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
    
    /**
     * Provides a detailed description of how to use this field type.
     * Recommended to put at the top of any file using this field type.
     */
    public static List<String> verboseDescription() {
        final List<String> comment = new ArrayList<>();
        comment.add( "Block State Weighted Value List fields: General format = [ \"weight namespace:block_name[property1=value1,...] value1 value2 ...\", ... ]" );
        comment.add( "  Block State Weighted Value Lists are collections of blocks and partial block states linked to one " +
                "or more values, in addition to weights for random selection." );
        comment.add( "  Blocks are identified by their key in the block registry, usually following the pattern " +
                "'namespace:block_name'." );
        comment.add( "  Which type of values and how many values are linked to each entry varies, " +
                "so make sure to read the field description for details." );
        comment.add( "  Entry-value pairs with higher weight are more likely to be chosen, while pairs with a weight of 0 will " +
                "never be chosen. Weights cannot be negative." );
        
        comment.add( "" );
        comment.add( "  Block tags can be used here. To declare a tag entry, start with a '#' followed by the rest of the tag path. " +
                "For example, '#minecraft:beehive_inhabitors' will pick a random (unweighted) registry key from that tag when chosen." );
        
        comment.add( "" );
        comment.add( "  Wildcard, blacklist, and default entries are not supported by this field type." );
        
        comment.add( "" );
        comment.add( "  Unless specified, entries use the block's default block state. The block state can be refined " +
                "by specifying properties. The syntax for block state properties is the same as for commands. Any " +
                "properties not specified will remain the default value. For example, 'minecraft:beehive[honey_level=5]' " +
                "represents a full beehive facing North." );
        return comment;
    }
    
    /**
     * Inserts a detailed description into the given spec of how to use this field type.
     * Recommended to include either in a README or at the start of each config that contains any field of this type.
     * <br><br>
     * This is NOT shown in the GUI.
     */
    public static void describe( CrustConfigSpec spec ) {
        spec.paddedFileOnlyComment( verboseDescription() );
    }
    
    
    /** Creates a new field. */
    public BlockStateWeightedValueListField( String key, BlockStateWeightedValueList<V> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
}