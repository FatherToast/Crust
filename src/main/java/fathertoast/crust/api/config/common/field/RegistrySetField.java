package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.common.value.collection.CrustRegistrySet;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a config field with a registry set value.
 * <p>
 * See also: {@link net.minecraftforge.registries.ForgeRegistries}
 */
@ApiStatus.Experimental
public class RegistrySetField<T> extends FuzzySetField<T, CrustRegistrySet<T>> {
    
    /** Provides a detailed description of how to use registry entry lists. Recommended putting at the top of any file using registry entry lists. */
    public static List<String> verboseDescription() {
        List<String> comment = new ArrayList<>(); // TODO Convert from old reg entry list to reg set
        //        comment.add( "Registry Set fields: General format = [ \"namespace:path\", ... ]" );
        //        comment.add( "  Registry entry lists are arrays of registry keys. Many things in the game, such as blocks or " +
        //                "potions, are defined by their registry key within a registry. For example, all items are registered " +
        //                "in the \"minecraft:item\" registry." );
        //        comment.add( "  An asterisk '*' can be used to match all registry entries/keys belonging to X namespace. For " +
        //                "example, 'minecraft:*' will match all vanilla entries." );
        //        comment.add( "  Tags can also be used here. To declare a tag, start with a '#' followed by the rest of the tag path." );
        //        comment.add( "  Tag example: '#minecraft:oak_logs'" );
        return comment;
    }
    
    
    /** Creates a new field. */
    public RegistrySetField( String key, CrustRegistrySet<T> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /** @return The target registry. */
    public IForgeRegistry<T> getRegistry() { return getDefaultValue().getRegistry(); }
}