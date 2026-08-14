package fathertoast.crust.api.config.common.value.collection.value;

import fathertoast.crust.api.config.common.field.IntField;

/**
 * A color integer value codec. Defines a default value and an allowed value range (i.e., alpha or no alpha).
 */
@SuppressWarnings( "unused" )
public class ColorIntValueCodec extends HexIntValueCodec {
    
    /** The standard color integer codec for colors with an alpha (opacity) channel. Defaults to fully opaque white. */
    public static final ColorIntValueCodec ALPHA = of( 0xFF_FFFFFF, true );
    
    /** The standard color integer codec for colors without alpha (opacity) channel. Defaults to white. */
    public static final ColorIntValueCodec NO_ALPHA = of( 0xFFFFFF, false );
    
    public static ColorIntValueCodec of( int defaultValue, boolean useAlpha ) { return new ColorIntValueCodec( defaultValue, useAlpha ); }
    
    
    // ---- Instance Methods ---- //
    
    protected ColorIntValueCodec( int def, boolean useAlpha ) {
        super( def, useAlpha ? 8 : 6,
                useAlpha ? IntField.Range.ANY.MIN : 0x000000,
                useAlpha ? IntField.Range.ANY.MAX : 0xFFFFFF );
    }
    
    /** @return True if the alpha bits on this color are usable. */
    public boolean usesAlpha() { return getMinDigits() > 6; }
    
    /** @return The value format (for example, {@literal "<Number (Any Value)>"}). */
    @Override
    public String getFormat() { return getFormat( "Color" ); }
}