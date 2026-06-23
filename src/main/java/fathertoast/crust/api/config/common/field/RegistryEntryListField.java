package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.StringListFieldWidgetProvider;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.RegistryEntryList;
import fathertoast.crust.api.util.OnClient;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Represents a config field with a registry entry list value.
 * <p>
 * See also: {@link net.minecraftforge.registries.ForgeRegistries}
 */
@SuppressWarnings( "unused" )
@Deprecated( forRemoval = true )
public class RegistryEntryListField<T> extends GenericField<RegistryEntryList<T>> implements IStringListScreenEditable {
    
    /** Provides a detailed description of how to use registry entry lists. Recommended putting at the top of any file using registry entry lists. */
    public static List<String> verboseDescription() {
        List<String> comment = new ArrayList<>();
        comment.add( "Registry Entry List fields: General format = [ \"namespace:entry_name\", ... ]" );
        comment.add( "  Registry entry lists are arrays of registry keys. Many things in the game, such as blocks or " +
                "potions, are defined by their registry key within a registry. For example, all items are registered " +
                "in the \"minecraft:item\" registry." );
        comment.add( "  An asterisk '*' can be used to match all registry entries/keys belonging to X namespace. For example, 'minecraft:*' will " +
                "match all vanilla entries." );
        comment.add( "  Tags can also be used here. To declare a tag, start with a '#' followed by the rest of the tag path." );
        comment.add( "  Tag example: '#minecraft:oak_logs'" );
        return comment;
    }
    
    /** Creates a new field. */
    public RegistryEntryListField( String key, RegistryEntryList<T> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoFormat( "\"" + ConfigUtil.toString( valueDefault.getRegistry().getRegistryName() ) +
                "\" Registry List", valueDefault, "[ \"namespace:entry_name\", ... ]" ) );
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
        
        if( raw instanceof RegistryEntryList ) {
            try {
                //noinspection unchecked
                value = (RegistryEntryList<T>) raw;
            }
            catch( ClassCastException ex ) {
                ConfigUtil.errorFor( this );
                ConfigUtil.LOG.error( "Attempted to assign registry list of the wrong registry! Falling back to default. Invalid value: {}",
                        raw );
                value = valueDefault;
            }
        }
        else {
            // All the actual loading is done through the objects
            value = new RegistryEntryList<>( this, valueDefault.getRegistry(), TomlHelper.parseStringList( raw ) );
        }
    }
    
    /** @return This field's gui component provider. */
    @Override
    @OnClient
    public IConfigFieldWidgetProvider getWidgetProvider() { return new StringListFieldWidgetProvider<>( this ); }
    
    /** Converts the displayable string list to a field value. */
    @Override // IStringListScreenEditable
    public Object stringListToValue( List<String> value ) {
        return new RegistryEntryList<>( this, valueDefault.getRegistry(), value );
    }
    
    /** @return This field's line validator, or null if any string is allowed. */
    @Override // IStringListScreenEditable
    public Predicate<String> getLineValidator() {
        return null;//TODO
    }
    
    
    // Convenience methods
    
    /** @return The registry this list draws from. */
    public IForgeRegistry<T> getRegistry() { return get().getRegistry(); }
    
    /** @return The entries in this list. Does not account for tag or namespace entries! */
    public Set<T> getEntries() { return get().getEntries(); }
    
    /** @return Returns true if there are no entries in this list. */
    public boolean isEmpty() { return get().isEmpty(); }
    
    /** @return Returns true if the entry is contained in this list. */
    public boolean contains( @Nullable T entry ) { return get().contains( entry ); }
}