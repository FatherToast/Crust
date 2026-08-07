package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.value.collection.ItemStackWeightedValueList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a config field with an item stack weighted value list value.
 * Use {@link #next(RandomSource)} to draw a random key-value pair, or null if empty or nothing is drawn.
 * <p>
 * Allows any value type that has a codec.
 *
 * @param <V> The value type.
 * @see fathertoast.crust.api.config.common.value.collection.value.IValueCodec
 */
public class ItemStackWeightedValueListField<V> extends FuzzyWeightedValueListField<ItemStack, V, ItemStackWeightedValueList<V>> {
    
    /**
     * Provides a detailed description of how to use this field type.
     * Recommended to put at the top of any file using this field type.
     */
    public static List<String> verboseDescription() {
        final List<String> comment = new ArrayList<>();
        comment.add( "Item Stack Weighted Value List fields: General format = [ \"weight namespace:item_name{tag1=value1,...} value1 value2 ...\", ... ]" );
        comment.add( "  Item Stack Weighted Value Lists are collections of items, which may include data tags, linked to one " +
                "or more values, in addition to weights for random selection." );
        comment.add( "  Items are identified by their key in the item registry, usually following the pattern " +
                "'namespace:item_name'." );
        comment.add( "  Which type of values and how many values are linked to each entry varies, " +
                "so make sure to read the field description for details." );
        comment.add( "  Entry-value pairs with higher weight are more likely to be chosen, while pairs with a weight of 0 will " +
                "never be chosen. Weights cannot be negative." );
        
        comment.add( "" );
        comment.add( "  Item tags can also be used here. To declare a tag entry, start with a '#' followed by the rest of the " +
                "tag path. For example, '#minecraft:arrows' will pick a random (unweighted) registry key from that tag when chosen." );
        
        comment.add( "" );
        comment.add( "  Wildcard, blacklist, and default entries are not supported by this field type." );
        
        comment.add( "" );
        comment.add( "  Unless specified, item stacks will have no data tags. The item stack can be customized " +
                "by specifying data tags. The syntax for data tags is the same as for commands." );
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
    public ItemStackWeightedValueListField( String key, ItemStackWeightedValueList<V> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
}