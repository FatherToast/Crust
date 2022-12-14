package fathertoast.crust.client;

import fathertoast.crust.client.button.ButtonInfo;
import fathertoast.crust.client.button.ExtraInventoryButton;
import fathertoast.crust.common.core.Crust;
import net.minecraft.client.Minecraft;
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

@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = Crust.MOD_ID )
public class ScreenEvents {
    
    /** Called when a GUI is initialized. */
    @SubscribeEvent
    static void onGuiInit( GuiScreenEvent.InitGuiEvent.Post event ) {
        PlayerController gameMode = Minecraft.getInstance().gameMode;
        if( ClientRegister.EXTRA_INV_BUTTONS.GENERAL.enabled.get() && event.getGui() instanceof ContainerScreen ) {
            boolean creative = gameMode != null && gameMode.hasInfiniteItems(); // Avoid double-initializing our buttons
            if( !(creative && event.getGui() instanceof InventoryScreen) && !(!creative && event.getGui() instanceof CreativeScreen) ) {
                addExtraInventoryButtons( event, (ContainerScreen<?>) event.getGui() );
            }
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
}