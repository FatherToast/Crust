package fathertoast.crust.api.config.common.value.collection.value;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

/**
 * A simple multi-value codec.
 * When loaded as a value, holds the duration and amplifier for a mob effect instance.
 */
public class MobEffectStats extends MultiValueCodec<MobEffectStats> {
    /** The standard mob effect stats codec that defaults to 0 duration and 0 amplitude. */
    public static final MobEffectStats CODEC = new MobEffectStats();
    
    /** @return New mob effect stats with the provided default values. */
    public static MobEffectStats of( int duration, int amplifier ) { return new MobEffectStats( duration, amplifier ); }
    
    
    /** The effect duration, in ticks (20 ticks = 1 second). */
    public final SubValue<Integer> duration = subValue( IntValueCodec.NON_NEGATIVE,
            IntValueCodec.NON_NEGATIVE.getFormat( "Duration" ) );
    
    /** The effect amplifier (0 = I, 1 = II, etc.). */
    public final SubValue<Integer> amplifier = subValue( IntValueCodec.ANY,
            IntValueCodec.ANY.getFormat( "Amplifier" ) );
    
    /** The constructor used to define default values. */
    public MobEffectStats( int dur, int amp ) {
        duration.set( dur );
        amplifier.set( amp );
    }
    
    /** The no-args constructor used to create the codec "singleton" and for value loading. */
    public MobEffectStats() { }
    
    //    /** @return A copy of this multi-value codec, with no values loaded. */
    //    @Override
    //    public MobEffectStats duplicate() { return new MobEffectStats(); }
    
    /** @return A new effect instance using the loaded duration and amplifier. */
    public MobEffectInstance create( MobEffect effect ) {
        return new MobEffectInstance( effect, duration.get(), amplifier.get() );
    }
    
    /** @return A new invisible effect instance using the loaded duration and amplifier. */
    public MobEffectInstance createInvisible( MobEffect effect ) {
        return new MobEffectInstance( effect, duration.get(), amplifier.get(), true, false, false );
    }
}