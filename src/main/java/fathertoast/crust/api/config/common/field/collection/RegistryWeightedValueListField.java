package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.value.collection.RegistryWeightedValueList;
import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

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
    
    /** Creates a new field. */
    public RegistryWeightedValueListField( String key, RegistryWeightedValueList<T, V> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /** @return The target registry. */
    public IRegWrapper<T> getRegistry() { return getDefaultValue().getRegistry(); }
}