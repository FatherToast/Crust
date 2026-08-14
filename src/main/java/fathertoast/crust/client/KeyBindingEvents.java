package fathertoast.crust.client;

import com.mojang.blaze3d.platform.InputConstants;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.client.SortedKeyMapping;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.client.config.ExtraInvButtonsCrustConfig;
import fathertoast.crust.client.screen.CrustConfigSelectScreen;
import fathertoast.crust.client.screen.widget.button.ButtonInfo;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Locale;

@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = ICrustApi.MOD_ID )
public final class KeyBindingEvents {
    
    private static final String KEY_CAT = "key.categories." + ICrustApi.MOD_ID;
    private static final String KEY_CAT_BUTTONS = KEY_CAT + ".buttons";
    
    private static final String KEY = "key." + ICrustApi.MOD_ID + ".";
    
    private static final KeyMapping CONFIG_EDITOR = new SortedKeyMapping( 0, KEY + "configs", KEY_CAT );
    
    private static KeyMapping[] BUTTONS;
    
    
    /** Registers this mod's additional key bindings. */
    static void register( RegisterKeyMappingsEvent event ) {
        ClientRegister.EXTRA_INV_BUTTONS = new ExtraInvButtonsCrustConfig(
                ConfigManager.getRequired( ICrustApi.MOD_ID ), "client/extra_inv_buttons" );
        init();
        
        event.register( CONFIG_EDITOR );
        
        //event.register( EQUIP );
        for( KeyMapping binding : BUTTONS ) {
            event.register( binding );
        }
    }
    
    /** Called when a mouse button action occurs. */
    @SubscribeEvent
    static void onMouseInput( InputEvent.MouseButton.Pre event ) {
        if( onInput( event.getButton(), event.getAction() ) ) event.setCanceled( true );
    }
    
    /** Called when a keyboard key action occurs. */
    @SubscribeEvent
    static void onKeyInput( InputEvent.Key event ) {
        onInput( event.getKey(), event.getAction() );
    }
    
    /**
     * Called when a mouse button or keyboard key action occurs.
     *
     * @return True if the input event should be canceled (only applicable to mouse button inputs).
     */
    private static boolean onInput( int key, int action ) {
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;
        if( key != InputConstants.UNKNOWN.getValue() && (screen == null || !screen.isPauseScreen()) ) {
            if( action == GLFW.GLFW_PRESS ) {
                // Open the config editor
                if( isActive( key, CONFIG_EDITOR ) ) {
                    minecraft.setScreen( new CrustConfigSelectScreen( screen ) );
                }
                else {
                    // Check for extra inventory button keybinding presses
                    for( int i = 0; i < BUTTONS.length; i++ ) {
                        KeyMapping binding = BUTTONS[i];
                        if( isActive( key, binding ) ) {
                            pressButton( i < ButtonInfo.builtInIds().size() ? ButtonInfo.builtInIds().get( i ) :
                                    "custom" + (i + 1 - ButtonInfo.builtInIds().size()) );
                            break;
                        }
                    }
                }
            }
        }
        return false;
        
    }
    
    /** @return True if the key code should be considered an action on a specific key bind. */
    private static boolean isActive( int key, KeyMapping keyBind ) {
        return key == keyBind.getKey().getValue() && keyBind.isConflictContextAndModifierActive();
    }
    
    /** Presses the button described. */
    private static void pressButton( String id ) {
        ButtonInfo button = ButtonInfo.get( id );
        if( button == null ) return;
        //noinspection ConstantConditions
        button.ON_PRESS.onPress( null );
    }
    
    private static void init() {
        String key = KEY + "buttons.";
        List<String> builtInButtons = ButtonInfo.builtInIds();
        BUTTONS = new KeyMapping[builtInButtons.size() + ClientRegister.EXTRA_INV_BUTTONS.CUSTOM_BUTTONS.length];
        
        // Built-in buttons
        int index = 0;
        for( ; index < builtInButtons.size(); index++ ) {
            ButtonInfo button = ButtonInfo.get( builtInButtons.get( index ) );
            if( button != null ) {
                //                if( button.getDefaultKey() != null ) {
                //                    BUTTONS[index] = new SortedKeyBinding( index, key + button.ID.toLowerCase( Locale.ROOT ),
                //                            KeyConflictContext.UNIVERSAL, button.getDefaultKey().MODIFIER,
                //                            button.getDefaultKey().KEY_CODE, KEY_CAT_BUTTONS );
                //                }
                //                else {
                //                    // Do the below here
                //                }
                BUTTONS[index] = new SortedKeyMapping( index,
                        key + button.ID.toLowerCase( Locale.ROOT ), KEY_CAT_BUTTONS );
            }
        }
        
        // User-defined buttons
        for( int i = 0; i < ClientRegister.EXTRA_INV_BUTTONS.CUSTOM_BUTTONS.length; i++ ) {
            BUTTONS[index + i] = new SortedKeyMapping( index + i, key + "custom" + (i + 1), KEY_CAT_BUTTONS );
        }
    }
    
    
    // Static listener, no instantiation
    private KeyBindingEvents() {}
}