package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.collection.key.NumberKey;
import fathertoast.crust.api.config.common.value.collection.value.FuzzyEntry;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * A fuzzy list used to iterate over registered objects with associated values.
 *
 * @param <T> The type of list (i.e., the registry type).
 * @param <V> The value type.
 * @see fathertoast.crust.api.config.common.value.collection.key.NumberKey
 * @see IValueCodec
 * @see fathertoast.crust.api.config.common.field.collection.NumberValueListField
 * @see NumberList NumberList - A similar collection that does not allow values
 */
@ApiStatus.Experimental
public class NumberValueList<T extends Number, V> extends FuzzyValueList<T, V> {
    
    /** Creates a new builder for a {@code byte} number value list. */
    public static <V> NumberValueList.Builder<Byte, V, ?> byteBuilder( IValueCodec<V> valueCodec ) {
        return new NumberValueList.Builder<>( NumberKey.ValueType.BYTE, valueCodec );
    }
    
    /** Creates a new builder for a {@code short} number value list. */
    public static <V> NumberValueList.Builder<Short, V, ?> shortBuilder( IValueCodec<V> valueCodec ) {
        return new NumberValueList.Builder<>( NumberKey.ValueType.SHORT, valueCodec );
    }
    
    /** Creates a new builder for an {@code int} number value list. */
    public static <V> NumberValueList.Builder<Integer, V, ?> intBuilder( IValueCodec<V> valueCodec ) {
        return new NumberValueList.Builder<>( NumberKey.ValueType.INT, valueCodec );
    }
    
    /** Creates a new builder for a {@code long} number value list. */
    public static <V> NumberValueList.Builder<Long, V, ?> longBuilder( IValueCodec<V> valueCodec ) {
        return new NumberValueList.Builder<>( NumberKey.ValueType.LONG, valueCodec );
    }
    
    /** Creates a new builder for a {@code float} number value list. */
    public static <V> NumberValueList.Builder<Float, V, ?> floatBuilder( IValueCodec<V> valueCodec ) {
        return new NumberValueList.Builder<>( NumberKey.ValueType.FLOAT, valueCodec );
    }
    
    /** Creates a new builder for a {@code double} number value list. */
    public static <V> NumberValueList.Builder<Double, V, ?> doubleBuilder( IValueCodec<V> valueCodec ) {
        return new NumberValueList.Builder<>( NumberKey.ValueType.DOUBLE, valueCodec );
    }
    
    
    /** The {@link NumberKey.ValueType} of this number value list. */
    private final NumberKey.ValueType valueType;
    
    
    /** Constructs an empty value list. Use this if you want to {@link #load} a value list from file/NBT. */
    public NumberValueList( NumberKey.ValueType type, IValueCodec<V> codec ) {
        super( NumberKey.getParserForType( type ), codec );
        valueType = type;
    }
    
    /**
     * Constructs a value list containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link NumberValueList.Builder} is much easier.
     */
    @SafeVarargs
    public NumberValueList( NumberKey.ValueType type, IValueCodec<V> codec, FuzzyEntry<T, V>... keys ) {
        super( NumberKey.getParserForType( type ), codec, keys );
        valueType = type;
    }
    
    /**
     * Constructs a value list containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link NumberValueList.Builder} is much easier.
     */
    public NumberValueList( NumberKey.ValueType type, IValueCodec<V> codec, Collection<FuzzyEntry<T, V>> keys ) {
        super( NumberKey.getParserForType( type ), codec, keys );
        valueType = type;
    }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public NumberValueList<T, V> makeNew() { return new NumberValueList<>( valueType, valueCodec ); }
    
    /** @return The freshly loaded entry, or null if the line should be deleted. */
    @Override
    @Nullable
    public FuzzyEntry<T, V> loadLine( @Nullable AbstractConfigField field, String line ) {
        return super.loadLine( field, line );
    }
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing number value lists smoother. */
    @ApiStatus.Experimental
    public static class Builder<T extends Number, V, B extends NumberValueList.Builder<T, V, B>> extends AbstractBuilder<T, V, NumberValueList<T, V>, B> {
        
        /** The {@link fathertoast.crust.api.config.common.value.collection.key.NumberKey.ValueType} of this builder. */
        private final NumberKey.ValueType valueType;
        
        
        /** For internal use. Use one of the builder methods above. */
        private Builder( NumberKey.ValueType type, IValueCodec<V> codec ) {
            super( codec );
            valueType = type;
        }
        
        /** @return A new number value list reflecting the current state of this builder. */
        @Override
        public NumberValueList<T, V> build() { return new NumberValueList<>( valueType, valueCodec, list ); }
        
        
        // ---- Basic Keys ---- //
        
        /** Adds a key-value pair based on the number. Matches only the provided . */
        public B exactly( T number, V value ) { return put( NumberKey.exactly( number, false ), value ); }
        
        /** Adds a key-value pair based on the number. Matches all values that are not equal to the given value. */
        public B notEquals( T number, V value ) { return put( NumberKey.notEquals( number, false ), value ); }
        
        /** Adds a key-value pair based on the number. Matches all values that are lower than the given value. */
        public B lessThan( T number, V value ) { return put( NumberKey.lessThan( number, false ), value ); }
        
        /** Adds a key-value pair based on the number. Matches all values greater than the given value. */
        public B greaterThan( T number, V value ) { return put( NumberKey.greaterThan( number, false ), value ); }
        
        /** Adds a key-value pair based on the number. Matches all values that are lower or equal to the given value. */
        public B lessOrEq( T number, V value ) { return put( NumberKey.lessOrEqual( number, false ), value ); }
        
        /** Adds a key-value pair based on the number. Matches all values that are greater than or equal to the given value. */
        public B greaterOrEq( T number, V value ) { return put( NumberKey.greaterOrEqual( number, false ), value ); }
        
        /** Adds a key-value pair based on the number. Matches all values that are perfectly divisible by (0 remainder) the given value. */
        public B divisibleBy( T number, V value ) { return put( NumberKey.divisibleBy( number, false ), value ); }
    }
}
