package fathertoast.crust.api.config.common.value.collection.value;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.function.Supplier;

/**
 * A simple multi-value codec.
 * When loaded as a value, holds the duration and amplifier for a mob effect instance.
 */
public class MobEffectStats extends MultiValueCodec<MobEffectStats> {
    /** The mob effect stats codec "singleton". */
    public static final MobEffectStats CODEC = new MobEffectStats();
    
    /** The effect duration, in ticks (20 ticks = 1 second). */
    public final Supplier<Integer> duration = value( IntValueCodec.NON_NEGATIVE );
    
    /** The effect amplifier (0 = I, 1 = II, etc.). */
    public final Supplier<Integer> amplifier = value( IntValueCodec.ANY );
    
    /** @return A new effect instance using the loaded duration and amplifier. */
    public MobEffectInstance create( MobEffect effect ) {
        return new MobEffectInstance( effect, duration.get(), amplifier.get() );
    }
    
    /** @return A new invisible effect instance using the loaded duration and amplifier. */
    public MobEffectInstance createInvisible( MobEffect effect ) {
        return new MobEffectInstance( effect, duration.get(), amplifier.get(), true, false, false );
    }
}