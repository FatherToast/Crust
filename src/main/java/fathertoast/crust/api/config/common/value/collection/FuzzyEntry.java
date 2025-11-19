package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.value.ITomlStringValue;
import fathertoast.crust.api.config.common.value.collection.key.DefaultKey;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

/**
 * Represents a key used for fuzzy maps.
 * <p>
 * It functions by wrapping a fuzzy key and associating it with a value.
 */
@ApiStatus.Experimental
public class FuzzyEntry<T, V extends ITomlStringValue> extends FuzzyKey<T> {
    
    /** Creates an entry that defines a default value for the map. */
    public static <T, V extends ITomlStringValue> FuzzyEntry<T, V> ofDefault( V value ) {
        return of( DefaultKey.get(), value );
    }
    
    /** Creates a blacklist type entry. */
    public static <T, V extends ITomlStringValue> FuzzyEntry<T, V> ofBlacklist( FuzzyKey<T> key ) {
        return new FuzzyEntry<>( key, null );
    }
    
    /** Creates an entry that associates a non-blacklist key with a value. */
    public static <T, V extends ITomlStringValue> FuzzyEntry<T, V> of( FuzzyKey<T> key, V value ) {
        return new FuzzyEntry<>( key, value );
    }
    
    
    private final FuzzyKey<T> key;
    @Nullable
    private final V value;
    
    /** Constructs a key from the loaded string definition. */
    private FuzzyEntry( FuzzyKey<T> k, @Nullable V v ) {
        super( k.isBlacklist() );
        key = k;
        value = v;
        
        // Perform some validation
        if( k.isBlacklist() ) {
            if( v != null ) {
                throw new IllegalArgumentException( "Blacklist key cannot map to value!" );
            }
        }
        else if( v == null ) {
            throw new IllegalArgumentException( "Non-blacklist key must map to a value!" );
        }
    }
    
    /** @return This entry's underlying key. */
    public FuzzyKey<T> getKey() { return key; }
    
    /** @return This entry's associated value, or null if this is a blacklist entry. */
    @Nullable
    public V getValue() { return value; }
    
    
    /** @return True if the other key is contained within this one. */
    @Override
    public boolean matches( T target ) { return key.matches( target ); }
    
    /** @return This fuzzy key's string definition. */
    @Override
    public String keyString() { return key.keyString(); }
    
    /**
     * @return True if this key is a blacklist type; in other words, when this is the best match,
     * the containing set/map should treat it as if no match was found.
     */
    public boolean isBlacklist() { return key.isBlacklist(); }
    
    /** @return This value, converted to a single-line string. */
    @Override // ITomlStringValue
    public String toTomlString() { return keyWithValue( this ); }
}