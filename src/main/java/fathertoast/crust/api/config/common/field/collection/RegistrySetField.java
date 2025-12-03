package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.value.collection.CrustRegistrySet;
import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

/**
 * Represents a config field with a registry set value.
 * Use {@link #contains(T)} to check if a target object is in the set.
 * <p>
 * All standard registry types are supported: Forge registries, vanilla registries,
 * and the data-driven dynamic registries.
 *
 * @see net.minecraft.core.registries.Registries
 * @see net.minecraftforge.registries.ForgeRegistries
 * @see IRegWrapper
 */
@ApiStatus.Experimental
public class RegistrySetField<T> extends FuzzySetField<T, CrustRegistrySet<T>> {
    
    /** Creates a new field. */
    public RegistrySetField( String key, CrustRegistrySet<T> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /** @return The target registry. */
    public IRegWrapper<T> getRegistry() { return getDefaultValue().getRegistry(); }
}