package fathertoast.crust.api.config.client.gui.widget;

import fathertoast.crust.api.config.client.gui.widget.field.searchbar.ISearchable;
import fathertoast.crust.api.config.client.gui.widget.field.searchbar.Searchbar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import org.jetbrains.annotations.Nullable;

/**
 * A selection list implementation designed to
 * work in conjunction with a {@link Searchbar} widget.
 */
public abstract class SearchableSelectionList<T extends ContainerObjectSelectionList.Entry<T> & ISearchable> extends ContainerObjectSelectionList<T> {
    
    /** The searchbar this list "communicates" with. */
    @Nullable
    private Searchbar searchbar;
    
    
    public SearchableSelectionList( Minecraft minecraft, int width, int height, int topY, int bottomY, int itemHeight ) {
        super( minecraft, width, height, topY, bottomY, itemHeight );
    }
    
    /**
     * Sets the searchbar for this list.
     * Without a searchbar, this list doesn't do much interesting.
     */
    public void setSearchbar( Searchbar searchbar ) {
        this.searchbar = searchbar;
    }
    
    /** Tells the underlying searchbar to forcibly run a new search. */
    protected void rerunSearch() {
        if( searchbar != null && !searchbar.getValue().isEmpty() )
            searchbar.search( searchbar.getValue(), true );
    }
    
    /** Tells this searchbar's selection list to scroll to the element at the given index. */
    public void scrollToIndex( int index ) {
        // Negative index, assume it is intentional for defocusing the focused search candidate.
        if( index < 0 ) {
            setScrollAmount( 0.0 );
        }
        // Out of bounds
        else if( index > children().size() - 1 ) {
            throw new IndexOutOfBoundsException( "Attempted to scroll to an out-of-bounds index in a selection list!" );
        }
        setScrollAmount( index * itemHeight + (double) (itemHeight / 2) - (double) ((getBottom() - getTop()) / 2) );
    }
    
    /**
     * Called from {@link net.minecraft.client.gui.components.AbstractSelectionList#renderList(GuiGraphics, int, int, float)}.
     * Draws an item from this list for the given index.
     */
    @Override
    protected void renderItem( GuiGraphics graphics, int mouseX, int mouseY, float partialTick,
                               int itemIndex, int rowLeft, int rowTop, int rowWidth, int itemHeight ) {
        // Check if highlights are enabled in the config
        if( searchbar != null && Searchbar.showHighlights.get() ) {
            if( searchbar.getElementByMatchIndexes().inverse().containsKey( itemIndex ) ) {
                ISearchable searchable = children().get( itemIndex );
                // noinspection ConstantConditions
                boolean isFocusedItem = searchbar.getElementByMatchIndexes().inverse().get( itemIndex ) == searchbar.getFocusedIndex();
                
                searchable.renderHighlight( graphics, isFocusedItem, getScrollbarPosition(), mouseX, mouseY, partialTick,
                        itemIndex, rowLeft, rowTop, rowWidth, itemHeight );
            }
        }
        super.renderItem( graphics, mouseX, mouseY, partialTick, itemIndex, rowLeft, rowTop, rowWidth, itemHeight );
    }
}