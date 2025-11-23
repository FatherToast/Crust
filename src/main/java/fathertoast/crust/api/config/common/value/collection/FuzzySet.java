package fathertoast.crust.api.config.common.value.collection;


import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.field.FuzzySetField;
import fathertoast.crust.api.config.common.value.collection.key.DefaultKey;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.IFuzzyKeyParser;
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
 * @param <T> The type to match against.
 * @see FuzzyKey
 * @see FuzzySetField
 */
@ApiStatus.Experimental
public class FuzzySet<T> extends TomlStringList<FuzzyKey<T>> {
    
    /** This set's key parser. */
    protected final IFuzzyKeyParser<T> keyParser;
    
    /** Constructs an empty set. Use this if you want to {@link #load} a set from file/NBT. */
    public FuzzySet( IFuzzyKeyParser<T> parser ) { keyParser = parser; }
    
    /** Constructs a set containing the keys provided. Use this for creating default values during config definition. */
    @SafeVarargs
    public FuzzySet( IFuzzyKeyParser<T> parser, FuzzyKey<T>... keys ) {
        super( keys );
        keyParser = parser;
        validate();
    }
    
    /** Constructs a set containing the keys provided. Use this for creating default values during config definition. */
    public FuzzySet( IFuzzyKeyParser<T> parser, Collection<? extends FuzzyKey<T>> keys ) {
        super( keys );
        keyParser = parser;
        validate();
    }
    
    /** @return A fresh, empty set of the same type as this one. */
    public FuzzySet<T> makeNew() { return new FuzzySet<>( keyParser ); }
    
    
    /** @return The field's type name. */
    public String getTypeName() { return keyParser.getTypeName(); }
    
    /** @return The field's key patterns (e.g., "\"pattern_1\", \"pattern_2\", \"pattern_n\""). */
    public String getKeyPatterns() { return keyParser.getPatterns(); }
    
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
        
        // Used to error report unreachable keys
        final Set<FuzzyKey<T>> loadedKeys = field == null ? null : new HashSet<>( value.size() );
        boolean foundDefault = false;
        
        for( String line : value ) {
            if( foundDefault ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Unreachable entry (defined after \"{}\" key): \"{}\"",
                        FuzzyKey.DEFAULT_KEY, line );
            }
            
            FuzzyKey<T> entry = loadLine( field, line );
            if( entry != null ) {
                list.add( entry );
                
                if( field != null ) {
                    if( entry.isDefault() ) foundDefault = true;
                    else if( !loadedKeys.add( entry ) ) {
                        ConfigUtil.warnFor( field );
                        ConfigUtil.LOG.warn( "Unreachable entry (duplicate key): \"{}\"", line );
                    }
                }
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
        String value = keyAndValue.length > 1 ? keyAndValue[1].trim() : null;
        boolean blacklist = value != null && value.equalsIgnoreCase( FuzzyKey.BLACKLIST_VALUE );
        // Load default keys
        if( keyAndValue[0].equalsIgnoreCase( FuzzyKey.DEFAULT_KEY ) ) {
            if( blacklist ) {
                if( field != null ) {
                    ConfigUtil.warnFor( field );
                    ConfigUtil.LOG.warn( "Default key defined as blacklist - this is not allowed! Deleting." );
                }
                return null;
            }
            return loadDefaultEntry( field, line, value );
        }
        // Load all other key types
        return loadEntry( field, line, keyAndValue[0], value, blacklist );
    }
    
    /** @return The freshly loaded default entry, or null if the line is invalid. */
    @Nullable
    protected FuzzyKey<T> loadDefaultEntry( @Nullable AbstractConfigField field, String line, @Nullable String value ) {
        if( field != null && value != null ) {
            ConfigUtil.warnFor( field );
            ConfigUtil.LOG.warn( "Values not allowed for this field! Deleting value \"{}\". Entry: {}",
                    value, line );
        }
        return DefaultKey.get();
    }
    
    /**
     * Loads an entry from the provided TOML string. If anything goes wrong, correct it at the lowest level possible.
     * If the config field is not null, provide useful feedback and identify the field.
     *
     * @return The freshly loaded entry, or null if the line is invalid.
     */
    @Nullable
    protected FuzzyKey<T> loadEntry( @Nullable AbstractConfigField field, String line, String key,
                                     @Nullable String value, boolean blacklist ) {
        if( field != null && !blacklist && value != null ) {
            ConfigUtil.warnFor( field );
            ConfigUtil.LOG.warn( "Values not allowed for this field! Deleting value \"{}\". Entry: {}",
                    value, line );
        }
        return keyParser.parseTomlString( field, line, key, blacklist );
    }
    
    
    /** @return True if the given target is contained within this set. */
    public boolean contains( T target ) { return getKey( target ) != null; }
    
    /** @return The first matching key, or null if no match was found or the match was a blacklist key. */
    @Nullable
    public FuzzyKey<T> getKey( T target ) {
        final List<FuzzyKey<T>> keys = getList();
        for( FuzzyKey<T> key : keys ) if( key.matches( target ) ) return key.isBlacklist() ? null : key;
        return null;
    }
    
    
    /**
     * Called after constructing any of the pre-populated constructors, with the expectation
     * that they are only used for creating default config values.
     * <p>
     * This checks for these common set construction errors:<p>
     * * Unreachable entries (duplicate keys / any entries after a default key)<p>
     * * Useless entries (trailing blacklist keys / null entries)
     *
     * @throws IllegalArgumentException If any errors are found.
     */
    private void validate() {
        final List<FuzzyKey<T>> list = getList();
        if( list.isEmpty() ) return;
        
        final Set<FuzzyKey<T>> loadedKeys = new HashSet<>( list.size() );
        boolean foundDefault = false;
        boolean lastWasBlacklist = true;
        for( FuzzyKey<T> entry : list ) {
            if( entry == null ) throw new IllegalArgumentException( "Null keys not allowed!" );
            
            if( foundDefault ) throw new IllegalArgumentException( "No keys allowed after a default key!" );
            if( entry.isDefault() ) foundDefault = true;
            
            if( loadedKeys.contains( entry ) )
                throw new IllegalArgumentException( "Duplicate keys not allowed! Duplicate entry: " + entry.toTomlString() );
            loadedKeys.add( entry );
            
            lastWasBlacklist = entry.isBlacklist();
        }
        // Check for trailing blacklist keys (also catches sets with no non-blacklist keys)
        if( lastWasBlacklist )
            throw new IllegalArgumentException( "Blacklist keys must be followed by at least one non-blacklist key!" );
    }
    
    
    /** Boilerplate builder class for fuzzy sets. */
    @ApiStatus.Experimental
    public static abstract class Builder<T, F extends FuzzySet<T>, B extends Builder<T, F, B>> {
        public final ArrayList<FuzzyKey<T>> list = new ArrayList<>();
        
        /** @return A new fuzzy set reflecting the current state of this builder. */
        public abstract F build();
        
        /** @return A new fuzzy set with a default key reflecting the current state of this builder. */
        public F buildWithDefault() {
            list.add( DefaultKey.get() );
            return build();
        }
        
        /** Adds a pre-constructed key. */
        public B add( FuzzyKey<T> key ) {
            list.add( key );
            //noinspection unchecked
            return (B) this;
        }
    }
    
    
    /** Builder class for a generic fuzzy set. */
    @ApiStatus.Experimental
    public static class GenericBuilder<T, B extends GenericBuilder<T, B>> extends Builder<T, FuzzySet<T>, B> {
        public final IFuzzyKeyParser<T> keyParser;
        
        public GenericBuilder( IFuzzyKeyParser<T> parser ) { keyParser = parser; }
        
        /** @return A new fuzzy set reflecting the current state of this builder. */
        @Override
        public FuzzySet<T> build() { return new FuzzySet<>( keyParser, list ); }
        
        /** Adds a parsed key. */
        public B add( String key ) { return add( Objects.requireNonNull( keyParser.parseTomlString( null, key, key, false ) ) ); }
    }
}