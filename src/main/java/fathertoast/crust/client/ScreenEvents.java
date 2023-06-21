package fathertoast.crust.client;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.config.client.gui.screen.CrustConfigSelectScreen;
import fathertoast.crust.client.button.ButtonInfo;
import fathertoast.crust.client.button.ExtraMenuButton;
import fathertoast.crust.client.button.ExtraInventoryButton;
import fathertoast.crust.common.core.Crust;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = ICrustApi.MOD_ID )
public class ScreenEvents {
    
    /** Called when a GUI is initialized. */
    @SubscribeEvent
    static void onGuiInit( GuiScreenEvent.InitGuiEvent.Post event ) {
        if( ClientRegister.EXTRA_INV_BUTTONS.GENERAL.enabled.get() && event.getGui() instanceof ContainerScreen ) {
            PlayerController gameMode = Minecraft.getInstance().gameMode;
            boolean creative = gameMode != null && gameMode.hasInfiniteItems(); // Avoid double-initializing our buttons
            if( !(creative && event.getGui() instanceof InventoryScreen) && !(!creative && event.getGui() instanceof CreativeScreen) ) {
                addExtraInventoryButtons( event, (ContainerScreen<?>) event.getGui() );
            }
        }
        else if( ClientRegister.CONFIG_EDITOR.PAUSE_BUTTON.enabled.get() && event.getGui() instanceof IngameMenuScreen ) {
            addExtraPauseMenuButtons( event, (IngameMenuScreen) event.getGui() );
        }
        else if( ClientRegister.CONFIG_EDITOR.MAIN_BUTTON.enabled.get() && event.getGui() instanceof MainMenuScreen ) {
            addExtraMainMenuButtons( event, (MainMenuScreen) event.getGui() );
        }
    }
    
    /** Adds the extra buttons to the player's (creative) inventory, if enabled. */
    private static void addExtraInventoryButtons( GuiScreenEvent.InitGuiEvent event, ContainerScreen<?> screen ) {
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
        int buttonRows = MathHelper.ceil( (float) buttonCount / buttonsPerRow );
        
        int width = buttonsPerRow * ExtraInventoryButton.BUTTON_SPACING - ExtraInventoryButton.BUTTON_PADDING;
        int height = buttonRows * ExtraInventoryButton.BUTTON_SPACING - ExtraInventoryButton.BUTTON_PADDING;
        
        int posX = config.anchorX.get().pos( screenWidth, screen.imageWidth, width ) + config.offsetX.get();
        int posY = config.anchorY.get().pos( screenHeight, screen.imageHeight, height ) + config.offsetY.get();
        
        for( int i = 0; i < buttonCount; i++ ) {
            event.addWidget( new ExtraInventoryButton( screen,
                    posX + (i % buttonsPerRow) * ExtraInventoryButton.BUTTON_SPACING,
                    posY + (i / buttonsPerRow) * ExtraInventoryButton.BUTTON_SPACING, buttons.get( i ) ) );
        }
    }
    
    /** Adds the extra buttons to the pause menu, if enabled. */
    private static void addExtraPauseMenuButtons( GuiScreenEvent.InitGuiEvent event, IngameMenuScreen screen ) {
        Minecraft mc = screen.getMinecraft();
        CfgEditorCrustConfigFile.Button config = ClientRegister.CONFIG_EDITOR.PAUSE_BUTTON;
        
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        int guiWidth = 204;
        int guiHeight = 116;
        
        int posX = config.anchorX.get().pos( screenWidth, guiWidth, ExtraMenuButton.BUTTON_SIZE ) + config.offsetX.get();
        int posY = config.anchorY.get().pos( screenHeight, guiHeight, screenHeight / 4 + 8, ExtraMenuButton.BUTTON_SIZE )
                + config.offsetY.get();
        
        event.addWidget( new ExtraMenuButton( posX, posY,
                button -> mc.setScreen( new CrustConfigSelectScreen( screen ) ) ) );
    }
    
    /** Adds the extra buttons to the main menu, if enabled. */
    private static void addExtraMainMenuButtons( GuiScreenEvent.InitGuiEvent event, MainMenuScreen screen ) {
        Minecraft mc = screen.getMinecraft();
        CfgEditorCrustConfigFile.Button config = ClientRegister.CONFIG_EDITOR.MAIN_BUTTON;
        
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        int guiWidth = 200;
        int guiHeight = 104;
        
        int posX = config.anchorX.get().pos( screenWidth, guiWidth, ExtraMenuButton.BUTTON_SIZE ) + config.offsetX.get();
        int posY = config.anchorY.get().pos( screenHeight, guiHeight, screenHeight / 4 + 48, ExtraMenuButton.BUTTON_SIZE )
                + config.offsetY.get();
        
        event.addWidget( new ExtraMenuButton( posX, posY,
                button -> mc.setScreen( new CrustConfigSelectScreen( screen ) ) ) );
    }
}