package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.StringListFieldWidgetProvider;
import fathertoast.crust.api.config.common.value.collection.FuzzySet;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

/**
 * Boilerplate for fuzzy set fields.
 */
@ApiStatus.Experimental
public abstract class AbstractFuzzySetField<T, F extends FuzzySet<T>> extends GenericField<F> implements IStringListScreenEditable {
    
    /** Creates a new field. */
    public AbstractFuzzySetField( String key, F defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    /** @return This field's gui component provider. */
    @Override
    public IConfigFieldWidgetProvider getWidgetProvider() { return new StringListFieldWidgetProvider<>( this ); }
    
    /** Converts the displayable string list to a field value. */
    @Override // IStringListScreenEditable
    public Object stringListToValue( List<String> value ) {
        FuzzySet<T> regSet = getDefaultValue().makeNew();
        regSet.load( this, value );
        return regSet;
    }
    
    /** @return This field's line validator, or null if any string is allowed. */
    @Override // IStringListScreenEditable
    public Predicate<String> getLineValidator() {
        return ( line ) -> getDefaultValue().loadLine( null, line ) != null;
    }
    
    
    // ---- Convenience Methods ---- //
    
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