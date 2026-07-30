package fathertoast.crust.api.config.common.value.collection.value;

import fathertoast.crust.api.util.ResourceLocationUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

/**
 * A simple multi-value codec.
 * When loaded as a value, holds a sound event ID, volume and pitch to play a sound instance.
 */
public class SoundInstanceStats extends MultiValueCodec<SoundInstanceStats> {
    
    /** The standard sound instance stats codec that defaults to volume 1.0 and pitch 1.0. */
    public static final SoundInstanceStats CODEC = new SoundInstanceStats();
    
    /** A double value codec for parsing a valid sound pitch value. */
    private static final DoubleValueCodec PITCH_CODEC = DoubleValueCodec.of( 1.0, 0.5, 2.0 );
    
    
    /** @return New sound instance stats with the provided default values. */
    public static SoundInstanceStats of( ResourceLocation soundId, float volume, float pitch ) {
        return new SoundInstanceStats( soundId, volume, pitch );
    }
    
    /** @return New sound instance stats with the given sound ID and default volume and pitch. */
    public static SoundInstanceStats of( ResourceLocation soundId ) {
        return of( soundId, 1.0F, 1.0F );
    }
    
    
    /** The sound event ID. Defaults to "crust:empty". */
    public final SubValue<ResourceLocation> soundId;
    /** The playback volume, which is effectively the hearing range (0.0 ~). */
    public SubValue<Double> volume;
    /** The playback pitch (0.5 ~ 2.0). */
    public final SubValue<Double> pitch;
    
    
    /** The constructor used to define default values. */
    public SoundInstanceStats( ResourceLocation sound, float vol, float pit ) {
        this();
        soundId.set( sound );
        volume.set( (double) vol );
        pitch.set( (double) pit );
    }
    
    /** The no-args constructor used to create the codec "singleton" and for value loading. */
    public SoundInstanceStats() {
        soundId = subValue( ResourceLocValueCodec.DEFAULT, ResourceLocValueCodec.DEFAULT.getFormat() );
        volume = subValue( DoubleValueCodec.NON_NEGATIVE, DoubleValueCodec.NON_NEGATIVE.getFormat( "Volume" ) );
        final DoubleValueCodec pitchCodec = DoubleValueCodec.of( 1.0, 0.5, 2.0 );
        pitch = subValue( pitchCodec, pitchCodec.getFormat( "Pitch" ) );
    }
    
    /** @return The sound event associated with this sound instance's sound ID, if it exists in the registry. */
    @Nullable
    public SoundEvent getSound() {
        final ResourceLocation id = soundId.get();
        if( ForgeRegistries.SOUND_EVENTS.containsKey( id ) ) {
            return ForgeRegistries.SOUND_EVENTS.getValue( id );
        }
        return null;
    }
    
    /** @return A new effect instance using the loaded duration and amplifier. */
    public static SoundInstanceStats create( SoundEvent soundEvent ) {
        ResourceLocation soundId = ForgeRegistries.SOUND_EVENTS.getKey( soundEvent );
        if( soundId == null ) soundId = ResourceLocationUtils.EMPTY;
        return new SoundInstanceStats( soundId, 1.0F, 1.0F );
    }
}
