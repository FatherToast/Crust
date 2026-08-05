package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.NumberKey;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

/**
 * A fuzzy list used to iterate over numbers.
 *
 * @see NumberKey
 * @see fathertoast.crust.api.config.common.field.collection.NumberListField
 * @see NumberValueList NumberValueList - A similar collection that allows values
 */
@ApiStatus.Experimental
public class NumberList<T extends Number> extends FuzzyList<T> {
    
    /** Creates a new builder for a {@code byte} number list. */
    public static NumberList.Builder<Byte, ?> byteBuilder() {
        return new NumberList.Builder<>( NumberKey.ValueType.BYTE );
    }
    
    /** Creates a new builder for a {@code short} number list. */
    public static NumberList.Builder<Short, ?> shortBuilder() {
        return new NumberList.Builder<>( NumberKey.ValueType.SHORT );
    }
    
    /** Creates a new builder for an {@code int} number list. */
    public static NumberList.Builder<Integer, ?> intBuilder() {
        return new NumberList.Builder<>( NumberKey.ValueType.INT );
    }
    
    /** Creates a new builder for a {@code long} number list. */
    public static NumberList.Builder<Long, ?> longBuilder() {
        return new NumberList.Builder<>( NumberKey.ValueType.LONG );
    }
    
    /** Creates a new builder for a {@code float} number list. */
    public static NumberList.Builder<Float, ?> floatBuilder() {
        return new NumberList.Builder<>( NumberKey.ValueType.FLOAT );
    }
    
    /** Creates a new builder for a {@code double} number list. */
    public static NumberList.Builder<Double, ?> doubleBuilder() {
        return new NumberList.Builder<>( NumberKey.ValueType.DOUBLE );
    }
    
    
    /** The {@link NumberKey.ValueType} of this number list. */
    private final NumberKey.ValueType valueType;
    
    
    /** Constructs an empty list. Use this if you want to {@link #load} a list from file/NBT. */
    public NumberList( NumberKey.ValueType type ) {
        super( NumberKey.getParserForType( type ) );
        valueType = type;
    }
    
    /**
     * Constructs a list containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link ItemStackList.Builder} is much easier.
     */
    @SafeVarargs
    public NumberList( NumberKey.ValueType type, FuzzyKey<T>... keys ) {
        super( NumberKey.getParserForType( type ), keys );
        valueType = type;
    }
    
    /**
     * Constructs a list containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link ItemStackList.Builder} is much easier.
     */
    public NumberList( NumberKey.ValueType type, Collection<FuzzyKey<T>> keys ) {
        super( NumberKey.getParserForType( type ), keys );
        valueType = type;
    }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public NumberList<T> makeNew() { return new NumberList<>( valueType ); }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing item stack lists smoother. */
    @ApiStatus.Experimental
    public static class Builder<V extends Number, B extends NumberList.Builder<V, B>> extends AbstractBuilder<V, NumberList<V>, B> {
        
        /** The {@link fathertoast.crust.api.config.common.value.collection.key.NumberKey.ValueType} of this builder. */
        private final NumberKey.ValueType valueType;
        
        
        /** For internal use. Use one of the builder methods above. */
        private Builder( NumberKey.ValueType type ) {
            valueType = type;
        }
        
        /** @return A new number list reflecting the current state of this builder. */
        @Override
        public NumberList<V> build() { return new NumberList<>( valueType, list ); }
        
        
        /** Adds a key based on the value. Matches only the provided value. */
        public B exactly( V value ) { return add( NumberKey.exactly( value, false ) ); }
        
        /** Adds a key based on the value. Matches all values that are not equal to the given value. */
        public B notEquals( V value ) { return add( NumberKey.notEquals( value, false ) ); }
        
        /** Adds a key based on the value. Matches all values that are lower than the given value. */
        public B lessThan( V value ) { return add( NumberKey.lessThan( value, false ) ); }
        
        /** Adds a key based on the value. Matches all values greater than the given value. */
        public B greaterThan( V value ) { return add( NumberKey.greaterThan( value, false ) ); }
        
        /** Adds a key based on the value. Matches all values that are lower or equal to the given value. */
        public B lessOrEq( V value ) { return add( NumberKey.lessOrEqual( value, false ) ); }
        
        /** Adds a key based on the value. Matches all values that are greater than or equal to the given value. */
        public B greaterOrEq( V value ) { return add( NumberKey.greaterOrEqual( value, false ) ); }
        
        /** Adds a key based on the value. Matches all values that are perfectly divisible by (0 remainder) the given value. */
        public B divisibleBy( V value ) { return add( NumberKey.divisibleBy( value, false ) ); }
    }
}
