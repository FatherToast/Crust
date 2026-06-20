package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.value.collection.ItemStackMap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a config field with an item stack map value.
 * Use {@link #get(ItemStack)} to retrieve the value for a target item stack (or null if the item stack is
 * not mapped). If the value type is a number, you may use {@link #rollChance(ItemStack, RandomSource)} to
 * retrieve the value, roll it like a percentage or 1-in-X chance, and get back a pass/fail boolean instead.
 * <p>
 * Allows any value type that has a codec.
 *
 * @param <V> The value type.
 * @see fathertoast.crust.api.config.common.value.collection.value.IValueCodec
 */
@ApiStatus.Experimental
public class ItemStackMapField<V> extends FuzzyMapField<ItemStack, V, ItemStackMap<V>> {
    
    /**
     * Provides a detailed description of how to use this field type.
     * Recommended to put at the top of any file using this field type.
     */
    public static List<String> verboseDescription() {
        final List<String> comment = new ArrayList<>();
        comment.add( "Item Stack Map fields: General format = [ \"namespace:item_name{tag1=value1,...} value1 value2 ...\", ... ]" );
        comment.add( "  Item Stack Maps are collections of items, which may include data tags, linked to one or more values." );
        comment.add( "  Items are identified by their key in the item registry, usually following the pattern " +
                "'namespace:item_name'." );
        comment.add( "  Which type of values and how many values are linked to each entry varies, " +
                "so make sure to read the field description for details." );
        
        comment.add( "" );
        comment.add( "  An asterisk '*' can be used to define a wildcard entry. For example, 'minecraft:*' matches all " +
                "vanilla items, and 'minecraft:iron*' matches all vanilla items with names that start with 'iron'." );
        
        comment.add( "" );
        comment.add( "  Item tags can also be used here. To declare a tag entry, start with a '#' followed by the rest " +
                "of the tag path. For example, '#minecraft:arrows' matches any item in the tag." );
        
        comment.add( "" );
        comment.add( "  Blacklist entries are supported by this field type. Any entry type (normal, tag, wildcard) can " +
                "be turned into a blacklist entry by appending 'exclude' to the end of it. For example, 'minecraft:diamond exclude' " +
                "prevents vanilla diamonds from being matched by any entries below it." );
        comment.add( "  Blacklist entries cannot have any values associated with them, so for example 'minecraft:diamond exclude 1.0' " +
                "would be an invalid entry." );
        
        comment.add( "" );
        comment.add( "  A 'default' entry can also be specified to provide default values. To declare a default entry, " +
                "start with 'default' and append the desired default value(s). Note that only ONE default entry can exist " +
                "in an Item Stack Map and all entries after it will be ignored." );
        
        comment.add( "" );
        comment.add( "  Unless specified, entries match any item stack of the right item. The item stacks to match can " +
                "be narrowed down by specifying data tags. The syntax for data tags is the same as for commands. " +
                "Any data tags not specified will not be checked. For example, 'minecraft:stick{display:{Name:\\\"The " +
                "Holy Stick\\\"}}' matches vanilla sticks with the custom name 'The Holy Stick'." );
        comment.add( "  This field type also supports using data tags without specifying an item. For example, " +
                "'{display:{Name:\\\"Bob\\\"}' matches any item with the custom name 'Bob'." );
        
        comment.add( "" );
        comment.add( "  IMPORTANT: The order of entries in this map matters! Entries are always checked from top to bottom, " +
                "and the first matching entry decides which value is assigned." );
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
    public ItemStackMapField( String key, ItemStackMap<V> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
}