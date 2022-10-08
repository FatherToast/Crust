package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.client.gui.widget.field.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.common.file.CrustConfigSpec;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents a config field that loads its value into an external location, such as a static variable.
 * <p>
 * This field can wrap any other config field, and the actual method used to inject its value is defined at construction.
 */
@SuppressWarnings( "unused" )
public class InjectionWrapperField<T extends AbstractConfigField> extends AbstractConfigField {
    
    /** The wrapped field. */
    private final T wrappedField;
    /** The callback used any time the wrapped field value is (re)loaded. */
    private final Consumer<T> injectionCallback;
    
    /** Creates a new injection wrapper field that performs a generic load callback function to auto-inject the value. */
    public InjectionWrapperField( T field, Consumer<T> callback ) {
        super( field.getKey(), field.getComment() );
        wrappedField = field;
        injectionCallback = callback;
    }
    
    /** @return Returns the wrapped config field. */
    public T field() { return wrappedField; }
    
    /**
     * Called after the spec is set. Wrapper fields should override this method and call
     * {@link #setSpec(CrustConfigSpec)} on any underlying fields.
     */
    @Override
    protected void onSpecSet() { wrappedField.setSpec( getSpec() ); }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) { wrappedField.appendFieldInfo( comment ); }
    
    /**
     * Loads this field's value from the given raw toml value. If anything goes wrong, correct it at the lowest level possible.
     * <p>
     * For example, a missing value should be set to the default, while an out-of-range value should be adjusted to the
     * nearest in-range value
     */
    @Override
    public void load( @Nullable Object raw ) {
        // Load the wrapped field, then run the injection callback
        wrappedField.load( raw );
        injectionCallback.accept( wrappedField );
    }
    
    /** @return The raw toml value that should be assigned to this field in the config file. */
    @Override
    public Object getRaw() { return wrappedField.getRaw(); }
    
    /** @return The default raw toml value of this field. */
    @Override
    public Object getRawDefault() { return wrappedField.getRawDefault(); }
    
    /** @return This field's gui component provider. */
    @Override
    public IConfigFieldWidgetProvider getWidgetProvider() { return wrappedField.getWidgetProvider(); }
}