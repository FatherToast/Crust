package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.key.NumberKey;
import fathertoast.crust.api.config.common.value.collection.value.FuzzyEntry;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * A fuzzy list used to iterate over registered objects with associated values.
 *
 * @param <T> The type of list (i.e., the number type).
 * @param <V> The value type.
 * @see fathertoast.crust.api.config.common.value.collection.key.NumberKey
 * @see IValueCodec
 * @see fathertoast.crust.api.config.common.field.collection.NumberValueListField
 * @see NumberList NumberList - A similar collection that does not allow values
 */
@ApiStatus.Experimental
public class NumberValueList<T extends Number, V> extends FuzzyValueList<T, V> implements INumberCollection {
    
    /** Creates a new builder for a {@code byte} number value list. */
    public static <V> NumberValueList.Builder<Byte, V, ?> byteBuilder( IValueCodec<V> valueCodec ) {
        return new NumberValueList.Builder<>( NumberKey.NumberType.BYTE, valueCodec );
    }
    
    /** Creates a new builder for a {@code short} number value list. */
    public static <V> NumberValueList.Builder<Short, V, ?> shortBuilder( IValueCodec<V> valueCodec ) {
        return new NumberValueList.Builder<>( NumberKey.NumberType.SHORT, valueCodec );
    }
    
    /** Creates a new builder for an {@code int} number value list. */
    public static <V> NumberValueList.Builder<Integer, V, ?> intBuilder( IValueCodec<V> valueCodec ) {
        return new NumberValueList.Builder<>( NumberKey.NumberType.INT, valueCodec );
    }
    
    /** Creates a new builder for a {@code long} number value list. */
    public static <V> NumberValueList.Builder<Long, V, ?> longBuilder( IValueCodec<V> valueCodec ) {
        return new NumberValueList.Builder<>( NumberKey.NumberType.LONG, valueCodec );
    }
    
    /** Creates a new builder for a {@code float} number value list. */
    public static <V> NumberValueList.Builder<Float, V, ?> floatBuilder( IValueCodec<V> valueCodec ) {
        return new NumberValueList.Builder<>( NumberKey.NumberType.FLOAT, valueCodec );
    }
    
    /** Creates a new builder for a {@code double} number value list. */
    public static <V> NumberValueList.Builder<Double, V, ?> doubleBuilder( IValueCodec<V> valueCodec ) {
        return new NumberValueList.Builder<>( NumberKey.NumberType.DOUBLE, valueCodec );
    }
    
    
    /** The {@link NumberKey.NumberType} of this number value list. */
    private final NumberKey.NumberType numberType;
    
    
    /** Constructs an empty value list. Use this if you want to {@link #load} a value list from file/NBT. */
    public NumberValueList( NumberKey.NumberType type, IValueCodec<V> codec ) {
        super( NumberKey.getParserForType( type ), codec );
        numberType = type;
    }
    
    /**
     * Constructs a value list containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link NumberValueList.Builder} is much easier.
     */
    @SafeVarargs
    public NumberValueList( NumberKey.NumberType type, IValueCodec<V> codec, FuzzyEntry<T, V>... keys ) {
        super( NumberKey.getParserForType( type ), codec, keys );
        numberType = type;
    }
    
    /**
     * Constructs a value list containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link NumberValueList.Builder} is much easier.
     */
    public NumberValueList( NumberKey.NumberType type, IValueCodec<V> codec, Collection<FuzzyEntry<T, V>> keys ) {
        super( NumberKey.getParserForType( type ), codec, keys );
        numberType = type;
    }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public NumberValueList<T, V> makeNew() { return new NumberValueList<>( numberType, valueCodec ); }
    
    /** @return The freshly loaded entry, or null if the line should be deleted. */
    @Override
    @Nullable
    public FuzzyEntry<T, V> loadLine( @Nullable IConfigField<?> field, String line ) {
        return super.loadLine( field, line );
    }
    
    /** @return This number collection's number value type. */
    @Override
    public NumberKey.NumberType getNumberType() {
        return numberType;
    }
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing number value lists smoother. */
    @ApiStatus.Experimental
    public static class Builder<T extends Number, V, B extends NumberValueList.Builder<T, V, B>> extends AbstractBuilder<T, V, NumberValueList<T, V>, B> {
        
        /** The {@link NumberKey.NumberType} of this builder. */
        private final NumberKey.NumberType numberType;
        
        
        public Builder( NumberKey.NumberType type, IValueCodec<V> codec ) {
            super( codec );
            numberType = type;
        }
        
        /** @return A new number value list reflecting the current state of this builder. */
        @Override
        public NumberValueList<T, V> build() { return new NumberValueList<>( numberType, valueCodec, list ); }
        
        
        /** Adds a key-value pair based on the number. Matches only the provided number. */
        public B exactly( T number, V value ) { return put( NumberKey.exactly( number, false ), value ); }
    }
}
