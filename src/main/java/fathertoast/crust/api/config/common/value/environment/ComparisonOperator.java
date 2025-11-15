package fathertoast.crust.api.config.common.value.environment;

import javax.annotation.Nullable;

public enum ComparisonOperator {
    
    NOT_EQUAL_TO( "!=" ), LESS_OR_EQUAL( "<=" ), GREATER_OR_EQUAL( ">=" ),
    EQUAL_TO( "=" ), LESS_THAN( "<" ), GREATER_THAN( ">" );
    
    private final String LITERAL;
    
    ComparisonOperator( String str ) { LITERAL = str; }
    
    @Override
    public String toString() { return LITERAL; }
    
    /** @return A convenience method that returns the opposite comparison operator only if the passed value is true. */
    public ComparisonOperator invert( boolean invert ) { return invert ? invert() : this; }
    
    /** @return The opposite comparison operator. */
    public ComparisonOperator invert() {
        return switch( this ) {
            case LESS_THAN -> GREATER_OR_EQUAL;
            case LESS_OR_EQUAL -> GREATER_THAN;
            case GREATER_THAN -> LESS_OR_EQUAL;
            case GREATER_OR_EQUAL -> LESS_THAN;
            case EQUAL_TO -> NOT_EQUAL_TO;
            case NOT_EQUAL_TO -> EQUAL_TO;
        };
    }
    
    public boolean apply( float first, float second ) {
        return switch( this ) {
            case LESS_THAN -> first < second;
            case LESS_OR_EQUAL -> first <= second;
            case GREATER_THAN -> first > second;
            case GREATER_OR_EQUAL -> first >= second;
            case EQUAL_TO -> first == second;
            case NOT_EQUAL_TO -> first != second;
        };
    }
    
    public boolean apply( int first, int second ) {
        return switch( this ) {
            case LESS_THAN -> first < second;
            case LESS_OR_EQUAL -> first <= second;
            case GREATER_THAN -> first > second;
            case GREATER_OR_EQUAL -> first >= second;
            case EQUAL_TO -> first == second;
            case NOT_EQUAL_TO -> first != second;
        };
    }
    
    public boolean apply( long first, long second ) {
        return switch( this ) {
            case LESS_THAN -> first < second;
            case LESS_OR_EQUAL -> first <= second;
            case GREATER_THAN -> first > second;
            case GREATER_OR_EQUAL -> first >= second;
            case EQUAL_TO -> first == second;
            case NOT_EQUAL_TO -> first != second;
        };
    }
    
    /** @return The operator described by a given string, or null if invalid. */
    @Nullable
    public static ComparisonOperator parse( String op ) {
        for( ComparisonOperator operator : values() ) {
            if( op.startsWith( operator.LITERAL ) ) return operator;
        }
        return null;
    }
}