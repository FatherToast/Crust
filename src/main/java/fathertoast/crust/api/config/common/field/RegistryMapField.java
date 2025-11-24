package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.common.value.collection.CrustRegistryMap;
import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

/**
 * Represents a config field with a registry map value.
 *
 * @see net.minecraft.core.registries.Registries
 * @see net.minecraftforge.registries.ForgeRegistries
 * @see IRegWrapper
 * @see fathertoast.crust.api.config.common.value.collection.value.IValueCodec
 */
@ApiStatus.Experimental
public class RegistryMapField<T, V> extends FuzzyMapField<T, V, CrustRegistryMap<T, V>> {
    
    /** Creates a new field. */
    public RegistryMapField( String key, CrustRegistryMap<T, V> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /** @return The target registry. */
    public IRegWrapper<T> getRegistry() { return getDefaultValue().getRegistry(); }
}