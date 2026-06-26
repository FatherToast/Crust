package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.value.collection.BlockStateSet;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a config field with a block state set value.
 * Use {@link #contains(BlockState)} to check if a target block state is in the set.
 */
@ApiStatus.Experimental
public class BlockStateSetField extends FuzzySetField<BlockState, BlockStateSet> {
    
    /**
     * Provides a detailed description of how to use this field type.
     * Recommended to put at the top of any file using this field type.
     */
    public static List<String> verboseDescription() {
        final List<String> comment = new ArrayList<>();
        comment.add( "Block State Set fields: General format = [ \"namespace:block_name[property1=value1,...]\", ... ]" );
        comment.add( "  Block State Sets are collections of blocks and partial block states used exclusively for matching." );
        comment.add( "  Blocks are identified by their key in the block registry, usually following the pattern " +
                "'namespace:block_name'." );
        
        comment.add( "" );
        comment.add( "  An asterisk '*' can be used to define a wildcard entry. For example, 'minecraft:*' matches all " +
                "vanilla blocks, and 'minecraft:oak*' matches all vanilla blocks with names that start with 'oak'." );
        
        comment.add( "" );
        comment.add( "  Block tags can also be used here. To declare a tag entry, start with a '#' followed by the rest " +
                "of the tag path. For example, '#minecraft:beehive_inhabitors' matches any block in the tag." );
        
        comment.add( "" );
        comment.add( "  Blacklist entries are supported by this field type. Any entry type (normal, tag, wildcard) can " +
                "be turned into a blacklist entry by appending 'exclude' to the end of it. For example, 'minecraft:stone exclude' " +
                "prevents vanilla stone blocks from being matched by any entries below it." );
        
        comment.add( "" );
        comment.add( "  A 'default' entry can be added to effectively turn this field type into a blacklist" );
        comment.add( "  This makes it so ALL values get matches, and exceptions can be added by specifying blacklist entries." );
        
        comment.add( "" );
        comment.add( "  Unless specified, entries match any block state. The block states to match can be narrowed down " +
                "by specifying properties. The syntax for block state properties is the same as for commands. Any " +
                "properties not specified will match any value. For example, 'minecraft:beehive[honey_level=5]' matches " +
                "any full beehive regardless of the direction it faces." );
        comment.add( "  This field type also supports using block state properties without specifying a block. For " +
                "example, '[facing=south]' matches any block that is facing South." );
        
        comment.add( "" );
        comment.add( "  IMPORTANT: The order of entries in this set matters! Entries are always checked from top to bottom." );
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
    public BlockStateSetField( String key, BlockStateSet defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
}