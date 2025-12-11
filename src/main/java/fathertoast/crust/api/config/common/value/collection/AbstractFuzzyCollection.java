package fathertoast.crust.api.config.common.value.collection;


import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.IFuzzyKeyParser;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.*;

/**
 * An ordered set of values represented in files by a string array.
 * <p>
 * This implementation is semi-fixed to protect against inadvertent modification, but allows
 * direct {@link #load} operations to make it easier to use in non-config applications (e.g., NBT).
 *
 * @param <T> The collection type.
 * @param <K> The type of fuzzy key.
 * @see FuzzySet
 * @see FuzzyMap
 * @see FuzzyList
 * @see FuzzyValueList
 * @see FuzzyWeightedList
 * @see FuzzyWeightedValueList
 * @see fathertoast.crust.api.config.common.field.collection.AbstractFuzzyCollectionField
 */
@ApiStatus.Experimental
public abstract class AbstractFuzzyCollection<T, K extends FuzzyKey<T>> extends TomlStringList<K> {
    
    /** This collection's fuzzy key parser. */
    protected final IFuzzyKeyParser<T> keyParser;
    
    /** Constructs an empty set. Use this if you want to {@link #load} a set from file/NBT. */
    public AbstractFuzzyCollection( IFuzzyKeyParser<T> parser ) { keyParser = parser; }
    
    /** Constructs a set containing the keys provided. Use this for creating default values during config definition. */
    @SafeVarargs
    public AbstractFuzzyCollection( IFuzzyKeyParser<T> parser, K... keys ) {
        super( keys );
        keyParser = parser;
        validate();
    }
    
    /** Constructs a set containing the keys provided. Use this for creating default values during config definition. */
    public AbstractFuzzyCollection( IFuzzyKeyParser<T> parser, Collection<? extends K> keys ) {
        super( keys );
        keyParser = parser;
        validate();
    }
    
    /** @return A fresh, empty collection of the same type as this one. */
    public abstract AbstractFuzzyCollection<T, K> makeNew();
    
    
    /** @return The field's type name. */
    public String getTypeName() { return keyParser.getTypeName(); }
    
    /** @return The field's key patterns (e.g., "\"pattern_1\", \"pattern_2\", \"pattern_n\""). */
    public String getKeyPatterns() { return keyParser.getPatterns( keyUsage() ); }
    
    
    /** @return How this fuzzy collection intends to use its keys. */
    public abstract KeyUsage keyUsage();
    
    /**
     * Loads this value from the given list. If anything goes wrong, correct it at the lowest level possible.
     * If the field is null, error reporting is suppressed.
     *
     * @param field The config field we are loading for, or null if not loading from a config.
     * @param value List value to load from. This generally comes from a TOML string array value
     *              (config loading) or a string list tag (NBT loading).
     */
    public void load( @Nullable AbstractConfigField field, List<String> value ) {
        final ArrayList<K> list = new ArrayList<>( value.size() );
        for( String line : value ) {
            K entry = loadLine( field, line );
            if( entry != null ) list.add( entry );
        }
        
        // Tidy up and set value
        list.trimToSize();
        setList( list );
    }
    
    /** @return The freshly loaded entry, or null if the line should be deleted. */
    @Nullable
    public abstract K loadLine( @Nullable AbstractConfigField field, String line );
    
    
    /** @return True if the given target is contained within this collection. */
    public boolean contains( T target ) { return getKey( target ) != null; }
    
    /** @return The first matching key, or null if no match was found or the match was a blacklist key. */
    @Nullable
    public K getKey( T target ) {
        for( K key : this ) if( key.matches( target ) ) return key.isBlacklist() ? null : key;
        return null;
    }
    
    
    /**
     * Called after constructing any of the pre-populated constructors, with the expectation
     * that they are only used for creating default config values.
     * <p>
     * This checks for common construction errors, such as unreachable or useless keys.
     *
     * @throws IllegalArgumentException If any errors are found.
     */
    protected void validate() {
        if( isEmpty() || keyUsage() != KeyUsage.MATCH ) return; // We only need to validate matchers
        
        final Set<FuzzyKey<T>> loadedKeys = new HashSet<>( size() );
        boolean foundDefault = false;
        boolean lastWasBlacklist = true;
        for( FuzzyKey<T> entry : this ) {
            if( entry == null ) throw new IllegalArgumentException( "Null keys not allowed!" );
            
            if( foundDefault ) throw new IllegalArgumentException( "No keys allowed after a default key!" );
            if( entry.isDefault() ) foundDefault = true;
            
            if( loadedKeys.contains( entry ) )
                throw new IllegalArgumentException( "Duplicate keys not allowed! Duplicate entry: " + entry.toTomlString() );
            loadedKeys.add( entry );
            
            lastWasBlacklist = entry.isBlacklist();
        }
        // Check for trailing blacklist keys (also catches sets with no non-blacklist keys)
        if( lastWasBlacklist ) {
            throw new IllegalArgumentException( "Blacklist keys must be followed by at least one non-blacklist key!" );
        }
    }
    
    /**
     * Scans through the collection for unreachable keys and outputs that info to the log, if needed.
     * Can only identify obviously unreachable keys; things like tags hiding registry keys are not picked up.
     * Only valid when using keys for matching (i.e., {@link KeyUsage#MATCH}).
     */
    protected static void checkUnreachableForMatching( @Nullable AbstractConfigField field, AbstractFuzzyCollection<?, ?> fuzz ) {
        if( field == null || fuzz.isEmpty() ) return;
        
        Set<FuzzyKey<?>> loadedKeys = new HashSet<>();
        int foundDefault = 0;
        boolean lastWasBlacklist = false;
        for( FuzzyKey<?> key : fuzz ) {
            if( foundDefault == 1 ) {
                foundDefault = 2;
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Unreachable entry/entries (defined after \"{}\" key)! First unreachable entry: {}",
                        FuzzyKey.DEFAULT_KEY, key );
            }
            
            if( key.isDefault() ) {
                if( foundDefault == 0 ) foundDefault = 1;
            }
            else if( !loadedKeys.add( key ) ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Unreachable entry (duplicate key): {}", key );
            }
            
            lastWasBlacklist = key.isBlacklist();
        }
        // Check for trailing blacklist keys (also catches sets with no non-blacklist keys)
        if( lastWasBlacklist ) {
            ConfigUtil.warnFor( field );
            ConfigUtil.LOG.warn( "Useless key(s)! Blacklist keys at the end of a set/map are useless." );
        }
    }
    
    
    /** Boilerplate builder class for fuzzy collections. */
    @ApiStatus.Experimental
    public static abstract class AbstractBuilder<T, K extends FuzzyKey<T>, C extends AbstractFuzzyCollection<T, K>,
            B extends AbstractBuilder<T, K, C, B>> {
        
        public final ArrayList<K> list = new ArrayList<>();
        
        /** @return A new fuzzy collection reflecting the current state of this builder. */
        public abstract C build();
        
        /** Adds a pre-constructed key. */
        public B add( K key ) {
            list.add( key );
            //noinspection unchecked
            return (B) this;
        }
    }
}