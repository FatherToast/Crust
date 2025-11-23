package fathertoast.crust.api.config.common.value.collection.key;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

/**
 * Provides instructions on how to read a fuzzy key from TOML.
 *
 * @param <T> The type that parsed keys match against.
 */
@ApiStatus.Experimental
public interface IFuzzyKeyParser<T> {
    
    /** @return The key parser's type name. */
    String getTypeName();
    
    /** @return The key parser's patterns (e.g., "\"pattern_1\", \"pattern_2\", \"pattern_n\""). */
    String getPatterns();
    
    /**
     * @param field The config field we are loading for, or null if error reporting should be suppressed.
     * @param line  The full line, for error context.
     * @param key   The key string to parse from.
     * @return A new fuzzy key based on the key string, or null if the key is invalid.
     */
    @Nullable
    FuzzyKey<T> parseTomlString( @Nullable AbstractConfigField field, String line, String key, boolean blacklist );
}