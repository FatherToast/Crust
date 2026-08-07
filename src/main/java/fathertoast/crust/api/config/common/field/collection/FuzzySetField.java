package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.collection.FuzzySet;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Boilerplate for fuzzy set fields, but can also be used directly with generic fuzzy sets.
 * Use {@link #contains(T)} to check if a target object is in the set.
 *
 * @param <T> The type to match against.
 * @see fathertoast.crust.api.config.common.value.collection.key.IFuzzyKeyParser
 * @see FuzzyMapField
 */
public class FuzzySetField<T, F extends FuzzySet<T>> extends AbstractFuzzyCollectionField<T, FuzzyKey<T>, F> {
    
    /** A simple implementation for using generic fuzzy sets without the extra type parameter. */
    @SuppressWarnings( "unused" )
    public static class Generic<T> extends FuzzySetField<T, FuzzySet<T>> {
        /** Creates a new field. */
        public Generic( String key, FuzzySet<T> defaultValue, @Nullable String... description ) {
            super( key, defaultValue, description );
        }
    }
    
    /** Creates a new field. */
    public FuzzySetField( String key, F defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoNoDefault( getDefaultValue().getTypeName() + " Set",
                "[ \"key_1\", \"key_2 " + FuzzyKey.BLACKLIST_VALUE + "\", \"key_3\", ... ]" ) );
        comment.add( "Key Patterns: " + getDefaultValue().getKeyPatterns() );
        comment.add( TomlHelper.fieldInfoOnlyDefault( getDefaultValue() ) );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /** @return True if the given target is contained within this set. */
    public boolean contains( T target ) { return get().contains( target ); }
}