package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.value.collection.key.NumberKey;
import fathertoast.crust.api.config.common.value.collection.key.WeightedKey;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

/**
 * A fuzzy weighted list used to randomly pick numbers.
 *
 * @param <T> The type of list (i.e., the number type).
 * @see fathertoast.crust.api.config.common.field.collection.NumberWeightedListField
 * @see NumberWeightedValueList NumberWeightedValueList - A similar collection that allows values
 */
@ApiStatus.Experimental
public class NumberWeightedList<T extends Number> extends FuzzyWeightedList<T> implements INumberCollection {
    
    /** Creates a new builder for a {@code byte} number value list. */
    public static NumberWeightedList.Builder<Byte, ?> byteBuilder() {
        return new NumberWeightedList.Builder<>( NumberKey.NumberType.BYTE );
    }
    
    /** Creates a new builder for a {@code short} number value list. */
    public static NumberWeightedList.Builder<Short, ?> shortBuilder() {
        return new NumberWeightedList.Builder<>( NumberKey.NumberType.SHORT );
    }
    
    /** Creates a new builder for an {@code int} number value list. */
    public static NumberWeightedList.Builder<Integer, ?> intBuilder() {
        return new NumberWeightedList.Builder<>( NumberKey.NumberType.INT );
    }
    
    /** Creates a new builder for a {@code long} number value list. */
    public static NumberWeightedList.Builder<Long, ?> longBuilder() {
        return new NumberWeightedList.Builder<>( NumberKey.NumberType.LONG );
    }
    
    /** Creates a new builder for a {@code float} number value list. */
    public static NumberWeightedList.Builder<Float, ?> floatBuilder() {
        return new NumberWeightedList.Builder<>( NumberKey.NumberType.FLOAT );
    }
    
    /** Creates a new builder for a {@code double} number value list. */
    public static NumberWeightedList.Builder<Double, ?> doubleBuilder() {
        return new NumberWeightedList.Builder<>( NumberKey.NumberType.DOUBLE );
    }
    
    
    /** The {@link NumberKey.NumberType} of this number weighted list. */
    private final NumberKey.NumberType numberType;
    
    
    /** Constructs an empty weighted list. Use this if you want to {@link #load} a weighted list from file/NBT. */
    public NumberWeightedList( NumberKey.NumberType type ) {
        super( NumberKey.getParserForType( type ) );
        this.numberType = type;
    }
    
    /**
     * Constructs a weighted list containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link NumberWeightedList.Builder} is much easier.
     */
    @SafeVarargs
    public NumberWeightedList( NumberKey.NumberType type, WeightedKey<T>... keys ) {
        super( NumberKey.getParserForType( type ), keys );
        this.numberType = type;
    }
    
    /**
     * Constructs a weighted list containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link NumberWeightedList.Builder} is much easier.
     */
    public NumberWeightedList( NumberKey.NumberType type, Collection<WeightedKey<T>> keys ) {
        super( NumberKey.getParserForType( type ), keys );
        this.numberType = type;
    }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public NumberWeightedList<T> makeNew() { return new NumberWeightedList<>( numberType ); }
    
    /** @return This number collection's number value type. */
    @Override
    public NumberKey.NumberType getNumberType() {
        return numberType;
    }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing number weighted lists smoother. */
    @ApiStatus.Experimental
    public static class Builder<T extends Number, B extends NumberWeightedList.Builder<T, B>> extends AbstractBuilder<T, NumberWeightedList<T>, B> {
        
        /** The {@link NumberKey.NumberType} of this builder. */
        private final NumberKey.NumberType numberType;
        
        
        public Builder( NumberKey.NumberType type ) {
            numberType = type;
        }
        
        /** @return A new fuzzy weighted list reflecting the current state of this builder. */
        @Override
        public NumberWeightedList<T> build() { return new NumberWeightedList<>( numberType, list ); }
        
        
        /** Adds a key based on the resource location. Matches only the provided number. */
        public B exactly( int weight, T key ) { return add( weight, NumberKey.exactly( key, false ) ); }
    }
}
