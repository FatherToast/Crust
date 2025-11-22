package fathertoast.crust.api.config.common.value.collection;


import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.collection.key.DefaultKey;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.*;

/**
 * An ordered set of values represented in files by a string array. This set allows each key to match
 * any number of elements, and are checked in the order defined, the first matching key is taken as the result.
 * <p>
 * At its base, this does not support values.
 * <p>
 * This implementation is semi-fixed to protect against inadvertent modification, but allows
 * direct {@link #load} operations to make it easier to use in non-config applications (e.g., NBT).
 *
 * @see FuzzyKey
 */
@ApiStatus.Experimental
public abstract class FuzzySet<T> extends TomlStringList<FuzzyKey<T>> {
    
    /** Constructs an empty set. Use this if you want to {@link #load} a set from file/NBT. */
    protected FuzzySet() { }
    
    /** Constructs a set containing the keys provided. Use this for creating default values during config definition. */
    @SafeVarargs
    protected FuzzySet( FuzzyKey<T>... keys ) {
        super( keys );
        validate();
    }
    
    /** Constructs a set containing the keys provided. Use this for creating default values during config definition. */
    protected FuzzySet( Collection<FuzzyKey<T>> keys ) {
        super( keys );
        validate();
    }
    
    /** @return A fresh, empty set of the same type as this one. */
    public abstract FuzzySet<T> makeNew();
    
    /**
     * Loads an entry from the provided TOML string. If anything goes wrong, correct it at the lowest level possible.
     * If the config field is not null, provide useful feedback and identify the field.
     *
     * @return The freshly loaded entry, or null if the line is invalid.
     */
    @Nullable
    protected abstract FuzzyKey<T> loadEntry( @Nullable AbstractConfigField field, String line, String key,
                                              @Nullable String value, boolean blacklist );
    
    /**
     * Loads a default entry from the provided TOML string. If anything goes wrong, correct it at the lowest level.
     * If the config field is not null, provide useful feedback and identify the field.
     * <p>
     * This is ONLY called for sets flagged as {@link #allowsValues()} (i.e., maps), and MUST be overridden for
     * those types of sets.
     *
     * @return The freshly loaded default entry, or null if the line is invalid.
     */
    @Nullable
    protected FuzzyKey<T> loadValueForDefault( @Nullable AbstractConfigField field, String line, String value ) {
        throw new IllegalArgumentException( "Default entry does not know how to load values!" );
    }
    
    /**
     * Loads this value from the given list. If anything goes wrong, correct it at the lowest level possible.
     * If the field is null, error reporting is suppressed.
     *
     * @param field The config field we are loading for, or null if not loading from a config.
     * @param value List value to load from. This generally comes from a TOML string array value
     *              (config loading) or a string list tag (NBT loading).
     */
    public void load( @Nullable AbstractConfigField field, List<String> value ) {
        final ArrayList<FuzzyKey<T>> list = new ArrayList<>( value.size() );
        // Used to exclude repeat keys (blacklist and/or value ignored)
        final Set<FuzzyKey<T>> loadedKeys = new HashSet<>( value.size() );
        boolean foundDefault = false;
        for( String line : value ) {
            // Print a warning for every entry after the default - these are all "dead code"
            if( foundDefault ) {
                if( field != null ) {
                    ConfigUtil.warnFor( field );
                    ConfigUtil.LOG.warn( "Unreachable entry (defined after \"{}\" key): \"{}\"",
                            FuzzyKey.DEFAULT_KEY, line );
                }
            }
            // Parse the line
            FuzzyKey<T> entry = loadLine( field, line );
            if( entry != null ) {
                if( entry instanceof DefaultKey ) foundDefault = true;
                else if( !loadedKeys.add( entry ) && field != null ) {
                    ConfigUtil.warnFor( field );
                    ConfigUtil.LOG.warn( "Unreachable entry (duplicate key): \"{}\"", line );
                }
                list.add( entry );
            }
        }
        // Tidy up and set value
        list.trimToSize();
        setList( list );
    }
    
    /** @return The freshly loaded entry, or null if the line is invalid. */
    @Nullable
    public FuzzyKey<T> loadLine( @Nullable AbstractConfigField field, String line ) {
        String[] keyAndValue = FuzzyKey.getKeyAndValue( line );
        String lineValue = keyAndValue.length > 1 ? keyAndValue[1].trim() : null;
        boolean blacklist = lineValue != null && lineValue.equalsIgnoreCase( FuzzyKey.BLACKLIST_VALUE );
        // Handle default key loading
        if( keyAndValue[0].equalsIgnoreCase( FuzzyKey.DEFAULT_KEY ) ) {
            if( blacklist ) {
                if( field != null ) {
                    ConfigUtil.warnFor( field );
                    ConfigUtil.LOG.warn( "Default key defined as blacklist - this is not allowed! Deleting." );
                }
            }
            else {
                return loadDefaultEntry( field, line, lineValue );
            }
        }
        // Load all other key types
        else {
            if( !blacklist && !allowsValues() && lineValue != null && field != null ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Values not allowed for this field! Deleting value \"{}\". Entry: {}",
                        lineValue, line );
            }
            return loadEntry( field, line, keyAndValue[0], lineValue, blacklist );
        }
        return null;
    }
    
    /** @return The freshly loaded default entry, or null if the line is invalid. */
    @Nullable
    protected FuzzyKey<T> loadDefaultEntry( @Nullable AbstractConfigField field, String line, @Nullable String value ) {
        if( value == null ) {
            if( allowsValues() ) {
                if( field != null ) {
                    ConfigUtil.warnFor( field );
                    ConfigUtil.LOG.warn( "Default key missing value - this does nothing! Deleting entry." );
                }
                return null;
            }
        }
        else {
            if( allowsValues() ) {
                return loadValueForDefault( field, line, value );
            }
            if( field != null ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Default key has value - this does nothing! Deleting value \"{}\". Entry: {}",
                        value, line );
            }
        }
        return DefaultKey.get();
    }
    
    /**
     * Note: If you override this to return true, you must also override
     * {@link #loadValueForDefault(AbstractConfigField, String, String)}.
     *
     * @return True if values are generally allowed.
     */
    protected boolean allowsValues() { return false; } // Override this for map-type implementations
    
    
    /** @return True if the given target is contained within this set. */
    public boolean contains( T target ) { return get( target ) != null; }
    
    /** @return The best match key, or null if no match was found. */
    @Nullable
    public FuzzyKey<T> get( T target ) {
        final List<FuzzyKey<T>> keys = getList();
        for( FuzzyKey<T> key : keys ) if( key.matches( target ) ) return key.isBlacklist() ? null : key;
        return null;
    }
    
    
    /**
     * Called after constructing any of the pre-populated constructors, with the expectation
     * that they are only used for creating default config values. This is fail-fast.
     *
     * @throws IllegalArgumentException If there are any issues.
     */
    private void validate() {
        final List<FuzzyKey<T>> list = getList();
        // Used to exclude repeat keys (blacklist and/or value ignored)
        Set<FuzzyKey<T>> loadedKeys = new HashSet<>( list.size() );
        for( FuzzyKey<T> entry : list ) {
            if( loadedKeys.contains( entry ) ) {
                throw new IllegalArgumentException( "Duplicate keys not allowed! Duplicate entry: " + entry.toTomlString() );
            }
            else {
                loadedKeys.add( entry );
            }
        }
    }
    
    
    /** Boilerplate builder class for fuzzy sets/maps. */
    @ApiStatus.Experimental
    public static abstract class Builder<T> {
        public final ArrayList<FuzzyKey<T>> list = new ArrayList<>();
        
        /** @return A new fuzzy set reflecting the current state of this builder. */
        public abstract FuzzySet<T> build();
        
        /** @return A new fuzzy set with a default key reflecting the current state of this builder. */
        public abstract FuzzySet<T> buildWithDefault();
        
        /** Adds a pre-constructed key. */
        public Builder<T> add( FuzzyKey<T> key ) {
            list.add( key );
            return this;
        }
    }
}