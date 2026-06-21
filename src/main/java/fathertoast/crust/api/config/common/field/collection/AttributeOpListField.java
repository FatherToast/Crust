package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.ConfigDrivenAttributeSupplier;
import fathertoast.crust.api.config.common.value.collection.AttributeOpList;
import fathertoast.crust.api.config.common.value.collection.value.OperationStats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a config field with an attribute operation list value.
 * Use {@link #entries()} to iterate through the defined list of operations, or just directly apply the operations
 * to an entity ({@link #apply(LivingEntity)}) or attribute supplier ({@link #build(AttributeSupplier.Builder)}).
 *
 * @see fathertoast.crust.api.config.common.value.ConfigDrivenAttributeSupplier
 */
@ApiStatus.Experimental
public class AttributeOpListField extends FuzzyValueListField<Attribute, OperationStats, AttributeOpList> {
    
    /**
     * Provides a detailed description of how to use this field type.
     * Recommended to put at the top of any file using this field type.
     */
    public static List<String> verboseDescription() {
        final List<String> comment = new ArrayList<>();
        comment.add( "Attribute Operation List fields: General format = [ \"namespace:attribute_name operation value\", ... ]" );
        comment.add( "  Attribute Operation Lists are collections of attributes linked to operators and operands." );
        comment.add( "  The operator can be any of five symbols; = (assign), * (multiply), / (divide), + (add), or - (subtract). " +
                "The assign operator sets the attribute's value, while the others modify the attribute value by arithmetic." );
        
        comment.add( "" );
        comment.add( "  Tags can technically be used here if any attribute tags are defined, but this is not recommended. " +
                "To declare a tag entry, start with a '#' followed by the rest of the tag path. For example, " +
                "'#modid:tag_name' applies the same operation to each attribute in the tag." );
        
        comment.add( "" );
        comment.add( "  IMPORTANT: The order of entries in this list matters! Operations are performed from top to bottom." );
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
    
    /** The linked attribute supplier, if any. You must have a separate field per supplier you want to link. */
    public ConfigDrivenAttributeSupplier linkedAttributeSupplier;
    
    /** Creates a new field. */
    public AttributeOpListField( String key, AttributeOpList defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoNoDefault( "Attribute List",
                "[ \"namespace:attribute_name operation\", ... ]" ) );
        comment.add( "Attribute Patterns: " + valueDefault.getKeyPatterns() );
        comment.add( "Operation Format: " + valueDefault.getValueFormat() );
        comment.add( TomlHelper.fieldInfoOnlyDefault( valueDefault ) );
    }
    
    /**
     * Loads this field's value from the given value or raw toml. If anything goes wrong, correct it at the lowest level possible.
     * <p>
     * For example, a missing value should be set to the default, while an out-of-range value should be adjusted to the
     * nearest in-range value and print a warning explaining the change.
     */
    @Override
    public void load( @Nullable Object raw ) {
        super.load( raw );
        if( linkedAttributeSupplier != null ) linkedAttributeSupplier.invalidate();
    }
    
    
    // ---- Convenience Methods ---- //
    
    /**
     * Call this to build your attribute suppliers instead of their own #build() method when assigning entity types'
     * attribute suppliers in {@link net.minecraftforge.event.entity.EntityAttributeCreationEvent} to make entity
     * attributes more responsive to runtime config changes.
     *
     * @return A new config-driven attribute supplier generated from a baseline attribute supplier builder.
     */
    public ConfigDrivenAttributeSupplier build( AttributeSupplier.Builder builder ) {
        return new ConfigDrivenAttributeSupplier( this, builder );
    }
    
    /**
     * Applies all attribute operations in this list to the entity attribute builder. You can use this when assigning
     * entity types' attribute suppliers in {@link net.minecraftforge.event.entity.EntityAttributeCreationEvent}.
     * <p>
     * Attribute suppliers made this way are more stable than the config-driven attribute suppliers due to using only
     * vanilla logic, but require a game restart to reflect any changes to the config.
     *
     * @return The builder, for convenience in building.
     */
    public AttributeSupplier.Builder apply( AttributeSupplier.Builder builder ) { return get().apply( builder ); }
    
    /** Applies all attribute operations in this list to the entity. */
    public void apply( LivingEntity entity ) { get().apply( entity ); }
}