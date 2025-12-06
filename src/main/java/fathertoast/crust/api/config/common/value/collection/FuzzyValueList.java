package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.collection.key.*;
import fathertoast.crust.api.config.common.value.collection.value.FuzzyEntry;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import fathertoast.crust.api.lib.CrustMath;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Random;

/**
 * An ordered list of key-value entries represented in files by a string array. The primary way
 * to use this is by iterating through its {@link #entries()}.
 * <p>
 * Fuzzy value lists are intended to allow users to define a list of things with additional values
 * attached that should be iterated through to do something for each (for example, a list of mob
 * effects with amplifiers and durations).
 * <p>
 * This implementation is semi-fixed to protect against inadvertent modification, but allows
 * direct {@link #load} operations to make it easier to use in non-config applications (e.g., NBT).
 *
 * @param <T> The type of list.
 * @param <V> The value type.
 * @see FuzzyKey
 * @see IFuzzyKeyParser
 * @see FuzzyEntry
 * @see IValueCodec
 * @see fathertoast.crust.api.config.common.field.collection.FuzzyValueListField
 * @see FuzzyList FuzzyList - A similar collection that does not allow values
 */
public class FuzzyValueList<T, V> extends AbstractFuzzyCollection<T, FuzzyEntry<T, V>> {
    
    /** This map's value codec. */
    protected final IValueCodec<V> valueCodec;
    
    /** Constructs an empty map. Use this if you want to {@link #load} a map from file/NBT. */
    protected FuzzyValueList( IFuzzyKeyParser<T> parser, IValueCodec<V> codec ) {
        super( parser );
        valueCodec = codec;
    }
    
    /**
     * Constructs a map containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link AbstractBuilder} is much easier.
     */
    @SafeVarargs
    protected FuzzyValueList( IFuzzyKeyParser<T> parser, IValueCodec<V> codec, FuzzyEntry<T, V>... keys ) {
        super( parser, keys );
        valueCodec = codec;
    }
    
    /**
     * Constructs a map containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link AbstractBuilder} is much easier.
     */
    protected FuzzyValueList( IFuzzyKeyParser<T> parser, IValueCodec<V> codec, Collection<FuzzyEntry<T, V>> keys ) {
        super( parser, keys );
        valueCodec = codec;
    }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public FuzzyValueList<T, V> makeNew() { return new FuzzyValueList<>( keyParser, valueCodec ); }
    
    
    /** @return The field's value format (e.g., {@literal "<Number (Any Value)>"}). */
    public String getValueFormat() { return valueCodec.getFormat(); }
    
    
    /** @return How this fuzzy collection intends to use its keys. */
    @Override
    public KeyUsage keyUsage() { return KeyUsage.ITERATE; }
    
    /** @return The freshly loaded entry, or null if the line should be deleted. */
    @Override
    @Nullable
    public FuzzyEntry<T, V> loadLine( @Nullable AbstractConfigField field, String line ) {
        FuzzyEntry<T, V> loaded = FuzzyEntry.parseLine( keyParser, valueCodec, field, line );
        return loaded != null && keyUsage().allowsKey( loaded.getKey() ) ? loaded : null;
    }
    
    
    /**
     * @return An iterator over the key-value pairs represented by the keys in this list that can be used in
     * an enhanced for loop. The iterator skips over null objects, but it may still return null in some cases.
     */
    public KeyValueIterator<T, V> entries() { return new KeyValueIterator<>( this ); }
    
    
    /** Boilerplate builder class for fuzzy value lists. */
    @ApiStatus.Experimental
    public static abstract class AbstractBuilder<T, V, C extends FuzzyValueList<T, V>, B extends AbstractBuilder<T, V, C, B>>
            extends AbstractFuzzyCollection.AbstractBuilder<T, FuzzyEntry<T, V>, C, B> {
        
        public final IValueCodec<V> valueCodec;
        
        public AbstractBuilder( IValueCodec<V> codec ) { valueCodec = codec; }
        
        /** Adds a pre-constructed key-value pair. */
        public B put( FuzzyKey<T> key, V value ) { return add( FuzzyEntry.of( key, value, valueCodec ) ); }
        
        /** Adds a pre-constructed key. */
        @Override
        public B add( FuzzyEntry<T, V> key ) {
            if( KeyUsage.ITERATE.allowsKey( key.getKey() ) ) return super.add( key );
            throw new IllegalArgumentException( "Key type not allowed for this usage! " + key.getKey() );
        }
    }
    
    /** Builder class for a generic fuzzy value list. */
    @ApiStatus.Experimental
    public static class Builder<T, V, B extends Builder<T, V, B>> extends AbstractBuilder<T, V, FuzzyValueList<T, V>, B> {
        
        public final IFuzzyKeyParser<T> keyParser;
        
        public Builder( IFuzzyKeyParser<T> parser, IValueCodec<V> codec ) {
            super( codec );
            keyParser = parser;
        }
        
        /** @return A new fuzzy value list reflecting the current state of this builder. */
        @Override
        public FuzzyValueList<T, V> build() { return new FuzzyValueList<>( keyParser, valueCodec, list ); }
        
        /** Adds a parsed key-value pair. */
        public B put( String key, V value ) { return put( Objects.requireNonNull( keyParser.parseKeyString( null, key, key, false ) ), value ); }
    }
    
    /** Builder class for a fuzzy string-value list. */
    @ApiStatus.Experimental
    public static class StrBuilder<V> extends Builder<String, V, StrBuilder<V>> {
        
        public StrBuilder( IValueCodec<V> codec ) { super( StringKey.PARSER, codec ); }
        
        /** @return A new fuzzy value list reflecting the current state of this builder. */
        @Override
        public FuzzyValueList<String, V> build() { return new FuzzyValueList<>( keyParser, valueCodec, list ); }
    }
    
    
    /** Just used to return key-value pairs. */
    public record Pair<T, V>( T key, V value ) {
        /**
         * @return The result of a random roll against the value based on its type:<p>
         * Double/Float: Treats the value as a percent chance (from 0 to 1).<p>
         * Integer/Short/etc.: Treats the value as a 1-in-X chance (Note: long is truncated to int).<p>
         * Non-Number types (or no value found for target): Returns false.
         */
        public boolean rollChance( Random random ) { return CrustMath.rollChance( value(), random ); }
        
        /**
         * @return The result of a random roll against the value based on its type:<p>
         * Double/Float: Treats the value as a percent chance (from 0 to 1).<p>
         * Integer/Short/etc.: Treats the value as a 1-in-X chance (Note: long is truncated to int).<p>
         * Non-Number types (or no value found for target): Returns false.
         */
        public boolean rollChance( RandomSource random ) { return CrustMath.rollChance( value(), random ); }
    }
    
    /** A simple iterator over the objects represented by the keys, rather than over the keys themselves. */
    public static final class KeyValueIterator<T, V> implements Iterator<Pair<T, V>>, Iterable<Pair<T, V>> {
        
        private final Iterator<FuzzyEntry<T, V>> keyIterator;
        
        private Iterator<T> subIterator;
        private V subValue;
        
        private KeyValueIterator( FuzzyValueList<T, V> list ) { keyIterator = list.getList().listIterator(); }
        
        @Override // Iterable
        public Iterator<Pair<T, V>> iterator() { return this; }
        
        @Override
        public boolean hasNext() { return keyIterator.hasNext() || subIterator != null && subIterator.hasNext(); }
        
        @Override
        @Nullable
        public Pair<T, V> next() {
            // Use the sub-iterator, if one is active
            if( subIterator != null ) {
                if( subIterator.hasNext() ) return new Pair<>( subIterator.next(), subValue );
                subIterator = null;
                subValue = null;
            }
            // Otherwise, churn until we hit something
            do {
                FuzzyEntry<T, V> entry = keyIterator.next();
                // See if we should open a new sub-iterator
                if( entry.getKey() instanceof IMultiKey<?> ) {
                    @SuppressWarnings( "unchecked" )
                    Iterator<T> sub = ((IMultiKey<T>) entry.getKey()).getValueIterator();
                    if( sub != null && sub.hasNext() ) {
                        subIterator = sub;
                        subValue = entry.get();
                        return new Pair<>( sub.next(), subValue );
                    }
                }
                // Otherwise, assume it's a reverse key
                Pair<T, V> p = tryCast( entry );
                if( p != null ) return p;
            }
            while( hasNext() );
            return null;
        }
        
        @Nullable
        private Pair<T, V> tryCast( FuzzyEntry<T, V> entry ) {
            T k;
            try {
                //noinspection unchecked
                k = ((IReverseKey<T>) entry.getKey()).asValue();
            }
            catch( ClassCastException ex ) {
                ConfigUtil.LOG.error( "Somehow, an invalid iteration key was iterated! Entry: \"{}\", Fuzzy value list: {}",
                        entry, this, ex );
                k = null;
            }
            return k == null ? null : new Pair<>( k, entry.get() );
        }
    }
}