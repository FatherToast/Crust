package fathertoast.crust.test.client;

import fathertoast.crust.api.IDifficultyAccessor;
import fathertoast.crust.api.config.common.field.EnvironmentListField;
import fathertoast.crust.client.KeyBindingEvents.Key;
import fathertoast.crust.client.SortedKeyBinding;
import fathertoast.crust.common.core.Crust;
import fathertoast.crust.test.common.TestCrust;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
                if( minecraft.player != null ) {
                    World world = minecraft.player.level;
                    BlockPos pos = minecraft.player.blockPosition();
                    
                    // Test the Apocalypse Rebooted difficulty hooks
                    IDifficultyAccessor diffAccess = TestCrust.API.getDifficultyAccessor();
                    if( diffAccess == null ) {
                        TestCrust.LOG.info( "Player Difficulty: N/A" );
                    }
                    else {
                        TestCrust.LOG.info( "Player Difficulty: current = {}, max = {}",
                                diffAccess.getPlayerDifficulty( minecraft.player ),
                                diffAccess.getMaxPlayerDifficulty( minecraft.player ) );
                    }
                    
                    // Poll state of each environment condition at player's position and print result
                    EnvironmentListField[] envs = TestCrust.CONFIG.ENVIRONMENT.fields;
                    TestCrust.LOG.info( "Environment Test Results:" );
                    for( EnvironmentListField env : envs ) {
                        TestCrust.LOG.info( "  {} = {}", env.getKey(), env.getOrElse( world, pos, 0.0 ) );
                    }
                }
            }
        }
    }
}