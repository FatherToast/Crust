package fathertoast.crust.client;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.config.client.gui.screen.CrustConfigSelectScreen;
import fathertoast.crust.client.button.ButtonInfo;
import fathertoast.crust.client.button.ExtraMenuButton;
import fathertoast.crust.client.button.ExtraInventoryButton;
import fathertoast.crust.client.config.CfgEditorCrustConfigFile;
import fathertoast.crust.client.config.ExtraInvButtonsCrustConfigFile;
import fathertoast.crust.common.core.Crust;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = ICrustApi.MOD_ID )
public class ScreenEvents {
    
    /** Called when a GUI is initialized. */
    @SubscribeEvent
    static void onGuiInit( ScreenEvent.Init.Post event ) {
        if( ClientRegister.EXTRA_INV_BUTTONS.GENERAL.enabled.get() && event.getScreen() instanceof AbstractContainerScreen ) {
            MultiPlayerGameMode gameMode = Minecraft.getInstance().gameMode;
            boolean creative = gameMode != null && gameMode.hasInfiniteItems(); // Avoid double-initializing our buttons
            if( !(creative && event.getScreen() instanceof InventoryScreen) && !(!creative && event.getScreen() instanceof CreativeModeInventoryScreen) ) {
                addExtraInventoryButtons( event, (AbstractContainerScreen<?>) event.getScreen() );
            }
        }
        else if( ClientRegister.CONFIG_EDITOR.PAUSE_BUTTON.enabled.get() && event.getScreen() instanceof PauseScreen pauseScreen ) {
            addExtraPauseMenuButtons( event, pauseScreen );
        }
        else if( ClientRegister.CONFIG_EDITOR.MAIN_BUTTON.enabled.get() && event.getScreen() instanceof TitleScreen titleScreen ) {
            addExtraMainMenuButtons( event, titleScreen );
        }
    }
    
    /** Adds the extra buttons to the player's (creative) inventory, if enabled. */
    private static void addExtraInventoryButtons( ScreenEvent.Init event, AbstractContainerScreen<?> screen ) {
        Minecraft mc = screen.getMinecraft();
        ExtraInvButtonsCrustConfigFile.General config = ClientRegister.EXTRA_INV_BUTTONS.GENERAL;
        
        List<ButtonInfo> buttons = new ArrayList<>();
        for( String buttonId : config.buttons.get() ) {
            ButtonInfo button = ButtonInfo.get( buttonId );
            if( button != null ) {
                if( button.isUsable() ) {
                    button.setCanBeActive( true );
                }
                else {
                    button.setCanBeActive( false );
                    if( config.hideUnusable.get() ) continue;
                }
                buttons.add( button );
            }
            else Crust.LOG.warn( "Skipping button with invalid id \"{}\"!", buttonId );
        }
        if( buttons.isEmpty() ) return;
        
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        
        int buttonCount = buttons.size();
        int buttonsPerRow = config.buttonsPerRow.get();
        int buttonRows = Mth.ceil( (float) buttonCount / buttonsPerRow );
        
        int width = buttonsPerRow * ExtraInventoryButton.BUTTON_SPACING - ExtraInventoryButton.BUTTON_PADDING;
        int height = buttonRows * ExtraInventoryButton.BUTTON_SPACING - ExtraInventoryButton.BUTTON_PADDING;
        
        int posX = config.anchorX.get().pos( screenWidth, screen.getXSize(), width ) + config.offsetX.get();
        int posY = config.anchorY.get().pos( screenHeight, screen.getYSize(), height ) + config.offsetY.get();
        
        for( int i = 0; i < buttonCount; i++ ) {
            event.addListener( new ExtraInventoryButton( screen,
                    posX + (i % buttonsPerRow) * ExtraInventoryButton.BUTTON_SPACING,
                    posY + (i / buttonsPerRow) * ExtraInventoryButton.BUTTON_SPACING, buttons.get( i ) ) );
        }
    }
    
    /** Adds the extra buttons to the pause menu, if enabled. */
    private static void addExtraPauseMenuButtons( ScreenEvent.Init event, PauseScreen screen ) {
        Minecraft mc = screen.getMinecraft();
        CfgEditorCrustConfigFile.Button config = ClientRegister.CONFIG_EDITOR.PAUSE_BUTTON;
        
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        int guiWidth = 204;
        int guiHeight = 140;
        int topPadding = 50;
        // Padding is 4, rows are 20 tall, total of 6 rows => 140 height
        // First row has a top padding of 50 to offset all buttons downward
        
        int posX = config.anchorX.get().pos( screenWidth, guiWidth,
                (int) Mth.lerp( 0.5F, 0.0F, (float) (screenWidth - guiWidth) ),
                ExtraMenuButton.BUTTON_SIZE ) + config.offsetX.get();
        int posY = config.anchorY.get().pos( screenHeight, guiHeight,
                (int) Mth.lerp( 0.25F, 0.0F, (float) (screenHeight - guiHeight - topPadding) ) + topPadding,
                ExtraMenuButton.BUTTON_SIZE ) + config.offsetY.get();
        
        event.addListener( new ExtraMenuButton( posX, posY,
                button -> mc.setScreen( new CrustConfigSelectScreen( screen ) ) ) );
    }
    
    /** Adds the extra buttons to the main menu, if enabled. */
    private static void addExtraMainMenuButtons( ScreenEvent.Init event, TitleScreen screen ) {
        Minecraft mc = screen.getMinecraft();
        CfgEditorCrustConfigFile.Button config = ClientRegister.CONFIG_EDITOR.MAIN_BUTTON;
        
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        int guiWidth = 200;
        int guiHeight = 104;
        // Bottom element is placed at top + 72 + 12 and is 20 tall => 104 height
        
        int posX = config.anchorX.get().pos( screenWidth, guiWidth, ExtraMenuButton.BUTTON_SIZE ) + config.offsetX.get();
        int posY = config.anchorY.get().pos( screenHeight, guiHeight, screenHeight / 4 + 48, ExtraMenuButton.BUTTON_SIZE )
                + config.offsetY.get();
        
        event.addListener( new ExtraMenuButton( posX, posY,
                button -> mc.setScreen( new CrustConfigSelectScreen( screen ) ) ) );
    }
}