package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.value.collection.BlockStateWeightedList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a config field with a block state weighted list value.
 * Use {@link #next(RandomSource)} to draw a random block state, or null if empty or nothing is drawn.
 */
public class BlockStateWeightedListField extends FuzzyWeightedListField<BlockState, BlockStateWeightedList> {
    
    /**
     * Provides a detailed description of how to use this field type.
     * Recommended to put at the top of any file using this field type.
     */
    public static List<String> verboseDescription() {
        final List<String> comment = new ArrayList<>();
        comment.add( "Block State Weighted List fields: General format = [ \"weight namespace:block_name[property1=value1,...]\", ... ]" );
        comment.add( "  Block State Weighted Lists are collections of blocks and partial block states linked to weights " +
                "for random selection." );
        comment.add( "  Blocks are identified by their key in the block registry, usually following the pattern " +
                "'namespace:block_name'." );
        comment.add( "  Entries with higher weight are more likely to be chosen, while entries with a weight of 0 will " +
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
    public BlockStateWeightedListField( String key, BlockStateWeightedList defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
}