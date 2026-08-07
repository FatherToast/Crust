package fathertoast.crust.api.config.common.value.collection.value;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.value.ITomlStringValue;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * A value that can be used as an operator.
 */
public enum OperatorValue implements ITomlStringValue {
    
    ASSIGN( "=" ) {
        /** @return The result of the operation. */
        @Override
        public double apply( double left, double right ) { return right; }
    },
    
    MULTIPLY( "*" ) {
        /** @return The result of the operation. */
        @Override
        public double apply( double left, double right ) { return left * right; }
    },
    
    DIVIDE( "/" ) {
        /** @return The result of the operation. */
        @Override
        public double apply( double left, double right ) { return left / right; }
    },
    
    ADD( "+" ) {
        /** @return The result of the operation. */
        @Override
        public double apply( double left, double right ) { return left + right; }
    },
    
    SUBTRACT( "-" ) {
        /** @return The result of the operation. */
        @Override
        public double apply( double left, double right ) { return left - right; }
    };
    
    
    /** The string representation of this operator. */
    public final String code;
    
    OperatorValue( String c ) { code = c; }
    
    /** @return The result of the operation. */
    public abstract double apply( double left, double right );
    
    /** @return The result of the operation. */
    public double apply( double left, Supplier<? extends Number> right ) { return apply( left, right.get().doubleValue() ); }
    
    /** @return The result of the operation. */
    public double apply( Supplier<? extends Number> left, double right ) { return apply( left.get().doubleValue(), right ); }
    
    /** @return The result of the operation. */
    public double apply( Supplier<? extends Number> left, Supplier<? extends Number> right ) { return apply( left.get().doubleValue(), right.get().doubleValue() ); }
    
    /** @return This value, converted to a single-line string. */
    @Override
    public String toTomlString() { return code; }
    
    /** @return This value, converted to a single-line string. */
    @Override
    public String toString() { return toTomlString(); }
    
    
    /**
     * An operator value codec. Defines a default value and the types of operators allowed.
     */
    @SuppressWarnings( "ClassCanBeRecord" )
    public static class Codec implements IValueCodec<OperatorValue> {
        
        /** The standard operator codec that allows any operator. Defaults to ADD (+). */
        public static final Codec ANY = of( ADD );
        
        /** The standard operator codec that allows any operator except ASSIGN (=). Defaults to ADD (+). */
        public static final Codec NO_ASSIGN = of( ADD, false, true, true );
        
        /** The standard operator codec that allows only MULTIPLY (*) and DIVIDE (/). Defaults to MULTIPLY (*). */
        public static final Codec MULT_OR_DIV = of( MULTIPLY, false, true, false );
        
        /** The standard operator codec that allows only ADD (+) and SUBTRACT (-). Defaults to ADD (+). */
        public static final Codec ADD_OR_SUB = of( ADD, false, false, true );
        
        /** @return An operator codec that allows any operator. */
        public static Codec of( OperatorValue def ) { return of( def, 0 ); }
        
        /** @return An operator codec that allows only the specified operator types. */
        public static Codec of( OperatorValue def, boolean assign, boolean multiplyDivide, boolean addSubtract ) {
            int flags = 0;
            if( !assign ) flags |= 0b001;
            if( !multiplyDivide ) flags |= 0b010;
            if( !addSubtract ) flags |= 0b100;
            return of( def, flags );
        }
        
        private static Codec of( OperatorValue def, int flags ) { return new Codec( def, flags ); }
        
        
        private final OperatorValue defaultValue;
        /** 0b001: assign, 0b010: multiply/divide, 0b100: add/subtract */
        private final byte disableFlags;
        
        private Codec( OperatorValue def, int flags ) {
            defaultValue = def;
            disableFlags = (byte) flags;
            if( !isAllowed( def ) ) throw new IllegalArgumentException( "Default value must be an allowed type!" );
            if( (disableFlags & 0b111) == 0b111 ) throw new IllegalArgumentException( "No valid values!" );
        }
        
        /** @return True if the operator is allowed. */
        public boolean isAllowed( OperatorValue op ) {
            return disableFlags == 0 || switch( op ) {
                case ASSIGN -> (disableFlags & 0b001) == 0;
                case MULTIPLY, DIVIDE -> (disableFlags & 0b010) == 0;
                case ADD, SUBTRACT -> (disableFlags & 0b100) == 0;
            };
        }
        
        /** @return The value format (for example, {@literal "<Number (Any Value)>"}). */
        @Override
        public String getFormat() {
            StringBuilder str = new StringBuilder( "<" );
            if( (disableFlags & 0b010) == 0 ) str.append( "*|/|" );
            if( (disableFlags & 0b100) == 0 ) str.append( "+|-|" );
            if( (disableFlags & 0b001) == 0 ) str.append( "=|" );
            return str.deleteCharAt( str.length() - 1 ).append( ">" ).toString();
        }
        
        /**
         * @param field The config field we are loading for, or null if error reporting should be suppressed.
         * @param line  The full line, for error context.
         * @param value The value string to parse from.
         * @return A new value based on the value string. If the parse fails, returns a non-null default value.
         */
        @Override
        public OperatorValue parseTomlString( @Nullable IConfigField<?> field, String line, @Nullable String value ) {
            if( value == null ) return defaultValue;
            OperatorValue op = switch( value ) {
                case "*", "x" -> MULTIPLY;
                case "/" -> DIVIDE;
                case "+" -> ADD;
                case "-" -> SUBTRACT;
                case "=", "==" -> ASSIGN;
                default -> {
                    if( field != null ) {
                        ConfigUtil.warnFor( field );
                        ConfigUtil.LOG.warn( "Invalid operator ({})! Falling back to {}. Entry: {}",
                                value, defaultValue, line );
                    }
                    yield defaultValue;
                }
            };
            return isAllowed( op ) ? op : notAllowed( field, line, op );
        }
        
        private OperatorValue notAllowed( @Nullable IConfigField<?> field, String line, OperatorValue op ) {
            if( field != null ) {
                ConfigUtil.warnFor( field );
                ConfigUtil.LOG.warn( "Operator ({}) not allowed for this field! Falling back to {}. Entry: {}",
                        op.code, defaultValue, line );
            }
            return defaultValue;
        }
    }
}