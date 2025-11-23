package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.common.value.collection.CrustRegistryMap;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a config field with a registry map value.
 * <p>
 * See also: {@link net.minecraftforge.registries.ForgeRegistries}
 */
@ApiStatus.Experimental
public class RegistryMapField<T, V> extends FuzzyMapField<T, V, CrustRegistryMap<T, V>> {
    
    /** Creates a new field. */
    public RegistryMapField( String key, CrustRegistryMap<T, V> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /** @return The target registry. */
    public IForgeRegistry<T> getRegistry() { return getDefaultValue().getRegistry(); }
}