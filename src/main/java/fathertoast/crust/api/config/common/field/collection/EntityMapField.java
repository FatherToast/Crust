package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.value.collection.EntityMap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

/**
 * Represents a config field with an entity map value.
 * Use {@link #get(Entity)} to retrieve to value for a target entity (or null if the entity is not mapped).
 * If the value type is a number, you may use {@link #rollChance(Entity, RandomSource)} to retrieve the
 * value, roll it like a percentage or 1-in-X chance, and get back a pass/fail boolean instead.
 * <p>
 * Allows any value type that has a codec.
 *
 * @param <V> The value type.
 * @see fathertoast.crust.api.config.common.value.collection.value.IValueCodec
 */
@ApiStatus.Experimental
public class EntityMapField<V> extends FuzzyMapField<Entity, V, EntityMap<V>> {
    
    /** Creates a new field. */
    public EntityMapField( String key, EntityMap<V> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
}