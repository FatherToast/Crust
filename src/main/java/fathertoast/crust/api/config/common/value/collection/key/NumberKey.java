package fathertoast.crust.api.config.common.value.collection.key;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.collection.KeyUsage;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * A key for fuzzy collections that test against a specific type of numeric value.
 * <br><br>
 * All primitive numeric types are supported, which includes
 * {@code byte}, {@code double}, {@code float}, {@code int}, {@code long}, and {@code short}.
 */
@ApiStatus.Experimental
public class NumberKey<T extends Number> extends FuzzyKey<T> implements IReverseKey<T> {
    
    
    /** @return A parser appropriate for bytes, optionally with a value codec. */
    public static IFuzzyKeyParser<Byte> byteParser( @Nullable IValueCodec<Byte> codec ) {
        if( codec != null ) return new Parser<>( Type.BYTE, codec );
        // noinspection unchecked
        return (Parser<Byte>) PARSERS.get( Type.BYTE );
    }
    
    /** @return A parser appropriate for shorts, optionally with a value codec. */
    public static IFuzzyKeyParser<Short> shortParser( @Nullable IValueCodec<Short> codec ) {
        if( codec != null ) return new Parser<>( Type.SHORT, codec );
        // noinspection unchecked
        return (Parser<Short>) PARSERS.get( Type.SHORT );
    }
    
    /** @return A parser appropriate for integers, optionally with a value codec. */
    public static IFuzzyKeyParser<Integer> intParser( @Nullable IValueCodec<Integer> codec ) {
        if( codec != null ) return new Parser<>( Type.SHORT, codec );
        // noinspection unchecked
        return (Parser<Integer>) PARSERS.get( Type.INT );
    }
    
    /** @return A parser appropriate for longs, optionally with a value codec. */
    public static IFuzzyKeyParser<Long> longParser( @Nullable IValueCodec<Long> codec ) {
        if( codec != null ) return new Parser<>( Type.LONG, codec );
        // noinspection unchecked
        return (Parser<Long>) PARSERS.get( Type.LONG );
    }
    
    /** @return A parser appropriate for floats, optionally with a value codec. */
    public static IFuzzyKeyParser<Float> floatParser( @Nullable IValueCodec<Float> codec ) {
        if( codec != null ) return new Parser<>( Type.FLOAT, codec );
        // noinspection unchecked
        return (Parser<Float>) PARSERS.get( Type.FLOAT );
    }
    
    /** @return A parser appropriate for doubles, optionally with a value codec. */
    public static IFuzzyKeyParser<Double> doubleParser( @Nullable IValueCodec<Double> codec ) {
        if( codec != null ) return new Parser<>( Type.DOUBLE, codec );
        // noinspection unchecked
        return (Parser<Double>) PARSERS.get( Type.DOUBLE );
    }
    
    /** @return A new non-blacklist key with the given value. */
    public static <T extends Number> NumberKey<T> of( T value ) {
        return of( value, false );
    }
    
    /** @return A new key with the given value. */
    public static <T extends Number> NumberKey<T> of( T value, boolean blacklist ) {
        Type type = null;
        if( value instanceof Byte ) type = Type.BYTE;
        else if( value instanceof Short ) type = Type.SHORT;
        else if( value instanceof Integer ) type = Type.INT;
        else if( value instanceof Long ) type = Type.LONG;
        else if( value instanceof Float ) type = Type.FLOAT;
        else if( value instanceof Double ) type = Type.DOUBLE;
        
        if( type == null )
            throw new IllegalArgumentException( "Attempted to construct NumberKey with an unsupported type: " + value.getClass() );
        
        return new NumberKey<>( value, type, blacklist );
    }
    
    // ---- Key Implementations ---- //
    
    protected static final String PATTERN = "value";
    
    /** The key's value. */
    protected final T value;
    /** The key's value type. */
    protected final Type type;
    
    
    private NumberKey( T value, Type type, boolean blacklist ) {
        super( blacklist );
        this.value = value;
        this.type = type;
    }
    
    /** @return This fuzzy key's string definition. This must uniquely describe the match conditions. */
    @Override
    public String keyString() {
        return switch( type ) {
            case BYTE -> String.valueOf( value.byteValue() );
            case SHORT -> String.valueOf( value.shortValue() );
            case INT -> String.valueOf( value.intValue() );
            case LONG -> String.valueOf( value.longValue() );
            case FLOAT -> String.valueOf( value.floatValue() );
            case DOUBLE -> String.valueOf( value.doubleValue() );
        };
    }
    
    /** @return True if this key matches the target. */
    @Override
    public boolean matches( T target ) {
        return Objects.equals( value, target );
    }
    
    /** @return The value that matches this key, or null if anything goes wrong. */
    @Override
    @Nullable // IReverseKey
    public T asValue() {
        return value;
    }
    
    /** Represents a numeric value type. */
    public enum Type {
        BYTE( "Byte" ), SHORT( "Short" ),
        INT( "Integer" ), LONG( "Long" ),
        FLOAT( "Float" ), DOUBLE( "Double" );
        
        final String name;
        
        Type( String name ) {
            this.name = name;
        }
        
        /** @return The display name of the type. */
        public String getName() {
            return name;
        }
    }
    
    // ---- Parser Implementation ---- //
    
    /** Default parsers with no value codecs. */
    private static final Map<Type, Parser<?>> PARSERS = new HashMap<>();
    
    static {
        for( Type t : Type.values() ) PARSERS.put( t, new Parser<>( t, null ) );
    }
    
    /**
     * @param type  A {@link Type} representing the type of value.
     * @param codec Value codec for parsing. This is optional.
     */
    private record Parser<T extends Number>(Type type, @Nullable IValueCodec<T> codec) implements IFuzzyKeyParser<T> {
        
        /** @return The key parser's type name (e.g., "Fuzzy"). */
        @Override
        public String getTypeName() {
            return type.getName();
        }
        
        /** @return The key parser's patterns (e.g., "\"pattern_1\", \"pattern_2\", \"pattern_n\""). */
        @Override
        public String getPatterns( KeyUsage usage ) {
            return NumberKey.PATTERN;
        }
        
        /**
         * Loads a key from the provided TOML string. If anything goes wrong, correct it at the lowest level possible,
         * and if the config field is not null, provide useful feedback and identify the field.
         *
         * @param field The config field we are loading for, or null if error reporting should be suppressed.
         * @param line  The full line, for error context.
         * @param key   The key string to parse from.
         * @return A new fuzzy key based on the key string, or null if parsing fails.
         */
        @Override
        @Nullable
        public FuzzyKey<T> parseKeyString( @Nullable AbstractConfigField field, String line, String key, boolean blacklist ) {
            return switch( type ) {
                case BYTE -> parse( field, line, key, Byte::parseByte );
                case SHORT -> parse( field, line, key, Short::parseShort );
                case INT -> parse( field, line, key, Integer::parseInt );
                case LONG -> parse( field, line, key, Long::parseLong );
                case FLOAT -> parse( field, line, key, Float::parseFloat );
                case DOUBLE -> parse( field, line, key, Double::parseDouble );
            };
        }
        
        /**
         * Convenience method for parsing a key.
         * Uses this parser's value codec if it is present. Otherwise,
         * the provided default parser is used.
         *
         * @param field         The config field we are loading for, or null if error reporting should be suppressed.
         * @param line          The full line, for error context.
         * @param key           The key string to parse from.
         * @param defaultParser The default parser to use if {@link Parser#codec} is null.
         * @return A new fuzzy key based on the key string, or null if parsing fails.
         */
        @Nullable
        private FuzzyKey<T> parse( @Nullable AbstractConfigField field, String line, String key, Function<String, ?> defaultParser ) {
            T val;
            if( codec != null ) {
                val = codec.parseTomlString( field, line, key );
            }
            else {
                try {
                    //noinspection unchecked
                    val = (T) defaultParser.apply( key );
                }
                catch( Exception e ) {
                    if( field != null ) {
                        ConfigUtil.warnFor( field );
                        ConfigUtil.LOG.warn( "Found invalid value ({}) in entry '{}', expected value with type '{}'! Field does not use a value codec; entry will be discarded.",
                                key, line, type.name.toLowerCase( Locale.ROOT ) );
                    }
                    return null;
                }
            }
            if( val == null ) return null;
            return of( val );
        }
    }
}
