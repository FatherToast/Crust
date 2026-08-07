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
        
        
        public Builder( NumberKey.ValueType type, IValueCodec<V> codec ) {
            super( codec );
            valueType = type;
        }
        
        /** @return A new fuzzy map reflecting the current state of this builder. */
        @Override
        public NumberMap<T, V> build() { return new NumberMap<>( valueType, valueCodec, list ); }
        
        
        // ---- Basic Keys ---- //
        
        /** Adds a key based on the resource location. Matches only the provided item with an appropriate data tag. */
        public B put( T key, V value ) { return put( NumberKey.exactly( key, false ), value ); }
    }
}