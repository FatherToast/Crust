package fathertoast.crust.api.config.common.value.collection.key;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.collection.KeyUsage;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

/**
 * Provides instructions on how to read a fuzzy key from TOML.
 * <p>
 * May also be used as a value codec to read/write fuzzy keys as non-null value arguments.
 *
 * @param <T> The type that parsed keys match against.
 */
@ApiStatus.Experimental
public interface IFuzzyKeyParser<T> extends IValueCodec<FuzzyKey<T>> {
    
    /** @return The key parser's type name. */
    String getTypeName();
    
    /** @return The key parser's allowed patterns (e.g., "\"pattern_1\", \"pattern_2\", \"pattern_n\""). */
    String getPatterns( KeyUsage usage );
    
    /**
     * Loads a key from the provided TOML string. If anything goes wrong, correct it at the lowest level possible,
     * and if the config field is not null, provide useful feedback and identify the field.
     *
     * @param field The config field we are loading for, or null if error reporting should be suppressed.
     * @param line  The full line, for error context.
     * @param key   The key string to parse from.
     * @return A new fuzzy key based on the key string, or null if parsing fails.
     */
    @Nullable
    FuzzyKey<T> parseKeyString( @Nullable AbstractConfigField field, String line, String key, boolean blacklist );
    
    /**
     * Loads a key from the provided TOML string. If anything goes wrong, correct it at the lowest level possible,
     * and if the config field is not null, provide useful feedback and identify the field.
     *
     * @param field The config field we are loading for, or null if error reporting should be suppressed.
     * @param line  The full line, for error context.
     * @param key   The key string to parse from.
     * @return A new fuzzy key based on the key string. Returns {@link NullKey} if parsing fails.
     */
    default FuzzyKey<T> parseKeyStringNonNull( @Nullable AbstractConfigField field, String line, String key, boolean blacklist ) {
        FuzzyKey<T> loadedKey = parseKeyString( field, line, key, blacklist );
        // It's expected that a warning was already printed for failing to parse if null was returned
        return loadedKey == null ? NullKey.of( key, blacklist ) : loadedKey;
    }
    
    /**
     * @param field The config field we are loading for, or null if error reporting should be suppressed.
     * @param line  The full line, for error context.
     * @param value The value string to parse from.
     * @return A new value based on the value string. If the parse fails, returns a non-null default value.
     */
    @Override
    default FuzzyKey<T> parseTomlString( @Nullable AbstractConfigField field, String line, @Nullable String value ) {
        return value == null || value.equalsIgnoreCase( FuzzyKey.NULL_KEY ) ? NullKey.ofValue() :
                parseKeyStringNonNull( field, line, value, false );
    }
}