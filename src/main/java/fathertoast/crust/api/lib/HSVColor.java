package fathertoast.crust.api.lib;

import net.minecraft.util.Mth;
import org.jetbrains.annotations.ApiStatus;

@SuppressWarnings( "unused" )
public class HSVColor {
    
    // ---- Conversion Methods ---- //
    
    /** @return An HSV color representing the separate RGB values (0x00 - 0xFF). */
    public static HSVColor fromRGB( int r, int g, int b ) {
        return internalFromARGB( CrustMath.getFloat( r ), CrustMath.getFloat( g ),
                CrustMath.getFloat( b ), CrustMath.bitsToRGB( r, g, b ) );
    }
    
    /** @return An HSV color representing the ARGB color int. */
    public static HSVColor fromARGB( int argb ) {
        return internalFromARGB( CrustMath.getRed( argb ), CrustMath.getGreen( argb ), CrustMath.getBlue( argb ), argb );
    }
    
    /** @return An HSV color representing the separate ARGB values (0x00 - 0xFF). */
    public static HSVColor fromARGB( int a, int r, int g, int b ) {
        return internalFromARGB( CrustMath.getFloat( r ), CrustMath.getFloat( g ),
                CrustMath.getFloat( b ), CrustMath.bitsToARGB( a, r, g, b ) );
    }
    
    /** @return An HSV color representing the separate RGB float values (0.0 - 1.0). */
    public static HSVColor fromRGB( float r, float g, float b ) {
        return internalFromARGB( r, g, b, CrustMath.toRGB( r, g, b ) );
    }
    
    /** @return An HSV color representing the separate ARGB float values (0.0 - 1.0). */
    public static HSVColor fromARGB( float a, float r, float g, float b ) {
        return internalFromARGB( r, g, b, CrustMath.toARGB( a, r, g, b ) );
    }
    
    /** @return An HSV color representing the separate HSV float values (0.0 - 1.0). */
    public static HSVColor of( float h, float s, float v ) {
        return new HSVColor( h, s, v, toRGB( h, s, v ) );
    }
    
    /** @return An HSV color representing the separate alpha & HSV float values (0.0 - 1.0). */
    public static HSVColor of( float a, float h, float s, float v ) {
        return new HSVColor( h, s, v, toARGB( a, h, s, v ) );
    }
    
    /** @return The separate HSV float values (0.0 - 1.0) combined into a single ARGB color int with no alpha. */
    public static int toRGB( float h, float s, float v ) {
        float c = v * s;
        float m = v - c;
        float x = c * (1.0F - Math.abs( h * 6.0F % 2.0F - 1.0F )) + m;
        if( h < 0.16666667F )
            return CrustMath.toRGB( v, x, m );
        else if( h < 0.33333334F )
            return CrustMath.toRGB( x, v, m );
        else if( h < 0.5F )
            return CrustMath.toRGB( m, v, x );
        else if( h < 0.6666667F )
            return CrustMath.toRGB( m, x, v );
        else if( h < 0.8333333F )
            return CrustMath.toRGB( x, m, v );
        else
            return CrustMath.toRGB( v, m, x );
    }
    
    /** @return The separate alpha & HSV float values (0.0 - 1.0) combined into a single ARGB color int. */
    public static int toARGB( float a, float h, float s, float v ) {
        return CrustMath.bitsToARGB( CrustMath.getBits( a ), toRGB( h, s, v ) );
    }
    
    
    // ---- Instance Methods ---- //
    
    /** @return The color's alpha/opacity (0.0 - 1.0). */
    public float alpha() { return CrustMath.getAlpha( asARGB() ); }
    
    /** @return The color's hue (0.0 - 1.0). */
    public float hue() { return hue; }
    
    /** @return The color's hue in degrees (0.0 - 360.0). */
    public float hueDegrees() { return hue * 360.0F; }
    
    /** @return The color's hue in radians (0.0 - 2.0 pi). */
    public float hueRadians() { return hue * Mth.TWO_PI; }
    
    /** @return The color's saturation (0.0 - 1.0). */
    public float saturation() { return saturation; }
    
    /** @return The color's value/brightness (0.0 - 1.0). */
    public float value() { return value; }
    
    /** @return The color as an ARGB color int. */
    public int asARGB() { return argbInt; }
    
    
    // ---- Internal Methods ---- //
    
    /** @return An HSV color representing the ARGB color int with its pre-separated RGB float values (0.0 - 1.0). */
    @ApiStatus.Internal
    private static HSVColor internalFromARGB( float r, float g, float b, int argb ) {
        float v = Math.max( Math.max( r, g ), b );
        float c = v - Math.min( Math.min( r, g ), b );
        if( c == 0.0F ) return new HSVColor( 0.0F, 0.0F, v, argb ); // When saturation is 0, hue is undefined
        return new HSVColor( r == v ? (g - b) / (c * 6.0F) + (g < b ? 1.0F : 0.0F) :
                g == v ? (b - r) / (c * 6.0F) + 0.33333334F :
                        (r - g) / (c * 6.0F) + 0.6666667F,
                c / v, v, argb );
    }
    
    
    private final float hue;
    private final float saturation;
    private final float value;
    private final int argbInt;
    
    @ApiStatus.Internal
    private HSVColor( float h, float s, float v, int argb ) {
        hue = h;
        saturation = s;
        value = v;
        argbInt = argb;
    }
}