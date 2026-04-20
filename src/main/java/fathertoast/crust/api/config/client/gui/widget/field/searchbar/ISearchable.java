package fathertoast.crust.api.config.client.gui.widget.field.searchbar;

import fathertoast.crust.api.config.client.gui.widget.SearchableSelectionList;
import fathertoast.crust.client.ClientRegister;
import net.minecraft.client.gui.GuiGraphics;

import javax.annotation.Nullable;

/** Represents an element that can be looked up by name by a {@link Searchbar}. */
public interface ISearchable {
    
    /** @return An identifying String to be looked up by a {@link Searchbar} */
    @Nullable
    String getLookupName();
    
    /**
     * This is used by {@link SearchableSelectionList} to render
     * a highlight behind this searchable if it is said list's
     * currently focused search match.
     *
     * @param isFocused    True if this searchable is the currently focused entry in the underlying searchable list.
     * @param scrollbarPos The position of the underlying searchable list's scrollbar.
     * @see SearchableSelectionList#renderItem(GuiGraphics, int, int, float, int, int, int, int, int)
     */
    default void renderHighlight( GuiGraphics graphics, boolean isFocused, int scrollbarPos, int mouseX, int mouseY, float partialTick,
                                  int itemIndex, int rowLeft, int rowTop, int rowWidth, int itemHeight ) {
        int width = rowLeft + (scrollbarPos - rowLeft);
        int height = rowTop + itemHeight + 3;
        
        drawDefaultHighlight( graphics, isFocused, rowLeft - 3, rowTop, width, height );
    }
    
    /**
     * Renders the default highlight.
     *
     * @param graphics  The GuiGraphics instance to make draw calls to.
     * @param isFocused True if this searchable is the currently focused entry in the underlying searchable list.
     * @param x         The x-position to draw the highlight at.
     * @param y         The y-position to draw the highlight at.
     * @param width     The width of the highlight rectangle to draw.
     * @param height    The height of the highlight rectangle to draw.
     */
    static void drawDefaultHighlight( GuiGraphics graphics, boolean isFocused, int x, int y, int width, int height ) {
        if( isFocused ) {
            int color = ClientRegister.CONFIG_EDITOR.SEARCHBAR.highlightColor.get();
            
            if( (color >>> 24) == 0 ) {
                // Force the color to be solid if it is fully transparent.
                color |= 0xFF000000;
            }
            graphics.fill( x, y, width, height, color );
        }
        else {
            graphics.fillGradient( x, y, width, height, 0x40_878787, 0x60_878787 );
        }
    }
}
