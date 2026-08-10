package fathertoast.crust.api.config.client.gui.widget.field;

import com.mojang.blaze3d.platform.InputConstants;

/**
 * Optional interface that can be implemented by 'popup widgets' to receive additional events.
 */
public interface IPopupWidget {
    /** Called when the widget is opened to perform standard setup. */
    default void init() {}
    
    /**
     * Called when a mouse button is clicked out of the widget's bounds.
     *
     * @param mouseKey The mouse key that was clicked (see {@link InputConstants.Type#MOUSE}).
     * @return True if the popup should be closed.
     */
    default boolean mouseClickedOutOfBounds( double x, double y, int mouseKey ) { return !isFullScreen(); }
    
    /** @return True if this widget covers the entire screen. Causes the screen to skip rendering if so. */
    default boolean isFullScreen() { return false; }
    
    /** @return True if this popup should close when the Esc key is pressed. */
    default boolean shouldCloseOnEsc() { return true; }
    
    /** Called each tick to update animations. */
    default void tick() {}
}