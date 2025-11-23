package fathertoast.crust.api.config.common.value.collection.value;

import fathertoast.crust.api.config.common.value.collection.key.DefaultKey;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Represents a key-value pair used for fuzzy maps.
 * <p>
 * It functions by wrapping a fuzzy key and associating it with a value.
 *
 * @param <T> The type to match against.
 * @param <V> The value type.
 */
@ApiStatus.Experimental
public class FuzzyEntry<T, V> extends FuzzyKey<T> implements Supplier<V> {
    
    /** Creates an entry that defines a default value for the map. */
    public static <T, V> FuzzyEntry<T, V> ofDefault( V value, IValueCodec<V> codec ) {
        return of( DefaultKey.get(), value, codec );
    }
    
    /** Creates a blacklist type entry. */
    public static <T, V> FuzzyEntry<T, V> ofBlacklist( FuzzyKey<T> key ) {
        return new FuzzyEntry<>( key, null, null );
    }
    
    /** Creates an entry that associates a non-blacklist key with a value. */
    public static <T, V> FuzzyEntry<T, V> of( FuzzyKey<T> key, V value, IValueCodec<V> codec ) {
        return new FuzzyEntry<>( key, value, codec );
    }
    
    
    private final FuzzyKey<T> key;
    @Nullable
    private final V value;
    @Nullable
    private final IValueCodec<V> valueCodec;
    
    /** Constructs a key from the loaded string definition. */
    private FuzzyEntry( FuzzyKey<T> k, @Nullable V v, @Nullable IValueCodec<V> codec ) {
        super( k.isBlacklist() );
        key = k;
        value = v;
        valueCodec = codec;
        
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
    
    /**
     * @return This entry's associated value.
     * @throws NullPointerException If this key is a blacklist type.
     */
    @Override // Supplier
    public V get() { return Objects.requireNonNull( value ); }
    
    
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
    @Override
    public boolean isBlacklist() { return key.isBlacklist(); }
    
    /** @return True if this key is a default key. A default key's {@link #matches(Object)} always returns true. */
    @Override
    public boolean isDefault() { return key.isDefault(); }
    
    /** @return This value, converted to a single-line string. */
    @Override // ITomlStringValue
    public String toTomlString() {
        //noinspection DataFlowIssue
        return keyWithValue( keyString(), isBlacklist() ? BLACKLIST_VALUE : valueCodec.toTomlString( get() ) );
    }
}