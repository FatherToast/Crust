package fathertoast.crust.api.config.client.gui.widget.field;

import com.mojang.blaze3d.platform.InputConstants;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.config.common.value.collection.value.SoundData;
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
@SuppressWarnings( "unused" )
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
        super( x, y, width, height, Component.empty(), TEXTURE, ( button ) -> {} );
        // Initially inactive, until a sound is set
        active = false;
    }
    
    /** Creates a new instance with an initial sound. */
    public SoundPlayerWidget( String soundId, float volume, float pitch, int x, int y, int width, int height ) {
        this( x, y, width, height );
        setSoundById( soundId );
        setVolume( volume );
        setPitch( pitch );
    }
    
    /** Creates a new instance with an initial sound. */
    public SoundPlayerWidget( @Nullable ResourceLocation soundId, float volume, float pitch, int x, int y, int width, int height ) {
        this( x, y, width, height );
        setSoundById( soundId );
        setVolume( volume );
        setPitch( pitch );
    }
    
    /** Creates a new instance with an initial sound. */
    public SoundPlayerWidget( @Nullable SoundEvent sound, float volume, float pitch, int x, int y, int width, int height ) {
        this( x, y, width, height );
        setSound( sound );
        setVolume( volume );
        setPitch( pitch );
    }
    
    /** Creates a new instance with an initial sound. */
    public SoundPlayerWidget( @Nullable SoundData soundData, int x, int y, int width, int height ) {
        this( x, y, width, height );
        setSoundData( soundData );
    }
    
    /** Sets this widget's playback sound, volume, and pitch. */
    public void setSoundData( @Nullable SoundData soundData ) {
        if( soundData == null ) {
            setSound( null );
            setVolume( 1.0F );
            setPitch( 1.0F );
        }
        else {
            setSound( ForgeRegistries.SOUND_EVENTS.getValue( soundData.soundId.get() ) );
            setVolume( soundData.volume.get() );
            setPitch( soundData.pitch.get() );
        }
    }
    
    
    /** Sets this widget's playback sound and updates active state. */
    public void setSoundById( String soundId ) {
        setSoundById( ResourceLocation.tryParse( soundId ) );
    }
    
    /** Sets this widget's playback sound and updates active state. */
    public void setSoundById( @Nullable ResourceLocation soundId ) {
        setSound( ForgeRegistries.SOUND_EVENTS.getValue( soundId ) );
    }
    
    /** Sets this widget's playback sound and updates active state. */
    public void setSound( @Nullable SoundEvent sound ) {
        soundEvent = sound;
        active = sound != null;
    }
    
    /** Sets this widget's playback volume. Ensures the value is not negative. */
    public void setVolume( float vol ) { volume = Math.max( 0.0F, vol ); }
    
    /** Sets this widget's playback pitch, clamped between the min and max allowed values. */
    public void setPitch( float pit ) { pitch = Mth.clamp( pit, 0.5F, 2.0F ); }
    
    /** Plays this widget's sound, if it is not null. */
    protected void playSound() {
        if( soundEvent != null ) {
            // This method takes volume and pitch in opposite order from the usual
            Minecraft.getInstance().getSoundManager().play( SimpleSoundInstance.forUI(
                    soundEvent, pitch, volume ) );
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
    protected boolean brightWhenFocused() { return false; }
}