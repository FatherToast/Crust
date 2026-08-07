package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.value.collection.ItemStackWeightedList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a config field with an item stack weighted list value.
 * Use {@link #next(RandomSource)} to draw a random item stack, or null if empty or nothing is drawn.
 */
public class ItemStackWeightedListField extends FuzzyWeightedListField<ItemStack, ItemStackWeightedList> {
    
    /**
     * Provides a detailed description of how to use this field type.
     * Recommended to put at the top of any file using this field type.
     */
    public static List<String> verboseDescription() {
        final List<String> comment = new ArrayList<>();
        comment.add( "Item Stack Weighted List fields: General format = [ \"weight namespace:item_name{tag1=value1,...}\", ... ]" );
        comment.add( "  Item Stack Weighted Lists are collections of items, which may include data tags, linked to weights " +
                "for random selection." );
        comment.add( "  Items are identified by their key in the item registry, usually following the pattern " +
                "'namespace:item_name'." );
        comment.add( "  Entries with higher weight are more likely to be chosen, while entries with a weight of 0 will " +
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
    public ItemStackWeightedListField( String key, ItemStackWeightedList defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
}