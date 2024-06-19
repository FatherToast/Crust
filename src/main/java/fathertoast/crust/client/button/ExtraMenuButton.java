package fathertoast.crust.client.button;

import com.mojang.blaze3d.systems.RenderSystem;
import fathertoast.crust.api.ICrustApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class ExtraMenuButton extends Button {
    
    public static final ResourceLocation BUTTON_TEXTURE = new ResourceLocation( ICrustApi.MOD_ID, "textures/editor_button.png" );
    
    public static final int BUTTON_SIZE = 20;
    
    
    public ExtraMenuButton( int leftPos, int topPos, OnPress onPress ) {
        super( leftPos, topPos, BUTTON_SIZE, BUTTON_SIZE,
                Component.literal(""), onPress, Supplier::get );
    }
    
    @Override
    public void renderWidget( GuiGraphics graphics, int mouseX, int mouseY, float partialTicks ) {
        RenderSystem.setShaderColor( 1.0F, 1.0F, 1.0F, alpha );
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        graphics.blit( BUTTON_TEXTURE, getX(), getY(), 0.0F, BUTTON_SIZE * getTextureY( ),
                BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE * 2 );
    }
    
    @Override
    public int getTextureY( ) { return isHovered ? 1 : 0; }
}