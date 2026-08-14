package fathertoast.crust.api.config.common.value.collection.value;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.ITomlStringValue;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * A value that can be used to compare two self-comparable objects of the same type.
 */
@SuppressWarnings( "unused" )
public enum ComparatorValue implements ITomlStringValue {
    
    EQUAL( "==" ),
    NOT_EQUAL( "!=" ),
    LESS( "<" ),
    GREATER( ">" ),
    LESS_OR_EQUAL( "<=" ),
    GREATER_OR_EQUAL( ">=" );
    
    
    /** The string representation of this operator. */
    public final String code;
    
    ComparatorValue( String c ) { code = c; }
    
    /** @return The comparator with opposite comparison results. */
    public ComparatorValue invert() {
        return switch( this ) {
            case EQUAL -> NOT_EQUAL;
            case NOT_EQUAL -> EQUAL;
            case LESS -> GREATER_OR_EQUAL;
            case GREATER -> LESS_OR_EQUAL;
            case LESS_OR_EQUAL -> GREATER;
            case GREATER_OR_EQUAL -> LESS;
        };
    }
    
    /** @return The result of the comparison. */
    public <T extends Comparable<T>> boolean evaluate( T left, T right ) {
        return switch( this ) {
            case EQUAL -> left.compareTo( right ) == 0;
            case NOT_EQUAL -> left.compareTo( right ) != 0;
            case LESS -> left.compareTo( right ) < 0;
            case GREATER -> left.compareTo( right ) > 0;
            case LESS_OR_EQUAL -> left.compareTo( right ) <= 0;
            case GREATER_OR_EQUAL -> left.compareTo( right ) >= 0;
        };
    }
    
    /** @return The result of the comparison. */
    public <T extends Comparable<T>> boolean evaluate( T left, Supplier<T> right ) { return evaluate( left, right.get() ); }
    
    /** @return The result of the comparison. */
    public <T extends Comparable<T>> boolean evaluate( Supplier<T> left, T right ) { return evaluate( left.get(), right ); }
    
    /** @return The result of the comparison. */
    public <T extends Comparable<T>> boolean evaluate( Supplier<T> left, Supplier<T> right ) { return evaluate( left.get(), right.get() ); }
    
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
        
        protected Codec( ComparatorValue def ) { defaultValue = def; }
        
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
        public ComparatorValue parseTomlString( @Nullable IConfigField<?> field, String line, @Nullable String value ) {
            if( value == null ) return defaultValue;
            return switch( value ) {
                case "<" -> LESS;
                case ">" -> GREATER;
                case "<=", "=<" -> LESS_OR_EQUAL;
                case ">=", "=>" -> GREATER_OR_EQUAL;
                case "==", "=" -> EQUAL;
                case "!=", "=!", "/=", "=/", "=/=", "~=", "=~" -> NOT_EQUAL; // A man of many names
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