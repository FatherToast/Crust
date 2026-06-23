package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.client.gui.widget.provider.BooleanFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.util.OnClient;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

/**
 * Represents a config field with a boolean value.
 */
@SuppressWarnings( "unused" )
public class BooleanField extends AbstractConfigField implements Supplier<Boolean> {
    
    /** The default field value. */
    private final boolean valueDefault;
    
    /** The underlying field value. */
    private boolean value;
    
    /** Creates a new field. */
    public BooleanField( String key, boolean defaultValue, @Nullable String... description ) {
        super( key, description );
        valueDefault = defaultValue;
    }
    
    /** @return Returns the config field's value. */
    @Override
    public Boolean get() { return value; }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoValidValues( "Boolean", valueDefault, true, false ) );
    }
    
    /**
     * Loads this field's value from the given value or raw toml. If anything goes wrong, correct it at the lowest level possible.
     * <p>
     * For example, a missing value should be set to the default, while an out-of-range value should be adjusted to the
     * nearest in-range value and print a warning explaining the change.
     */
    @Override
    public void load( @Nullable Object raw ) {
        Boolean newValue = TomlHelper.readAsBoolean( this, raw );
        if( newValue == null ) {
            if( raw != null ) {
                ConfigUtil.warnFor( this );
                ConfigUtil.LOG.warn( "Invalid boolean! Falling back to default ({}). Invalid value: {}",
                        valueDefault, raw );
            }
            value = valueDefault;
        }
        else {
            value = newValue;
        }
    }
    
    /** @return The value that should be assigned to this field in the config file. */
    @Override
    public Boolean getValue() { return value; }
    
    /** @return The default value of this field. */
    @Override
    public Boolean getDefaultValue() { return valueDefault; }
    
    /** @return This field's gui component provider. */
    @Override
    @OnClient
    public IConfigFieldWidgetProvider getWidgetProvider() { return new BooleanFieldWidgetProvider( this ); }
}