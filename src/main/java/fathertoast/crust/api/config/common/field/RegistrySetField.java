package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.StringListFieldWidgetProvider;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.collection.CrustRegistrySet;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Represents a config field with a registry set value.
 * <p>
 * See also: {@link net.minecraftforge.registries.ForgeRegistries}
 */
@SuppressWarnings( "unused" )
public class RegistrySetField<T> extends GenericField<CrustRegistrySet<T>> implements IStringListScreenEditable {
    
    /** Provides a detailed description of how to use registry entry lists. Recommended putting at the top of any file using registry entry lists. */
    public static List<String> verboseDescription() {
        List<String> comment = new ArrayList<>(); // TODO Convert from old reg entry list to reg set
        comment.add( "Registry Entry List fields: General format = [ \"namespace:path\", ... ]" );
        comment.add( "  Registry entry lists are arrays of registry keys. Many things in the game, such as blocks or " +
                "potions, are defined by their registry key within a registry. For example, all items are registered " +
                "in the \"minecraft:item\" registry." );
        comment.add( "  An asterisk '*' can be used to match all registry entries/keys belonging to X namespace. For " +
                "example, 'minecraft:*' will match all vanilla entries." );
        comment.add( "  Tags can also be used here. To declare a tag, start with a '#' followed by the rest of the tag path." );
        comment.add( "  Tag example: '#minecraft:oak_logs'" );
        return comment;
    }
    
    /** Creates a new field. */
    public RegistrySetField( String key, CrustRegistrySet<T> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoFormat( "\"" + ConfigUtil.toString( valueDefault.getRegistry()
                        .getRegistryName() ) + "\" Registry Set", valueDefault,
                "[ \"namespace:path\", \"namespace:partial_path*\", \"#namespace:tag_path\", ... ]" ) );
    }
    
    /**
     * Loads this field's value from the given value or raw toml. If anything goes wrong, correct it at the lowest level possible.
     * <p>
     * For example, a missing value should be set to the default, while an out-of-range value should be adjusted to the
     * nearest in-range value and print a warning explaining the change.
     */
    @Override
    public void load( @Nullable Object raw ) {
        if( raw == null ) {
            value = valueDefault;
            return;
        }
        
        if( raw instanceof CrustRegistrySet ) {
            try {
                //noinspection unchecked
                value = (CrustRegistrySet<T>) raw;
            }
            catch( ClassCastException ex ) {
                ConfigUtil.errorFor( this );
                ConfigUtil.LOG.error( "Attempted to assign registry set of the wrong registry! Falling back to default. Invalid value: {}",
                        raw );
                value = valueDefault;
            }
        }
        else {
            // All the actual loading is done through the object
            value = new CrustRegistrySet<>( valueDefault.getRegistry() );
            value.load( this, TomlHelper.parseStringList( raw ) );
        }
    }
    
    /** @return This field's gui component provider. */
    @Override
    public IConfigFieldWidgetProvider getWidgetProvider() { return new StringListFieldWidgetProvider<>( this ); }
    
    /** Converts the displayable string list to a field value. */
    @Override // IStringListScreenEditable
    public Object stringListToValue( List<String> value ) {
        CrustRegistrySet<T> regSet = new CrustRegistrySet<>( valueDefault.getRegistry() );
        regSet.load( this, value );
        return regSet;
    }
    
    /** @return This field's line validator, or null if any string is allowed. */
    @Override // IStringListScreenEditable
    public Predicate<String> getLineValidator() {//TODO
        return null;//( line ) -> CrustRegistrySet.tryParse( getRegistry(), null, line, line, false ) != null;
    }
    
    
    // ---- Convenience Methods ---- //
    
    /** @return The target registry. */
    public IForgeRegistry<T> getRegistry() { return getDefaultValue().getRegistry(); }
    
    /** @return An unmodifiable list of objects that represent this field's value when written to file. */
    public List<FuzzyKey<T>> getList() { return get().getList(); }
    
    /** @return The number of elements. */
    public int size() { return get().size(); }
    
    /** @return True if this contains no elements. */
    public boolean isEmpty() { return get().isEmpty(); }
    
    /** @return True if the given target is contained within this set. */
    public boolean contains( T target ) { return get().contains( target ); }
    
    /** @return The best match key, or null if no match was found. */
    @Nullable
    public FuzzyKey<T> get( T target ) { return get().get( target ); }
}