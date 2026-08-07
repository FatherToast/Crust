package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.value.collection.key.NumberKey;
import fathertoast.crust.api.config.common.value.collection.value.FuzzyEntry;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

/**
 * A fuzzy map used to associate values with numbers.
 *
 * @param <T> The type of number to match against.
 * @param <V> The value type.
 * @see NumberKey
 * @see fathertoast.crust.api.config.common.field.collection.NumberMapField
 * @see NumberSet NumberSet - A similar collection that does not allow values
 */
@SuppressWarnings( "unused" )
@ApiStatus.Experimental
public class NumberMap<T extends Number, V> extends FuzzyMap<T, V> {
    
    /** Creates a new builder for a {@code byte} number list. */
    public static <V> NumberMap.Builder<Byte, V, ?> byteBuilder( IValueCodec<V> valueCodec ) {
        return new NumberMap.Builder<>( NumberKey.ValueType.BYTE, valueCodec );
    }
    
    /** Creates a new builder for a {@code short} number list. */
    public static <V> NumberMap.Builder<Short, V, ?> shortBuilder( IValueCodec<V> valueCodec ) {
        return new NumberMap.Builder<>( NumberKey.ValueType.SHORT, valueCodec );
    }
    
    /** Creates a new builder for an {@code int} number list. */
    public static <V> NumberMap.Builder<Integer, V, ?> intBuilder( IValueCodec<V> valueCodec ) {
        return new NumberMap.Builder<>( NumberKey.ValueType.INT, valueCodec );
    }
    
    /** Creates a new builder for a {@code long} number list. */
    public static <V> NumberMap.Builder<Long, V, ?> longBuilder( IValueCodec<V> valueCodec ) {
        return new NumberMap.Builder<>( NumberKey.ValueType.LONG, valueCodec );
    }
    
    /** Creates a new builder for a {@code float} number list. */
    public static <V> NumberMap.Builder<Float, V, ?> floatBuilder( IValueCodec<V> valueCodec ) {
        return new NumberMap.Builder<>( NumberKey.ValueType.FLOAT, valueCodec );
    }
    
    /** Creates a new builder for a {@code double} number list. */
    public static <V> NumberMap.Builder<Double, V, ?> doubleBuilder( IValueCodec<V> valueCodec ) {
        return new NumberMap.Builder<>( NumberKey.ValueType.DOUBLE, valueCodec );
    }
    
    
    /** The {@link NumberKey.ValueType} of this number list. */
    private final NumberKey.ValueType valueType;
    
    
    /** Constructs an empty map. Use this if you want to {@link #load} a map from file/NBT. */
    public NumberMap( NumberKey.ValueType type, IValueCodec<V> codec ) {
        super( NumberKey.getParserForType( type ), codec );
        valueType = type;
    }
    
    /**
     * Constructs a map containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link BlockStateMap.Builder} is much easier.
     */
    @SafeVarargs
    public NumberMap( NumberKey.ValueType type, IValueCodec<V> codec, FuzzyEntry<T, V>... keys ) {
        super( NumberKey.getParserForType( type ), codec, keys );
        valueType = type;
    }
    
    /**
     * Constructs a map containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link BlockStateMap.Builder} is much easier.
     */
    public NumberMap( NumberKey.ValueType type, IValueCodec<V> codec, Collection<FuzzyEntry<T, V>> keys ) {
        super( NumberKey.getParserForType( type ), codec, keys );
        valueType = type;
    }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public NumberMap<T, V> makeNew() { return new NumberMap<>( valueType, valueCodec ); }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing block state maps smoother. */
    public static class Builder<T extends Number, V, B extends NumberMap.Builder<T, V, B>> extends AbstractBuilder<T, V, NumberMap<T, V>, B> {
        
        /** The {@link fathertoast.crust.api.config.common.value.collection.key.NumberKey.ValueType} of this builder. */
        private final NumberKey.ValueType valueType;
        
        
        /** For internal use. Use one of the builder methods above. */
        private Builder( NumberKey.ValueType type, IValueCodec<V> codec ) {
            super( codec );
            valueType = type;
        }
        
        /** @return A new number map reflecting the current state of this builder. */
        @Override
        public NumberMap<T, V> build() { return new NumberMap<>( valueType, valueCodec, list ); }
        
        
        // ---- Exact Value Keys ---- //
        
        /** Adds a key-value pair based on the value. Matches only the provided value. */
        public B exactly( T key, V value ) { return put( NumberKey.exactly( key, false ), value ); }
        
        /** Adds a blacklist key based on the value. Matches only the provided value. */
        public B exactlyBlacklist( T key, V value ) { return put( NumberKey.exactly( key, true ), value ); }
        
        
        // ---- Not Equal Keys ---- //
        
        /** Adds a key-value pair based on the value. Matches all values that are not equal to the given value. */
        public B notEquals( T key, V value ) { return put( NumberKey.notEquals( key, false ), value ); }
        
        /** Adds a blacklist key based on the value. Matches all values that are not equal to the given value. */
        public B notEqualsBlacklist( T key, V value ) { return put( NumberKey.notEquals( key, true ), value ); }
        
        
        // ---- "Less than" Keys ---- //
        
        /** Adds a key-value pair based on the value. Matches all values that are lower than the given value. */
        public B lessThan( T key, V value ) { return put( NumberKey.lessThan( key, false ), value ); }
        
        /** Adds a blacklist key based on the value. Matches all values that are lower than the given value. */
        public B lessThanBlacklist( T key ) { return putBlacklist( NumberKey.lessThan( key, true ) ); }
        
        
        // ---- "Greater than" Keys ---- //
        
        /** Adds a key-value pair based on the value. Matches all values greater than the given value. */
        public B greaterThan( T key, V value ) { return put( NumberKey.greaterThan( key, false ), value ); }
        
        /** Adds a blacklist key based on the value. Matches all values greater than the given value. */
        public B greaterThanBlacklist( T key ) { return putBlacklist( NumberKey.greaterThan( key, true ) ); }
        
        
        // ---- "Less or equal" Keys ---- //
        
        /** Adds a key-value pair based on the value. Matches all values that are lower or equal to the given value. */
        public B lessOrEq( T key, V value ) { return put( NumberKey.lessOrEqual( key, false ), value ); }
        
        /** Adds a blacklist key based on the value. Matches all values that are lower or equal to the given value. */
        public B lessOrEqBlacklist( T key ) { return putBlacklist( NumberKey.lessOrEqual( key, true ) ); }
        
        
        // ---- "Greater or equal" Keys ---- //
        
        /** Adds a key-value pair based on the value. Matches all values that are greater than or equal to the given value. */
        public B greaterOrEq( T key, V value ) { return put( NumberKey.greaterOrEqual( key, false ), value ); }
        
        /** Adds a blacklist key based on the value. Matches all values that are greater than or equal to the given value. */
        public B greaterOrEqBlacklist( T key ) { return putBlacklist( NumberKey.greaterOrEqual( key, true ) ); }
        
        
        // ---- "Divisible By" Keys ---- //
        
        /** Adds a key-value pair based on the value. Matches all values that are perfectly divisible by (0 remainder) the given value. */
        public B divisibleBy( T key, V value ) { return put( NumberKey.divisibleBy( key, false ), value ); }
        
        /** Adds a blacklist key based on the value. Matches all values that are perfectly divisible by (0 remainder) the given value. */
        public B divisibleByBlacklist( T key ) { return putBlacklist( NumberKey.divisibleBy( key, true ) ); }
        
        
        // ---- "Between Inclusive" Keys ---- //
        
        /** Adds a key-value pair based on the value. Matches all values between the specified minimum and maximum (inclusive) values. */
        public B betweenInclusive( T min, T max, V value ) {
            if( !NumberKey.isValidRange( min, max ) )
                throw new IllegalArgumentException( "Min value must be less than max value!" );
            return put( NumberKey.betweenInclusive( min, max, false ), value );
        }
        
        /** Adds a blacklist key based on the value. Matches all values between the specified minimum and maximum (inclusive) values. */
        public B betweenInclusiveBlacklist( T min, T max ) {
            if( !NumberKey.isValidRange( min, max ) )
                throw new IllegalArgumentException( "Min value must be less than max value!" );
            return putBlacklist( NumberKey.betweenInclusive( min, max, true ) );
        }
    }
}