package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.value.collection.key.NumberKey;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import fathertoast.crust.api.config.common.value.collection.value.WeightedEntry;
import fathertoast.crust.api.lib.number.NumberType;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

@ApiStatus.Experimental
public class NumberWeightedValueList<T extends Number, V> extends FuzzyWeightedValueList<T, V> implements INumberCollection {
    
    /** Creates a new builder for a {@code byte} number weighted value list. */
    public static <V> NumberWeightedValueList.Builder<Byte, V, ?> byteBuilder( IValueCodec<V> valueCodec ) {
        return new NumberWeightedValueList.Builder<>( NumberType.BYTE, valueCodec );
    }
    
    /** Creates a new builder for a {@code short} number weighted value list. */
    public static <V> NumberWeightedValueList.Builder<Short, V, ?> shortBuilder( IValueCodec<V> valueCodec ) {
        return new NumberWeightedValueList.Builder<>( NumberType.SHORT, valueCodec );
    }
    
    /** Creates a new builder for an {@code int} number weighted value list. */
    public static <V> NumberWeightedValueList.Builder<Integer, V, ?> intBuilder( IValueCodec<V> valueCodec ) {
        return new NumberWeightedValueList.Builder<>( NumberType.INT, valueCodec );
    }
    
    /** Creates a new builder for a {@code long} number weighted value list. */
    public static <V> NumberWeightedValueList.Builder<Long, V, ?> longBuilder( IValueCodec<V> valueCodec ) {
        return new NumberWeightedValueList.Builder<>( NumberType.LONG, valueCodec );
    }
    
    /** Creates a new builder for a {@code float} number weighted value list. */
    public static <V> NumberWeightedValueList.Builder<Float, V, ?> floatBuilder( IValueCodec<V> valueCodec ) {
        return new NumberWeightedValueList.Builder<>( NumberType.FLOAT, valueCodec );
    }
    
    /** Creates a new builder for a {@code double} number weighted value list. */
    public static <V> NumberWeightedValueList.Builder<Double, V, ?> doubleBuilder( IValueCodec<V> valueCodec ) {
        return new NumberWeightedValueList.Builder<>( NumberType.DOUBLE, valueCodec );
    }
    
    
    /** The {@link NumberType} of this number weighted value list. */
    private final NumberType numberType;
    
    
    /** Constructs an empty weighted value list. Use this if you want to {@link #load} a weighted value list from file/NBT. */
    public NumberWeightedValueList( NumberType type, IValueCodec<V> codec ) {
        super( NumberKey.getParserForType( type ), codec );
        numberType = type;
    }
    
    /**
     * Constructs a weighted value list containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link NumberWeightedValueList.Builder} is much easier.
     */
    @SafeVarargs
    public NumberWeightedValueList( NumberType type, IValueCodec<V> codec, WeightedEntry<T, V>... keys ) {
        super( NumberKey.getParserForType( type ), codec, keys );
        numberType = type;
    }
    
    /**
     * Constructs a weighted value list containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link NumberWeightedValueList.Builder} is much easier.
     */
    public NumberWeightedValueList( NumberType type, IValueCodec<V> codec, Collection<WeightedEntry<T, V>> keys ) {
        super( NumberKey.getParserForType( type ), codec, keys );
        numberType = type;
    }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public NumberWeightedValueList<T, V> makeNew() { return new NumberWeightedValueList<>( numberType, valueCodec ); }
    
    /** @return This number collection's number value type. */
    @Override
    public NumberType getNumberType() {
        return numberType;
    }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing number weighted value lists smoother. */
    @ApiStatus.Experimental
    public static class Builder<T extends Number, V, B extends NumberWeightedValueList.Builder<T, V, B>> extends AbstractBuilder<T, V, NumberWeightedValueList<T, V>, B> {
        
        /** The {@link NumberType} of this builder. */
        private final NumberType numberType;
        
        
        public Builder( NumberType type, IValueCodec<V> codec ) {
            super( codec );
            numberType = type;
        }
        
        /** @return A new fuzzy weighted value list reflecting the current state of this builder. */
        @Override
        public NumberWeightedValueList<T, V> build() { return new NumberWeightedValueList<>( numberType, valueCodec, list ); }
        
        
        /** Adds a key-value pair based on the number. */
        public B exactly( int weight, T number, V value ) { return put( weight, NumberKey.exactly( number, false ), value ); }
        
        /** Adds a key-value pair based on the specified min and max. Matches all values between the specified minimum and maximum (inclusive) values. */
        public B betweenInclusive( int weight, T min, T max, V value ) {
            if( !NumberKey.isValidRange( min, max ) )
                throw new IllegalArgumentException( "Min value must be less than max value!" );
            return put( weight, NumberKey.betweenInclusive( min, max, false ), value );
        }
    }
}
