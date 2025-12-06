package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.value.collection.RegistryValueList;
import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

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
@ApiStatus.Experimental
public class RegistryValueListField<T, V> extends FuzzyValueListField<T, V, RegistryValueList<T, V>> {
    
    /** Creates a new field. */
    public RegistryValueListField( String key, RegistryValueList<T, V> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /** @return The target registry. */
    public IRegWrapper<T> getRegistry() { return getDefaultValue().getRegistry(); }
}