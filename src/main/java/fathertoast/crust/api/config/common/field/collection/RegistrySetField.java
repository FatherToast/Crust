package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.value.collection.RegistrySet;
import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a config field with a registry set value.
 * Use {@link #contains(T)} to check if a target object is in the set.
 * <p>
 * All standard registry types are supported: Forge registries, vanilla registries,
 * and the data-driven dynamic registries.
 *
 * @param <T> The type to match against (i.e., the registry type).
 * @see net.minecraftforge.registries.ForgeRegistries
 * @see net.minecraft.core.registries.Registries
 * @see IRegWrapper
 */
@ApiStatus.Experimental
public class RegistrySetField<T> extends FuzzySetField<T, RegistrySet<T>> {
    
    /**
     * Provides a detailed description of how to use this field type.
     * Recommended to put at the top of any file using this field type.
     */
    public static List<String> verboseDescription() {
        final List<String> comment = new ArrayList<>();
        comment.add( "Registry Set fields: General format = [ \"namespace:path\", ... ]" );
        comment.add( "  Registry Sets are sets of registry keys used exclusively for matching." );
        comment.add( "  Many things in the game, such as blocks or potions, are defined by their registry key within a registry." );
        comment.add( "  For example, all items are registered in the \"minecraft:item\" registry." );
        
        comment.add( "" );
        comment.add( "An asterisk '*' can be used to define a wildcard entry. For example, 'minecraft:*' will " +
                "match all vanilla entries, and 'minecraft:jeb*' will match all vanilla entries with names that start with 'jeb'." );
        
        comment.add( "" );
        comment.add( "Tags can also be used here. To declare a tag entry, start with a '#' followed by the rest of the tag path." );
        comment.add( "  Tag example: '#minecraft:is_cave'." );
        
        comment.add( "" );
        comment.add( "Blacklist entries are supported by this field type. Any entry type (normal, tag, wildcard) can be turned into a blacklist entry " +
                "by appending 'exclude' to the end of it." );
        comment.add( "  Blacklist entries cannot have any values associated with them, so for example the entry 'minecraft:badlands exclude' " +
                "is a valid blacklist entry, but 'minecraft:desert exclude 1.0' is not." );
        
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
    public RegistrySetField( String key, RegistrySet<T> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /** @return The target registry. */
    public IRegWrapper<T> getRegistry() { return getDefaultValue().getRegistry(); }
}