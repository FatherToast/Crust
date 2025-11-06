package fathertoast.crust.api.config.client.gui.widget;

import fathertoast.crust.api.config.client.gui.widget.field.SearchBar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

import java.util.List;

/**
 * A selection list implementation designed to
 * work in conjunction with a {@link SearchBar} widget.
 */
public abstract class SearchableSelectionList<T extends ContainerObjectSelectionList.Entry<T>> extends ContainerObjectSelectionList<T> {
    
    private final HighlightOffsets highlightOffsets;
    private List<SearchBar.Searchable> searchCandidates = List.of();
    
    
    public SearchableSelectionList( Minecraft minecraft, int width, int height, int topY, int bottomY, int itemHeight, HighlightOffsets highlightOffsets ) {
        super( minecraft, width, height, topY, bottomY, itemHeight );
        this.highlightOffsets = highlightOffsets;
    }
    
    public void setSearchCandidates( List<SearchBar.Searchable> searchCandidates ) {
        this.searchCandidates = searchCandidates;
    }
    
    @Override
    protected void renderItem( GuiGraphics graphics, int mouseX, int mouseY, float partialTick,
                               int itemIndex, int rowLeft, int rowTop, int rowWidth, int itemHeight ) {
        if( searchCandidates.contains( children().get( itemIndex ) ) ) {
            int x = (getLeft() + ((getWidth() - rowWidth) / 2)) + highlightOffsets.xOffset;
            int y = rowTop + highlightOffsets.yOffset;
            int width = (getLeft() + ((getWidth() + rowWidth) / 2) - 3) + highlightOffsets.widthOffset;
            int height = (rowTop + itemHeight + 3) + highlightOffsets.heightOffset;
            
            graphics.fillGradient( x, y, width, height, 0xC0_878787, 0xD0_878787 );
        }
        super.renderItem( graphics, mouseX, mouseY, partialTick, itemIndex, rowLeft, rowTop, rowWidth, itemHeight );
    }
    
    public record HighlightOffsets(int xOffset, int yOffset, int widthOffset, int heightOffset) { }
}
