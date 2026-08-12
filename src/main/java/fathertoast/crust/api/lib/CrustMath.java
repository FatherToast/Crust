package fathertoast.crust.api.lib;


import fathertoast.crust.api.util.JavaRandomSource;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

@SuppressWarnings( "unused" )
public final class CrustMath {
    
    // ---- RANDOM METHODS ---- //
    
    /**
     * A function that returns a RandomSource implementation's seed when possible.
     * The initial function is a placeholder and gets replaced via reflection once Crust has been constructed.
     */
    @SuppressWarnings( "FieldMayBeFinal" )
    private static Function<RandomSource, Long> RANDOM_SOURCE_SEED_GETTER = RandomSource::nextLong;
    
    
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
    
    /**
     * Convenience method for getting the seed from various vanilla implementations of {@link RandomSource}.
     *
     * @return The seed of the given {@link RandomSource}. If the implementation is not
     * supported or something goes wrong, a new pseudorandomly generated seed from
     * the random source instance is returned instead.
     */
    public static long getRandomSourceSeed( RandomSource random ) {
        Objects.requireNonNull( random );
        try {
            return RANDOM_SOURCE_SEED_GETTER.apply( random );
        }
        catch( Exception ignored ) { }
        return random.nextLong();
    }
    
    /**
     * Uses the given random source's seed to generate a pseudorandom long
     * with the current thread-local random.
     *
     * @param random The {@link RandomSource} to get the seed from.
     * @return The generated long value.
     * @see ThreadLocalRandom#nextLong(long)
     */
    public static long nextLong( RandomSource random ) {
        final ThreadLocalRandom rng = ThreadLocalRandom.current();
        rng.setSeed( getRandomSourceSeed( random ) );
        return rng.nextLong();
    }
    
    /**
     * Uses the given random source's seed to generate a pseudorandom long
     * with the current thread-local random.
     *
     * @param random The {@link RandomSource} to get the seed from.
     * @param bound  the upper bound (exclusive) for the returned value.
     *               Must be positive.
     * @return The generated long value.
     * @see ThreadLocalRandom#nextLong(long)
     */
    public static long nextLong( RandomSource random, long bound ) {
        final ThreadLocalRandom rng = ThreadLocalRandom.current();
        rng.setSeed( getRandomSourceSeed( random ) );
        return rng.nextLong( bound );
    }
    
    /**
     * Uses the given random source's seed to generate a pseudorandom long
     * with the current thread-local random.
     *
     * @param random The {@link RandomSource} to get the seed from.
     * @param origin The minimum value that can be returned.
     * @param bound  the upper bound (exclusive) for the returned value.
     *               Must be positive.
     * @return The generated long value.
     * @see ThreadLocalRandom#nextLong(long, long) (long)
     */
    public static long nextLong( RandomSource random, long origin, long bound ) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        rng.setSeed( getRandomSourceSeed( random ) );
        return rng.nextLong( origin, bound );
    }
    
    
    // ---- RGB/ARGB COLOR METHODS ---- //
    
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
    public static int bitsToARGB( int a, int r, int g, int b ) { return bitsToARGB( a, toRGB( r, g, b ) ); }
    
    /** @return The alpha value (0x00 - 0xFF) and RGB color int combined into a single ARGB color int. */
    public static int bitsToARGB( int a, int rgb ) { return a << 24 | rgb; }
    
    /** @return The red portion of an RGB/ARGB color int. Returned value will be in the range 0x00 - 0xFF. */
    public static int getRedBits( int argb ) { return argb >> 16 & 0xFF; }
    
    /** @return The green portion of an RGB/ARGB color int. Returned value will be in the range 0x00 - 0xFF. */
    public static int getGreenBits( int argb ) { return argb >> 8 & 0xFF; }
    
    /** @return The blue portion of an RGB/ARGB color int. Returned value will be in the range 0x00 - 0xFF. */
    public static int getBlueBits( int argb ) { return argb & 0xFF; }
    
    /** @return The alpha (opacity) portion of an ARGB color int. Returned value will be in the range 0x00 - 0xFF. */
    public static int getAlphaBits( int argb ) { return argb >>> 24; }
    
    /** @return The red portion of an RGB/ARGB color int. Returned value will be in the range 0.0 - 1.0. */
    public static float getRed( int argb ) { return getFloat( getRedBits( argb ) ); }
    
    /** @return The green portion of an RGB/ARGB color int. Returned value will be in the range 0.0 - 1.0. */
    public static float getGreen( int argb ) { return getFloat( getGreenBits( argb ) ); }
    
    /** @return The blue portion of an RGB/ARGB color int. Returned value will be in the range 0.0 - 1.0. */
    public static float getBlue( int argb ) { return getFloat( getBlueBits( argb ) ); }
    
    /** @return The alpha (opacity) portion of an ARGB color int. Returned value will be in the range 0.0 - 1.0. */
    public static float getAlpha( int argb ) { return getFloat( getAlphaBits( argb ) ); }
    
    /** @return The color float value (0.0 - 1.0) converted to bits (0x00 - 0xFF). */
    public static int getBits( float f ) { return (int) (f * 0xFF); }
    
    /** @return The color bits (0x00 - 0xFF) converted to a float value (0.0 - 1.0). */
    public static float getFloat( int bits ) { return (float) bits / 0xFF; }
    
    
    // Utility class
    private CrustMath() { }
}