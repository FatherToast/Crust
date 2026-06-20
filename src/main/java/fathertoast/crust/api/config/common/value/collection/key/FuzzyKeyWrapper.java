package fathertoast.crust.api.config.common.value.collection.key;

import org.jetbrains.annotations.ApiStatus;

/**
 * Extended by fuzzy keys that wrap the "actual" fuzzy keys to add extra functionality
 * to those keys (for example, a weight or value).
 *
 * @param <T> The type to match against and to supply.
 * @see WeightedKey
 * @see fathertoast.crust.api.config.common.value.collection.value.FuzzyEntry
 * @see fathertoast.crust.api.config.common.value.collection.value.WeightedEntry
 */
@ApiStatus.Experimental
public abstract class FuzzyKeyWrapper<T> extends FuzzyKey<T> {
    /** @see #wrappedKey() */
    private final FuzzyKey<T> wrappedKey;
    
    public FuzzyKeyWrapper( FuzzyKey<T> wrapped ) {
        super( wrapped.isBlacklist() );
        wrappedKey = wrapped;
    }
    
    /** @return The wrapped key. */
    public FuzzyKey<T> wrappedKey() { return wrappedKey; }
    
    /** @return Unwraps this key (if wrapped) and returns it. Used when checking for valid key usage. */
    @Override
    public FuzzyKey<T> unwrap() { return wrappedKey().unwrap(); }
    
    
    /**
     * @return True if this key is a blacklist type; in other words, when this key is the
     * resulting match, then the containing set/map treats it as if no match was found.
     */
    @Override
    public boolean isBlacklist() { return wrappedKey.isBlacklist(); }
    
    /** @return True if this key is a default key. A default key's {@link #matches(Object)} always returns true. */
    @Override
    public boolean isDefault() { return wrappedKey.isDefault(); }
    
    /** @return True if this key is a null key. A null key's {@link #matches(Object)} always returns false. */
    @Override
    public boolean isNull() { return wrappedKey.isNull(); }
    
    
    /** @return This fuzzy key's string definition. This must uniquely describe the match conditions. */
    @Override
    public String keyString() { return wrappedKey.keyString(); }
    
    /** @return True if this key matches the target. */
    @Override
    public boolean matches( T target ) { return wrappedKey.matches( target ); }
}