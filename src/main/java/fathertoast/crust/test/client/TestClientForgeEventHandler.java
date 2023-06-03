package fathertoast.crust.test.client;

import fathertoast.crust.api.config.client.gui.screen.CrustConfigSelectScreen;
import fathertoast.crust.client.KeyBindingEvents.Key;
import fathertoast.crust.client.SortedKeyBinding;
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

@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = Crust.MOD_ID )
public class TestClientForgeEventHandler {
    
    /** Register anything needed specific to client-side Forge events. */
    static void register() {
        // Key bindings
        ClientRegistry.registerKeyBinding( KEY_CFG );
    }
    
    
    private static final String KEY_CAT = "CRUST TEST KEYS";
    private static final KeyBinding KEY_CFG = new SortedKeyBinding( 0, "TEST CONFIG", KeyConflictContext.UNIVERSAL,
            KeyModifier.CONTROL, Key.code( "c" ), KEY_CAT );
    
    /** Called when a key is pressed. */
    @SubscribeEvent
    static void onKeyInput( InputEvent.KeyInputEvent event ) {
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;
        if( event.getKey() == InputMappings.UNKNOWN.getValue() || screen != null && screen.isPauseScreen() ) return;
        
        if( event.getAction() == GLFW.GLFW_PRESS ) {
            if( event.getKey() == KEY_CFG.getKey().getValue() && KEY_CFG.isConflictContextAndModifierActive() ) {
                //TODO print out whatever data to test the config file (test each environment?)
                minecraft.setScreen( new CrustConfigSelectScreen( screen ) );
            }
        }
    }
}