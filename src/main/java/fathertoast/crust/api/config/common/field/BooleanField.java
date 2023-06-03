package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.client.gui.widget.provider.BooleanFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.TomlHelper;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents a config field with a boolean value.
 */
@SuppressWarnings( "unused" )
public class BooleanField extends AbstractConfigField {
    
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
    public boolean get() { return value; }
    
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
        Object newValue;
        if( raw instanceof String ) {
            ConfigUtil.LOG.info( "Unboxing string value for {} \"{}\" to a different primitive.",
                    getClass(), getKey() );
            newValue = TomlHelper.parseStringPrimitive( (String) raw );
        }
        else {
            newValue = raw;
        }
        
        if( newValue instanceof Boolean ) {
            value = (Boolean) newValue;
        }
        else if( newValue instanceof Number ) {
            final double newNumberValue = ((Number) newValue).doubleValue();
            ConfigUtil.LOG.warn( "Value for {} \"{}\" is numerical! Converting value. Invalid value: {}",
                    getClass(), getKey(), raw );
            value = newNumberValue != 0.0; // 0 is false, anything else is true
        }
        else {
            if( newValue != null ) {
                ConfigUtil.LOG.warn( "Invalid value for {} \"{}\"! Falling back to default. Invalid value: {}",
                        getClass(), getKey(), raw );
            }
            value = valueDefault;
        }
    }
    
    /** @return The value that should be assigned to this field in the config file. */
    @Override
    @Nullable
    public Object getValue() { return value; }
    
    /** @return The default value of this field. */
    @Override
    public Object getDefaultValue() { return valueDefault; }
    
    /** @return This field's gui component provider. */
    @Override
    public IConfigFieldWidgetProvider getWidgetProvider() { return new BooleanFieldWidgetProvider( this ); }
}