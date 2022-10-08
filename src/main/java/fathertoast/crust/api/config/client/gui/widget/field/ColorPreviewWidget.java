package fathertoast.crust.api.config.client.gui.widget.field;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class ColorPreviewWidget extends Widget {
    
    private static final ResourceLocation BACKGROUND_TEXTURE =
            new ResourceLocation( "crust", "textures/swatch.png" );
    
    public static final int SIZE = 20;
    
    private int argb;
    
    public ColorPreviewWidget( int x, int y ) {
        super( x, y, SIZE, SIZE, StringTextComponent.EMPTY );
    }
    
    /** Sets the color displayed in this swatch. */
    public void setColor( int color, boolean usesAlpha ) {
        argb = usesAlpha ? color : color | 0xFF000000;
    }
    
    @Override
    public void renderButton( MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks ) {
        if( !visible ) return;
        
        Minecraft.getInstance().getTextureManager().bind( BACKGROUND_TEXTURE );
        RenderSystem.enableDepthTest();
        blit( matrixStack, x, y, 0.0F, SIZE,
                SIZE, SIZE, SIZE, SIZE );
        fill( matrixStack, x + 1, y + 1,
                x + SIZE - 1, y + SIZE - 1, argb );
    }
}