package fathertoast.crust.api.config.common.value.collection.key;

import org.jetbrains.annotations.ApiStatus;

/**
 * Represents the default key for fuzzy sets and maps.
 * <p>
 * Always matches everything and cannot be a blacklist type.
 */
@ApiStatus.Experimental
public class DefaultKey<T> extends FuzzyKey<T> {
    
    public static <T> DefaultKey<T> get() { return new DefaultKey<>(); }
    
    
    private DefaultKey() { super( false ); }
    
    /** @return This fuzzy key's string definition. This must uniquely describe the match conditions. */
    public String keyString() { return DEFAULT_KEY; }
    
    /** @return True if this key matches the target. */
    public boolean matches( T target ) { return true; }
}