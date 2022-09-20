package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.lib.CrustMath;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * Represents a config field with a color int value.
 */
@SuppressWarnings( "unused" )
public class ColorIntField extends IntField.Hex {
    
    /** Creates a new field. */
    public ColorIntField( String key, int defaultValue, boolean useAlpha, @Nullable String... description ) {
        super( key, useAlpha ? defaultValue : defaultValue & 0xFFFFFF, useAlpha ? 8 : 6,
                useAlpha ? Range.ANY.MIN : 0x000000, useAlpha ? Range.ANY.MAX : 0xFFFFFF, description );
    }
    
    /** Creates a new field. */
    public ColorIntField( String key, Color defaultValue, boolean useAlpha, @Nullable String... description ) {
        this( key, defaultValue.getRGB(), useAlpha, description );
    }
    
    
    /** @return Returns the config field's value as a 'color' object. If this field does not 'use alpha', alpha will always be 0. */
    public Color getColor() { return new Color( get(), true ); }
    
    
    /** @return The red portion of the config field's value. Returned value will be in the range 0x00 - 0xFF. */
    public int getRedBits() { return CrustMath.getRedBits( get() ); }
    
    /** @return The red portion of the config field's value. Returned value will be in the range 0.0 - 1.0. */
    public float getRed() { return CrustMath.getRed( get() ); }
    
    
    /** @return The green portion of the config field's value. Returned value will be in the range 0x00 - 0xFF. */
    public int getGreenBits() { return CrustMath.getGreenBits( get() ); }
    
    /** @return The green portion of the config field's value. Returned value will be in the range 0.0 - 1.0. */
    public float getGreen() { return CrustMath.getGreen( get() ); }
    
    
    /** @return The blue portion of the config field's value. Returned value will be in the range 0x00 - 0xFF. */
    public int getBlueBits() { return CrustMath.getBlueBits( get() ); }
    
    /** @return The blue portion of the config field's value. Returned value will be in the range 0.0 - 1.0. */
    public float getBlue() { return CrustMath.getBlue( get() ); }
    
    
    /** @return The alpha (opacity) portion of the config field's value. Returned value will be in the range 0x00 - 0xFF. */
    public int getAlphaBits() { return CrustMath.getAlphaBits( get() ); }
    
    /** @return The alpha (opacity) portion of the config field's value. Returned value will be in the range 0.0 - 1.0. */
    public float getAlpha() { return CrustMath.getAlpha( get() ); }
}