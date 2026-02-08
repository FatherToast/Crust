package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.IMultiKey;
import fathertoast.crust.api.config.common.value.collection.key.IRandomKey;
import fathertoast.crust.api.config.common.value.collection.key.IReverseKey;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

/**
 * Describes how fuzzy keys will be used. Allows the various fuzzy collections to filter out key
 * types that are not compatible with their use case without needing multiple parser implementations.
 * <p>
 * For example, blacklist keys can be useful when matching, but have no meaning in a simple list.
 */
public enum KeyUsage {
    
    /** The keys are used for matching; e.g., set contains checks and map value lookups. */
    MATCH {
        /** @return True if the unwrapped key is allowed for this usage. */
        @Override
        protected boolean allows( FuzzyKey<?> key ) { return true; }
    },
    
    /** The keys are drawn individually on demand; e.g., weighted lists. */
    POLL {
        /** @return True if the unwrapped key is allowed for this usage. */
        @Override
        protected boolean allows( FuzzyKey<?> key ) { return !key.isBlacklist() && key instanceof IRandomKey<?>; }
    },
    
    /** The keys are iterated through to do something for each element; e.g., list iterators. */
    ITERATE {
        /** @return True if the unwrapped key is allowed for this usage. */
        @Override
        protected boolean allows( FuzzyKey<?> key ) {
            return !key.isBlacklist() && !key.isDefault() && (key instanceof IReverseKey<?> || key instanceof IMultiKey<?>);
        }
    };
    
    
    /** @return The key if it is allowed for this usage; null otherwise. */
    @Nullable
    public <T, K extends FuzzyKey<T>> K ifAllowed( @Nullable K key ) { return key != null && allowsKey( key ) ? key : null; }
    
    /** @return True if the key is allowed for this usage. */
    public boolean allowsKey( FuzzyKey<?> key ) { return allows( key.unwrap() ); }
    
    
    /** @return True if the unwrapped key is allowed for this usage. */
    @ApiStatus.Internal
    protected abstract boolean allows( FuzzyKey<?> key );
}