package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.value.collection.RegistryList;
import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a config field with a registry list value.
 * Use {@link #entries()} to iterate through the defined list.
 * <p>
 * All standard registry types are supported: Forge registries, vanilla registries,
 * and the data-driven dynamic registries.
 *
 * @param <T> The type of list (i.e., the registry type).
 * @see net.minecraftforge.registries.ForgeRegistries
 * @see net.minecraft.core.registries.Registries
 * @see IRegWrapper
 */
@ApiStatus.Experimental
public class RegistryListField<T> extends FuzzyListField<T, RegistryList<T>> {
    
    /**
     * Provides a detailed description of how to use this field type.
     * Recommended putting at the top of any file using block lists.
     */
    public static List<String> verboseDescription() {
        final List<String> comment = new ArrayList<>();
        comment.add( "Registry List fields: General format = [ \"namespace:entry_name\", ... ]" );
        comment.add( "  Registry Lists are arrays of registry keys. Many things in the game, such as blocks or " +
                "potions, are defined by their registry key within a registry. For example, all items are registered " +
                "in the \"minecraft:item\" registry." );
        
        comment.add( "" );
        comment.add( "Tags can be used here. To declare a tag entry, start with a '#' followed by the rest of the tag path." );
        comment.add( "  Tag example: '#minecraft:candles'" );
        
        comment.add( "" );
        comment.add( "Wildcard entries are not supported by this field type." );
        
        comment.add( "" );
        comment.add( "Blacklist entries are not supported by this field type." );
        
        comment.add( "" );
        comment.add( "This field type does not support having a default entry." );
        
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
    public RegistryListField( String key, RegistryList<T> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /** @return The target registry. */
    public IRegWrapper<T> getRegistry() { return getDefaultValue().getRegistry(); }
}