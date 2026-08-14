package fathertoast.crust.api.config.common.value.collection.value;

import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.HexIntWrapper;

/**
 * A hex integer value codec. Defines a default value and an allowed value range.
 */
public class HexIntValueCodec extends IntValueCodec {
    
    /** The standard hex integer codec for any value. Defaults to 0x00000000. */
    public static final HexIntValueCodec ANY = of( 0x00000000, 8, IntField.Range.ANY );
    
    /** The standard hex integer codec for non-negative values (>= 0). Defaults to 0x000000. */
    public static final HexIntValueCodec NON_NEGATIVE = of( 0x000000, 6, IntField.Range.NON_NEGATIVE );
    
    public static HexIntValueCodec of( int defaultValue, int minDigits, IntField.Range range ) { return of( defaultValue, minDigits, range.MIN, range.MAX ); }
    
    public static HexIntValueCodec of( int defaultValue, int minDigits, int min, int max ) { return new HexIntValueCodec( defaultValue, minDigits, min, max ); }
    
    
    // ---- Instance Methods ---- //
    
    /** Minimum hex digits to output. */
    private final int minDigits;
    
    protected HexIntValueCodec( int def, int digitsMin, int min, int max ) {
        super( def, min, max );
        minDigits = digitsMin;
        if( (min < 0 || max < 0) && (min != IntField.Range.ANY.MIN || max != IntField.Range.ANY.MAX) ) {
            throw new IllegalArgumentException( "Negatives are unsupported by hex int unless allowing any value!" );
        }
    }
    
    /** @return The minimum number of digits this field prints. */
    public int getMinDigits() { return minDigits; }
    
    /** @return The value in an appropriate hex wrapper. */
    public HexIntWrapper wrap( int value ) { return new HexIntWrapper( value, getMinDigits() ); }
    
    /** @return The value format (for example, {@literal "<Number (Any Value)>"}). */
    @Override
    public String getFormat( String name ) {
        return String.format( "<%s (%s)>", name, TomlHelper.fieldRange( wrap( minValue ), wrap( maxValue ) ) );
    }
    
    /** @return The value, converted to a single-line string. */
    @Override
    public String toTomlString( Integer value ) { return wrap( value ).toTomlLiteral(); }
}