package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.value.collection.RegistryList;
import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

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
    
    /** Creates a new field. */
    public RegistryListField( String key, RegistryList<T> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /** @return The target registry. */
    public IRegWrapper<T> getRegistry() { return getDefaultValue().getRegistry(); }
}