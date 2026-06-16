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
        comment.add( "  Entity Sets are sets of entities exclusively for matching." );
        comment.add( "  Entity types are defined by their key in the entity registry, usually following the pattern " +
                "'namespace:entity_name'." );
        
        comment.add( "" );
        comment.add( "An asterisk '*' can be used to define a wildcard entry. For example, 'minecraft:*' will " +
                "match all vanilla entities, and 'minecraft:ender*' will match all vanilla entities with names that start with 'ender', like enderman or endermite." );
        
        comment.add( "" );
        comment.add( "Entity type tags can also be used here. To declare a tag entry, start with a '#' followed by the rest of the tag path." );
        comment.add( "  Tag example: '#minecraft:skeletons'." );
        
        comment.add( "" );
        comment.add( "Blacklist entries are supported by this field type. Any entry type (normal, tag, wildcard) can be turned into a blacklist entry " +
                "by appending 'exclude' to the end of it." );
        comment.add( "  Blacklist entries cannot have any values associated with them, so for example the entry 'minecraft:creeper exclude' " +
                "is a valid blacklist entry, but 'minecraft:creeper exclude 1.0' is not." );
        
        comment.add( "" );
        comment.add( "This field type does not support having a default entry." );
        
        comment.add( "" );
        comment.add( "Entries by default match any block state. The block states to match can be narrowed down " +
                "by specifying properties. The syntax for block state properties is the same as for commands. Any " +
                "properties not specified will match any value. For example, 'minecraft:beehive[honey_level=5]' will " +
                "match any full beehives, regardless of the direction they face." );
        comment.add( "  Note that tags and wildcard entries are not block state sensitive; they only care about the base block!" );
        
        comment.add( "" );
        comment.add( "IMPORTANT: the order of entries in this list matters! Entries are always checked from top to bottom." );
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