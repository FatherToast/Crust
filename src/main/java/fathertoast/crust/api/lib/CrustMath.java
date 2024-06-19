package fathertoast.crust.api.lib;


import net.minecraft.util.Mth;

@SuppressWarnings( "unused" )
public final class CrustMath {
    
    // ---- COLOR METHODS ---- //
    
    /** @return The separate RGB float values (0.0-1.0) combined into a single ARGB color int with no alpha. */
    public static int toRGB( float r, float g, float b ) {
        return bitsToRGB( Mth.ceil( r * 0xFF ), Mth.ceil( g * 0xFF ),
                Mth.ceil( b * 0xFF ) );
    }
    
    /** @return The separate ARGB float values (0.0-1.0) combined into a single ARGB color int. */
    public static int toARGB( float a, float r, float g, float b ) {
        return bitsToARGB( Mth.ceil( a * 0xFF ), Mth.ceil( r * 0xFF ),
                Mth.ceil( g * 0xFF ), Mth.ceil( b * 0xFF ) );
    }
    
    /** @return The separate RGB values (0x00 - 0xFF) combined into a single ARGB color int with no alpha. */
    public static int bitsToRGB( int r, int g, int b ) { return (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF); }
    
    /** @return The separate ARGB values (0x00 - 0xFF) combined into a single ARGB color int. */
    public static int bitsToARGB( int a, int r, int g, int b ) { return (a & 0xFF) << 24 | toRGB( r, g, b ); }
    
    /** @return The red portion of an ARGB color int. Returned value will be in the range 0x00 - 0xFF. */
    public static int getRedBits( int color ) { return color >> 16 & 0xFF; }
    
    /** @return The green portion of an ARGB color int. Returned value will be in the range 0x00 - 0xFF. */
    public static int getGreenBits( int color ) { return color >> 8 & 0xFF; }
    
    /** @return The blue portion of an ARGB color int. Returned value will be in the range 0x00 - 0xFF. */
    public static int getBlueBits( int color ) { return color & 0xFF; }
    
    /** @return The alpha (opacity) portion of an ARGB color int. Returned value will be in the range 0x00 - 0xFF. */
    public static int getAlphaBits( int color ) { return color >> 24 & 0xFF; }
    
    /** @return The red portion of an ARGB color int. Returned value will be in the range 0.0 - 1.0. */
    public static float getRed( int color ) { return (float) getRedBits( color ) / 0xFF; }
    
    /** @return The green portion of an ARGB color int. Returned value will be in the range 0.0 - 1.0. */
    public static float getGreen( int color ) { return (float) getGreenBits( color ) / 0xFF; }
    
    /** @return The blue portion of an ARGB color int. Returned value will be in the range 0.0 - 1.0. */
    public static float getBlue( int color ) { return (float) getBlueBits( color ) / 0xFF; }
    
    /** @return The alpha (opacity) portion of an ARGB color int. Returned value will be in the range 0.0 - 1.0. */
    public static float getAlpha( int color ) { return (float) getAlphaBits( color ) / 0xFF; }
}