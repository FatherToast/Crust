package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.value.collection.BlockStateMap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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
    
    /**
     * Provides a detailed description of how to use this field type.
     * Recommended to put at the top of any file using this field type.
     */
    public static List<String> verboseDescription() {
        final List<String> comment = new ArrayList<>();
        comment.add( "Block State Map fields: General format = [ \"namespace:block_name[property1=value1,...] value1 value2 ...\", ... ]" );
        comment.add( "  Block State Maps are arrays of blocks and partial block states that are linked to one or multiple values." );
        comment.add( "  Blocks are defined by their key in the block registry, usually following the pattern " +
                "'namespace:block_name'." );
        comment.add( "  Which type of value and how many values are linked to each entry varies, " +
                "so make sure to read the field description for details." );
        
        comment.add( "" );
        comment.add( "An asterisk '*' can be used to define a wildcard entry. For example, 'minecraft:*' will " +
                "match all vanilla blocks, and 'minecraft:oak*' will match all vanilla blocks with names that start with 'oak'." );
        
        comment.add( "" );
        comment.add( "Block tags can also be used here. To declare a tag entry, start with a '#' followed by the rest of the tag path." );
        comment.add( "  Tag example: '#minecraft:beehive_inhabitors'." );
        
        comment.add( "" );
        comment.add( "Blacklist entries are supported by this field type. Any entry type (normal, tag, wildcard) can be turned into a blacklist entry " +
                "by appending 'exclude' to the end of it." );
        comment.add( "  Blacklist entries cannot have any values associated with them, so for example the entry 'minecraft:stone exclude' " +
                "is a valid blacklist entry, but 'minecraft:oak_stairs exclude 1.0' is not." );
        
        comment.add( "" );
        comment.add( "A 'default' entry can also be specified to provide default values. To declare a default entry, start with 'default' " +
                "and append the desired default value(s). Note that only ONE default entry can exist in a Block State Map." );
        
        comment.add( "" );
        comment.add( "Entries by default match any block state. The block states to match can be narrowed down " +
                "by specifying properties. The syntax for block state properties is the same as for commands. Any " +
                "properties not specified will match any value. For example, 'minecraft:beehive[honey_level=5]' will " +
                "match any full beehives, regardless of the direction they face." );
        comment.add( "  Note that tags and wildcard entries are not block state sensitive; they only care about the base block!" );
        
        comment.add( "" );
        comment.add( "IMPORTANT: the order of entries in this list matters! Entries are always checked from top to bottom." );
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
    public BlockStateMapField( String key, BlockStateMap<V> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
}