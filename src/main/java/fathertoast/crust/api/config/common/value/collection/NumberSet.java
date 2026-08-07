package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import fathertoast.crust.api.config.common.value.collection.key.NumberKey;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

/**
 * A fuzzy set used to match numbers.
 *
 * @see NumberKey
 * @see fathertoast.crust.api.config.common.field.collection.NumberSetField
 * @see NumberMap NumberMap - A similar collection that allows values
 */
@SuppressWarnings( "unused" )
@ApiStatus.Experimental
public class NumberSet<T extends Number> extends FuzzySet<T> {
    
    /** Creates a new builder for a {@code byte} number set. */
    public static Builder<Byte, ?> byteBuilder() {
        return new Builder<>( NumberKey.ValueType.BYTE );
    }
    
    /** Creates a new builder for a {@code short} number set. */
    public static Builder<Short, ?> shortBuilder() {
        return new Builder<>( NumberKey.ValueType.SHORT );
    }
    
    /** Creates a new builder for an {@code int} number set. */
    public static Builder<Integer, ?> intBuilder() {
        return new Builder<>( NumberKey.ValueType.INT );
    }
    
    /** Creates a new builder for a {@code long} number set. */
    public static Builder<Long, ?> longBuilder() {
        return new Builder<>( NumberKey.ValueType.LONG );
    }
    
    /** Creates a new builder for a {@code float} number set. */
    public static Builder<Float, ?> floatBuilder() {
        return new Builder<>( NumberKey.ValueType.FLOAT );
    }
    
    /** Creates a new builder for a {@code double} number set. */
    public static Builder<Double, ?> doubleBuilder() {
        return new Builder<>( NumberKey.ValueType.DOUBLE );
    }
    
    
    /** The {@link NumberKey.ValueType} of this number set. */
    private final NumberKey.ValueType valueType;
    
    
    /** Constructs an empty set. Use this if you want to {@link #load} a set from file/NBT. */
    public NumberSet( NumberKey.ValueType type ) {
        super( NumberKey.getParserForType( type ) );
        valueType = type;
    }
    
    /**
     * Constructs a set containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link NumberSet.Builder} is much easier.
     */
    @SafeVarargs
    public NumberSet( NumberKey.ValueType type, FuzzyKey<T>... keys ) {
        super( NumberKey.getParserForType( type ), keys );
        valueType = type;
    }
    
    /**
     * Constructs a set containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link NumberSet.Builder} is much easier.
     */
    public NumberSet( NumberKey.ValueType type, Collection<FuzzyKey<T>> keys ) {
        super( NumberKey.getParserForType( type ), keys );
        valueType = type;
    }
    
    /** @return A fresh, empty collection of the same valueType as this one. */
    @Override
    public NumberSet<T> makeNew() {
        return new NumberSet<>( valueType );
    }
    
    @Override
    public KeyUsage keyUsage() {
        return super.keyUsage();
    }
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing item stack sets smoother. */
    public static class Builder<V extends Number, B extends NumberSet.Builder<V, B>> extends AbstractBuilder<V, NumberSet<V>, B> {
        
        /** The {@link fathertoast.crust.api.config.common.value.collection.key.NumberKey.ValueType} of this builder. */
        private final NumberKey.ValueType valueType;
        
        
        /** For internal use. Use one of the builder methods above. */
        private Builder( NumberKey.ValueType type ) {
            valueType = type;
        }
        
        /** @return A new number set reflecting the current state of this builder. */
        @Override
        public NumberSet<V> build() { return new NumberSet<>( valueType, list ); }
        
        
        // ---- Exact Value Keys ---- //
        
        /** Adds a key based on the value. Matches only the provided value. */
        public B exactly( V value ) { return add( NumberKey.exactly( value, false ) ); }
        
        /** Adds a blacklist key based on the value. Matches only the provided value. */
        public B exactlyBlacklist( V value ) { return add( NumberKey.exactly( value, true ) ); }
        
        
        // ---- Not Equal Keys ---- //
        
        /** Adds a key based on the value. Matches all values that are not equal to the given value. */
        public B notEquals( V value ) { return add( NumberKey.notEquals( value, false ) ); }
        
        /** Adds a blacklist key based on the value. Matches all values that are not equal to the given value. */
        public B notEqualsBlacklist( V value ) { return add( NumberKey.notEquals( value, true ) ); }
        
        
        // ---- "Less than" Keys ---- //
        
        /** Adds a key based on the value. Matches all values that are lower than the given value. */
        public B lessThan( V value ) { return add( NumberKey.lessThan( value, false ) ); }
        
        /** Adds a blacklist key based on the value. Matches all values that are lower than the given value. */
        public B lessThanBlacklist( V value ) { return add( NumberKey.lessThan( value, true ) ); }
        
        
        // ---- "Greater than" Keys ---- //
        
        /** Adds a key based on the value. Matches all values greater than the given value. */
        public B greaterThan( V value ) { return add( NumberKey.greaterThan( value, false ) ); }
        
        /** Adds a blacklist key based on the value. Matches all values greater than the given value. */
        public B greaterThanBlacklist( V value ) { return add( NumberKey.greaterThan( value, true ) ); }
        
        
        // ---- "Less or equal" Keys ---- //
        
        /** Adds a key based on the value. Matches all values that are lower or equal to the given value. */
        public B lessOrEq( V value ) { return add( NumberKey.lessOrEqual( value, false ) ); }
        
        /** Adds a blacklist key based on the value. Matches all values that are lower or equal to the given value. */
        public B lessOrEqBlacklist( V value ) { return add( NumberKey.lessOrEqual( value, true ) ); }
        
        
        // ---- "Greater or equal" Keys ---- //
        
        /** Adds a key based on the value. Matches all values that are greater than or equal to the given value. */
        public B greaterOrEq( V value ) { return add( NumberKey.greaterOrEqual( value, false ) ); }
        
        /** Adds a blacklist key based on the value. Matches all values that are greater than or equal to the given value. */
        public B greaterOrEqBlacklist( V value ) { return add( NumberKey.greaterOrEqual( value, true ) ); }
        
        
        // ---- "Divisible By" Keys ---- //
        
        /** Adds a key based on the value. Matches all values that are perfectly divisible by (0 remainder) the given value. */
        public B divisibleBy( V value ) { return add( NumberKey.divisibleBy( value, false ) ); }
        
        /** Adds a blacklist key based on the value. Matches all values that are perfectly divisible by (0 remainder) the given value. */
        public B divisibleByBlacklist( V value ) { return add( NumberKey.divisibleBy( value, true ) ); }
        
        
        // ---- "Between Inclusive" Keys ---- //
        
        /** Adds a key based on the value. Matches all values between the specified minimum and maximum (inclusive) values. */
        public B betweenInclusive( V min, V max ) {
            if( !NumberKey.isValidRange( min, max ) )
                throw new IllegalArgumentException( "Min value must be less than max value!" );
            return add( NumberKey.betweenInclusive( min, max, false ) );
        }
        
        /** Adds a blacklist key based on the value. Matches all values between the specified minimum and maximum (inclusive) values. */
        public B betweenInclusiveBlacklist( V min, V max ) {
            if( !NumberKey.isValidRange( min, max ) )
                throw new IllegalArgumentException( "Min value must be less than max value!" );
            return add( NumberKey.betweenInclusive( min, max, true ) );
        }
    }
}