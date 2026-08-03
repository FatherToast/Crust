package fathertoast.crust.api.config.client.gui.widget.field;

import com.mojang.blaze3d.platform.InputConstants;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.config.common.value.collection.value.SoundInstanceStats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

/**
 * A simple button widget that plays a sound event.
 */
public class SoundPlayerWidget extends SimpleTextureButton {
    
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath( ICrustApi.MOD_ID, "textures/speaker.png" );
    
    /** The sound event to play. Can be null. */
    @Nullable
    private SoundEvent soundEvent;
    /** The playback volume. */
    private float volume = 1.0F;
    /** The playback pitch. */
    private float pitch = 1.0F;
    
    
    /** Creates a new instance with no initial sound data. */
    public SoundPlayerWidget( int x, int y, int width, int height ) {
        super( x, y, width, height, Component.empty(), TEXTURE, ( button ) -> { } );
        // Initially inactive, until a sound is set
        active = false;
    }
    
    /**
     * Creates a new instance with an initial sound.
     *
     * @param initialSound Either a sound event or the ID of a sound event in the form of a {@link ResourceLocation} or a string.
     */
    public SoundPlayerWidget( Object initialSound, int x, int y, int width, int height ) {
        super( x, y, width, height, Component.empty(), TEXTURE, ( button ) -> { } );
        if( initialSound instanceof SoundEvent sound ) {
            setSound( sound );
        }
        else if( initialSound instanceof ResourceLocation soundId ) {
            setSoundFromId( soundId );
        }
        else if( initialSound instanceof String s ) {
            setSoundFromId( ResourceLocation.tryParse( s ) );
        }
        else {
            throw new IllegalArgumentException( "Initial sound object must either be a sound event or a sound event ID " +
                    "in the form of a resource location or a string!" );
        }
    }
    
    
    /** Sets this widget's playback sound and updates active state. */
    public void setSound( @Nullable SoundEvent sound ) {
        soundEvent = sound;
        active = sound != null;
    }
    
    /** Sets this widget's playback sound from the specified ID and updates active state. */
    public void setSoundFromId( @Nullable ResourceLocation soundId ) {
        if( soundId == null ) {
            setSound( null );
            return;
        }
        if( ForgeRegistries.SOUND_EVENTS.containsKey( soundId ) ) {
            setSound( ForgeRegistries.SOUND_EVENTS.getValue( soundId ) );
        }
    }
    
    /** Sets this widget's playback sound from the specified ID string and updates active state. */
    public void setSoundFromId( @Nullable String soundId ) {
        if( soundId == null ) {
            setSound( null );
            return;
        }
        setSoundFromId( ResourceLocation.tryParse( soundId ) );
    }
    
    /** Sets this widget's playback volume. Ensures the value is not negative. */
    public void setVolume( float vol ) {
        volume = Math.max( 0.0F, vol );
    }
    
    /** Sets this widget's playback pitch, clamped between the min and max allowed values. */
    public void setPitch( float pit ) {
        pitch = Mth.clamp( pit, 0.5F, 2.0F );
    }
    
    /** Plays this widget's sound, if it is not null. */
    protected void playSound() {
        if( soundEvent != null ) {
            playUISound( soundEvent, volume, pitch );
        }
    }
    
    /** Plays the specified sound with a UI configuration. */
    private void playUISound( SoundEvent sound, float volume, float pitch ) {
        Minecraft.getInstance().getSoundManager().play( SimpleSoundInstance.forUI( sound, volume, pitch ) );
    }
    
    /**
     * Attempts to play a sound event using the specified sound instance stats.
     * If the instance is null, this method will do nothing.
     */
    private void playUISound( @Nullable SoundInstanceStats soundData ) {
        if( soundData == null ) return;
        
        final SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue( soundData.soundId.get() );
        if( sound != null ) {
            float volume = soundData.volume.get().floatValue();
            float pitch = soundData.pitch.get().floatValue();
            playUISound( sound, volume, pitch );
        }
    }
    
    /**
     * Called when a keyboard key is pressed.
     *
     * @param key      The keyboard key that was pressed (see {@link InputConstants.Type#KEYSYM}).
     * @param scancode The system-specific scancode of the key (see {@link InputConstants.Type#SCANCODE}).
     * @param mods     Bitfield describing which modifier keys were held down.
     * @return True if the event has been handled.
     * @see org.lwjgl.glfw.GLFWKeyCallbackI#invoke(long, int, int, int, int)
     */
    @Override
    public boolean keyPressed( int key, int scancode, int mods ) {
        if( active && visible && CommonInputs.selected( key ) ) {
            playSound();
            return true;
        }
        return false;
    }
    
    /**
     * Called when a mouse button is clicked.
     *
     * @param mouseKey The mouse key that was clicked (see {@link InputConstants.Type#MOUSE}).
     * @return True if the event has been handled.
     */
    public boolean mouseClicked( double x, double y, int mouseKey ) {
        if( active && visible && isValidClickButton( mouseKey ) ) {
            if( clicked( x, y ) ) {
                playSound();
                return true;
            }
        }
        return false;
    }
    
    /** @return True if this button should use its "bright" texture when focused. */
    @Override
    protected boolean brightWhenFocused() {
        return false;
    }
}
