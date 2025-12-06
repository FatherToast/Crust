package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.StringKey;
import fathertoast.crust.api.config.common.value.collection.key.IFuzzyKeyParser;
import fathertoast.crust.api.config.common.value.collection.value.FuzzyEntry;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import fathertoast.crust.api.util.JavaRandomSource;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * An ordered map of key-value entries represented in files by a string array. The primary way
 * to use this is by calling {@link #get(T)}.
 * <p>
 * This map allows each key to match any number of elements, and are checked in the order defined, the
 * first matching key is taken as the result. If the matching key is a blacklist
 * type, it is treated as if no match was found.
 * <p>
 * Fuzzy maps are intended to allow users to define which target objects receive which values (for example,
 * how much damage a particular entity type should deal).
 * <p>
 * This implementation is semi-fixed to protect against inadvertent modification, but allows
 * direct {@link #load} operations to make it easier to use in non-config applications (e.g., NBT).
 *
 * @param <T> The type to match against.
 * @param <V> The value type.
 * @see FuzzyKey
 * @see IFuzzyKeyParser
 * @see FuzzyEntry
 * @see IValueCodec
 * @see fathertoast.crust.api.config.common.field.collection.FuzzyMapField
 * @see FuzzySet FuzzySet - A similar collection that does not allow values
 */
public class FuzzyMap<T, V> extends AbstractFuzzyCollection<T, FuzzyEntry<T, V>> {
    
    /** This map's value codec. */
    protected final IValueCodec<V> valueCodec;
    
    /** Constructs an empty map. Use this if you want to {@link #load} a map from file/NBT. */
    protected FuzzyMap( IFuzzyKeyParser<T> parser, IValueCodec<V> codec ) {
        super( parser );
        valueCodec = codec;
    }
    
    /**
     * Constructs a map containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link AbstractBuilder} is much easier.
     */
    @SafeVarargs
    protected FuzzyMap( IFuzzyKeyParser<T> parser, IValueCodec<V> codec, FuzzyEntry<T, V>... keys ) {
        super( parser, keys );
        valueCodec = codec;
    }
    
    /**
     * Constructs a map containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link AbstractBuilder} is much easier.
     */
    protected FuzzyMap( IFuzzyKeyParser<T> parser, IValueCodec<V> codec, Collection<FuzzyEntry<T, V>> keys ) {
        super( parser, keys );
        valueCodec = codec;
    }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public FuzzyMap<T, V> makeNew() { return new FuzzyMap<>( keyParser, valueCodec ); }
    
    
    /** @return The value for the given target, or null if the target is not contained in this map. */
    @Nullable
    public V get( T target ) {
        FuzzyEntry<T, V> entry = getEntry( target );
        return entry == null ? null : entry.get();
    }
    
    /** @return The first matching entry, or null if no match was found or the match was a blacklist entry. */
    @Nullable
    public FuzzyEntry<T, V> getEntry( T target ) { return getKey( target ); }
    
    /**
     * @return Gets the value for the given target and returns the result of a random roll
     * against it based on this map's value type:<p>
     * Double/Float: Treats the value as a percent chance (from 0 to 1).<p>
     * Integer/Short/etc.: Treats the value as a 1-in-X chance (Note: long is truncated to int).<p>
     * Non-Number types (or no value found for target): Returns false.
     */
    public boolean rollChance( T target, Random random ) { return rollChance( target, JavaRandomSource.of( random ) ); }
    
    /**
     * @return Gets the value for the given target and returns the result of a random roll
     * against it based on this map's value type:<p>
     * Double/Float: Treats the value as a percent chance (from 0 to 1).<p>
     * Integer/Short/etc.: Treats the value as a 1-in-X chance (Note: long is truncated to int).<p>
     * Non-Number types (or no value found for target): Returns false.
     */
    public boolean rollChance( T target, RandomSource random ) {
        return get( target ) instanceof Number n &&
                (n instanceof Double || n instanceof Float ? random.nextDouble() < n.doubleValue() :
                        n.intValue() > 0 && random.nextInt( n.intValue() ) == 0);
    }
    
    
    /** @return The field's value format (e.g., {@literal "<Number (Any Value)>"}). */
    public String getValueFormat() { return valueCodec.getFormat(); }
    
    
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
    public FuzzyEntry<T, V> loadLine( @Nullable AbstractConfigField field, String line ) {
        FuzzyEntry<T, V> loaded = FuzzyEntry.parseLine( keyParser, valueCodec, field, line );
        return loaded != null && keyUsage().allowsKey( loaded.getKey() ) ? loaded : null;
    }
    
    
    /** Boilerplate builder class for fuzzy maps. */
    @ApiStatus.Experimental
    public static abstract class AbstractBuilder<T, V, C extends FuzzyMap<T, V>, B extends AbstractBuilder<T, V, C, B>>
            extends AbstractFuzzyCollection.AbstractBuilder<T, FuzzyEntry<T, V>, C, B> {
        
        public final IValueCodec<V> valueCodec;
        
        public AbstractBuilder( IValueCodec<V> codec ) { valueCodec = codec; }
        
        /** @return A new fuzzy map (with a default key) reflecting the current state of this builder. */
        public C buildWithDefault( V value ) { return add( FuzzyEntry.ofDefault( value, valueCodec ) ).build(); }
        
        /** Adds a pre-constructed key-value pair. */
        public B put( FuzzyKey<T> key, V value ) { return add( FuzzyEntry.of( key, value, valueCodec ) ); }
        
        /** Adds a pre-constructed blacklist key. */
        public B putBlacklist( FuzzyKey<T> key ) { return add( FuzzyEntry.ofBlacklist( key ) ); }
        
        /** Adds a pre-constructed key. */
        @Override
        public B add( FuzzyEntry<T, V> key ) {
            if( KeyUsage.MATCH.allowsKey( key.getKey() ) ) return super.add( key );
            throw new IllegalArgumentException( "Key type not allowed for this usage!" );
        }
    }
    
    /** Builder class for a generic fuzzy map. */
    @ApiStatus.Experimental
    public static class Builder<T, V, B extends Builder<T, V, B>> extends AbstractBuilder<T, V, FuzzyMap<T, V>, B> {
        
        public final IFuzzyKeyParser<T> keyParser;
        
        public Builder( IFuzzyKeyParser<T> parser, IValueCodec<V> codec ) {
            super( codec );
            keyParser = parser;
        }
        
        /** @return A new fuzzy map reflecting the current state of this builder. */
        @Override
        public FuzzyMap<T, V> build() { return new FuzzyMap<>( keyParser, valueCodec, list ); }
        
        /** Adds a parsed key-value pair. */
        public B put( String key, V value ) { return put( Objects.requireNonNull( keyParser.parseKeyString( null, key, key, false ) ), value ); }
        
        /** Adds a parsed blacklist key. */
        public B putBlacklist( String key ) { return putBlacklist( Objects.requireNonNull( keyParser.parseKeyString( null, key, key, true ) ) ); }
    }
    
    /** Builder class for a fuzzy string map. */
    @ApiStatus.Experimental
    public static class StrBuilder<V> extends Builder<String, V, StrBuilder<V>> {
        
        public StrBuilder( IValueCodec<V> codec ) { super( StringKey.PARSER, codec ); }
        
        /** @return A new fuzzy map reflecting the current state of this builder. */
        @Override
        public FuzzyMap<String, V> build() { return new FuzzyMap<>( keyParser, valueCodec, list ); }
    }
}