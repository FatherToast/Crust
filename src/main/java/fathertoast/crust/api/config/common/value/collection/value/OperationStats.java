package fathertoast.crust.api.config.common.value.collection.value;

/**
 * A simple multi-value codec.
 * When loaded as a value, holds an operator and right-hand operand for use as a numerical operation.
 * <p>
 * This allows any operator and any operand (as a double). If you want to impose limits on these arguments,
 * you can copy/paste this class to make a multi-value codec with those limits defined in the sub-value fields.
 */
public class OperationStats extends MultiValueCodec<OperationStats> {
    /** The standard operation codec that defaults to 'add zero' (+ 0.0). */
    public static final OperationStats CODEC = new OperationStats();
    
    /** @return A new default value representing an 'assign' operation. */
    public static OperationStats assign( double d ) { return of( OperatorValue.ASSIGN, d ); }
    
    /** @return A new default value representing a 'multiply' operation. */
    public static OperationStats multiply( double d ) { return of( OperatorValue.MULTIPLY, d ); }
    
    /** @return A new default value representing a 'divide' operation. */
    public static OperationStats divide( double d ) { return of( OperatorValue.DIVIDE, d ); }
    
    /** @return A new default value representing an 'add' operation. */
    public static OperationStats add( double d ) { return of( OperatorValue.ADD, d ); }
    
    /** @return A new default value representing a 'subtract' operation. */
    public static OperationStats subtract( double d ) { return of( OperatorValue.SUBTRACT, d ); }
    
    /** @return A new default value representing the supplied operation. */
    public static OperationStats of( OperatorValue operator, double operand ) {
        return new OperationStats( operator, operand );
    }
    
    
    /** The operator. */
    public final SubValue<OperatorValue> operator = subValue( OperatorValue.Codec.ANY );
    
    /** The right-hand operand. */
    public final SubValue<Double> operand = subValue( DoubleValueCodec.ANY );
    
    /** The constructor used to define default values. */
    public OperationStats( OperatorValue op, double d ) {
        operator.set( op );
        operand.set( d );
    }
    
    /** The no-args constructor used to create the codec "singleton" and for value loading. */
    public OperationStats() {}
    
    /** @return The result of applying this operation to the provided number. */
    public double apply( double d ) { return operator.get().apply( d, operand ); }
}