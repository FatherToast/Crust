package fathertoast.crust.client;

import com.mojang.blaze3d.platform.InputConstants;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.config.client.gui.screen.CrustConfigSelectScreen;
import fathertoast.crust.client.button.ButtonInfo;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
//import net.minecraftforge.client.settings.KeyConflictContext;
//import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Locale;

@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = ICrustApi.MOD_ID )
public class KeyBindingEvents {
    
    private static final String KEY_CAT = "key.categories." + ICrustApi.MOD_ID;
    private static final String KEY_CAT_BUTTONS = KEY_CAT + ".buttons";
    
    private static final String KEY = "key." + ICrustApi.MOD_ID + ".";
    
    private static final KeyMapping CONFIG_EDITOR = new SortedKeyBinding( 0, KEY + "configs", KEY_CAT );
    //    private static final KeyBinding EQUIP = new SortedKeyBinding( 1, KEY + "equip", KEY_CAT );
    
    private static final KeyMapping[] BUTTONS;
    
    /** Registers this mod's additional key bindings. */
    static void register( RegisterKeyMappingsEvent event ) {
        event.register( CONFIG_EDITOR );
        
        //event.register( EQUIP );
        for( KeyMapping binding : BUTTONS ) {
            event.register( binding );
        }
    }
    
    /** Called when a key is pressed. */
    @SubscribeEvent
    static void onKeyInput( InputEvent.Key event ) {
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;
        if( event.getKey() == InputConstants.UNKNOWN.getValue() || screen != null && screen.isPauseScreen() ) return;
        
        if( event.getAction() == GLFW.GLFW_PRESS ) {
            if( event.getKey() == CONFIG_EDITOR.getKey().getValue() && CONFIG_EDITOR.isConflictContextAndModifierActive() ) {
                // Open the config editor
                minecraft.setScreen( new CrustConfigSelectScreen( screen ) );
            }
            // Equip the currently held or moused-over item
            //            else if( event.getKey() == EQUIP.getKey().getValue() && EQUIP.isConflictContextAndModifierActive() ) {
            //                // NYI
            //            }
            else {
                // Check for extra inventory button keybinding presses
                for( int i = 0; i < BUTTONS.length; i++ ) {
                    KeyMapping binding = BUTTONS[i];
                    if( event.getKey() == binding.getKey().getValue() && binding.isConflictContextAndModifierActive() ) {
                        pressButton( i < ButtonInfo.builtInIds().size() ? ButtonInfo.builtInIds().get( i ) :
                                "custom" + (i + 1 - ButtonInfo.builtInIds().size()) );
                        break;
                    }
                }
            }
        }
    }
    
    /** Presses the button described. */
    private static void pressButton( String id ) {
        ButtonInfo button = ButtonInfo.get( id );
        if( button == null ) return;
        //noinspection ConstantConditions
        button.ON_PRESS.onPress( null );
    }
    
    static {
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
                BUTTONS[index] = new SortedKeyBinding( index,
                        key + button.ID.toLowerCase( Locale.ROOT ), KEY_CAT_BUTTONS );
            }
        }
        
        // User-defined buttons
        for( int i = 0; i < ClientRegister.EXTRA_INV_BUTTONS.CUSTOM_BUTTONS.length; i++ ) {
            BUTTONS[index + i] = new SortedKeyBinding( index + i, key + "custom" + (i + 1), KEY_CAT_BUTTONS );
        }
    }
    
    //    public static class Key {
    //
    //        /** @return A new object that holds info about a specific keystroke with no modifiers. */
    //        public static Key of( String key ) { return of( KeyModifier.NONE, key ); }
    //
    //        /** @return A new object that holds info about a specific keystroke, including required modifier. */
    //        public static Key of( KeyModifier modifier, String key ) { return new Key( modifier, code( key ) ); }
    //
    //        /** @return The key code for a keyboard key. */
    //        public static InputConstants.Key code( String key ) { return InputConstants.getKey( "key.keyboard." + key ); }
    //
    //        final KeyModifier MODIFIER;
    //        final InputConstants.Key KEY_CODE;
    //
    //        private Key( KeyModifier modifier, InputConstants.Key keyCode ) {
    //            MODIFIER = modifier;
    //            KEY_CODE = keyCode;
    //        }
    //    }
}