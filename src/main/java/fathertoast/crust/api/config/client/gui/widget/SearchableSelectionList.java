package fathertoast.crust.api.config.client.gui.widget;

import fathertoast.crust.api.config.client.gui.ElementOffset;
import fathertoast.crust.api.config.client.gui.widget.field.Searchbar;
import fathertoast.crust.client.ClientRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import org.jetbrains.annotations.Nullable;

/**
 * A selection list implementation designed to
 * work in conjunction with a {@link Searchbar} widget.
 */
public abstract class SearchableSelectionList<T extends ContainerObjectSelectionList.Entry<T>> extends ContainerObjectSelectionList<T> {
    
    /** Offsets to be applied to element highlights. */
    private final ElementOffset highlightOffset;
    
    /** The searchbar this list "communicates" with. */
    @Nullable
    private Searchbar searchbar;
    
    
    public SearchableSelectionList( Minecraft minecraft, int width, int height, int topY, int bottomY, int itemHeight, ElementOffset highlightOffset ) {
        super( minecraft, width, height, topY, bottomY, itemHeight );
        this.highlightOffset = highlightOffset;
    }
    
    /**
     * Sets the searchbar for this list.
     * Without a searchbar, this list doesn't do much interesting.
     */
    public void setSearchbar( Searchbar searchbar ) {
        this.searchbar = searchbar;
    }
    
    /**
     * Tells the underlying searchbar to forcibly run a new search.
     * Make sure to call this if the number of elements/children in
     * this list changes.
     */
    protected void rerunSearch() {
        if( searchbar != null && !searchbar.getValue().isEmpty() )
            searchbar.search( searchbar.getValue(), true );
    }
    
    /**
     * Called from {@link net.minecraft.client.gui.components.AbstractSelectionList#renderList(GuiGraphics, int, int, float)}.
     * Draws an item from this list for the given index.
     */
    @Override
    protected void renderItem( GuiGraphics graphics, int mouseX, int mouseY, float partialTick,
                               int itemIndex, int rowLeft, int rowTop, int rowWidth, int itemHeight ) {
        // Check if highlights are enabled in the config.
        if( searchbar != null && ClientRegister.CONFIG_EDITOR.SEARCHBAR.showSearchHighlights.get() ) {
            if( searchbar.getElementByCandidateIndexes().inverse().containsKey( itemIndex ) ) {
                int x = (getLeft() + ((getWidth() - rowWidth) / 2)) + highlightOffset.getX();
                int y = rowTop + highlightOffset.getY();
                int width = (getLeft() + ((getWidth() + rowWidth) / 2) - 3) + highlightOffset.getWidth();
                int height = (rowTop + itemHeight + 3) + highlightOffset.getHeight();
                
                // TODO - Maybe make highlight color configurable
                // noinspection ConstantConditions
                if( searchbar.getElementByCandidateIndexes().inverse().get( itemIndex ) == searchbar.getFocusedIndex() ) {
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
