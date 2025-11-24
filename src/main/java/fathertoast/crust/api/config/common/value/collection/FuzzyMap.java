package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.IFuzzyKeyParser;
import fathertoast.crust.api.config.common.value.collection.value.FuzzyEntry;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * An ordered map of key-value entries represented in files by a string array. This map allows each key to match
 * any number of elements, and are checked in the order defined, the first matching key is taken as the result.
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
 * @see fathertoast.crust.api.config.common.field.FuzzyMapField
 * @see FuzzySet FuzzySet - A similar collection that does not allow values
 */
public class FuzzyMap<T, V> extends FuzzySet<T> {
    
    /** This map's value codec. */
    protected final IValueCodec<V> valueCodec;
    
    /** Constructs an empty map. Use this if you want to {@link #load} a map from file/NBT. */
    protected FuzzyMap( IFuzzyKeyParser<T> parser, IValueCodec<V> codec ) {
        super( parser );
        valueCodec = codec;
    }
    
    /**
     * Constructs a map containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link Builder} is much easier.
     */
    @SafeVarargs
    protected FuzzyMap( IFuzzyKeyParser<T> parser, IValueCodec<V> codec, FuzzyEntry<T, V>... keys ) {
        super( parser, keys );
        valueCodec = codec;
    }
    
    /**
     * Constructs a map containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link Builder} is much easier.
     */
    protected FuzzyMap( IFuzzyKeyParser<T> parser, IValueCodec<V> codec, Collection<FuzzyEntry<T, V>> keys ) {
        super( parser, keys );
        valueCodec = codec;
    }
    
    /** @return A fresh, empty map of the same type as this one. */
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
    public FuzzyEntry<T, V> getEntry( T target ) { return (FuzzyEntry<T, V>) getKey( target ); }
    
    
    /** @return The field's value format (e.g., {@literal "<Number (Any Value)>"}). */
    public String getValueFormat() { return valueCodec.getFormat(); }
    
    /** @return The freshly loaded default entry, or null if the line is invalid. */
    @Nullable
    @Override
    protected FuzzyEntry<T, V> loadDefaultEntry( @Nullable AbstractConfigField field, String line, @Nullable String value ) {
        return FuzzyEntry.ofDefault( loadValue( field, line, value ), valueCodec );
    }
    
    /**
     * Loads an entry from the provided TOML string. If anything goes wrong, correct it at the lowest level possible.
     * If the config field is not null, provide useful feedback and identify the field.
     *
     * @return The freshly loaded entry, or null if the line is invalid.
     */
    @Override
    @Nullable
    protected FuzzyEntry<T, V> loadEntry( @Nullable AbstractConfigField field, String line, String key,
                                          @Nullable String value, boolean blacklist ) {
        FuzzyKey<T> loadedKey = keyParser.parseTomlString( field, line, key, blacklist );
        return loadedKey == null ? null : blacklist ? FuzzyEntry.ofBlacklist( loadedKey ) :
                FuzzyEntry.of( loadedKey, loadValue( field, line, value ), valueCodec );
    }
    
    /**
     * Loads a value from the provided TOML string. If anything goes wrong, correct it at the lowest level possible.
     * If the config field is not null, provide useful feedback and identify the field.
     *
     * @return The freshly loaded value.
     */
    protected V loadValue( @Nullable AbstractConfigField field, String line, @Nullable String value ) {
        return valueCodec.parseTomlString( field, line, value );
    }
    
    
    /** Boilerplate builder class for fuzzy maps. */
    @ApiStatus.Experimental
    public static abstract class Builder<T, V, F extends FuzzyMap<T, V>, B extends Builder<T, V, F, B>> {
        public final ArrayList<FuzzyEntry<T, V>> list = new ArrayList<>();
        public final IValueCodec<V> valueCodec;
        
        public Builder( IValueCodec<V> codec ) { valueCodec = codec; }
        
        /** @return A new fuzzy map reflecting the current state of this builder. */
        public abstract F build();
        
        /** @return A new fuzzy map with a default key-value pair reflecting the current state of this builder. */
        public F buildWithDefault( V value ) {
            add( FuzzyEntry.ofDefault( value, valueCodec ) );
            return build();
        }
        
        /** Adds a pre-constructed key-value pair. */
        public B put( FuzzyKey<T> key, V value ) { return add( FuzzyEntry.of( key, value, valueCodec ) ); }
        
        /** Adds a pre-constructed blacklist key. */
        public B putBlacklist( FuzzyKey<T> key ) { return add( FuzzyEntry.ofBlacklist( key ) ); }
        
        /** Adds a pre-constructed entry. */
        public B add( FuzzyEntry<T, V> entry ) {
            list.add( entry );
            //noinspection unchecked
            return (B) this;
        }
    }
    
    /** Builder class for a generic fuzzy map. */
    @ApiStatus.Experimental
    public static class GenericBuilder<T, V, B extends GenericBuilder<T, V, B>> extends Builder<T, V, FuzzyMap<T, V>, B> {
        public final IFuzzyKeyParser<T> keyParser;
        
        public GenericBuilder( IFuzzyKeyParser<T> parser, IValueCodec<V> codec ) {
            super( codec );
            keyParser = parser;
        }
        
        /** @return A new fuzzy set reflecting the current state of this builder. */
        @Override
        public FuzzyMap<T, V> build() { return new FuzzyMap<>( keyParser, valueCodec, list ); }
        
        /** Adds a parsed key-value pair. */
        public B put( String key, V value ) { return put( Objects.requireNonNull( keyParser.parseTomlString( null, key, key, false ) ), value ); }
        
        /** Adds a parsed blacklist key. */
        public B put( String key ) { return putBlacklist( Objects.requireNonNull( keyParser.parseTomlString( null, key, key, true ) ) ); }
    }
}