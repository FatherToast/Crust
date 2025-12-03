package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.field.collection.WeightedValueListField;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.IFuzzyKeyParser;
import fathertoast.crust.api.config.common.value.collection.key.IRandomKey;
import fathertoast.crust.api.config.common.value.collection.value.FuzzyEntry;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import fathertoast.crust.api.config.common.value.collection.value.WeightedEntry;
import fathertoast.crust.api.util.JavaRandomSource;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;
import java.util.Random;

/**
 * An ordered list of weighted key-value entries represented in files by a string array. The primary way
 * to use this is by calling {@link #next(RandomSource)}.
 * <p>
 * Weighted value lists are intended to allow users to define a list of things with additional values
 * attached that should be polled at random (for example, pick a random item with stack size).
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
 * @see WeightedValueListField
 * @see WeightedList WeightedList - A similar collection that does not allow values
 */
public class WeightedValueList<T, V> extends AbstractFuzzyCollection<T, WeightedEntry<T, V>> {
    
    /** This map's value codec. */
    protected final IValueCodec<V> valueCodec;
    
    /** The sum of all elements' weights. */
    private int totalWeight;
    
    /** Constructs an empty map. Use this if you want to {@link #load} a map from file/NBT. */
    protected WeightedValueList( IFuzzyKeyParser<T> parser, IValueCodec<V> codec ) {
        super( parser );
        valueCodec = codec;
    }
    
    /**
     * Constructs a map containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link AbstractBuilder} is much easier.
     */
    @SafeVarargs
    protected WeightedValueList( IFuzzyKeyParser<T> parser, IValueCodec<V> codec, WeightedEntry<T, V>... keys ) {
        super( parser, keys );
        valueCodec = codec;
    }
    
    /**
     * Constructs a map containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link AbstractBuilder} is much easier.
     */
    protected WeightedValueList( IFuzzyKeyParser<T> parser, IValueCodec<V> codec, Collection<WeightedEntry<T, V>> keys ) {
        super( parser, keys );
        valueCodec = codec;
    }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public WeightedValueList<T, V> makeNew() { return new WeightedValueList<>( keyParser, valueCodec ); }
    
    
    /** @return The field's value format (e.g., {@literal "<Number (Any Value)>"}). */
    public String getValueFormat() { return valueCodec.getFormat(); }
    
    
    /** @return How this fuzzy collection intends to use its keys. */
    @Override
    public KeyUsage keyUsage() { return KeyUsage.POLL; }
    
    /** @return The freshly loaded entry, or null if the line should be deleted. */
    @Override
    @Nullable
    public WeightedEntry<T, V> loadLine( @Nullable AbstractConfigField field, String line ) {
        WeightedEntry<T, V> loaded = WeightedEntry.parseLine( keyParser, valueCodec, field, line );
        return keyUsage().allowsKey( loaded.getKey() ) ? loaded : null;
    }
    
    
    /** @return True if this weighted list is enabled (it is non-empty and its total weight is positive). */
    public boolean isEnabled() { return totalWeight > 0; }
    
    /**
     * @return A randomly chosen element from this list with its value, or null if a null entry is
     * selected or if the list is empty or disabled (all weights are 0).
     */
    @Nullable
    public FuzzyValueList.Pair<T, V> next( Random random ) { return next( JavaRandomSource.of( random ) ); }
    
    /**
     * @return A randomly chosen element from this list with its value, or null if a null entry is
     * selected or if the list is empty or disabled (all weights are 0).
     */
    @Nullable
    public FuzzyValueList.Pair<T, V> next( RandomSource random ) {
        if( isEnabled() ) {
            int choice = random.nextInt( totalWeight );
            for( WeightedEntry<T, V> entry : this ) {
                choice -= entry.getWeight();
                if( choice < 0 ) {
                    T k;
                    try {
                        //noinspection unchecked
                        k = ((IRandomKey<T>) entry.getKey()).nextValue( random );
                    }
                    catch( ClassCastException ex ) {
                        ConfigUtil.LOG.error( "Somehow, an invalid poll key was polled! Entry: \"{}\", Weighted value list: {}",
                                entry, this, ex );
                        k = null;
                    }
                    return k == null ? null : new FuzzyValueList.Pair<>( k, entry.get() );
                }
            }
            ConfigUtil.LOG.error( "Weight error! Big oof. Weighted value list: {}", this );
        }
        return null;
    }
    
    /** Calculates this list's total weight and caches it for later. */
    private void calculateTotalWeight() {
        if( isEmpty() ) {
            totalWeight = 0;
        }
        else {
            int weight = 0;
            for( WeightedEntry<T, V> entry : this ) {
                weight += entry.getWeight();
            }
            totalWeight = weight;
        }
    }
    
    
    /** Boilerplate builder class for fuzzy value lists. */
    @ApiStatus.Experimental
    public static abstract class AbstractBuilder<T, V, C extends WeightedValueList<T, V>, B extends AbstractBuilder<T, V, C, B>>
            extends AbstractFuzzyCollection.AbstractBuilder<T, WeightedEntry<T, V>, C, B> {
        
        public final IValueCodec<V> valueCodec;
        
        public AbstractBuilder( IValueCodec<V> codec ) { valueCodec = codec; }
        
        /**
         * @return A new weighted list (with a null key) reflecting the current state of this builder.
         * A null key is a chance to pick nothing. Multiple are allowed, but discouraged.
         */
        public C buildWithNull( int weight ) { return add( WeightedEntry.ofNull( weight ) ).build(); }
        
        /** Adds a pre-constructed weight and key-value pair. */
        public B put( int weight, FuzzyKey<T> key, V value ) { return add( WeightedEntry.of( weight, key, value, valueCodec ) ); }
        
        /** Adds a pre-constructed key. */
        @Override
        public B add( WeightedEntry<T, V> key ) {
            if( KeyUsage.POLL.allowsKey( key.getKey() ) ) return super.add( key );
            throw new IllegalArgumentException( "Key type not allowed for this usage! " + key.getKey() );
        }
    }
    
    /** Builder class for a generic fuzzy value list. */
    @ApiStatus.Experimental
    public static class Builder<T, V, B extends Builder<T, V, B>> extends AbstractBuilder<T, V, WeightedValueList<T, V>, B> {
        
        public final IFuzzyKeyParser<T> keyParser;
        
        public Builder( IFuzzyKeyParser<T> parser, IValueCodec<V> codec ) {
            super( codec );
            keyParser = parser;
        }
        
        /** @return A new fuzzy map reflecting the current state of this builder. */
        @Override
        public WeightedValueList<T, V> build() { return new WeightedValueList<>( keyParser, valueCodec, list ); }
        
        /** Adds a parsed key-value pair. */
        public B put( int weight, String key, V value ) {
            return put( weight, Objects.requireNonNull(
                    keyParser.parseKeyString( null, key, key, false ) ), value );
        }
    }
}