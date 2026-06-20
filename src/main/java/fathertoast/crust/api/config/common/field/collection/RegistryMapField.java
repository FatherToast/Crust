package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.value.collection.RegistryMap;
import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a config field with a registry map value.
 * Use {@link #get(T)} to retrieve the value for a target object (or null if the object is not mapped).
 * If the value type is a number, you may use {@link #rollChance(T, RandomSource)} to retrieve the
 * value, roll it like a percentage or 1-in-X chance, and get back a pass/fail boolean instead.
 * <p>
 * Allows any value type that has a codec.
 * <p>
 * All standard registry types are supported: Forge registries, vanilla registries,
 * and the data-driven dynamic registries.
 *
 * @param <T> The type to match against (i.e., the registry type).
 * @param <V> The value type.
 * @see net.minecraftforge.registries.ForgeRegistries
 * @see net.minecraft.core.registries.Registries
 * @see IRegWrapper
 * @see fathertoast.crust.api.config.common.value.collection.value.IValueCodec
 */
@ApiStatus.Experimental
public class RegistryMapField<T, V> extends FuzzyMapField<T, V, RegistryMap<T, V>> {
    
    /**
     * Provides a detailed description of how to use this field type.
     * Recommended to put at the top of any file using this field type.
     */
    public static List<String> verboseDescription() {
        final List<String> comment = new ArrayList<>();
        comment.add( "Registry Map fields: General format = [ \"namespace:path value1 value2 ...\", ... ]" );
        comment.add( "  Registry Maps are collections of registry keys linked to one or more values." );
        comment.add( "  Many things in the game, such as blocks and potions, are identified by their key within " +
                "a registry. For example, all items are registered in the \"minecraft:item\" registry." );
        comment.add( "  Which type of values and how many values are linked to each entry varies, " +
                "so make sure to read the field description for details." );
        
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
        comment.add( "  Blacklist entries cannot have any values associated with them, so for example 'minecraft:badlands exclude 1.0' " +
                "would be an invalid entry." );
        
        comment.add( "" );
        comment.add( "  A 'default' entry can also be specified to provide default values. To declare a default entry, " +
                "start with 'default' and append the desired default value(s). Note that only ONE default entry can exist " +
                "in a Registry Map and all entries after it will be ignored." );
        
        comment.add( "" );
        comment.add( "  IMPORTANT: The order of entries in this map matters! Entries are always checked from top to bottom, " +
                "and the first matching entry decides which value is assigned." );
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
    public RegistryMapField( String key, RegistryMap<T, V> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /** @return The target registry. */
    public IRegWrapper<T> getRegistry() { return getDefaultValue().getRegistry(); }
}