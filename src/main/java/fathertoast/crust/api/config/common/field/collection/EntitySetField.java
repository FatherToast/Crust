package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.value.collection.EntitySet;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

/**
 * Represents a config field with an entity set value.
 * Use {@link #contains(Entity)} to check if a target entity is in the set.
 */
@ApiStatus.Experimental
public class EntitySetField extends FuzzySetField<Entity, EntitySet> {
    
    /** Creates a new field. */
    public EntitySetField( String key, EntitySet defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
}