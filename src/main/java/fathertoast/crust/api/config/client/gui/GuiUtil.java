package fathertoast.crust.api.config.client.gui;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import javax.annotation.Nonnull;

public class GuiUtil {
    
    
    public record TooltipPositioner(boolean centered) implements ClientTooltipPositioner {
        
        /**
         * Positions tooltip same as {@link net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner},
         * except measures are taken to prevent tooltips from going off-screen on the Y-axis by
         * putting them below the cursor.
         */
        public static final TooltipPositioner STANDARD = new TooltipPositioner( false );
        /**
         * Similar to {@link TooltipPositioner#STANDARD}, except tooltip gets centered
         * on the X-axis when possible without going off-screen.
         */
        public static final TooltipPositioner CENTERED = new TooltipPositioner( true );
        
        
        /**
         * @param guiWidth      The width of the GUI
         * @param guiHeight     The height of the GUI
         * @param x             The tooltip's unmodified X-position
         * @param y             The tooltip's unmodified Y-position
         * @param tooltipWidth  The width of the tooltip
         * @param tooltipHeight The height of the tooltip
         * @return A vector containing the new X and Y positions of the tooltip.
         */
        @Override
        @Nonnull
        public Vector2ic positionTooltip( int guiWidth, int guiHeight, int x, int y, int tooltipWidth, int tooltipHeight ) {
            Vector2i pos = (new Vector2i( x, y )).add( centered ? 0 : 12, -14 );
            
            if( centered ) {
                pos.x = pos.x - tooltipWidth / 2;
                
                if( pos.x < 4 )
                    pos.x = 4;
                else if( pos.x > (guiWidth - tooltipWidth) - 4 )
                    pos.x = (guiWidth - tooltipWidth) - 4;
            }
            else if( pos.x + tooltipWidth > guiWidth ) {
                pos.x = Math.max( pos.x - 24 - tooltipWidth, 4 );
            }
            
            if( pos.y < 10 ) {
                pos.y = pos.y + 25;
            }
            
            return pos;
        }
    }
}
