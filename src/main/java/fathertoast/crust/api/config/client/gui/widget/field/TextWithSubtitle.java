package fathertoast.crust.api.config.client.gui.widget.field;

import com.mojang.blaze3d.platform.InputConstants;
import fathertoast.crust.api.client.util.GuiUtil;
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
    private final boolean CENTER_X;
    
    private final Screen SCREEN;
    private final Font FONT;
    
    
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
        FONT = font;
        CENTER_X = centerText;
        SCREEN = screen;
        TEXT = text;
        
        if( subtitle != null )
            setTooltip( Tooltip.create( subtitle ) );
    }
    
    /**
     * Called when a mouse button is clicked.
     *
     * @param mouseKey The mouse key that was clicked (see {@link InputConstants.Type#MOUSE}).
     * @return True if the event has been handled.
     */
    @Override
    public boolean mouseClicked( double x, double y, int mouseKey ) {
        // Do nothing and return
        return false;
    }
    
    /** Creates a tooltip positioner for this widget. */
    @Override
    protected ClientTooltipPositioner createTooltipPositioner() {
        return GuiUtil.getOrForMenu( this, CENTER_X
                ? GuiUtil.TooltipPositioner.CENTERED_X
                : GuiUtil.TooltipPositioner.CENTERED_Y
        );
    }
    
    /** Renders this widget. */
    @Override
    protected void renderWidget( GuiGraphics graphics, int mouseX, int mouseY, float partialTick ) {
        graphics.drawString( FONT, TEXT, getX(), getY(), 0xFFFFFF );
    }
    
    /** Called when building narration elements for this widget. */
    @Override
    protected void updateWidgetNarration( NarrationElementOutput output ) {
        output.add( NarratedElementType.TITLE, TEXT );
    }
    
    /** @return This widget's current focus path. */
    @Override
    @Nullable
    public ComponentPath getCurrentFocusPath() {
        // Return null; this widget should not be focusable
        return null;
    }
    
    /**
     * Called when focus change is requested (for example, tab or shift+tab).
     *
     * @param event Represents the type of focus shift. In vanilla, this is always
     *              one of the three following types: {@code ArrowNavigation}, {@code InitialFocus} or {@code TabNavigation}.
     * @return This GUI's new focus state.
     */
    @Override
    @Nullable
    public ComponentPath nextFocusPath( FocusNavigationEvent event ) {
        // Return a path to the next widget that comes after this one
        // in the parent screen's widget list.
        boolean returnNext = false;
        
        for( GuiEventListener listener : SCREEN.children() ) {
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
