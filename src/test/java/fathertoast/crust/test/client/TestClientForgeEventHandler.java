package fathertoast.crust.test.client;

import com.mojang.blaze3d.platform.InputConstants;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.IDifficultyAccessor;
import fathertoast.crust.api.config.common.field.EnvironmentListField;
import fathertoast.crust.client.KeyBindingEvents.Key;
import fathertoast.crust.client.SortedKeyBinding;
import fathertoast.crust.test.common.TestCrust;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = ICrustApi.MOD_ID )
public class TestClientForgeEventHandler {
    
    /** Register anything needed specific to client-side Forge events. */
    static void register() {

    }

    public static void registerKeyBindings( RegisterKeyMappingsEvent event ) {
        event.register( KEY_CFG );
    }
    
    
    private static final String KEY_CAT = "CRUST TEST KEYS";
    private static final KeyMapping KEY_CFG = new SortedKeyBinding( 0, "TEST CONFIG", KeyConflictContext.UNIVERSAL,
            KeyModifier.CONTROL, Key.code( "c" ), KEY_CAT );
    
    /** Called when a key is pressed. */
    @SubscribeEvent
    static void onKeyInput( InputEvent.Key event ) {
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;
        if( event.getKey() == InputConstants.UNKNOWN.getValue() || screen != null && screen.isPauseScreen() ) return;
        
        if( event.getAction() == GLFW.GLFW_PRESS ) {
            if( event.getKey() == KEY_CFG.getKey().getValue() && KEY_CFG.isConflictContextAndModifierActive() ) {
                if( minecraft.player != null ) {
                    Level level = minecraft.player.level();
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
                        TestCrust.LOG.info( "  {} = {}", env.getKey(), env.getOrElse( level, pos, 0.0 ) );
                    }
                }
            }
        }
    }
}