package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.field.collection.FuzzyWeightedListField;
import fathertoast.crust.api.config.common.value.collection.key.*;
import fathertoast.crust.api.util.JavaRandomSource;
import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * An ordered list of weighted entries represented in files by a string array. The primary way
 * to use this is by calling {@link #next(RandomSource)}.
 * <p>
 * Weighted lists are intended to allow users to define a list of things that should be polled at
 * random (for example, pick a random configured feature).
 * <p>
 * This implementation is semi-fixed to protect against inadvertent modification, but allows
 * direct {@link #load} operations to make it easier to use in non-config applications (e.g., NBT).
 *
 * @param <T> The type of list.
 * @see FuzzyKey
 * @see IFuzzyKeyParser
 * @see FuzzyWeightedListField
 * @see FuzzyWeightedValueList WeightedValueList - A similar collection that allows values
 */
@SuppressWarnings( "unused" )
public class FuzzyWeightedList<T> extends AbstractFuzzyCollection<T, WeightedKey<T>> {
    
    /** The sum of all elements' weights. */
    private int totalWeight;
    
    /** Constructs an empty list. Use this if you want to {@link #load} a set from file/NBT. */
    public FuzzyWeightedList( IFuzzyKeyParser<T> parser ) { super( parser ); }
    
    /** Constructs a list containing the keys provided. Use this for creating default values during config definition. */
    @SafeVarargs
    public FuzzyWeightedList( IFuzzyKeyParser<T> parser, WeightedKey<T>... keys ) { super( parser, keys ); }
    
    /** Constructs a list containing the keys provided. Use this for creating default values during config definition. */
    public FuzzyWeightedList( IFuzzyKeyParser<T> parser, Collection<? extends WeightedKey<T>> keys ) { super( parser, keys ); }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public FuzzyWeightedList<T> makeNew() { return new FuzzyWeightedList<>( keyParser ); }
    
    
    /** @return How this fuzzy collection intends to use its keys. */
    @Override
    public KeyUsage keyUsage() { return KeyUsage.POLL; }
    
    /** Call this to set the list value during {@link #load(IConfigField, List)}. */
    @Override
    protected void setList( Collection<? extends WeightedKey<T>> newList ) {
        super.setList( newList );
        calculateTotalWeight();
    }
    
    /** @return The freshly loaded entry, or null if the line should be deleted. */
    @Override
    @Nullable
    public WeightedKey<T> loadLine( @Nullable IConfigField<?> field, String line ) {
        return keyUsage().ifAllowed( WeightedKey.parseLine( keyParser, field, line ) );
    }
    
    
    /** @return True if this weighted list is enabled (it is non-empty and its total weight is positive). */
    public boolean isEnabled() { return totalWeight > 0; }
    
    /**
     * @return A randomly chosen element from this list, or null if a null entry is selected
     * or if the list is empty or disabled (all weights are 0).
     */
    @Nullable
    public T next( Random random ) { return next( JavaRandomSource.of( random ) ); }
    
    /**
     * @return A randomly chosen element from this list, or null if a null entry is selected
     * or if the list is empty or disabled (all weights are 0).
     */
    @Nullable
    public T next( RandomSource random ) {
        if( isEnabled() ) {
            int choice = random.nextInt( totalWeight );
            for( WeightedKey<T> key : this ) {
                choice -= key.getWeight();
                if( choice < 0 ) {
                    try {
                        //noinspection unchecked
                        return ((IRandomKey<T>) key.wrappedKey()).nextValue( random );
                    }
                    catch( ClassCastException ex ) {
                        ConfigUtil.LOG.error( "Somehow, an invalid poll key was polled! Entry: \"{}\", Weighted list: {}",
                                key, this, ex );
                    }
                    return null;
                }
            }
            ConfigUtil.LOG.error( "Weight error! Big oof. Weighted list: {}", this );
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
            for( WeightedKey<T> key : this ) {
                weight += key.getWeight();
            }
            totalWeight = weight;
        }
    }
    
    
    /** Boilerplate builder class for fuzzy lists. */
    public static abstract class AbstractBuilder<T, C extends FuzzyWeightedList<T>, B extends AbstractBuilder<T, C, B>>
            extends AbstractFuzzyCollection.AbstractBuilder<T, WeightedKey<T>, C, B> {
        
        /**
         * @return A new weighted list (with a null key) reflecting the current state of this builder.
         * A null key is a chance to pick nothing. Multiple are allowed, but discouraged.
         */
        public C buildWithNull( int weight ) { return add( WeightedKey.ofNull( weight ) ).build(); }
        
        /** Adds a pre-constructed weight and key. */
        public B add( int weight, FuzzyKey<T> key ) { return add( WeightedKey.of( weight, key ) ); }
        
        /** Adds a pre-constructed key. */
        @Override
        public B add( WeightedKey<T> key ) {
            if( KeyUsage.POLL.allowsKey( key ) ) return super.add( key );
            throw new IllegalArgumentException( "Key type not allowed for this usage! " + key.unwrap() );
        }
    }
    
    /** Builder class for a generic fuzzy list. */
    public static class Builder<T, B extends Builder<T, B>> extends AbstractBuilder<T, FuzzyWeightedList<T>, B> {
        
        public final IFuzzyKeyParser<T> keyParser;
        
        public Builder( IFuzzyKeyParser<T> parser ) { keyParser = parser; }
        
        /** @return A new fuzzy weighted list reflecting the current state of this builder. */
        @Override
        public FuzzyWeightedList<T> build() { return new FuzzyWeightedList<>( keyParser, list ); }
        
        /** Adds a parsed key. */
        public B add( int weight, String key ) {
            return add( weight, Objects.requireNonNull(
                    keyParser.parseKeyString( null, key, key, false ) ) );
        }
    }
    
    /** Builder class for a fuzzy weighted string list. */
    public static class StrBuilder extends Builder<String, StrBuilder> {
        
        public StrBuilder() { super( StringKey.PARSER ); }
        
        /** @return A new fuzzy weighted list reflecting the current state of this builder. */
        @Override
        public FuzzyWeightedList<String> build() { return new FuzzyWeightedList<>( keyParser, list ); }
    }
}