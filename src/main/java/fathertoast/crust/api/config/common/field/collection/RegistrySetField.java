package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.value.collection.RegistrySet;
import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;

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
public class RegistrySetField<T> extends FuzzySetField<T, RegistrySet<T>> {
    
    /**
     * Provides a detailed description of how to use this field type.
     * Recommended to put at the top of any file using this field type.
     */
    public static List<String> verboseDescription() {
        final List<String> comment = new ArrayList<>();
        comment.add( "Registry Set fields: General format = [ \"namespace:path\", ... ]" );
        comment.add( "  Registry Sets are collections of registry keys used exclusively for matching." );
        comment.add( "  Many things in the game, such as blocks and potions, are identified by their key within " +
                "a registry. For example, all items are registered in the \"minecraft:item\" registry." );
        
        comment.add( "" );
        comment.add( "  An asterisk '*' can be used to define a wildcard entry. For example, 'minecraft:*' will " +
                "match all vanilla keys, and 'minecraft:jeb*' will match all vanilla keys with names that start with 'jeb'." );
        
        comment.add( "" );
        comment.add( "  Tags can be used here, if the registry supports tags. To declare a tag entry, start with a '#' " +
                "followed by the rest of the tag path. For example, '#minecraft:candles' adds all registry keys from " +
                "that tag to the list." );
        
        comment.add( "" );
        comment.add( "  Blacklist entries are supported by this field type. Any entry type (normal, tag, wildcard) can " +
                "be turned into a blacklist entry by appending 'exclude' to the end of it. For example, 'minecraft:badlands exclude' " +
                "prevents the vanilla 'badlands' key from being matched by any entries below it." );
        
        comment.add( "" );
        comment.add( "  A 'default' entry can be added to effectively turn this field type into a blacklist" );
        comment.add( "  This makes it so ALL values get matches, and exceptions can be added by specifying blacklist entries." );
        
        comment.add( "" );
        comment.add( "  IMPORTANT: The order of entries in this list matters! Entries are always checked from top to bottom." );
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