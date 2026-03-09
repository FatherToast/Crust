package fathertoast.crust.api.config.common.field.collection;

import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.collection.FuzzyMap;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.value.FuzzyEntry;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Boilerplate for fuzzy map fields, but can also be used directly with generic fuzzy maps.
 * Use {@link #get(T)} to retrieve to value for a target object (or null if the object is not mapped).
 * If the value type is a number, you may use {@link #rollChance(T, RandomSource)} to retrieve the
 * value, roll it like a percentage or 1-in-X chance, and get back a pass/fail boolean instead.
 *
 * @param <T> The type to match against.
 * @param <V> The value type.
 * @see fathertoast.crust.api.config.common.value.collection.key.IFuzzyKeyParser
 * @see fathertoast.crust.api.config.common.value.collection.value.IValueCodec
 * @see FuzzySetField
 */
@ApiStatus.Experimental
public class FuzzyMapField<T, V, F extends FuzzyMap<T, V>> extends AbstractFuzzyCollectionField<T, FuzzyEntry<T, V>, F> {
    
    /** A simple implementation for using generic fuzzy maps without the extra type parameter. */
    @SuppressWarnings( "unused" )
    @ApiStatus.Experimental
    public static class Generic<T, V> extends FuzzyMapField<T, V, FuzzyMap<T, V>> {
        /** Creates a new field. */
        public Generic( String key, FuzzyMap<T, V> defaultValue, @Nullable String... description ) {
            super( key, defaultValue, description );
        }
    }
    
    /** Creates a new field. */
    public FuzzyMapField( String key, F defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoNoDefault( valueDefault.getTypeName() + " Map",
                "[ \"" + FuzzyKey.keyWithValue( "key_1", "value_1" ) + "\", \"" +
                        FuzzyKey.keyWithValue( "key_2", FuzzyKey.BLACKLIST_VALUE ) + "\", \"" +
                        FuzzyKey.keyWithValue( "key_3", "value_3" ) + "\", ... ]" ) );
        comment.add( "Key Patterns: " + valueDefault.getKeyPatterns() );
        comment.add( "Value Format: " + valueDefault.getValueFormat() );
        comment.add( TomlHelper.fieldInfoOnlyDefault( valueDefault ) );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /** @return True if the given target is contained within this set. */
    public boolean contains( T target ) { return get().contains( target ); }
    
    /** @return The value for the given target, or null if the target is not contained in this map. */
    @Nullable
    public V get( T target ) { return get().get( target ); }
    
    /** @return The value for the given target, or the provided default value if the target is not contained in this map. */
    public V getOrElse( T target, V defaultValue ) {
        V val = get( target );
        return val == null ? defaultValue : val;
    }
    
    /**
     * @return Gets the value for the given target and returns the result of a random roll
     * against it based on this map's value type:<p>
     * Double/Float: Treats the value as a percent chance (from 0 to 1).<p>
     * Integer/Short/etc.: Treats the value as a 1-in-X chance (Note: long is truncated to int).<p>
     * Non-Number types (or no value found for target): Returns false.
     */
    public boolean rollChance( T target, Random random ) { return get().rollChance( target, random ); }
    
    /**
     * @return Gets the value for the given target and returns the result of a random roll
     * against it based on this map's value type:<p>
     * Double/Float: Treats the value as a percent chance (from 0 to 1).<p>
     * Integer/Short/etc.: Treats the value as a 1-in-X chance (Note: long is truncated to int).<p>
     * Non-Number types (or no value found for target): Returns false.
     */
    public boolean rollChance( T target, RandomSource random ) { return get().rollChance( target, random ); }
}