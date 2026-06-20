package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.value.collection.ItemStackList;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a config field with an item stack list value.
 * Use {@link #entries()} to iterate through the defined list.
 */
@ApiStatus.Experimental
public class ItemStackListField extends FuzzyListField<ItemStack, ItemStackList> {
    
    /**
     * Provides a detailed description of how to use this field type.
     * Recommended to put at the top of any file using this field type.
     */
    public static List<String> verboseDescription() {
        final List<String> comment = new ArrayList<>();
        comment.add( "Item Stack List fields: General format = [ \"namespace:item_name{tag1=value1,...}\", ... ]" );
        comment.add( "  Item Stack Lists are collections of items, which may include data tags." );
        comment.add( "  Items are identified by their key in the item registry, usually following the pattern " +
                "'namespace:item_name'." );
        
        comment.add( "" );
        comment.add( "  Item tags can also be used here. To declare a tag entry, start with a '#' followed by the rest " +
                "of the tag path. For example, '#minecraft:arrows' adds all items from that tag to the list." );
        
        comment.add( "" );
        comment.add( "  Wildcard, blacklist, and default entries are not supported by this field type." );
        
        comment.add( "" );
        comment.add( "  Unless specified, item stacks will have no data tags. The item stack can be customized " +
                "by specifying data tags. The syntax for data tags is the same as for commands." );
        
        comment.add( "" );
        comment.add( "  IMPORTANT: The order of entries in this list matters!" );
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
    public ItemStackListField( String key, ItemStackList defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
}