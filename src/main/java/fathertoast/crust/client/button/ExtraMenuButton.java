package fathertoast.crust.client.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import fathertoast.crust.api.config.client.gui.screen.CrustConfigSelectScreen;
import fathertoast.crust.common.core.Crust;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class ExtraMenuButton extends Button {
    
    public static final ResourceLocation BUTTON_TEXTURE = new ResourceLocation( Crust.MOD_ID, "textures/editor_button.png" );
    
    public static final int BUTTON_SIZE = 20;
    
    
    public ExtraMenuButton( int leftPos, int topPos, IPressable onPress ) {
        super( leftPos, topPos, BUTTON_SIZE, BUTTON_SIZE,
                new StringTextComponent( "" ), onPress, Button.NO_TOOLTIP );
    }
    
    @Override
    public void renderButton( MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks ) {
        Minecraft mc = Minecraft.getInstance();
        
        mc.getTextureManager().bind( BUTTON_TEXTURE );
        //noinspection deprecation
        RenderSystem.color4f( 1.0F, 1.0F, 1.0F, alpha );
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        
        blit( matrixStack, x, y, 0.0F, BUTTON_SIZE * getYImage( isHovered() ),
                BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE * 2 );
    }
    
    @Override
    protected int getYImage( boolean isHovered ) { return isHovered ? 1 : 0; }
}