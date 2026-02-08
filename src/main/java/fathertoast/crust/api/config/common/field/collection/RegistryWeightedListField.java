package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.value.collection.RegistryWeightedList;
import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

/**
 * Represents a config field with a registry weighted list value.
 * Use {@link #next(RandomSource)} to draw a random object, or null if empty or nothing is drawn.
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
public class RegistryWeightedListField<T> extends FuzzyWeightedListField<T, RegistryWeightedList<T>> {
    
    /** Creates a new field. */
    public RegistryWeightedListField( String key, RegistryWeightedList<T> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /** @return The target registry. */
    public IRegWrapper<T> getRegistry() { return getDefaultValue().getRegistry(); }
}