package fathertoast.crust.api.config.client.gui.widget.field.list;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;

/**
 * The class all entries in popup lists must extend.
 */
public abstract class AbstractPopupListEntry<E extends AbstractPopupListEntry<E>> implements GuiEventListener {
    
    /** The popup list containing this entry. */
    protected PopupListWidget<E> popup;
    
    private boolean focused;
    
    /** Called to render the list entry. */
    public abstract void render( GuiGraphics graphics, int index, int x, int y, int width, int height,
                                 int mouseX, int mouseY, boolean mouseOver, float partialTicks );
    
    @Override
    public boolean isMouseOver( double mouseX, double mouseY ) {
        return equals( popup.getEntryAtPosition( mouseX, mouseY ) );
    }
    
    @Override
    public void setFocused( boolean focus ) { focused = focus; }
    
    @Override
    public boolean isFocused() { return focused; }
}