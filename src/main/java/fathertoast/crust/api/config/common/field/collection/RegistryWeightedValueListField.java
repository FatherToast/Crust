package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.value.collection.RegistryWeightedValueList;
import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a config field with a registry weighted value list value.
 * Use {@link #next(RandomSource)} to draw a random key-value pair, or null if empty or nothing is drawn.
 * <p>
 * Allows any value type that has a codec.
 * <p>
 * All standard registry types are supported: Forge registries, vanilla registries,
 * and the data-driven dynamic registries.
 *
 * @param <T> The type of list (i.e., the registry type).
 * @param <V> The value type.
 * @see net.minecraftforge.registries.ForgeRegistries
 * @see net.minecraft.core.registries.Registries
 * @see IRegWrapper
 * @see fathertoast.crust.api.config.common.value.collection.value.IValueCodec
 */
@ApiStatus.Experimental
public class RegistryWeightedValueListField<T, V> extends FuzzyWeightedValueListField<T, V, RegistryWeightedValueList<T, V>> {
    
    /**
     * Provides a detailed description of how to use this field type.
     * Recommended to put at the top of any file using this field type.
     */
    public static List<String> verboseDescription() {
        final List<String> comment = new ArrayList<>();
        comment.add( "Registry Weighted Value List fields: General format = [ \"weight namespace:path value1 value2 ...\", ... ]" );
        comment.add( "  Registry Weighted Value Lists are arrays of weights linked to a registry key-value pair." );
        comment.add( "  Many things in the game, such as blocks or potions, are defined by their registry key within a registry." );
        comment.add( "  For example, all items are registered in the \"minecraft:item\" registry." );
        comment.add( "  Which type of value and how many values are linked to each entry varies, " +
                "so make sure to read the field description for details." );
        comment.add( "  An entry's weight can not be less than 0; it must be positive." );
        
        comment.add( "" );
        comment.add( "Tags can be used here. To declare a tag entry, start with a '#' followed by the rest of the tag path." );
        comment.add( "  Tag example: '#minecraft:is_cave'" );
        
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
    public RegistryWeightedValueListField( String key, RegistryWeightedValueList<T, V> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /** @return The target registry. */
    public IRegWrapper<T> getRegistry() { return getDefaultValue().getRegistry(); }
}