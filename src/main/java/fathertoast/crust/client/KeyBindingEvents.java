package fathertoast.crust.client;

import fathertoast.crust.client.button.ButtonInfo;
import fathertoast.crust.common.core.Crust;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Locale;

@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = Crust.MOD_ID )
public class KeyBindingEvents {
    
    private static final String KEY_CAT = "key.categories." + Crust.MOD_ID;
    private static final String KEY_CAT_BUTTONS = KEY_CAT + ".buttons";
    
    private static final String KEY = "key." + Crust.MOD_ID + ".";
    
    //    private static final KeyBinding EQUIP = new SortedKeyBinding( 0, KEY + "equip", KeyConflictContext.GUI,
    //            KeyModifier.NONE, InputMappings.getKey( "key.mouse.middle" ), KEY_CAT );
    
    private static final KeyBinding[] BUTTONS;
    
    /** Registers this mod's additional key bindings. */
    static void register() {
        //ClientRegistry.registerKeyBinding( EQUIP );
        for( KeyBinding binding : BUTTONS ) {
            ClientRegistry.registerKeyBinding( binding );
        }
    }
    
    /** Called when a key is pressed. */
    @SubscribeEvent
    static void onKeyInput( InputEvent.KeyInputEvent event ) {
        Screen screen = Minecraft.getInstance().screen;
        if( event.getKey() == InputMappings.UNKNOWN.getValue() || screen != null && screen.isPauseScreen() ) return;
        
        if( event.getAction() == GLFW.GLFW_PRESS ) {
            //            if( event.getKey() == EQUIP.getKey().getValue() && EQUIP.isConflictContextAndModifierActive() ) {
            //                return;
            //            }
            
            // Check for extra inventory button keybinding presses
            for( int i = 0; i < BUTTONS.length; i++ ) {
                KeyBinding binding = BUTTONS[i];
                if( event.getKey() == binding.getKey().getValue() && binding.isConflictContextAndModifierActive() ) {
                    pressButton( i < ButtonInfo.builtInIds().size() ? ButtonInfo.builtInIds().get( i ) :
                            "custom" + (i + 1 - ButtonInfo.builtInIds().size()) );
                    break;
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
        BUTTONS = new KeyBinding[builtInButtons.size() + ClientRegister.EXTRA_INV_BUTTONS.CUSTOM_BUTTONS.length];
        
        // Built-in buttons
        int index = 0;
        for( ; index < builtInButtons.size(); index++ ) {
            ButtonInfo button = ButtonInfo.get( builtInButtons.get( index ) );
            if( button != null ) {
                if( button.getDefaultKey() != null ) {
                    BUTTONS[index] = new SortedKeyBinding( index, key + button.ID.toLowerCase( Locale.ROOT ),
                            KeyConflictContext.UNIVERSAL, KeyModifier.CONTROL,
                            InputMappings.getKey( "key.keyboard.0" ), KEY_CAT_BUTTONS );
                }
                else if( button.ID.equals( "endPortal" ) ) {
                    BUTTONS[index] = new SortedKeyBinding( index, key + button.ID.toLowerCase( Locale.ROOT ),
                            KeyConflictContext.UNIVERSAL, KeyModifier.ALT,
                            InputMappings.getKey( "key.keyboard.0" ), KEY_CAT_BUTTONS );
                }
                else {
                    BUTTONS[index] = new SortedKeyBinding( index, key + button.ID.toLowerCase( Locale.ROOT ),
                            InputMappings.UNKNOWN.getValue(), KEY_CAT_BUTTONS );
                }
            }
        }
        
        // User-defined buttons
        for( int i = 0; i < ClientRegister.EXTRA_INV_BUTTONS.CUSTOM_BUTTONS.length; i++ ) {
            BUTTONS[index + i] = new SortedKeyBinding( index + i, key + "custom" + (i + 1),
                    InputMappings.UNKNOWN.getValue(), KEY_CAT_BUTTONS );
        }
    }
    
    public static class Key {
        
        /** @return A new object that holds info about a specific keystroke with no modifiers. */
        public static Key of( String key ) { return of( KeyModifier.NONE, key ); }
        
        /** @return A new object that holds info about a specific keystroke, including required modifier. */
        public static Key of( KeyModifier modifier, String key ) {
            return new Key( modifier, InputMappings.getKey( "key.keyboard." + key ) );
        }
        
        final KeyModifier MODIFIER;
        final InputMappings.Input KEY_CODE;
        
        private Key( KeyModifier modifier, InputMappings.Input keyCode ) {
            MODIFIER = modifier;
            KEY_CODE = keyCode;
        }
    }
}