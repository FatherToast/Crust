package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.value.collection.RegistryWeightedList;
import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a config field with a registry weighted list value.
 * Use {@link #next(RandomSource)} to draw a random object, or null if empty or nothing is drawn.
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
public class RegistryWeightedListField<T> extends FuzzyWeightedListField<T, RegistryWeightedList<T>> {
    
    /**
     * Provides a detailed description of how to use this field type.
     * Recommended to put at the top of any file using this field type.
     */
    public static List<String> verboseDescription() {
        final List<String> comment = new ArrayList<>();
        comment.add( "Registry Weighted List fields: General format = [ \"weight namespace:path\", ... ]" );
        comment.add( "  Registry Weighted Lists are collections of registry keys linked to weights for random selection." );
        comment.add( "  Many things in the game, such as blocks and potions, are identified by their key within " +
                "a registry. For example, all items are registered in the \"minecraft:item\" registry." );
        comment.add( "  Entries with higher weight are more likely to be chosen, while entries with a weight of 0 will " +
                "never be chosen. Weights cannot be negative." );
        
        comment.add( "" );
        comment.add( "  Tags can be used here, if the registry supports tags. To declare a tag entry, start with a '#' " +
                "followed by the rest of the tag path. For example, '#minecraft:candles' will pick a random (unweighted) " +
                "registry key from that tag when chosen." );
        
        comment.add( "" );
        comment.add( "  Wildcard, blacklist, and default entries are not supported by this field type." );
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
    public RegistryWeightedListField( String key, RegistryWeightedList<T> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /** @return The target registry. */
    public IRegWrapper<T> getRegistry() { return getDefaultValue().getRegistry(); }
}