package fathertoast.crust.api.config.client.gui.widget.field;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import fathertoast.crust.api.ICrustApi;
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
            ResourceLocation.fromNamespaceAndPath( ICrustApi.MOD_ID, "textures/swatch.png" );
    
    public static final int DEFAULT_SIZE = 20;
    
    private final int size;
    private int argb;
    
    
    /** Constructs a new color preview widget with the specified size. */
    public ColorPreviewWidget( int x, int y, int size ) {
        super( x, y, size, size, Component.empty() );
        this.size = Math.max( size, 5 );
    }
    
    /** Constructs a new color preview widget with the default size of 20. */
    public ColorPreviewWidget( int x, int y ) {
        this( x, y, DEFAULT_SIZE );
    }
    
    /** Sets the color displayed in this swatch. */
    public void setColor( int color, boolean usesAlpha ) {
        argb = usesAlpha ? color : color | 0xFF000000;
    }
    
    /** Renders this widget. */
    @Override
    public void renderWidget( GuiGraphics graphics, int mouseX, int mouseY, float partialTicks ) {
        if( !visible ) return;
        
        RenderSystem.enableDepthTest();
        graphics.blit( BACKGROUND_TEXTURE, getX(), getY(), 0.0F, size,
                size, size, size, size );
        graphics.fill( getX() + 1, getY() + 1,
                getX() + size - 1, getY() + size - 1, argb );
    }
    
    /** Called when building narration elements for this widget. */
    @Override
    protected void updateWidgetNarration( NarrationElementOutput output ) {
        // Nothing to narrate
    }
    
    /**
     * Called when a mouse button is clicked.
     *
     * @param mouseKey The mouse key that was clicked (see {@link InputConstants.Type#MOUSE}).
     * @return True if the event has been handled.
     */
    @Override
    public boolean mouseClicked( double x, double y, int mouseKey ) {
        return false;
    }
    
    /** @return The width and height of this widget. */
    public int getSize() {
        return size;
    }
}