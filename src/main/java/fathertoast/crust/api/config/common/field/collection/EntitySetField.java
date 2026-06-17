package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.value.collection.EntitySet;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a config field with an entity set value.
 * Use {@link #contains(Entity)} to check if a target entity is in the set.
 */
@ApiStatus.Experimental
public class EntitySetField extends FuzzySetField<Entity, EntitySet> {
    
    /**
     * Provides a detailed description of how to use this field type.
     * Recommended putting at the top of any file using block lists.
     */
    public static List<String> verboseDescription() {
        final List<String> comment = new ArrayList<>();
        comment.add( "Entity Set fields: General format = [ \"namespace:entity_type\", ... ]" );
        comment.add( "  Entity Sets are collections of entities used exclusively for matching." );
        comment.add( "  Entity types are identified by their key in the entity registry, usually following the pattern " +
                "'namespace:entity_name'." );
        
        comment.add( "" );
        comment.add( "  An asterisk '*' can be used to define a wildcard entry. For example, 'minecraft:*' will " +
                "match all vanilla entities, and 'minecraft:ender*' will match all vanilla entities with names that start " +
                "with 'ender', like enderman or endermite." );
        
        comment.add( "" );
        comment.add( "  Entity type tags can also be used here. To declare a tag entry, start with a '#' followed by the rest " +
                "of the tag path. For example, '#minecraft:skeletons' matches any entity type in the tag, like skeleton or stray." );
        
        comment.add( "" );
        comment.add( "  Blacklist entries are supported by this field type. Any entry type (normal, tag, wildcard, extends) " +
                "can be turned into a blacklist entry by appending 'exclude' to the end of it. For example, " +
                "'minecraft:creeper exclude' prevents the vanilla creepers from being matched by any entries below it." );
        
        comment.add( "" );
        comment.add( "  Default entries are not supported by this field type." );
        
        comment.add( "" );
        comment.add( "  Lastly, Entity Sets allow a special type of entry called 'extends' entries." );
        comment.add( "  If you are unfamiliar with Java or modding Minecraft, the following explanation may not make a lot of sense." );
        comment.add( "  This is a pretty niche but very powerful entry type that matches the target entity as well as all " +
                "other entities whose entity class inherits the entity class of the target entity." );
        comment.add( "  For example, the key '~minecraft:skeleton' will match normal skeletons as well as all other entities that " +
                "extend the Skeleton class." );
        comment.add( "  It is also possible to jump upwards in the class hierarchy if desired." );
        comment.add( "  For example, the key '~minecraft:skeleton' will match the vanilla skeleton entity, but not wither skeletons or strays, " +
                "since those entities extend the AbstractSkeleton instead of the Skeleton class." );
        comment.add( "  To jump up one superclass to AbstractSkeleton we can rewrite the key like this: '~1^minecraft:skeleton'." );
        comment.add( "  The number before the '^' symbol determines how many times to jump upwards in the hierarchy." );
        comment.add( "  The jump number cannot be lower than 0, but can safely be any positive number, " +
                "as it is not possible to jump past the base Entity class." );
        
        comment.add( "" );
        comment.add( "  IMPORTANT: The order of entries in this list matters! Entries are always checked from top to bottom." );
        return comment;
    }
    
    /**
     * Inserts a detailed description into the given spec of how to use this field type.
     * Recommended to include either in a README or at the start of each config that contains any field of this type.
     * <br><br>
     * This is NOT shown in the GUI.
     */
    public static void describe( CrustConfigSpec spec ) {
        spec.paddedFileOnlyComment( verboseDescription() );
    }
    
    
    /** Creates a new field. */
    public EntitySetField( String key, EntitySet defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
}