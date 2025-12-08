package fathertoast.crust.api.config.common.value.collection.key;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.ITomlStringValue;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

/**
 * Represents a key used for fuzzy sets and maps.
 * <p>
 * Fuzzy keys allow single keys to match any number of elements, and are checked in the order
 * defined, the first matching key is taken as the result.
 * <p>
 * These keys may be set as "blacklist" type keys. When a blacklist key is selected as the match,
 * it is treated as if no match was found.
 * <p>
 * For example, say you have two keys in a set, the first is a blacklist key that matches husks,
 * and the second is a tag key that matches all zombies - contains checks for husks will return
 * false, but all other zombies will return true.
 * <p>
 * If you were to swap the positions of those keys, the blacklist husk entry will never be used,
 * since husks will return true on matching the zombie tag key before it checks the husk blacklist
 * key. We attempt to do some verification to warn of these conflicts, but not all are detectable.
 *
 * @param <T> The type to match against.
 * @see IFuzzyKeyParser
 * @see fathertoast.crust.api.config.common.value.collection.FuzzySet
 * @see fathertoast.crust.api.config.common.value.collection.FuzzyMap
 */
@ApiStatus.Experimental
public abstract class FuzzyKey<T> implements ITomlStringValue {
    
    /** Key string for the default key; only used for matching. */
    public static final String DEFAULT_KEY = "default";
    
    /**
     * Key string for the null key; used to represent a "chance to pick nothing" in weighted lists
     * or a missing value argument when a key parser is used as a value codec.
     */
    public static final String NULL_KEY = "null";
    
    /**
     * May be used as a pseudo-value on any key (except "default") used for matching to make it into a blacklist key.
     * This applies to both maps and sets, even though sets do not allow values.
     */
    public static final String BLACKLIST_VALUE = "exclude";
    
    /** The string used to separate arguments. */
    public static final String ARG_SEPARATOR = " ";
    
    /**
     * Loads a basic fuzzy key from the provided TOML string. Expects the pattern "key exclude".<p>
     * If "exclude" is present, the key is declared a blacklist type.<p>
     * If a value is included after the key, the value is deleted.<p>
     * If the key is "default" and also declared a blacklist type, the line is deleted.
     *
     * @param parser The key parser to use.
     * @param field  The config field we are loading for, or null if error reporting should be suppressed.
     * @param line   The full TOML string.
     * @return A new fuzzy key based on the provided line, or null if the line should be deleted.
     */
    @Nullable
    public static <T> FuzzyKey<T> parseLine( IFuzzyKeyParser<T> parser, @Nullable AbstractConfigField field, String line ) {
        // Check for a value or blacklist declaration
        String[] keyAndValue = FuzzyKey.getKeyAndValue( line );
        String key = keyAndValue[0];
        boolean isDefault = key.equalsIgnoreCase( DEFAULT_KEY );
        boolean isBlacklist;
        if( keyAndValue.length > 1 ) {
            if( keyAndValue[1].equalsIgnoreCase( BLACKLIST_VALUE ) ) {
                if( isDefault ) {
                    if( field != null ) {
                        ConfigUtil.warnFor( field );
                        ConfigUtil.LOG.warn( "Default keys cannot be blacklist type! Deleting." );
                    }
                    return null;
                }
                isBlacklist = true;
            }
            else {
                if( field != null ) {
                    ConfigUtil.warnFor( field );
                    ConfigUtil.LOG.warn( "Basic keys do not allow values! Deleting value. Entry: {}", line );
                }
                isBlacklist = false;
            }
        }
        else {
            isBlacklist = false;
        }
        
        // Finally, parse the key
        return isDefault ? DefaultKey.get() : parser.parseKeyStringNonNull( field, line, key, isBlacklist );
    }
    
    /** @return The string, split into a key (index 0) and value (index 1, if present). */
    public static String[] getKeyAndValue( String tomlString ) {
        return tomlString.trim().split( ARG_SEPARATOR, 2 );
    }
    
    /** @return The key and value, combined into a single string. */
    public static String keyWithValue( String key, String value ) { return key + ARG_SEPARATOR + value; }
    
    
    // ---- Instance Methods ---- //
    
    /** @see #isBlacklist() */
    private final boolean isBlacklist;
    
    protected FuzzyKey( boolean blacklist ) { isBlacklist = blacklist; }
    
    /** @return Unwraps this key (if wrapped) and returns it. Used when checking for valid key usage. */
    public FuzzyKey<T> unwrap() { return this; }
    
    
    /**
     * @return True if this key is a blacklist type; in other words, when this key is the
     * resulting match, then the containing set/map treats it as if no match was found.
     */
    public boolean isBlacklist() { return isBlacklist; }
    
    /** @return True if this key is a default key. A default key's {@link #matches(Object)} always returns true. */
    public boolean isDefault() { return false; }
    
    /** @return True if this key is a null key. A null key's {@link #matches(Object)} always returns false. */
    public boolean isNull() { return false; }
    
    
    /** @return This fuzzy key's string definition. This must uniquely describe the match conditions. */
    public abstract String keyString();
    
    /** @return True if this key matches the target. */
    public abstract boolean matches( T target );
    
    
    /** @return This value, converted to a single-line string. */
    @Override // ITomlStringValue
    public String toTomlString() { return isBlacklist() ? keyWithValue( keyString(), BLACKLIST_VALUE ) : keyString(); }
    
    /** @return This value, converted to a single-line string. */
    @Override
    public String toString() { return toTomlString(); }
    
    /** Two fuzzy keys are equal if they match the exact same targets. */
    @Override
    public final boolean equals( @Nullable Object other ) {
        return other instanceof FuzzyKey<?> otherKey && keyString().equals( otherKey.keyString() );
    }
    
    /** Two fuzzy keys are equal if they match the exact same targets. */
    @Override
    public final int hashCode() { return keyString().hashCode(); }
}