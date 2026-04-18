package fathertoast.crust.api.client.util;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public final class GuiUtil {
    
    
    public static class TooltipPositioner {
        
        /**
         * Centers the tooltip's X position and puts it above the cursor when possible.
         * Attempts to always make the tooltip fit inside the screen.
         */
        public static final ClientTooltipPositioner CENTERED_X = new ClientTooltipPositioner() {
            
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
            public Vector2ic positionTooltip( int guiWidth, int guiHeight, int x, int y, int tooltipWidth, int tooltipHeight ) {
                Vector2i pos = (new Vector2i( x, y ));
                
                // Modify X
                pos.x = pos.x - tooltipWidth / 2;
                
                if( pos.x < 4 )
                    pos.x = 4;
                else if( pos.x > (guiWidth - tooltipWidth) - 4 )
                    pos.x = (guiWidth - tooltipWidth) - 4;
                
                // Modify Y
                if( (pos.y - tooltipHeight) - 14 < 0 )
                    pos.y = pos.y + 15;
                else
                    pos.y = (pos.y - tooltipHeight) - 10;
                
                return pos;
            }
        };
        
        /**
         * Centers the tooltip's Y position and puts it to the right of the cursor when possible.
         * Attempts to always make the tooltip fit inside the screen.
         */
        public static final ClientTooltipPositioner CENTERED_Y = new ClientTooltipPositioner() {
            
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
            public Vector2ic positionTooltip( int guiWidth, int guiHeight, int x, int y, int tooltipWidth, int tooltipHeight ) {
                Vector2i pos = (new Vector2i( x, y ));
                
                // Modify X
                if( (pos.x + tooltipWidth) + 14 > guiWidth )
                    pos.x -= tooltipWidth + 10;
                else
                    pos.x += 10;
                
                // Modify Y
                pos.y = pos.y - tooltipHeight / 2;
                
                if( pos.y < 4 )
                    pos.y = 4;
                else if( pos.y > (guiHeight - tooltipHeight) - 4 )
                    pos.y = (guiHeight - tooltipHeight) - 4;
                
                return pos;
            }
        };
    }
    
    // Utility class
    private GuiUtil() { }
}
