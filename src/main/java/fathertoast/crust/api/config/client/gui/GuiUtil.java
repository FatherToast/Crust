package fathertoast.crust.api.config.client.gui;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public class GuiUtil {
    
    
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
            positionTooltip( guiWidth, tooltipPos, tooltipWidth );
            return tooltipPos;
        }
        
        private void positionTooltip( int guiWidth, Vector2i tooltipPos, int tooltipWidth ) {
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
