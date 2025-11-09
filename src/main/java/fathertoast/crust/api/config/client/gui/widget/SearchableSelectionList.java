package fathertoast.crust.api.config.client.gui.widget;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import fathertoast.crust.api.config.client.gui.ElementOffset;
import fathertoast.crust.api.config.client.gui.widget.field.Searchbar;
import fathertoast.crust.client.ClientRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

/**
 * A selection list implementation designed to
 * work in conjunction with a {@link Searchbar} widget.
 */
public abstract class SearchableSelectionList<T extends ContainerObjectSelectionList.Entry<T>> extends ContainerObjectSelectionList<T> {
    
    /** Offsets to be applied to element highlights. */
    private final ElementOffset highlightOffset;
    /** A bidirectional map that maps search candidate indexes to selection list element indexes. */
    private BiMap<Integer, Integer> elementByCandidateIndexes = HashBiMap.create();
    /** The focused search candidate index, typically specified by a searchbar. */
    private int focusedIndex;
    
    public SearchableSelectionList( Minecraft minecraft, int width, int height, int topY, int bottomY, int itemHeight, ElementOffset highlightOffset ) {
        super( minecraft, width, height, topY, bottomY, itemHeight );
        this.highlightOffset = highlightOffset;
    }
    
    /**
     * Intended to be called by a {@link Searchbar} instance that
     * operates on this search list.<br><br>
     * <p>
     * Updates this search list's map of element-by-candidate indexes;
     * Element index being the index of an element in this search list
     * and candidate index being the element's index in the search bar as
     * a search match.
     */
    public void setIndexes( BiMap<Integer, Integer> elementByCandidateIndexes ) {
        this.elementByCandidateIndexes = elementByCandidateIndexes;
    }
    
    /**
     * Intended to be called by a {@link Searchbar} instance that
     * operates on this search list.<br><br>
     * <p>
     * Updates this search list's focused search match index.
     * Whatever item in this list that corresponds to the focused
     * index will have a special highlight drawn behind it to stand out.
     */
    public void setFocusedIndex( int index ) {
        this.focusedIndex = index;
    }
    
    /**
     * Called from {@link net.minecraft.client.gui.components.AbstractSelectionList#renderList(GuiGraphics, int, int, float)}.
     * Draws an item from this list for the given index.
     */
    @Override
    protected void renderItem( GuiGraphics graphics, int mouseX, int mouseY, float partialTick,
                               int itemIndex, int rowLeft, int rowTop, int rowWidth, int itemHeight ) {
        // Check if highlights are enabled in the config.
        if( ClientRegister.CONFIG_EDITOR.SEARCHBAR.showSearchHighlights.get() ) {
            if( elementByCandidateIndexes.inverse().containsKey( itemIndex ) ) {
                int x = (getLeft() + ((getWidth() - rowWidth) / 2)) + highlightOffset.getX();
                int y = rowTop + highlightOffset.getY();
                int width = (getLeft() + ((getWidth() + rowWidth) / 2) - 3) + highlightOffset.getWidth();
                int height = (rowTop + itemHeight + 3) + highlightOffset.getHeight();
                
                // TODO - Maybe make highlight color configurable
                // noinspection ConstantConditions
                if( elementByCandidateIndexes.inverse().get( itemIndex ) == focusedIndex ) {
                    graphics.fillGradient( x, y, width, height, 0x70_EDDB38, 0x90_EDDB38 );
                }
                else {
                    graphics.fillGradient( x, y, width, height, 0x40_878787, 0x50_878787 );
                }
            }
        }
        super.renderItem( graphics, mouseX, mouseY, partialTick, itemIndex, rowLeft, rowTop, rowWidth, itemHeight );
    }
}
