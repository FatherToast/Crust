package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.NumberKey;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

/**
 * A fuzzy list used to iterate over numbers.
 *
 * @param <T> The type of number to match against.
 * @see NumberKey
 * @see fathertoast.crust.api.config.common.field.collection.NumberListField
 */
@SuppressWarnings( "unused" )
@ApiStatus.Experimental
public class NumberList<T extends Number> extends FuzzyList<T> implements INumberCollection {
    
    /** Creates a new builder for a {@code byte} number list. */
    public static NumberList.Builder<Byte, ?> byteBuilder() {
        return new NumberList.Builder<>( NumberKey.NumberType.BYTE );
    }
    
    /** Creates a new builder for a {@code short} number list. */
    public static NumberList.Builder<Short, ?> shortBuilder() {
        return new NumberList.Builder<>( NumberKey.NumberType.SHORT );
    }
    
    /** Creates a new builder for an {@code int} number list. */
    public static NumberList.Builder<Integer, ?> intBuilder() {
        return new NumberList.Builder<>( NumberKey.NumberType.INT );
    }
    
    /** Creates a new builder for a {@code long} number list. */
    public static NumberList.Builder<Long, ?> longBuilder() {
        return new NumberList.Builder<>( NumberKey.NumberType.LONG );
    }
    
    /** Creates a new builder for a {@code float} number list. */
    public static NumberList.Builder<Float, ?> floatBuilder() {
        return new NumberList.Builder<>( NumberKey.NumberType.FLOAT );
    }
    
    /** Creates a new builder for a {@code double} number list. */
    public static NumberList.Builder<Double, ?> doubleBuilder() {
        return new NumberList.Builder<>( NumberKey.NumberType.DOUBLE );
    }
    
    
    /** The {@link NumberKey.NumberType} of this number list. */
    private final NumberKey.NumberType numberType;
    
    
    /** Constructs an empty list. Use this if you want to {@link #load} a list from file/NBT. */
    public NumberList( NumberKey.NumberType type ) {
        super( NumberKey.getParserForType( type ) );
        numberType = type;
    }
    
    /**
     * Constructs a list containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link ItemStackList.Builder} is much easier.
     */
    @SafeVarargs
    public NumberList( NumberKey.NumberType type, FuzzyKey<T>... keys ) {
        super( NumberKey.getParserForType( type ), keys );
        numberType = type;
    }
    
    /**
     * Constructs a list containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link ItemStackList.Builder} is much easier.
     */
    public NumberList( NumberKey.NumberType type, Collection<FuzzyKey<T>> keys ) {
        super( NumberKey.getParserForType( type ), keys );
        numberType = type;
    }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public NumberList<T> makeNew() { return new NumberList<>( numberType ); }
    
    /** @return This number collection's number value type. */
    @Override
    public NumberKey.NumberType getNumberType() {
        return numberType;
    }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing number lists smoother. */
    public static class Builder<V extends Number, B extends NumberList.Builder<V, B>> extends AbstractBuilder<V, NumberList<V>, B> {
        
        /** The {@link NumberKey.NumberType} of this builder. */
        private final NumberKey.NumberType numberType;
        
        
        public Builder( NumberKey.NumberType type ) {
            numberType = type;
        }
        
        /** @return A new number list reflecting the current state of this builder. */
        @Override
        public NumberList<V> build() { return new NumberList<>( numberType, list ); }
        
        
        /** Adds a key based on the value. Matches only the provided value. */
        public B exactly( V value ) { return add( NumberKey.exactly( value, false ) ); }
    }
}