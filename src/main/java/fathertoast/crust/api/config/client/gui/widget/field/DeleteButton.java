package fathertoast.crust.api.config.client.gui.widget.field;

import com.mojang.blaze3d.systems.RenderSystem;
import fathertoast.crust.api.ICrustApi;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class DeleteButton extends Button {
    
    private static final ResourceLocation DELETE_BUTTON_TEXTURE =
            ResourceLocation.fromNamespaceAndPath( ICrustApi.MOD_ID, "textures/delete_button.png" );
    
    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;
    
    public DeleteButton( Button.OnPress onPress ) {
        super( 0, 0, WIDTH, HEIGHT,
                Component.empty(), onPress, DEFAULT_NARRATION );
    }
    
    @Override
    public void renderWidget( GuiGraphics graphics, int mouseX, int mouseY, float partialTick ) {
        RenderSystem.enableDepthTest();
        
        graphics.blit( DELETE_BUTTON_TEXTURE, getX(), getY(), 0.0F, getTextureY(),
                WIDTH, HEIGHT, WIDTH, HEIGHT * 2 );
    }
    
    @Override
    public int getTextureY() { return HEIGHT * (!active ? 0 : isHoveredOrFocused() ? 1 : 2); }
}
