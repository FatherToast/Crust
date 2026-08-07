package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.value.collection.RegistryValueList;
import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a config field with a registry value list value.
 * Use {@link #entries()} to iterate through the defined list of key-value pairs.
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
public class RegistryValueListField<T, V> extends FuzzyValueListField<T, V, RegistryValueList<T, V>> {
    
    /**
     * Provides a detailed description of how to use this field type.
     * Recommended to put at the top of any file using this field type.
     */
    public static List<String> verboseDescription() {
        final List<String> comment = new ArrayList<>();
        comment.add( "Registry Value List fields: General format = [ \"namespace:path value1 value2 ...\", ... ]" );
        comment.add( "  Registry Value Lists are collections of registry keys linked to one or more values." );
        comment.add( "  Many things in the game, such as blocks and potions, are identified by their key within " +
                "a registry. For example, all items are registered in the \"minecraft:item\" registry." );
        comment.add( "  Which type of values and how many values are linked to each entry varies, " +
                "so make sure to read the field description for details." );
        
        comment.add( "" );
        comment.add( "  Tags can be used here, if the registry supports tags. To declare a tag entry, start with a '#' " +
                "followed by the rest of the tag path. For example, '#minecraft:candles' adds all registry keys from " +
                "that tag to the list." );
        
        comment.add( "" );
        comment.add( "  Wildcard, blacklist, and default entries are not supported by this field type." );
        
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
    public RegistryValueListField( String key, RegistryValueList<T, V> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /** @return The target registry. */
    public IRegWrapper<T> getRegistry() { return getDefaultValue().getRegistry(); }
}