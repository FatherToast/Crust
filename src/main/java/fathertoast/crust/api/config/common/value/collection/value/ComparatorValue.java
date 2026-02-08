package fathertoast.crust.api.config.common.value.collection.value;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.ITomlStringValue;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * A value that can be used to compare two numbers.
 */
public enum ComparatorValue implements ITomlStringValue {
    
    EQUAL( "==" ) {
        /** @return The result of the comparison. */
        @Override
        public boolean evaluate( double left, double right ) { return left == right; }
    },
    
    LESS( "<" ) {
        /** @return The result of the comparison. */
        @Override
        public boolean evaluate( double left, double right ) { return left < right; }
    },
    
    GREATER( ">" ) {
        /** @return The result of the comparison. */
        @Override
        public boolean evaluate( double left, double right ) { return left > right; }
    },
    
    LESS_OR_EQUAL( "<=" ) {
        /** @return The result of the comparison. */
        @Override
        public boolean evaluate( double left, double right ) { return left <= right; }
    },
    
    GREATER_OR_EQUAL( ">=" ) {
        /** @return The result of the comparison. */
        @Override
        public boolean evaluate( double left, double right ) { return left >= right; }
    };
    
    
    /** The string representation of this operator. */
    public final String code;
    
    ComparatorValue( String c ) { code = c; }
    
    /** @return The result of the comparison. */
    public abstract boolean evaluate( double left, double right );
    
    /** @return The result of the comparison. */
    public boolean evaluate( double left, Supplier<? extends Number> right ) { return evaluate( left, right.get().doubleValue() ); }
    
    /** @return The result of the comparison. */
    public boolean evaluate( Supplier<? extends Number> left, double right ) { return evaluate( left.get().doubleValue(), right ); }
    
    /** @return The result of the comparison. */
    public boolean evaluate( Supplier<? extends Number> left, Supplier<? extends Number> right ) { return evaluate( left.get().doubleValue(), right.get().doubleValue() ); }
    
    /** @return This value, converted to a single-line string. */
    @Override
    public String toTomlString() { return code; }
    
    /** @return This value, converted to a single-line string. */
    @Override
    public String toString() { return toTomlString(); }
    
    
    /**
     * A comparator value codec. Defines a default value.
     */
    @SuppressWarnings( "ClassCanBeRecord" )
    public static class Codec implements IValueCodec<ComparatorValue> {
        
        /** The standard comparator codec. Defaults to EQUAL (==). */
        public static final Codec ANY = of( EQUAL );
        
        public static Codec of( ComparatorValue def ) { return new Codec( def ); }
        
        
        private final ComparatorValue defaultValue;
        
        private Codec( ComparatorValue def ) { defaultValue = def; }
        
        /** @return The value format (for example, {@literal "<Number (Any Value)>"}). */
        @Override
        public String getFormat() { return "<Comparison>"; }
        
        /**
         * @param field The config field we are loading for, or null if error reporting should be suppressed.
         * @param line  The full line, for error context.
         * @param value The value string to parse from.
         * @return A new value based on the value string. If the parse fails, returns a non-null default value.
         */
        @Override
        public ComparatorValue parseTomlString( @Nullable AbstractConfigField field, String line, @Nullable String value ) {
            if( value == null ) return defaultValue;
            return switch( value ) {
                case "<" -> LESS;
                case ">" -> GREATER;
                case "<=", "=<" -> LESS_OR_EQUAL;
                case ">=", "=>" -> GREATER_OR_EQUAL;
                case "==", "=" -> EQUAL;
                default -> {
                    if( field != null ) {
                        ConfigUtil.warnFor( field );
                        ConfigUtil.LOG.warn( "Invalid comparator ({})! Falling back to {}. Entry: {}",
                                value, defaultValue, line );
                    }
                    yield defaultValue;
                }
            };
        }
    }
}