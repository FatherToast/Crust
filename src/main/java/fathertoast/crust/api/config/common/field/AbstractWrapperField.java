package fathertoast.crust.api.config.common.field;

import com.electronwill.nightconfig.core.io.CharacterOutput;
import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.file.CrustTomlWriter;
import fathertoast.crust.api.util.OnClient;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Wraps a single key-value mapping in a config.
 * <p>
 * This is a boilerplate wrapper intended to simplify field wrapper implementations by taking care of
 * all basic functionalities the wrapper needs to provide to the wrapped field.
 */
public abstract class AbstractWrapperField<T extends AbstractConfigField> extends AbstractConfigField {
    
    /** The wrapped field. */
    private final T wrappedField;
    
    /** Creates a new wrapper field around the supplied field. */
    public AbstractWrapperField( T wrapped ) {
        super( wrapped.getKey(), wrapped.getComment() );
        wrappedField = wrapped;
    }
    
    /** @return Returns the wrapped config field. */
    public T field() { return wrappedField; }
    
    /** @return Unwraps this config field (if wrapped) and returns it. */
    @Override
    public AbstractConfigField unwrap() { return field().unwrap(); }
    
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
     * Loads this field's value from the given value or raw toml. If anything goes wrong, correct it at the lowest level possible.
     * <p>
     * For example, a missing value should be set to the default, while an out-of-range value should be adjusted to the
     * nearest in-range value and print a warning explaining the change.
     */
    @Override
    public void load( @Nullable Object raw ) { wrappedField.load( raw ); }
    
    /** @return The value that should be assigned to this field in the config file. */
    @Override
    @Nullable
    public Object getValue() { return wrappedField.getValue(); }
    
    /** @return The default value of this field. */
    @Override
    public Object getDefaultValue() { return wrappedField.getDefaultValue(); }
    
    /** Writes this field's value to file. */
    @Override
    public void writeValue( CrustTomlWriter writer, CharacterOutput output ) { wrappedField.writeValue( writer, output ); }
    
    /** @return This field's gui component provider. */
    @Override
    @OnClient
    public IConfigFieldWidgetProvider getWidgetProvider() { return wrappedField.getWidgetProvider(); }
}