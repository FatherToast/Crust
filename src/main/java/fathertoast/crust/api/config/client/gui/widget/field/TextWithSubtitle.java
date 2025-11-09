package fathertoast.crust.api.config.client.gui.widget.field;

import fathertoast.crust.api.config.client.gui.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.MenuTooltipPositioner;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * A widget that draws a text component and displays
 * a tooltip containing the specified "subtitle" when hovered over.
 * Subtitle can be null.
 */
public class TextWithSubtitle extends AbstractWidget {
    
    private final Component TEXT;
    
    private final boolean centerText;
    private final Screen screen;
    private final Font font;
    
    
    /**
     * Creates a new TextWithSubtitle instance.
     *
     * @param x          The x-position of the widget.
     * @param y          The y-position of the widget.
     * @param centerText If true, x-position is recalculated so the widget becomes centered on the given x value.
     * @param text       The title/main text this widget should display always.
     * @param subtitle   The subtitle to draw as a tooltip when hovering over the main text. Optional.
     */
    public static TextWithSubtitle create( Screen screen, Font font, int x, int y, boolean centerText,
                                           Component text, @Nullable Component subtitle ) {
        int width = font.width( text );
        int height = font.lineHeight;
        
        if( centerText ) {
            x = x - width / 2;
        }
        return new TextWithSubtitle( screen, font, x, y, width, height, centerText, text, subtitle );
    }
    
    
    private TextWithSubtitle( Screen screen, Font font, int x, int y, int width, int height, boolean centerText, Component text, @Nullable Component subtitle ) {
        super( x, y, width, height, Component.literal( "" ) );
        Objects.requireNonNull( text );
        this.font = font;
        this.centerText = centerText;
        this.screen = screen;
        
        TEXT = text;
        
        if( subtitle != null )
            setTooltip( Tooltip.create( subtitle ) );
    }
    
    @Override
    public boolean mouseClicked( double x, double y, int mouseKey ) {
        // Do nothing and return
        return false;
    }
    
    @Override
    protected ClientTooltipPositioner createTooltipPositioner() {
        if( !isHovered && Minecraft.getInstance().getLastInputType().isKeyboard() )
            return new MenuTooltipPositioner( this );
        
        return centerText ? GuiUtil.TooltipPositioner.CENTERED : GuiUtil.TooltipPositioner.STANDARD;
    }
    
    @Override
    protected void renderWidget( GuiGraphics graphics, int mouseX, int mouseY, float partialTick ) {
        graphics.drawString( font, TEXT, getX(), getY(), 0xFFFFFF );
    }
    
    @Override
    protected void updateWidgetNarration( NarrationElementOutput neo ) {
        neo.add( NarratedElementType.TITLE, TEXT );
    }
    
    @Override
    @Nullable
    public ComponentPath getCurrentFocusPath() {
        // Return null; this widget should not be focusable
        return null;
    }
    
    @Override
    @Nullable
    public ComponentPath nextFocusPath( FocusNavigationEvent event ) {
        // Return a path to the next widget that comes after this one
        // in the parent screen's widget list.
        boolean returnNext = false;
        
        for( GuiEventListener listener : screen.children() ) {
            if( listener == this ) {
                returnNext = true;
                continue;
            }
            if( returnNext )
                return new ComponentPath.Leaf( listener );
        }
        return null;
    }
}
