package fathertoast.crust.api.config.common.value.collection;


import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.collection.key.DefaultKey;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.*;

/**
 * A set of values represented in files by a string array. This set's #contains() follows
 * a "best match" system, allowing the key types to define their own #contains() methods and
 * sorting them such that more specific keys take priority over less specific ones.
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
    
    /**
     * Loads an entry from the provided TOML string. If anything goes wrong, correct it at the lowest level possible
     * and provide useful feedback, identifying the config field if present.
     *
     * @return The freshly loaded entry, or null if the line is invalid.
     */
    @Nullable
    protected abstract FuzzyKey<T> loadEntry( @Nullable AbstractConfigField field, String line, String key,
                                              @Nullable String value, boolean blacklist );
    
    /**
     * Loads a default entry from the provided TOML string. If anything goes wrong, correct it at the lowest level
     * possible and provide useful feedback, identifying the config field if present.
     * <p>
     * This is ONLY called for sets that allow values (i.e., maps), and MUST be overridden for those types of sets.
     *
     * @return The freshly loaded default entry, or null if the line is invalid.
     */
    @Nullable
    protected FuzzyKey<T> loadValueForDefault( @Nullable AbstractConfigField field, String line, String value ) {
        throw new IllegalArgumentException( "Default entry does not know how to load values!" );
    }
    
    /** Loads this value from the given list. If anything goes wrong, correct it at the lowest level possible. */
    public void load( @Nullable AbstractConfigField field, List<String> value ) {
        final ArrayList<FuzzyKey<T>> list = new ArrayList<>( value.size() );
        // Used to exclude repeat keys (blacklist and/or value ignored)
        final Set<FuzzyKey<T>> loadedKeys = new HashSet<>( value.size() );
        boolean foundDefault = false;
        for( String line : value ) {
            // Print a warning for every entry after the default - these are all "dead code"
            if( foundDefault ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Unreachable entry (defined after \"{}\" key): \"{}\"",
                        FuzzyKey.DEFAULT_KEY, line );
            }
            // Parse the line
            String[] keyAndValue = FuzzyKey.getKeyAndValue( line );
            String lineValue = keyAndValue.length > 1 ? keyAndValue[1].trim() : null;
            boolean blacklist = lineValue != null && lineValue.equalsIgnoreCase( FuzzyKey.BLACKLIST_VALUE );
            // Handle default key loading
            if( keyAndValue[0].equalsIgnoreCase( FuzzyKey.DEFAULT_KEY ) ) {
                if( blacklist ) {
                    ConfigUtil.warnFor( field );
                    ConfigUtil.LOG.warn( "Default key defined as blacklist - this is not allowed! Deleting." );
                }
                else {
                    final FuzzyKey<T> entry = loadDefaultEntry( field, line, lineValue );
                    if( entry != null ) {
                        foundDefault = true;
                        list.add( entry );
                    }
                }
            }
            else {
                // Load all other key types
                if( !blacklist && !allowsValues() && lineValue != null ) {
                    ConfigUtil.warnFor( field );
                    ConfigUtil.LOG.warn( "Values not allowed for this field! Deleting value \"{}\". Entry: {}",
                            lineValue, line );
                }
                final FuzzyKey<T> entry = loadEntry( field, line, keyAndValue[0], lineValue, blacklist );
                if( entry != null ) {
                    if( !loadedKeys.add( entry ) ) {
                        ConfigUtil.warnFor( field );
                        ConfigUtil.LOG.warn( "Unreachable entry (duplicate key): \"{}\"", line );
                    }
                    list.add( entry );
                }
            }
        }
        // Tidy up and set value
        list.trimToSize();
        setList( list );
    }
    
    /** @return The freshly loaded default entry, or null if the line is invalid. */
    @Nullable
    private FuzzyKey<T> loadDefaultEntry( @Nullable AbstractConfigField field, String line, @Nullable String value ) {
        if( value == null ) {
            if( allowsValues() ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Default key missing value - this does nothing! Deleting entry." );
                return null;
            }
        }
        else {
            if( allowsValues() ) {
                return loadValueForDefault( field, line, value );
            }
            ConfigUtil.warnFor( field );
            ConfigUtil.LOG.warn( "Default key has value - this does nothing! Deleting value \"{}\". Entry: {}",
                    value, line );
        }
        return DefaultKey.get();
    }
    
    /** @return True if values are generally allowed. */
    protected boolean allowsValues() { return false; } // Just override this for map-type implementations
    
    
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