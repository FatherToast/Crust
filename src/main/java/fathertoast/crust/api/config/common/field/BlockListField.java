package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.BlockList;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a config field with a block list value.
 */
@SuppressWarnings( "unused" )
public class BlockListField extends GenericField<BlockList> {
    
    /** Provides a detailed description of how to use block lists. Recommended putting at the top of any file using block lists. */
    public static List<String> verboseDescription() {
        List<String> comment = new ArrayList<>();
        comment.add( "Block List fields: General format = [ \"namespace:block_name[property1=value1,...]\", ... ]" );
        comment.add( "  Block lists are arrays of blocks and partial block states." );
        comment.add( "  Blocks are defined by their key in the block registry, usually following the pattern " +
                "'namespace:block_name'." );
        comment.add( "  An asterisk '*' can be used to match multiple blocks. For example, 'minecraft:*' will match " +
                "all vanilla blocks." );
        comment.add( "  List entries by default match any block state. The block states to match can be narrowed down " +
                "by specifying properties. The syntax for block state properties is the same as for commands. Any " +
                "properties not specified will match any value. For example, 'minecraft:beehive[honey_level=5]' will " +
                "match any full beehives, regardless of the direction they face." );
        return comment;
    }
    
    /** Creates a new field. */
    public BlockListField( String key, BlockList defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoFormat( "Block List", valueDefault, "[ \"namespace:block_name[properties]\", ... ]" ) );
    }
    
    /**
     * Loads this field's value from the given value or raw toml. If anything goes wrong, correct it at the lowest level possible.
     * <p>
     * For example, a missing value should be set to the default, while an out-of-range value should be adjusted to the
     * nearest in-range value and print a warning explaining the change.
     */
    @Override
    public void load( @Nullable Object raw ) {
        if( raw == null ) {
            value = valueDefault;
            return;
        }
        
        if( raw instanceof BlockList ) {
            value = (BlockList) raw;
        }
        else {
            // All the actual loading is done through the objects
            value = new BlockList( this, TomlHelper.parseStringList( raw ) );
        }
    }
    
    
    // Convenience methods
    
    /** @return Returns true if there are no entries in this block list. */
    public boolean isEmpty() { return get().isEmpty(); }
    
    /** @return Returns true if the block is contained in this list. */
    public boolean matches( BlockState blockState ) { return get().matches( blockState ); }
    
    /**
     * Represents two block list fields, a blacklist and a whitelist, combined into one.
     */
    public static class Combined {
        
        /** The whitelist. To match, the entry must be present here. */
        public final BlockListField WHITELIST;
        /** The blacklist. Entries present here are ignored entirely. */
        public final BlockListField BLACKLIST;
        
        /** Links two lists together as blacklist and whitelist. */
        public Combined( BlockListField whitelist, BlockListField blacklist ) {
            WHITELIST = whitelist;
            BLACKLIST = blacklist;
        }
        
        /** @return Returns true if there are no entries in this block list. */
        public boolean isEmpty() { return WHITELIST.get().isEmpty(); }
        
        /** @return Returns true if the block is contained in this list. */
        public boolean matches( BlockState blockState ) {
            return !BLACKLIST.get().matches( blockState ) && WHITELIST.get().matches( blockState );
        }
    }
}