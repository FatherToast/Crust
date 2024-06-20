package fathertoast.crust.api.config.client.gui.widget.field;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * The 'reset button' displayed to the right of each config field in the in-game config editor.
 * Actual reset logic is handled in the config field list that creates these buttons.
 */
public class ResetButton extends Button {
    
    private static final ResourceLocation RESET_BUTTON_TEXTURE =
            new ResourceLocation( "crust", "textures/reset_button.png" );
    
    public static final int WIDTH = 10;
    public static final int HEIGHT = 20;
    
    public ResetButton( Button.OnPress onPress ) {
        super( 0, 0, WIDTH, HEIGHT,
                Component.empty(), onPress, DEFAULT_NARRATION );
    }
    
    @Override
    public void renderWidget( GuiGraphics graphics, int mouseX, int mouseY, float partialTick ) {
        RenderSystem.enableDepthTest();
        
        graphics.blit( RESET_BUTTON_TEXTURE, getX(), getY(), 0.0F, getTextureY(),
                WIDTH, HEIGHT, WIDTH, HEIGHT * 3 );
    }
    
    @Override
    public int getTextureY() { return HEIGHT * (!active ? 0 : isHoveredOrFocused() ? 2 : 1); }
}