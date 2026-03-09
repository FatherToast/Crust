package fathertoast.crust.api.config.common.value;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Tag entry for entity lists. Provides values for any entity type contained in a given tag.
 */

@Deprecated( forRemoval = true )
public class EntityTagEntry {
    
    /** The field containing this entry. We save a reference to help improve error/warning reports. */
    private final AbstractConfigField FIELD;
    
    /** The entity type tag this entry holds. */
    public final TagKey<EntityType<?>> TAG;
    
    /** The values given to this entry. Null for comparison objects. */
    public final double[] VALUES;
    
    
    /** Creates an entry with the specified values. Used for creating default configs. */
    public EntityTagEntry( ResourceLocation tagLocation, double... values ) {
        this( null, new TagKey<>( Registries.ENTITY_TYPE, tagLocation ), values );
    }
    
    /** Creates an entry with the specified values. Used for creating default configs. */
    public EntityTagEntry( TagKey<EntityType<?>> tagKey, double... values ) {
        this( null, tagKey, values );
    }
    
    /** Creates an entry with the specified values. */
    public EntityTagEntry( @Nullable AbstractConfigField field, TagKey<EntityType<?>> tagKey, double... values ) {
        Objects.requireNonNull( tagKey );
        
        FIELD = field;
        TAG = tagKey;
        VALUES = values;
    }
    
    
    /**
     * @return Returns true if the given entry's entity type belongs in this entry's entity tag.
     */
    public boolean contains( EntityType<?> entityType ) {
        return entityType.is( TAG );
    }
    
    /**
     * @return The string representation of this entity list tag entry, as it would appear in a config file.
     * <p>
     * Format is "#namespace:path value0 value1 ...".
     */
    @Override
    public String toString() {
        // Start with the tag
        StringBuilder str = new StringBuilder( ConfigUtil.toString( TAG ) );
        
        // Append values array
        if( VALUES != null ) {
            for( double value : VALUES ) {
                str.append( ' ' ).append( value );
            }
        }
        return str.toString();
    }
}
