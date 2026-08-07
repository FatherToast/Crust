package fathertoast.crust.api.config.common.value.collection.value;

import fathertoast.crust.api.util.ResourceLocationUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

/**
 * A simple multi-value codec.
 * When loaded as a value, holds a sound event ID, volume, and pitch to play a sound instance.
 */
@SuppressWarnings( "unused" )
public class SoundData extends MultiValueCodec<SoundData> {
    /** A float value codec for parsing a valid sound pitch value. */
    private static final FloatValueCodec PITCH_CODEC = FloatValueCodec.of( 1.0F, 0.5F, 2.0F );
    
    /** The standard sound instance stats codec that defaults to volume 1.0 and pitch 1.0. */
    public static final SoundData CODEC = new SoundData();
    
    
    /** @return New sound instance stats with the given sound ID and default volume and pitch. */
    public static SoundData of( ResourceLocation soundId ) { return of( soundId, 1.0F, 1.0F ); }
    
    /** @return New sound instance stats with the provided default values. */
    public static SoundData of( ResourceLocation soundId, float volume, float pitch ) {
        return new SoundData( soundId, volume, pitch );
    }
    
    
    /** @return New sound instance stats with the given sound event and default volume and pitch. */
    public static SoundData of( SoundEvent soundEvent ) { return of( soundEvent, 1.0F, 1.0F ); }
    
    /** @return New sound instance stats with the given sound event. */
    public static SoundData of( SoundEvent soundEvent, float volume, float pitch ) {
        ResourceLocation soundId = ForgeRegistries.SOUND_EVENTS.getKey( soundEvent );
        if( soundId == null ) soundId = ResourceLocationUtils.EMPTY;
        return of( soundId, volume, pitch );
    }
    
    
    /** The sound event ID. Defaults to "crust:empty". */
    public final SubValue<ResourceLocation> soundId = subValue( ResourceLocValueCodec.DEFAULT,
            ResourceLocValueCodec.DEFAULT.getFormat() );
    /** The playback volume, which is effectively the hearing range (0.0 ~). */
    public SubValue<Float> volume = subValue( FloatValueCodec.NON_NEGATIVE,
            DoubleValueCodec.NON_NEGATIVE.getFormat( "Volume" ) );
    /** The playback pitch (0.5 ~ 2.0). */
    public final SubValue<Float> pitch = subValue( PITCH_CODEC, PITCH_CODEC.getFormat( "Pitch" ) );
    
    /** The constructor used to define default values. */
    public SoundData( ResourceLocation sound, float vol, float pit ) {
        soundId.set( sound );
        volume.set( vol );
        pitch.set( pit );
    }
    
    /** The no-args constructor used to create the codec "singleton" and for value loading. */
    public SoundData() {}
    
    /** @return The saved sound event value, or null if invalid. */
    @Nullable
    public SoundEvent getSound() { return ForgeRegistries.SOUND_EVENTS.getValue( soundId.get() ); }
}