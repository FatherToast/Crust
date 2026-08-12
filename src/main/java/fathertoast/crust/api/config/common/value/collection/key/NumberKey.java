package fathertoast.crust.api.config.common.value.collection.key;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.collection.KeyUsage;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import fathertoast.crust.api.lib.CrustMath;
import fathertoast.crust.api.lib.number.NumberType;
import fathertoast.crust.api.lib.number.RangedNumberIterator;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

// TODO - CHECK INSANE OVERFLOW THING THAT HAPPENED IN CFG EDITOR

/**
 * A key for fuzzy collections that test against a specific type of numeric value.
 * <br><br>
 * All primitive numeric types are supported, which includes
 * {@code byte}, {@code double}, {@code float}, {@code int}, {@code long}, and {@code short}.
 */
@SuppressWarnings( "unused" )
public class NumberKey<T extends Number> extends FuzzyKey<T> {
    
    
    /** @return A parser appropriate for bytes, optionally with a value codec. */
    public static IFuzzyKeyParser<Byte> byteParser( @Nullable IValueCodec<Byte> codec ) {
        if( codec != null ) return new Parser<>( NumberType.BYTE, codec );
        // noinspection unchecked
        return (Parser<Byte>) PARSERS.get( NumberType.BYTE );
    }
    
    /** @return A parser appropriate for shorts, optionally with a value codec. */
    public static IFuzzyKeyParser<Short> shortParser( @Nullable IValueCodec<Short> codec ) {
        if( codec != null ) return new Parser<>( NumberType.SHORT, codec );
        // noinspection unchecked
        return (Parser<Short>) PARSERS.get( NumberType.SHORT );
    }
    
    /** @return A parser appropriate for integers, optionally with a value codec. */
    public static IFuzzyKeyParser<Integer> intParser( @Nullable IValueCodec<Integer> codec ) {
        if( codec != null ) return new Parser<>( NumberType.SHORT, codec );
        // noinspection unchecked
        return (Parser<Integer>) PARSERS.get( NumberType.INT );
    }
    
    /** @return A parser appropriate for longs, optionally with a value codec. */
    public static IFuzzyKeyParser<Long> longParser( @Nullable IValueCodec<Long> codec ) {
        if( codec != null ) return new Parser<>( NumberType.LONG, codec );
        // noinspection unchecked
        return (Parser<Long>) PARSERS.get( NumberType.LONG );
    }
    
    /** @return A parser appropriate for floats, optionally with a value codec. */
    public static IFuzzyKeyParser<Float> floatParser( @Nullable IValueCodec<Float> codec ) {
        if( codec != null ) return new Parser<>( NumberType.FLOAT, codec );
        // noinspection unchecked
        return (Parser<Float>) PARSERS.get( NumberType.FLOAT );
    }
    
    /** @return A parser appropriate for doubles, optionally with a value codec. */
    public static IFuzzyKeyParser<Double> doubleParser( @Nullable IValueCodec<Double> codec ) {
        if( codec != null ) return new Parser<>( NumberType.DOUBLE, codec );
        // noinspection unchecked
        return (Parser<Double>) PARSERS.get( NumberType.DOUBLE );
    }
    
    
    /** @return A new exact match key with the given value. */
    public static <T extends Number> Exactly<T> exactly( T value, boolean blacklist ) {
        return (Exactly<T>) of( ComparisonOp.EXACTLY, blacklist, value );
    }
    
    /** @return A new not-equals key with the given value. */
    public static <T extends Number> NotEquals<T> notEquals( T value, boolean blacklist ) {
        return (NotEquals<T>) of( ComparisonOp.NOT_EQUALS, blacklist, value );
    }
    
    /** @return A new less-than key with the given value. */
    public static <T extends Number> LessThan<T> lessThan( T value, boolean blacklist ) {
        return (LessThan<T>) of( ComparisonOp.LESS, blacklist, value );
    }
    
    /** @return A new less-or-equal key with the given value. */
    public static <T extends Number> LessOrEqual<T> lessOrEqual( T value, boolean blacklist ) {
        return (LessOrEqual<T>) of( ComparisonOp.LESS_OR_EQUAL, blacklist, value );
    }
    
    /** @return A new greater-than key with the given value. */
    public static <T extends Number> GreaterThan<T> greaterThan( T value, boolean blacklist ) {
        return (GreaterThan<T>) of( ComparisonOp.GREATER, blacklist, value );
    }
    
    /** @return A new greater-or-equal key with the given value. */
    public static <T extends Number> GreaterOrEqual<T> greaterOrEqual( T value, boolean blacklist ) {
        return (GreaterOrEqual<T>) of( ComparisonOp.GREATER_OR_EQUAL, blacklist, value );
    }
    
    /** @return A new divisible-by key with the given value. */
    public static <T extends Number> DivisibleBy<T> divisibleBy( T value, boolean blacklist ) {
        return (DivisibleBy<T>) of( ComparisonOp.MODULO, blacklist, value );
    }
    
    /** @return A new between-inclusive key with the given value. */
    public static <T extends Number> BetweenInclusive<T> betweenInclusive( T minValue, T maxValue, boolean blacklist ) {
        return (BetweenInclusive<T>) of( ComparisonOp.BETWEEN_INCLUSIVE, blacklist, minValue, maxValue );
    }
    
    
    /**
     * @return A new key with the given value(s) and comparison operation.
     * <br><br>
     * The amount of values that need to be specified depends on the type of key
     * that is being created.
     */
    @SafeVarargs
    public static <T extends Number> NumberKey<T> of( ComparisonOp op, boolean blacklist, T... values ) {
        Objects.requireNonNull( op );
        
        if( values.length == 0 )
            throw new IllegalArgumentException( "Attempted to instantiate number key with no specified value!" );
        
        final T firstValue = values[0];
        final NumberType type = getFromNumber( firstValue );
        
        if( type == null )
            throw new IllegalArgumentException( "Attempted to construct NumberKey with an unsupported type: " + firstValue.getClass() );
        
        return switch( op ) {
            case EXACTLY -> new Exactly<>( firstValue, type, blacklist );
            case NOT_EQUALS -> new NotEquals<>( firstValue, type, blacklist );
            case GREATER -> new GreaterThan<>( firstValue, type, blacklist );
            case LESS -> new LessThan<>( firstValue, type, blacklist );
            case GREATER_OR_EQUAL -> new GreaterOrEqual<>( firstValue, type, blacklist );
            case LESS_OR_EQUAL -> new LessOrEqual<>( firstValue, type, blacklist );
            case MODULO -> new DivisibleBy<>( firstValue, type, blacklist );
            case BETWEEN_INCLUSIVE -> new BetweenInclusive<>( firstValue, values[1], type, blacklist );
        };
    }
    
    /**
     * @return The {@link NumberType} of the given number.
     * Returns null if no appropriate value type exists.
     */
    @Nullable
    public static <T extends Number> NumberType getFromNumber( T value ) {
        if( value instanceof Byte ) return NumberType.BYTE;
        else if( value instanceof Short ) return NumberType.SHORT;
        else if( value instanceof Integer ) return NumberType.INT;
        else if( value instanceof Long ) return NumberType.LONG;
        else if( value instanceof Float ) return NumberType.FLOAT;
        else if( value instanceof Double ) return NumberType.DOUBLE;
        
        return null;
    }
    
    
    // ---- Key Implementations ---- //
    
    protected static final String PATTERN = "value";
    
    /** This key's value. */
    protected final T value;
    /** This key's value type. */
    protected final NumberType type;
    
    /** This key's comparison operator. */
    protected final ComparisonOp op;
    
    
    private NumberKey( T value, NumberType type, ComparisonOp op, boolean blacklist ) {
        super( blacklist );
        this.value = value;
        this.type = type;
        this.op = op;
    }
    
    /**
     * A key that matches only an exact value.
     */
    public static class Exactly<T extends Number> extends NumberKey<T> implements IReverseKey<T> {
        
        protected Exactly( T value, NumberType numberType, boolean blacklist ) {
            super( value, numberType, ComparisonOp.EXACTLY, blacklist );
        }
        
        /** @return This key's numeric value. */
        @Override
        @Nullable // IReverseKey
        public T asValue() {
            return value;
        }
    }
    
    /**
     * A key that matches all values except its own value.
     */
    public static class NotEquals<T extends Number> extends NumberKey<T> implements IRandomKey<T> {
        
        protected NotEquals( T value, NumberType numberType, boolean blacklist ) {
            super( value, numberType, ComparisonOp.EXACTLY, blacklist );
        }
        
        /** @return True if this key matches the target. */
        @Override
        public boolean matches( T target ) {
            return switch( type ) {
                case BYTE -> target.byteValue() != value.byteValue();
                case SHORT -> target.shortValue() != value.shortValue();
                case INT -> target.intValue() != value.intValue();
                case LONG -> target.longValue() != value.longValue();
                case FLOAT -> target.floatValue() != value.floatValue();
                case DOUBLE -> target.doubleValue() != value.doubleValue();
            };
        }
        
        /** @return A value that matches this key, or null if anything goes wrong. */
        @Nullable
        @Override // IRandomKey
        public T nextValue( RandomSource random ) {
            return value;
        }
    }
    
    /**
     * A key that matches all values greater than its own value.
     */
    public static class GreaterThan<T extends Number> extends NumberKey<T> implements IRandomKey<T> {
        
        protected GreaterThan( T value, NumberType numberType, boolean blacklist ) {
            super( value, numberType, ComparisonOp.GREATER, blacklist );
        }
        
        /** @return True if this key matches the target. */
        @Override
        public boolean matches( T target ) {
            return switch( type ) {
                case BYTE -> target.byteValue() > value.byteValue();
                case SHORT -> target.shortValue() > value.shortValue();
                case INT -> target.intValue() > value.intValue();
                case LONG -> target.longValue() > value.longValue();
                case FLOAT -> target.floatValue() > value.floatValue();
                case DOUBLE -> target.doubleValue() > value.doubleValue();
            };
        }
        
        /** @return A value that matches this key, or null if anything goes wrong. */
        @Nullable
        @Override // IRandomKey
        public T nextValue( RandomSource random ) {
            return value;
        }
    }
    
    /**
     * A key that matches all values lower than its own value.
     */
    public static class LessThan<T extends Number> extends NumberKey<T> implements IRandomKey<T> {
        
        protected LessThan( T value, NumberType numberType, boolean blacklist ) {
            super( value, numberType, ComparisonOp.LESS, blacklist );
        }
        
        /** @return True if this key matches the target. */
        @Override
        public boolean matches( T target ) {
            return switch( type ) {
                case BYTE -> target.byteValue() < value.byteValue();
                case SHORT -> target.shortValue() < value.shortValue();
                case INT -> target.intValue() < value.intValue();
                case LONG -> target.longValue() < value.longValue();
                case FLOAT -> target.floatValue() < value.floatValue();
                case DOUBLE -> target.doubleValue() < value.doubleValue();
            };
        }
        
        /** @return A value that matches this key, or null if anything goes wrong. */
        @Nullable
        @Override // IRandomKey
        public T nextValue( RandomSource random ) {
            return value;
        }
    }
    
    /**
     * A key that matches all values greater or equal to its own value.
     */
    public static class GreaterOrEqual<T extends Number> extends NumberKey<T> implements IRandomKey<T> {
        
        protected GreaterOrEqual( T value, NumberType numberType, boolean blacklist ) {
            super( value, numberType, ComparisonOp.GREATER_OR_EQUAL, blacklist );
        }
        
        /** @return True if this key matches the target. */
        @Override
        public boolean matches( T target ) {
            return switch( type ) {
                case BYTE -> target.byteValue() >= value.byteValue();
                case SHORT -> target.shortValue() >= value.shortValue();
                case INT -> target.intValue() >= value.intValue();
                case LONG -> target.longValue() >= value.longValue();
                case FLOAT -> target.floatValue() >= value.floatValue();
                case DOUBLE -> target.doubleValue() >= value.doubleValue();
            };
        }
        
        /** @return A value that matches this key, or null if anything goes wrong. */
        @Nullable
        @Override // IRandomKey
        public T nextValue( RandomSource random ) {
            return value;
        }
    }
    
    /**
     * A key that matches all values lower or equal to its own value.
     */
    public static class LessOrEqual<T extends Number> extends NumberKey<T> implements IRandomKey<T> {
        
        protected LessOrEqual( T value, NumberType numberType, boolean blacklist ) {
            super( value, numberType, ComparisonOp.LESS_OR_EQUAL, blacklist );
        }
        
        /** @return True if this key matches the target. */
        @Override
        public boolean matches( T target ) {
            return switch( type ) {
                case BYTE -> target.byteValue() <= value.byteValue();
                case SHORT -> target.shortValue() <= value.shortValue();
                case INT -> target.intValue() <= value.intValue();
                case LONG -> target.longValue() <= value.longValue();
                case FLOAT -> target.floatValue() <= value.floatValue();
                case DOUBLE -> target.doubleValue() <= value.doubleValue();
            };
        }
        
        /** @return A value that matches this key, or null if anything goes wrong. */
        @Nullable
        @Override // IRandomKey
        public T nextValue( RandomSource random ) {
            return value;
        }
    }
    
    /**
     * A key that matches all values that are perfectly divisible (0 remainder) by its own value.
     */
    public static class DivisibleBy<T extends Number> extends NumberKey<T> implements IRandomKey<T> {
        
        protected DivisibleBy( T value, NumberType numberType, boolean blacklist ) {
            super( value, numberType, ComparisonOp.MODULO, blacklist );
        }
        
        /** @return True if this key matches the target. */
        @Override
        public boolean matches( T target ) {
            return switch( type ) {
                case BYTE -> target.byteValue() % value.byteValue() == 0;
                case SHORT -> target.shortValue() % value.shortValue() == 0;
                case INT -> target.intValue() % value.intValue() == 0;
                case LONG -> target.longValue() % value.longValue() == 0;
                case FLOAT -> target.floatValue() % value.floatValue() == 0;
                case DOUBLE -> target.doubleValue() % value.doubleValue() == 0;
            };
        }
        
        /** @return A value that matches this key, or null if anything goes wrong. */
        @Nullable
        @Override // IRandomKey
        public T nextValue( RandomSource random ) {
            return value;
        }
    }
    
    /**
     * A key that matches all values between a minimum and maximum value (both inclusive).
     */
    public static class BetweenInclusive<T extends Number> extends NumberKey<T> implements IMultiKey<T> {
        
        /** The upper limit value used to determine the range of this key. */
        private final T maxValue;
        
        
        /**
         * Note: the caller is responsible for making sure
         * the min value is lesser than the max value.
         *
         * @param minValue The lower limit of this key's value range.
         * @param maxValue The upper limit of this key's value range.
         */
        protected BetweenInclusive( T minValue, T maxValue, NumberType numberType, boolean blacklist ) {
            super( minValue, numberType, ComparisonOp.BETWEEN_INCLUSIVE, blacklist );
            this.maxValue = maxValue;
        }
        
        /** @return True if this key matches the target. */
        @Override
        public boolean matches( T target ) {
            return switch( type ) {
                case BYTE -> target.byteValue() >= value.byteValue() && target.byteValue() <= maxValue.byteValue();
                case SHORT -> target.shortValue() >= value.shortValue() && target.shortValue() <= maxValue.shortValue();
                case INT -> target.intValue() >= value.intValue() && target.intValue() <= maxValue.intValue();
                case LONG -> target.longValue() >= value.longValue() && target.longValue() <= maxValue.longValue();
                case FLOAT -> target.floatValue() >= value.floatValue() && target.floatValue() <= maxValue.floatValue();
                case DOUBLE ->
                        target.doubleValue() >= value.doubleValue() && target.doubleValue() <= maxValue.doubleValue();
            };
        }
        
        /** @return This fuzzy key's string definition. This must uniquely describe the match conditions. */
        @Override
        public String keyString() {
            return value.toString() + op.getIdentifier() + maxValue.toString();
        }
        
        /** @return A value that matches this key, or null if anything goes wrong. */
        @SuppressWarnings( "unchecked" )
        @Nullable
        @Override // IRandomKey
        public T nextValue( RandomSource random ) {
            final int min = value.intValue();
            final int max = maxValue.intValue();
            
            return switch( type ) {
                case BYTE -> (T) Byte.valueOf( (byte) (random.nextIntBetweenInclusive( min, max )) );
                case SHORT -> (T) Short.valueOf( (short) (random.nextIntBetweenInclusive( min, max )) );
                case INT -> (T) Integer.valueOf( random.nextIntBetweenInclusive( min, max ) );
                case FLOAT ->
                        (T) Float.valueOf( (float) (random.nextIntBetweenInclusive( min, max )) * random.nextFloat() );
                case DOUBLE -> (T) Double.valueOf( random.nextIntBetweenInclusive( min, max ) * random.nextDouble() );
                case LONG -> {
                    final long minLong = value.longValue();
                    final long maxLong = maxValue.longValue();
                    yield (T) Long.valueOf( CrustMath.nextLong( random, minLong, maxLong ) );
                }
            };
        }
        
        @Override
        @Nullable
        public Iterator<T> getValueIterator() {
            return new RangedNumberIterator<>( type, value, maxValue );
        }
    }
    
    /** @return This fuzzy key's string definition. This must uniquely describe the match conditions. */
    @Override
    public String keyString() {
        return op.getIdentifier() + value.toString();
    }
    
    /** @return True if this key matches the target. */
    @Override
    public boolean matches( T target ) {
        return Objects.equals( value, target );
    }
    
    
    /** Represents a numerical comparison operation. */
    public enum ComparisonOp {
        // The order here is important; the default number key parsers
        // iterate through each value here in order to determine what type of
        // comparison op a key uses.
        NOT_EQUALS( "!=" ), GREATER_OR_EQUAL( ">=" ), LESS_OR_EQUAL( "<=" ),
        GREATER( ">" ), LESS( "<" ), MODULO( "%" ),
        EXACTLY( "" ),
        BETWEEN_INCLUSIVE( "~" ) {
            @Override
            @Nullable
            @SuppressWarnings( "unchecked" )
            public <T extends Number> FuzzyKey<T> parseSpecialKey( @Nullable IConfigField<?> field, String line, String key, NumberType numberType,
                                                                   boolean blacklist, Function<String, ?> numberParser ) {
                final String[] parts = key.split( "~", 2 );
                if( parts.length != 2 ) return null;
                T minValue, maxValue;
                
                try {
                    minValue = (T) numberParser.apply( parts[0] );
                    maxValue = (T) numberParser.apply( parts[1] );
                    if( !isValidRange( minValue, maxValue ) )
                        throw new IllegalArgumentException( "Min value must be lesser than max value!" );
                }
                catch( Exception e ) {
                    if( field != null ) {
                        ConfigUtil.warnFor( field );
                        ConfigUtil.LOG.warn( "Invalid value range ({}) specified for between-inclusive number key in entry '{}'! Entry will be discarded.",
                                key, line );
                    }
                    return null;
                }
                return new BetweenInclusive<>( minValue, maxValue, numberType, blacklist );
            }
        };
        
        final String identifier;
        
        ComparisonOp( String identifier ) {
            this.identifier = identifier;
        }
        
        /** @return This comparison operator's identifier. */
        public String getIdentifier() {
            return identifier;
        }
        
        /**
         * @return This comparison op's special key parsing result, if any.
         * <br><br>
         * By default, a comparison op is not responsible for key parsing,
         * but for ops that require a different key format than <strong>'identifier + value'</strong>,
         * this method can be used to return a custom parsed key.
         */
        @Nullable
        public <T extends Number> FuzzyKey<T> parseSpecialKey( @Nullable IConfigField<?> field, String line, String key, NumberType numberType,
                                                               boolean blacklist, Function<String, ?> numberParser ) {
            return null;
        }
    }
    
    // ---- Parser Implementation ---- //
    
    /** Default parsers with no value codecs. */
    private static final Map<NumberType, Parser<?>> PARSERS = new HashMap<>();
    
    static {
        for( NumberType numberType : NumberType.values() ) PARSERS.put( numberType, new Parser<>( numberType, null ) );
    }
    
    /**
     * @param type  A {@link NumberType} representing the type of value.
     * @param codec Value codec for parsing. This is optional.
     */
    private record Parser<T extends Number>(NumberType type,
                                            @Nullable IValueCodec<T> codec) implements IFuzzyKeyParser<T> {
        
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
        public FuzzyKey<T> parseKeyString( @Nullable IConfigField<?> field, String line, String key, boolean blacklist ) {
            return switch( type ) {
                case BYTE -> parse( field, line, key, blacklist, Byte::parseByte );
                case SHORT -> parse( field, line, key, blacklist, Short::parseShort );
                case INT -> parse( field, line, key, blacklist, Integer::parseInt );
                case LONG -> parse( field, line, key, blacklist, Long::parseLong );
                case FLOAT -> parse( field, line, key, blacklist, Float::parseFloat );
                case DOUBLE -> parse( field, line, key, blacklist, Double::parseDouble );
            };
        }
        
        /**
         * Convenience method for parsing a key.
         * Uses this parser's value codec if it is present. Otherwise,
         * the provided default parser is used.
         *
         * @param field        The config field we are loading for, or null if error reporting should be suppressed.
         * @param line         The full line, for error context.
         * @param key          The key string to parse from.
         * @param numberParser The default parser to use if {@link Parser#codec} is null.
         * @return A new fuzzy key based on the key string, or null if parsing fails.
         */
        @Nullable
        private FuzzyKey<T> parse( @Nullable IConfigField<?> field, String line, String key, boolean blacklist, Function<String, ?> numberParser ) {
            ComparisonOp comparisonOp = ComparisonOp.EXACTLY;
            
            // Determine which comparison op the key uses
            for( ComparisonOp op : ComparisonOp.values() ) {
                // EXACTLY op has no identifier, skip
                if( op == ComparisonOp.EXACTLY ) continue;
                
                // Special parsing for BETWEEN_INCLUSIVE
                if( key.contains( ComparisonOp.BETWEEN_INCLUSIVE.getIdentifier() ) ) {
                    return ComparisonOp.BETWEEN_INCLUSIVE.parseSpecialKey( field, line, key, type, blacklist,
                            codec == null ? numberParser : ( k ) -> codec.parseTomlString( field, line, k ) );
                }
                else if( key.startsWith( op.getIdentifier() ) ) {
                    comparisonOp = op;
                    key = key.substring( op.getIdentifier().length() );
                    break;
                }
            }
            T val;
            
            if( codec != null ) {
                val = codec.parseTomlString( field, line, key );
            }
            else {
                try {
                    //noinspection unchecked
                    val = (T) numberParser.apply( key );
                }
                catch( Exception e ) {
                    if( field != null ) {
                        ConfigUtil.warnFor( field );
                        ConfigUtil.LOG.warn( "Found invalid value ({}) in entry '{}', expected value with type '{}'! Field does not use a value codec; entry will be discarded.",
                                key, line, type.name().toLowerCase( Locale.ROOT ) );
                    }
                    return null;
                }
            }
            if( val == null ) return null;
            
            return switch( comparisonOp ) {
                case NOT_EQUALS -> new NotEquals<>( val, type, blacklist );
                case GREATER_OR_EQUAL -> new GreaterOrEqual<>( val, type, blacklist );
                case LESS_OR_EQUAL -> new LessOrEqual<>( val, type, blacklist );
                case GREATER -> new GreaterThan<>( val, type, blacklist );
                case LESS -> new LessThan<>( val, type, blacklist );
                case MODULO -> new DivisibleBy<>( val, type, blacklist );
                default -> new Exactly<>( val, type, blacklist );
            };
        }
    }
    
    /** @return The number key parser associated with the specified {@link NumberType}. */
    public static <T extends Number> IFuzzyKeyParser<T> getParserForType( NumberType type ) {
        // noinspection unchecked
        return (IFuzzyKeyParser<T>) PARSERS.get( type );
    }
    
    /**
     * @return True if the min value is less than the max value.
     * Always returns false if the provided values are unsupported number objects.
     */
    @SuppressWarnings( "BooleanMethodIsAlwaysInverted" )
    public static <T extends Number> boolean isValidRange( T min, T max ) {
        if( min instanceof Byte ) return min.byteValue() < max.byteValue();
        else if( min instanceof Short ) return min.shortValue() < max.shortValue();
        else if( min instanceof Integer ) return min.intValue() < max.intValue();
        else if( min instanceof Long ) return min.longValue() < max.longValue();
        else if( min instanceof Float ) return min.floatValue() < max.floatValue();
        else if( min instanceof Double ) return min.doubleValue() < max.doubleValue();
        return false;
    }
}