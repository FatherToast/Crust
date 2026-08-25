package fathertoast.crust.api.config.common.field;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Represents a config field that loads its value into an external location, such as a static variable.
 * <p>
 * This field can wrap any other config field, and the actual method used to inject its value is defined at construction.
 */
public class InjectionWrapperField<T, F extends IConfigField<T>> extends AbstractWrapperField<T, F> {
    
    /** The callback used any time the wrapped field value is (re)loaded. */
    private final Consumer<F> injectionCallback;
    
    /** Creates a new injection wrapper field that performs a generic load callback function to auto-inject the value. */
    public InjectionWrapperField( F field, Consumer<F> callback ) {
        super( field );
        injectionCallback = callback;
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
        injectionCallback.accept( field() );
    }
    
    /** Assigns a remote value and makes it 'active'. This is used for syncing values from the server. */
    @Override
    public void setSyncValue( @Nullable T value ) {
        super.setSyncValue( value );
        injectionCallback.accept( field() );
    }
}