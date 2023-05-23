package fathertoast.crust.api.config.client.gui.widget.field;

import net.minecraft.client.util.InputMappings;

/**
 * Optional interface that can be implemented by 'popup widgets' to receive additional events.
 */
public interface IPopupWidget {
    
    /**
     * Called when a mouse button is clicked out of the widget's bounds.
     *
     * @param mouseKey The mouse key that was clicked (see {@link InputMappings.Type#MOUSE}).
     * @return True if the popup should be closed.
     */
    @SuppressWarnings( "unused" )
    default boolean mouseClickedOutOfBounds( double x, double y, int mouseKey ) { return true; }
    
    /** @return True if this popup should close when the Esc key is pressed. */
    default boolean shouldCloseOnEsc() { return true; }
    
    /** Called each tick to update animations. */
    default void tick() { }
}