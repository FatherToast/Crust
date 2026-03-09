package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.LazyRegistryEntryList;
import fathertoast.crust.api.config.common.value.RegistryEntryList;

import javax.annotation.Nullable;

/**
 * Represents a config field with a lazy registry entry list value. The provided default value can be a regular registry
 * entry list; if you use the varargs constructor they will be functionally identical anyway.
 * <p>
 * See also: {@link net.minecraftforge.registries.ForgeRegistries}
 */
@SuppressWarnings( "unused" )
@Deprecated( forRemoval = true )
public class LazyRegistryEntryListField<T> extends RegistryEntryListField<T> {
    
    /** Creates a new field. */
    public LazyRegistryEntryListField( String key, RegistryEntryList<T> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
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
        
        if( raw instanceof LazyRegistryEntryList ) {
            try {
                //noinspection unchecked
                value = (LazyRegistryEntryList<T>) raw;
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
            value = new LazyRegistryEntryList<>( this, valueDefault.getRegistry(), TomlHelper.parseStringList( raw ) );
        }
    }
}