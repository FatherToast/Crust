package fathertoast.crust.api.config.common.value.collection.key;

/**
 * Represents a default key for fuzzy sets and maps.
 * <p>
 * Always matches everything. Cannot be a blacklist type key.
 */
public class DefaultKey<T> extends FuzzyKey<T> {
    
    /** @return A new parameterized default key. */
    public static <T> DefaultKey<T> get() { return new DefaultKey<>(); }
    
    
    private DefaultKey() { super( false ); }
    
    /** @return True if this key is a default key. A default key's {@link #matches(Object)} always returns true. */
    @Override
    public boolean isDefault() { return true; }
    
    
    /** @return This fuzzy key's string definition. This must uniquely describe the match conditions. */
    @Override
    public String keyString() { return DEFAULT_KEY; }
    
    /** @return True if this key matches the target. */
    @Override
    public boolean matches( T target ) { return true; }
}