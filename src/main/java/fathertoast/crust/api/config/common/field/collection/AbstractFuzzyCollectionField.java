package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.StringListFieldWidgetProvider;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.GenericField;
import fathertoast.crust.api.config.common.field.IStringListScreenEditable;
import fathertoast.crust.api.config.common.value.collection.AbstractFuzzyCollection;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

/**
 * Boilerplate for fuzzy collection fields.
 *
 * @param <T> The collection type.
 * @param <K> The type of fuzzy key.
 * @see fathertoast.crust.api.config.common.value.collection.key.IFuzzyKeyParser
 */
@ApiStatus.Experimental
public abstract class AbstractFuzzyCollectionField<T, K extends FuzzyKey<T>, C extends AbstractFuzzyCollection<T, K>>
        extends GenericField<C> implements IStringListScreenEditable {
    
    /** Creates a new field. */
    public AbstractFuzzyCollectionField( String key, C defaultValue, @Nullable String... description ) {
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
        
        if( raw instanceof AbstractFuzzyCollection<?, ?> ) {
            try {
                //noinspection unchecked
                value = (C) raw;
            }
            catch( ClassCastException ex ) {
                ConfigUtil.errorFor( this );
                ConfigUtil.LOG.error( "Attempted to assign fuzzy collection of the wrong type! Falling back to default. Invalid value: {}",
                        raw );
                value = valueDefault;
            }
        }
        else {
            // All the actual loading is done through the object
            try {
                //noinspection unchecked
                value = (C) valueDefault.makeNew();
                value.load( this, raw );
            }
            catch( ClassCastException ex ) {
                ConfigUtil.errorFor( this );
                ConfigUtil.LOG.error( "Fuzzy collection factory returns the wrong type! Falling back to default. Invalid factory method: {}#makeNew()",
                        valueDefault.getClass().getName() );
                value = valueDefault;
            }
        }
    }
    
    /** @return This field's gui component provider. */
    @Override
    public IConfigFieldWidgetProvider getWidgetProvider() { return new StringListFieldWidgetProvider<>( this ); }
    
    /** Converts the displayable string list to a field value. */
    @Override // IStringListScreenEditable
    public Object stringListToValue( List<String> value ) {
        AbstractFuzzyCollection<T, K> c = getDefaultValue().makeNew();
        c.load( this, value );
        return c;
    }
    
    /** @return This field's line validator, or null if any string is allowed. */
    @Override // IStringListScreenEditable
    public Predicate<String> getLineValidator() {
        return ( line ) -> {
            final K loaded = getDefaultValue().loadLine( null, line );
            return loaded != null && (!loaded.isNull() || FuzzyKey.NULL_KEY.equalsIgnoreCase( loaded.keyString() ));
        };
    }
    
    
    // ---- Convenience Methods ---- //
    
    /** @return True if this contains no elements. */
    public boolean isEmpty() { return get().isEmpty(); }
}