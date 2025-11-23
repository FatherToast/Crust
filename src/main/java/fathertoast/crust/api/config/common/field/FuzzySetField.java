package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.StringListFieldWidgetProvider;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.collection.FuzzySet;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

/**
 * Boilerplate for fuzzy set fields, but can also be used directly with generic fuzzy sets.
 *
 * @param <T> The
 */
@ApiStatus.Experimental
public class FuzzySetField<T, F extends FuzzySet<T>> extends GenericField<F> implements IStringListScreenEditable {
    
    /** Creates a new field. */
    public FuzzySetField( String key, F defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoNoDefault( valueDefault.getTypeName() + " Set",
                "[ \"key_1\", \"" + FuzzyKey.keyWithValue( "key_2", FuzzyKey.BLACKLIST_VALUE ) + "\", \"key_3\", ... ]" ) );
        comment.add( "Key Patterns: " + valueDefault.getKeyPatterns() );
        comment.add( TomlHelper.fieldInfoOnlyDefault( valueDefault ) );
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
        
        if( raw instanceof FuzzySet ) {
            try {
                //noinspection unchecked
                value = (F) raw;
            }
            catch( ClassCastException ex ) {
                ConfigUtil.errorFor( this );
                ConfigUtil.LOG.error( "Attempted to assign fuzzy set of the wrong type! Falling back to default. Invalid value: {}",
                        raw );
                value = valueDefault;
            }
        }
        else {
            // All the actual loading is done through the object
            try {
                //noinspection unchecked
                value = (F) valueDefault.makeNew();
                value.load( this, raw );
            }
            catch( ClassCastException ex ) {
                ConfigUtil.errorFor( this );
                ConfigUtil.LOG.error( "Fuzzy set factory returns the wrong type! Falling back to default. Invalid factory method: {}#makeNew()",
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
    
    /** @return The first matching key, or null if no match was found or the match was a blacklist key. */
    @Nullable
    public FuzzyKey<T> getKey( T target ) { return get().getKey( target ); }
}