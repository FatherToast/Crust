package fathertoast.crust.api.config.client.gui.widget.field;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * A simple gui component that displays a color.
 */
public class ColorPreviewWidget extends AbstractWidget {
    
    private static final ResourceLocation BACKGROUND_TEXTURE =
            new ResourceLocation( "crust", "textures/swatch.png" );
    
    public static final int SIZE = 20;
    
    private int argb;
    
    public ColorPreviewWidget( int x, int y ) {
        super( x, y, SIZE, SIZE, Component.empty() );
    }
    
    /** Sets the color displayed in this swatch. */
    public void setColor( int color, boolean usesAlpha ) {
        argb = usesAlpha ? color : color | 0xFF000000;
    }
    
    @Override
    public void renderWidget( GuiGraphics graphics, int mouseX, int mouseY, float partialTicks ) {
        if( !visible ) return;

        RenderSystem.enableDepthTest();
        graphics.blit( BACKGROUND_TEXTURE, getX(), getY(), 0.0F, SIZE,
                SIZE, SIZE, SIZE, SIZE );
        graphics.fill( getX() + 1, getY() + 1,
                getX() + SIZE - 1, getY() + SIZE - 1, argb );
    }

    @Override
    protected void updateWidgetNarration( NarrationElementOutput output ) {

    }
}