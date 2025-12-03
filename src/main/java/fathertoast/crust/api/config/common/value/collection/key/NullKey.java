package fathertoast.crust.api.config.common.value.collection.key;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an empty selection for polling (e.g., weighted lists) or a key that failed to parse.
 * <p>
 * Never matches anything, used to minimize deletion of user-entered data and as a placeholder for null values.
 */
@ApiStatus.Experimental
public class NullKey<T> extends FuzzyKey<T> implements IReverseKey<T> {
    
    /** @return A new parameterized null key. */
    public static <T> NullKey<T> of( String key, boolean blacklist ) { return new NullKey<>( key, blacklist ); }
    
    /** @return A new parameterized null key. */
    public static <T> NullKey<T> ofValue() { return of( NULL_KEY, false ); }
    
    
    private final String keyString;
    
    private NullKey( String key, boolean blacklist ) {
        super( blacklist );
        keyString = key;
    }
    
    /** @return True if this key is a null key. A null key's {@link #matches(Object)} always returns false. */
    @Override
    public boolean isNull() { return true; }
    
    
    /** @return This fuzzy key's string definition. This must uniquely describe the match conditions. */
    @Override
    public String keyString() { return keyString; }
    
    /** @return True if this key matches the target. */
    @Override
    public boolean matches( T target ) { return false; }
    
    
    /** @return The value that matches this key, or null if anything goes wrong. */
    @Override // IReverseKey
    @Nullable
    public T asValue() { return null; }
}