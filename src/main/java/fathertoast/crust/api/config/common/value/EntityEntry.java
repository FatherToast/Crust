package fathertoast.crust.api.config.common.value;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * One entity-value entry in an entity list. Uses a 'lazy' implementation so the entity type registry is
 * not polled until this entry is actually used.
 */

@Deprecated( forRemoval = true )
@SuppressWarnings( "unused" )
public class EntityEntry {
    
    /** The field containing this entry. We save a reference to help improve error/warning reports. */
    private final AbstractConfigField FIELD;
    
    /** The registry key for this entry's entity type. */
    public final ResourceLocation ENTITY_KEY;
    /** True if this should check for instanceof the entity class (as opposed to equals). */
    public final boolean EXTEND;
    /** The values given to this entry. Null for comparison objects. */
    public final double[] VALUES;
    
    /** The entity type this entry is defined for. */
    private EntityType<? extends Entity> entityType;
    /** The class this entry is defined for. This is not assigned until a world has been loaded. */
    Class<? extends Entity> entityClass;
    
    /** Creates an entry used to compare entity classes internally with the entries in an entity list. */
    EntityEntry( Entity entity ) {
        FIELD = null;
        EXTEND = false;
        VALUES = null;
        ENTITY_KEY = ForgeRegistries.ENTITY_TYPES.getKey( entity.getType() );
        entityType = entity.getType();
        entityClass = entity.getClass();
    }
    
    /** Creates an extendable entry with the specified values. Used for creating default configs. */
    public EntityEntry( EntityType<? extends Entity> type, double... values ) {
        this( type, true, values );
    }
    
    /** Creates an entry with the specified values. Used for creating default configs. */
    public EntityEntry( EntityType<? extends Entity> type, boolean extend, double... values ) {
        this( null, Objects.requireNonNull( ForgeRegistries.ENTITY_TYPES.getKey( type ) ), extend, values );
        entityType = type;
    }
    
    /** Creates an entry with the specified values. */
    public EntityEntry( @Nullable AbstractConfigField field, ResourceLocation regKey, boolean extend, double... values ) {
        FIELD = field;
        ENTITY_KEY = regKey;
        EXTEND = extend;
        VALUES = values;
    }
    
    /** @return Loads the entity type from registry. Returns true if successful. */
    private boolean validate() {
        if( entityType != null ) return true;
        
        if( !ForgeRegistries.ENTITY_TYPES.containsKey( ENTITY_KEY ) ) {
            ConfigUtil.warnFor( FIELD );
            ConfigUtil.LOG.warn( "Entity entry has invalid entity key ({})! Ignoring. Entry: {}", ENTITY_KEY, this );
            return false;
        }
        entityType = ForgeRegistries.ENTITY_TYPES.getValue( ENTITY_KEY );
        return true;
    }
    
    /** Called on this entry before using it to check if the entity class has been determined, and loads the class if it has not been. */
    void checkClass( Level level ) {
        if( validate() && entityType != null && entityClass == null ) {
            try {
                final Entity entity = entityType.create( level );
                if( entity != null ) {
                    entityClass = entity.getClass();
                    entity.discard();
                }
            }
            catch( Exception ex ) {
                ConfigUtil.warnFor( FIELD );
                ConfigUtil.LOG.warn( "Failed to load class of entity type {}! Entry: {}", entityType, this );
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * @return Returns true if the given entity description is contained within this one (is more specific).
     * <p>
     * This operates under the assumption that there will not be multiple non-extendable
     * entries for the same class in a list.
     */
    public boolean contains( EntityEntry entry ) {
        if( !validate() ) return false;
        
        if( entry.entityType == null ) return false;
        // Same entity, but non-extendable is more specific
        if( entityClass == entry.entityClass ) return !entry.EXTEND;
        // Extendable entry, check if the other is for a subclass
        if( EXTEND ) return entityClass.isAssignableFrom( entry.entityClass );
        // Non-extendable entries cannot contain other entries
        return false;
    }
    
    /**
     * @return The string representation of this entity list entry, as it would appear in a config file.
     * <p>
     * Format is "~registry_key value0 value1 ...", the ~ prefix is optional.
     */
    @Override
    public String toString() {
        // Start with the entity type registry key
        StringBuilder str = new StringBuilder( ENTITY_KEY.toString() );
        // Insert "specific" prefix if not extendable
        if( !EXTEND ) {
            str.insert( 0, '~' );
        }
        // Append values array
        //noinspection RedundantLengthCheck
        if( VALUES != null && VALUES.length > 0 ) {
            for( double value : VALUES ) {
                str.append( ' ' ).append( value );
            }
        }
        return str.toString();
    }
}