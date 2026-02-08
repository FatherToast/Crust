package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.collection.key.DefaultKey;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.StringKey;
import fathertoast.crust.api.config.common.value.collection.key.IFuzzyKeyParser;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * An ordered set of entries represented in files by a string array. The primary way
 * to use this is by calling {@link #contains(T)}.
 * <p>
 * This set allows each key to match any number of elements, and are checked in the order defined,
 * the first matching key is taken as the result. If the matching key is a blacklist type, it is
 * treated as if no match was found.
 * <p>
 * Fuzzy sets are intended to allow users to define which target objects to include (for example, which
 * block states to look for).
 * <p>
 * This implementation is semi-fixed to protect against inadvertent modification, but allows
 * direct {@link #load} operations to make it easier to use in non-config applications (e.g., NBT).
 *
 * @param <T> The type to match against.
 * @see FuzzyKey
 * @see IFuzzyKeyParser
 * @see fathertoast.crust.api.config.common.field.collection.FuzzySetField
 * @see FuzzyMap FuzzyMap - A similar collection that allows values
 */
@ApiStatus.Experimental
public class FuzzySet<T> extends AbstractFuzzyCollection<T, FuzzyKey<T>> {
    
    /** Constructs an empty set. Use this if you want to {@link #load} a set from file/NBT. */
    public FuzzySet( IFuzzyKeyParser<T> parser ) { super( parser ); }
    
    /** Constructs a set containing the keys provided. Use this for creating default values during config definition. */
    @SafeVarargs
    public FuzzySet( IFuzzyKeyParser<T> parser, FuzzyKey<T>... keys ) { super( parser, keys ); }
    
    /** Constructs a set containing the keys provided. Use this for creating default values during config definition. */
    public FuzzySet( IFuzzyKeyParser<T> parser, Collection<? extends FuzzyKey<T>> keys ) { super( parser, keys ); }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public FuzzySet<T> makeNew() { return new FuzzySet<>( keyParser ); }
    
    
    /** @return How this fuzzy collection intends to use its keys. */
    @Override
    public KeyUsage keyUsage() { return KeyUsage.MATCH; }
    
    /**
     * Loads this value from the given list. If anything goes wrong, correct it at the lowest level possible.
     * If the field is null, error reporting is suppressed.
     *
     * @param field The config field we are loading for, or null if not loading from a config.
     * @param value List value to load from. This generally comes from a TOML string array value
     *              (config loading) or a string list tag (NBT loading).
     */
    @Override
    public void load( @Nullable AbstractConfigField field, List<String> value ) {
        super.load( field, value );
        checkUnreachableForMatching( field, this );
    }
    
    /** @return The freshly loaded entry, or null if the line should be deleted. */
    @Override
    @Nullable
    public FuzzyKey<T> loadLine( @Nullable AbstractConfigField field, String line ) {
        return keyUsage().ifAllowed( FuzzyKey.parseLine( keyParser, field, line ) );
    }
    
    
    /** Boilerplate builder class for fuzzy sets. */
    @ApiStatus.Experimental
    public static abstract class AbstractBuilder<T, C extends FuzzySet<T>, B extends AbstractBuilder<T, C, B>>
            extends AbstractFuzzyCollection.AbstractBuilder<T, FuzzyKey<T>, C, B> {
        
        /** @return A new fuzzy set (with a default key) reflecting the current state of this builder. */
        public C buildWithDefault() { return add( DefaultKey.get() ).build(); }
        
        /** Adds a pre-constructed key. */
        @Override
        public B add( FuzzyKey<T> key ) {
            if( KeyUsage.MATCH.allowsKey( key ) ) return super.add( key );
            throw new IllegalArgumentException( "Key type not allowed for this usage!" );
        }
    }
    
    /** Builder class for a generic fuzzy set. */
    @ApiStatus.Experimental
    public static class Builder<T, B extends Builder<T, B>> extends AbstractBuilder<T, FuzzySet<T>, B> {
        
        public final IFuzzyKeyParser<T> keyParser;
        
        public Builder( IFuzzyKeyParser<T> parser ) { keyParser = parser; }
        
        /** @return A new fuzzy set reflecting the current state of this builder. */
        @Override
        public FuzzySet<T> build() { return new FuzzySet<>( keyParser, list ); }
        
        /** Adds a parsed key. */
        public B add( String key ) { return add( Objects.requireNonNull( keyParser.parseKeyString( null, key, key, false ) ) ); }
        
        /** Adds a parsed blacklist key. */
        public B addBlacklist( String key ) { return add( Objects.requireNonNull( keyParser.parseKeyString( null, key, key, true ) ) ); }
    }
    
    /** Builder class for a fuzzy string set. */
    @ApiStatus.Experimental
    public static class StrBuilder extends Builder<String, StrBuilder> {
        
        public StrBuilder() { super( StringKey.PARSER ); }
        
        /** @return A new fuzzy set reflecting the current state of this builder. */
        @Override
        public FuzzySet<String> build() { return new FuzzySet<>( keyParser, list ); }
    }
}