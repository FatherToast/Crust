package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.value.collection.RegistryMap;
import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

/**
 * Represents a config field with a registry map value.
 * Use {@link #get(T)} to retrieve to value for a target object (or null if the object is not mapped).
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
    
    /** Creates a new field. */
    public RegistryMapField( String key, RegistryMap<T, V> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /** @return The target registry. */
    public IRegWrapper<T> getRegistry() { return getDefaultValue().getRegistry(); }
}