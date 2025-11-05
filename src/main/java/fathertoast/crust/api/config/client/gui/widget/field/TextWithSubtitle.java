package fathertoast.crust.api.config.client.gui.widget.field;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.Component;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * A widget that draws a text component and displays
 * a tooltip containing the specified "subtitle" when hovered over.
 * Subtitle can be null.
 */
public class TextWithSubtitle extends AbstractWidget {
    
    private final Component TEXT;
    @Nullable
    private final Component SUBTITLE;
    
    private final boolean centerText;
    private final Font font;
    
    
    /**
     * Helper method for creating a new instance.
     *
     * @param x          The x-position of the widget.
     * @param y          The y-position of the widget.
     * @param centerText If true, x-position is recalculated so the widget becomes centered on the given x value.
     * @param text       The title/main text this widget should display always.
     * @param subtitle   The subtitle to draw as a tooltip when hovering over the main text. Optional.
     */
    public static TextWithSubtitle create( Font font, int x, int y, boolean centerText,
                                           Component text, @Nullable Component subtitle ) {
        int width = font.width( text );
        int height = font.lineHeight;
        
        if( centerText ) {
            x = x - width / 2;
        }
        return new TextWithSubtitle( font, x, y, width, height, centerText, text, subtitle );
    }
    
    
    private TextWithSubtitle( Font font, int x, int y, int width, int height, boolean centerText, Component text, @Nullable Component subtitle ) {
        super( x, y, width, height, Component.literal( "" ) );
        Objects.requireNonNull( text );
        TEXT = text;
        SUBTITLE = subtitle;
        this.font = font;
        this.centerText = centerText;
    }
    
    @Override
    public boolean mouseClicked( double x, double y, int mouseKey ) {
        // Do nothing and return
        return false;
    }
    
    @Override
    protected void renderWidget( GuiGraphics graphics, int mouseX, int mouseY, float partialTick ) {
        graphics.drawString( font, TEXT, getX(), getY(), 0xFFFFFF );
        
        if( SUBTITLE != null ) {
            if( mouseX >= getX() && mouseX <= getX() + width && mouseY >= getY() && mouseY <= getY() + height ) {
                graphics.renderTooltip(
                        font,
                        List.of( SUBTITLE.getVisualOrderText() ),
                        centerText ? TooltipPositioner.CENTERED : TooltipPositioner.STANDARD,
                        mouseX,
                        mouseY
                );
            }
        }
    }
    
    @Override
    protected void updateWidgetNarration( NarrationElementOutput neo ) {
        neo.add( NarratedElementType.TITLE, TEXT, SUBTITLE );
    }
    
    public record TooltipPositioner(boolean centered) implements ClientTooltipPositioner {
        /**
         * Positions tooltip same as {@link net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner},
         * except measures are taken to prevent tooltips from going off-screen on the Y-axis by
         * putting them below the cursor.
         */
        public static final TooltipPositioner STANDARD = new TooltipPositioner( false );
        /** Similar to {@link TooltipPositioner#STANDARD}, except tooltip gets centered on the X-axis. */
        public static final TooltipPositioner CENTERED = new TooltipPositioner( true );
        
        /**
         * @param guiWidth      The width of the GUI
         * @param guiHeight     The height of the GUI
         * @param x             The tooltip's X-position
         * @param y             The tooltip's Y-position
         * @param tooltipWidth  The width of the tooltip
         * @param tooltipHeight The height of the tooltip
         * @return A vector containing the starting X and Y positions of the tooltip.
         */
        @Override
        public Vector2ic positionTooltip( int guiWidth, int guiHeight, int x, int y, int tooltipWidth, int tooltipHeight ) {
            Vector2i tooltipPos = (new Vector2i( x, y )).add( centered ? 0 : 12, -14 );
            positionTooltip( guiWidth, guiHeight, tooltipPos, tooltipWidth, tooltipHeight );
            return tooltipPos;
        }
        
        private void positionTooltip( int guiWidth, int guiHeight, Vector2i tooltipPos, int tooltipWidth, int tooltipHeight ) {
            if( centered ) {
                tooltipPos.x = tooltipPos.x - tooltipWidth / 2;
                
                if( tooltipPos.x < 4 )
                    tooltipPos.x = 4;
                else if( tooltipPos.x > (guiWidth - tooltipWidth) - 4 )
                    tooltipPos.x = (guiWidth - tooltipWidth) - 4;
            }
            else if( tooltipPos.x + tooltipWidth > guiWidth ) {
                tooltipPos.x = Math.max( tooltipPos.x - 24 - tooltipWidth, 4 );
            }
            
            if( tooltipPos.y < 10 ) {
                tooltipPos.y = tooltipPos.y + 25;
            }
        }
    }
}
