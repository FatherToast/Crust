package fathertoast.crust.api.config.client.gui.widget.field;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

// TODO

/**
 * A popup that displays a color picker circle
 * as well as RGBA sliders for fine-tuning.
 */
public class ColorPickerWidget extends AbstractWidget implements IPopupWidget {
    
    public static final int SIZE = 20;
    
    private int argb;
    
    public ColorPickerWidget( int x, int y ) {
        super( x, y, SIZE, SIZE, Component.empty() );
    }
    
    /** Sets the color displayed in this swatch. */
    public void setColor( int color, boolean usesAlpha ) {
        argb = usesAlpha ? color : color | 0xFF000000;
    }
    
    @Override
    public void renderWidget( GuiGraphics graphics, int mouseX, int mouseY, float partialTicks ) {
        if( !visible ) return;
        
        renderColorPicker( graphics, mouseX, mouseY, partialTicks );
    }
    
    private void renderColorPicker( GuiGraphics graphics, int mouseX, int mouseY, float partialTicks ) {
    
    }
    
    @Override
    protected void updateWidgetNarration( NarrationElementOutput output ) {
    
    }
}
