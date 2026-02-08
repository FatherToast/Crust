package fathertoast.crust.api.lib;


import fathertoast.crust.api.util.JavaRandomSource;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

@SuppressWarnings( "unused" )
public final class CrustMath {
    
    // ---- RANDOM METHODS ---- //
    
    /**
     * @return The result of a random roll against the value based on its type:<p>
     * Double/Float: Treats the value as a percent chance (from 0 to 1).<p>
     * Integer/Short/etc.: Treats the value as a 1-in-X chance (Note: long is truncated to int).<p>
     * Non-Number types (or no value found for target): Returns false.
     */
    public static boolean rollChance( @Nullable Object value, Random random ) { return rollChance( value, JavaRandomSource.of( random ) ); }
    
    /**
     * @return The result of a random roll against the value based on its type:<p>
     * Double/Float: Treats the value as a percent chance (from 0 to 1).<p>
     * Integer/Short/etc.: Treats the value as a 1-in-X chance (Note: long is truncated to int).<p>
     * Non-Number types (or no value found for target): Returns false.
     */
    public static boolean rollChance( @Nullable Object value, RandomSource random ) {
        return value instanceof Number n &&
                (n instanceof Double || n instanceof Float ? random.nextDouble() < n.doubleValue() :
                        n.intValue() > 0 && random.nextInt( n.intValue() ) == 0);
    }
    
    
    // ---- COLOR METHODS ---- //
    
    /** @return The separate RGB float values (0.0 - 1.0) combined into a single ARGB color int with no alpha. */
    public static int toRGB( float r, float g, float b ) {
        return bitsToRGB( getBits( r ), getBits( g ), getBits( b ) );
    }
    
    /** @return The separate ARGB float values (0.0 - 1.0) combined into a single ARGB color int. */
    public static int toARGB( float a, float r, float g, float b ) {
        return bitsToARGB( getBits( a ), getBits( r ), getBits( g ), getBits( b ) );
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
    public static float getRed( int color ) { return getFloat( getRedBits( color ) ); }
    
    /** @return The green portion of an ARGB color int. Returned value will be in the range 0.0 - 1.0. */
    public static float getGreen( int color ) { return getFloat( getGreenBits( color ) ); }
    
    /** @return The blue portion of an ARGB color int. Returned value will be in the range 0.0 - 1.0. */
    public static float getBlue( int color ) { return getFloat( getBlueBits( color ) ); }
    
    /** @return The alpha (opacity) portion of an ARGB color int. Returned value will be in the range 0.0 - 1.0. */
    public static float getAlpha( int color ) { return getFloat( getAlphaBits( color ) ); }
    
    /** @return The color float value (0.0 - 1.0) converted to bits (0x00 - 0xFF). */
    public static int getBits( float v ) { return (int) (v * 0xFF); }
    
    /** @return The color bits (0x00 - 0xFF) converted to a float value (0.0 - 1.0). */
    public static float getFloat( int bits ) { return (float) bits / 0xFF; }
    
    
    // Utility class
    private CrustMath() {}
}