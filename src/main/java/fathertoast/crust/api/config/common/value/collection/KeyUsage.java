package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.IMultiKey;
import fathertoast.crust.api.config.common.value.collection.key.IRandomKey;
import fathertoast.crust.api.config.common.value.collection.key.IReverseKey;

/**
 * Describes how fuzzy keys will be used. Allows the various fuzzy collections to filter out key
 * types that are not compatible with their use case without needing multiple parser implementations.
 * <p>
 * For example, blacklist keys can be useful when matching, but have no meaning in a simple list.
 */
public enum KeyUsage {
    
    /** The keys are used for matching; e.g., set contains checks and map value lookups. */
    MATCH {
        /** @return True if the key is allowed for this usage. */
        @Override
        public boolean allowsKey( FuzzyKey<?> key ) { return true; }
    },
    
    /** The keys are drawn individually on demand; e.g., weighted lists. */
    POLL {
        /** @return True if the key is allowed for this usage. */
        @Override
        public boolean allowsKey( FuzzyKey<?> key ) { return !key.isBlacklist() && key instanceof IRandomKey<?>; }
    },
    
    /** The keys are iterated through to do something for each element; e.g., list iterators. */
    ITERATE {
        /** @return True if the key is allowed for this usage. */
        @Override
        public boolean allowsKey( FuzzyKey<?> key ) {
            return !key.isBlacklist() && !key.isDefault() && (key instanceof IReverseKey<?> || key instanceof IMultiKey<?>);
        }
    };
    
    /** @return True if the key is allowed for this usage. */
    public abstract boolean allowsKey( FuzzyKey<?> key );
}