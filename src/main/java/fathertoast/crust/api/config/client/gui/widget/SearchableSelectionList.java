package fathertoast.crust.api.config.client.gui.widget;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import fathertoast.crust.api.config.client.gui.widget.field.SearchBar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

/**
 * A selection list implementation designed to
 * work in conjunction with a {@link SearchBar} widget.
 */
public abstract class SearchableSelectionList<T extends ContainerObjectSelectionList.Entry<T>> extends ContainerObjectSelectionList<T> {
    
    /** Offsets to be applied to element highlights. */
    private final HighlightOffsets highlightOffsets;
    /** A bidirectional map that maps search candidate indexes to selection list element indexes. */
    private BiMap<Integer, Integer> elementByCandidateIndexes = HashBiMap.create();
    /** The current map index. */
    private int currentIndex;
    
    public SearchableSelectionList( Minecraft minecraft, int width, int height, int topY, int bottomY, int itemHeight, HighlightOffsets highlightOffsets ) {
        super( minecraft, width, height, topY, bottomY, itemHeight );
        this.highlightOffsets = highlightOffsets;
    }
    
    public void setIndexes( BiMap<Integer, Integer> elementByCandidateIndexes ) {
        this.elementByCandidateIndexes = elementByCandidateIndexes;
    }
    
    public void setCurrentIndex( int index ) {
        this.currentIndex = index;
    }
    
    @Override
    protected void renderItem( GuiGraphics graphics, int mouseX, int mouseY, float partialTick,
                               int itemIndex, int rowLeft, int rowTop, int rowWidth, int itemHeight ) {
        if( elementByCandidateIndexes.inverse().containsKey( itemIndex ) ) {
            int x = (getLeft() + ((getWidth() - rowWidth) / 2)) + highlightOffsets.xOffset;
            int y = rowTop + highlightOffsets.yOffset;
            int width = (getLeft() + ((getWidth() + rowWidth) / 2) - 3) + highlightOffsets.widthOffset;
            int height = (rowTop + itemHeight + 3) + highlightOffsets.heightOffset;
            
            // noinspection ConstantConditions
            if( elementByCandidateIndexes.inverse().get( itemIndex ) == currentIndex ) {
                graphics.fillGradient( x, y, width, height, 0x70_EDDB38, 0x90_EDDB38 );
            }
            else {
                graphics.fillGradient( x, y, width, height, 0x40_878787, 0x50_878787 );
            }
        }
        super.renderItem( graphics, mouseX, mouseY, partialTick, itemIndex, rowLeft, rowTop, rowWidth, itemHeight );
    }
    
    public record HighlightOffsets(int xOffset, int yOffset, int widthOffset, int heightOffset) { }
}
