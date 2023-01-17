package fathertoast.crust.api.config.client.gui.widget.field;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

/**
 * The 'reset button' displayed to the right of each config field in the in-game config editor.
 * Actual reset logic is handled in the config field list that creates these buttons.
 */
public class ResetButton extends Button {
    
    private static final ResourceLocation RESET_BUTTON_TEXTURE =
            new ResourceLocation( "crust", "textures/reset_button.png" );
    
    public static final int WIDTH = 10;
    public static final int HEIGHT = 20;
    
    public ResetButton( Button.IPressable onPress ) {
        super( 0, 0, WIDTH, HEIGHT,
                StringTextComponent.EMPTY, onPress, NO_TOOLTIP );
    }
    
    public void renderButton( MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks ) {
        Minecraft.getInstance().getTextureManager().bind( RESET_BUTTON_TEXTURE );
        RenderSystem.enableDepthTest();
        blit( matrixStack, x, y, 0.0F, HEIGHT * getYImage( isHovered() ),
                WIDTH, HEIGHT, WIDTH, HEIGHT * 3 );
    }
}