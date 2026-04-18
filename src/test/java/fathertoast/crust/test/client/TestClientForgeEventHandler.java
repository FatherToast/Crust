package fathertoast.crust.test.client;

import com.mojang.blaze3d.platform.InputConstants;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.IDifficultyAccessor;
import fathertoast.crust.api.client.SortedKeyMapping;
import fathertoast.crust.api.config.common.field.EnvironmentListField;
import fathertoast.crust.client.ClientRegister;
import fathertoast.crust.client.config.CfgEditorCrustConfig;
import fathertoast.crust.client.screen.widget.button.ExtraMenuButton;
import fathertoast.crust.test.common.TestCrust;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = ICrustApi.MOD_ID )
public class TestClientForgeEventHandler {
    
    /** Register anything needed specific to client-side Forge events. */
    protected static void register() {
    
    }
    
    protected static void registerKeyBindings( RegisterKeyMappingsEvent event ) {
        event.register( KEY_CFG );
    }
    
    
    private static final String KEY_CAT = "CRUST TEST KEYS";
    private static final KeyMapping KEY_CFG = new SortedKeyMapping( 0, "TEST CONFIG", KEY_CAT,
            KeyModifier.ALT, InputConstants.KEY_T );
    
    /** Called when a key is pressed. */
    @SubscribeEvent
    public static void onKeyInput( InputEvent.Key event ) {
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
    
    /**
     * Fired when a screen is being initialized, AFTER the
     * overridable init() method has been called.
     */
    @SubscribeEvent
    public static void onInitScreen( ScreenEvent.Init.Post event ) {
        // Add a button to the config select screen that opens the test screen.
        if( event.getScreen() instanceof TitleScreen screen ) {
            Minecraft mc = screen.getMinecraft();
            CfgEditorCrustConfig.Button config = ClientRegister.CONFIG_EDITOR.MAIN_BUTTON;
            
            final int screenWidth = mc.getWindow().getGuiScaledWidth();
            final int screenHeight = mc.getWindow().getGuiScaledHeight();
            final int guiWidth = 200;
            final int guiHeight = 104;
            // Bottom element is placed at top + 72 + 12 and is 20 tall => 104 height
            
            int buttonWidth = 100;
            int buttonHeight = 20;
            final int padding = 5;
            // The X and Y position of the cfg editor button.
            final int posX = config.anchorX.get().pos( screenWidth, guiWidth, ExtraMenuButton.BUTTON_SIZE ) + config.offsetX.get();
            final int posY = config.anchorY.get().pos( screenHeight, guiHeight, screenHeight / 4 + 48, ExtraMenuButton.BUTTON_SIZE )
                    + config.offsetY.get();
            
            // Offset X so the button ends up to the left of the cfg button.
            int xOffset = buttonWidth + padding;
            // Make sure the button actually fits in the screen lol.
            if( posX - xOffset < 0 ) {
                buttonWidth = Math.max( posX - padding, 10 );
                xOffset = buttonWidth + padding;
            }
            
            event.addListener( new Button( posX - xOffset, posY, buttonWidth, buttonHeight,
                    Component.literal( "Open test screen" ),
                    button -> screen.getMinecraft().setScreen( new TestScreen( screen ) ),
                    Supplier::get ) );
        }
    }
}