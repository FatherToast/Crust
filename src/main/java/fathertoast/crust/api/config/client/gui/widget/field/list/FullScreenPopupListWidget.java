package fathertoast.crust.api.config.client.gui.widget.field.list;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import fathertoast.crust.api.client.util.GuiUtil;
import fathertoast.crust.api.config.client.gui.widget.field.searchbar.ISearchable;
import fathertoast.crust.api.config.client.gui.widget.field.searchbar.Searchbar;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A full-screen popup that displays a scrollable list of row entries.
 * <p>
 * This is designed to act similar to a screen on top of a screen.
 * Unlike the standard popup list, this supports additional stationary widgets and a scrollbar.
 */
@SuppressWarnings( "unused" )
public class FullScreenPopupListWidget<E extends AbstractPopupListEntry<E>> extends PopupListWidget<E> {
    
    private final List<AbstractWidget> children = new ArrayList<>();
    
    public FullScreenPopupListWidget( Component message ) {
        this( DEFAULT_ROW_HEIGHT + ENTRY_PADDING, message );
    }
    
    public FullScreenPopupListWidget( int rowHeight, Component message ) {
        super( 0, 0, GuiUtil.getScreenWidth(), GuiUtil.getScreenHeight(), rowHeight, message );
    }
    
    public List<AbstractWidget> children() { return children; }
    
    
    // ---- Setup ---- //
    
    /** The searchbar this list "communicates" with. */
    @Nullable
    private Searchbar searchbar;
    
    /** @return The searchbar for this list. */
    @Nullable
    public Searchbar getSearchbar() { return searchbar; }
    
    /** Sets the searchbar for this list. */
    public void setSearchbar( @Nullable Searchbar search ) { searchbar = search; }
    
    /** When true, the footer will be rendered. */
    private boolean renderFooter;
    /** The render height of the footer. */
    protected int footerHeight;
    
    /** Set visibility and height of the list footer. */
    public void setRenderFooter( boolean visible, int height ) {
        renderFooter = visible;
        footerHeight = visible ? height : 0;
    }
    
    /** @param widget A stationary widget to be displayed on this popup. */
    public void addChild( AbstractWidget widget ) { children.add( widget ); }
    
    /** Removes all stationary widgets. */
    public final void clearChildren() { children.clear(); }
    
    /** Replaces all stationary widgets with the widgets of a collection, in the order returned by its iterator. */
    public void replaceChildren( Collection<? extends AbstractWidget> newWidgets ) {
        clearChildren();
        children.addAll( newWidgets );
    }
    
    /** Sets the x position for the left edge of this widget. */
    @Override
    public void setLeftPos( int x ) {} // NOOP
    
    
    /** @param entry A new entry to append to the end of this list. */
    @Override
    public void addEntry( E entry ) {
        super.addEntry( entry );
        rerunSearch();
    }
    
    /** @param entry A new entry to add to this list. */
    @Override
    public void addEntry( int index, E entry ) {
        super.addEntry( index, entry );
        rerunSearch();
    }
    
    /** Removes all entries in this list. */
    @Override
    public void clearEntries() {
        super.clearEntries();
        rerunSearch();
    }
    
    /** Replaces all entries in this list with the entries of a collection, in the order returned by its iterator. */
    @Override
    public void replaceEntries( Collection<? extends E> newEntries ) {
        super.replaceEntries( newEntries );
        rerunSearch();
    }
    
    /** Removes the entry at a specified index and returns the removed value if successful. */
    @Override
    @Nullable
    public E remove( int index ) {
        E entry = super.remove( index );
        rerunSearch();
        return entry;
    }
    
    /** Removes the entry and returns true if successful. */
    @Override
    public boolean removeEntry( E entry ) {
        boolean success = super.removeEntry( entry );
        rerunSearch();
        return success;
    }
    
    // ---- Dimensions ---- //
    
    /** @return The x position for the left edge of the scrollbar. */
    @Override
    protected int getScrollbarLeft() { return width - 2 - SCROLLBAR_WIDTH; }
    
    
    // ---- Input Handling ---- //
    
    /** The extra widget currently being dragged. */
    private AbstractWidget draggingChild;
    
    /** Called when an extra widget is clicked. */
    public void setDraggingChild( @Nullable AbstractWidget entry ) { draggingChild = entry; }
    
    /** Return the extra widget currently being dragged. */
    @Nullable
    public AbstractWidget getDraggingChild() { return draggingChild; }
    
    /**
     * Tells the underlying searchbar to forcibly run a new search.
     * Make sure to call this if the number of elements/children in this list changes.
     */
    protected void rerunSearch() {
        if( searchbar != null && !searchbar.getValue().isEmpty() )
            searchbar.search( searchbar.getValue(), true );
    }
    
    /** @return The maximum scroll distance. */
    @Override
    public int getMaxScrollDistance() { return Math.max( 0, getListContentHeight() - (height - headerHeight - footerHeight) ); }
    
    /** @return True if the mouse is over this widget. */
    @Override
    public boolean isMouseOver( double x, double y ) { return true; }
    
    /**
     * Called when a mouse button is clicked.
     *
     * @param mouseKey The mouse key that was clicked (see {@link InputConstants.Type#MOUSE}).
     * @return True if the event has been handled.
     */
    @Override
    public boolean mouseClicked( double x, double y, int mouseKey ) {
        // Give added widgets priority
        for( AbstractWidget widget : children() ) {
            if( widget.mouseClicked( x, y, mouseKey ) ) {
                widget.setFocused( true );
                setFocused( widget );
                if( mouseKey == 0 ) setDraggingChild( widget );
                return true;
            }
        }
        
        // Check if the header or footer is being clicked
        if( y < headerHeight ) {
            if( mouseKey == 0 ) {
                clickedHeader( (int) x, (int) y );
                return true;
            }
            return false;
        }
        else if( y > height - footerHeight ) {
            if( mouseKey == 0 ) {
                clickedFooter( (int) x, (int) y - height );
                return true;
            }
            return false;
        }
        
        // Check if the scrollbar is being clicked
        updateScrollingState( x, mouseKey );
        if( isDraggingScrollbar() ) return true;
        
        // Finally, find the entry being clicked on, if any
        E entry = getEntryAtPosition( x, y );
        if( entry != null ) {
            if( entry.mouseClicked( x, y, mouseKey ) ) {
                setFocused( entry );
                setDragging( entry );
                return true;
            }
        }
        return false;
    }
    
    /** @return True if this widget covers the entire screen. Causes the screen to skip rendering if so. */
    @Override // IPopupWidget
    public final boolean isFullScreen() { return true; }
    
    /** Called when the footer is clicked. */
    protected void clickedFooter( int footerX, int footerY ) {}
    
    /**
     * Called when a mouse button is released.
     *
     * @param mouseKey The mouse key that was released (see {@link InputConstants.Type#MOUSE}).
     * @return True if the event has been handled.
     */
    @Override
    public boolean mouseReleased( double x, double y, int mouseKey ) {
        if( getDraggingChild() != null ) {
            getDraggingChild().mouseReleased( x, y, mouseKey );
            setDraggingChild( null );
        }
        return super.mouseReleased( x, y, mouseKey );
    }
    
    /** Called when the mouse is moved while a mouse button is held. */
    @Override
    public boolean mouseDragged( double x, double y, int mouseKey, double deltaX, double deltaY ) {
        if( getDraggingChild() != null && mouseKey == 0 &&
                getDraggingChild().mouseDragged( x, y, mouseKey, deltaX, deltaY ) ) {
            return true;
        }
        return super.mouseDragged( x, y, mouseKey, deltaX, deltaY );
    }
    
    
    // ---- Rendering ---- //
    
    /** Renders the list's background. */
    @Override
    protected void renderBackground( GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, Tesselator tesselator, BufferBuilder buf ) {
        graphics.setColor( 0.125F, 0.125F, 0.125F, 1.0F );
        graphics.blit( Screen.BACKGROUND_LOCATION, 0, 0, 0,
                0.0F, height + (float) getScrollDistance(), width, height, 32, 32 );
        graphics.setColor( 1.0F, 1.0F, 1.0F, 1.0F );
    }
    
    
    /** Renders the list content (entries). */
    @Override
    protected void renderList( GuiGraphics graphics, int mouseX, int mouseY, float partialTick, Tesselator tesselator, BufferBuilder buf ) {
        graphics.enableScissor( 0, headerHeight, width, height - footerHeight );
        super.renderList( graphics, mouseX, mouseY, partialTick, tesselator, buf );
        graphics.disableScissor();
    }
    
    /** Renders an individual list entry. */
    protected void renderItem( GuiGraphics graphics, int mouseX, int mouseY, float partialTick,
                               int itemIndex, int rowLeft, int rowTop, int rowWidth, int itemHeight,
                               Tesselator tesselator, BufferBuilder buf ) {
        // Check if highlights are enabled in the config
        if( searchbar != null && Searchbar.showHighlights.get() &&
                searchbar.getElementByMatchIndexes().inverse().containsKey( itemIndex ) ) {
            E entry = entries().get( itemIndex );
            if( entry instanceof ISearchable searchable ) {
                // noinspection ConstantConditions
                boolean isFocusedItem = searchbar.getElementByMatchIndexes().inverse().get( itemIndex ) == searchbar.getFocusedIndex();
                searchable.renderHighlight( graphics, isFocusedItem, getScrollbarLeft(), mouseX, mouseY, partialTick,
                        itemIndex, rowLeft, rowTop, rowWidth, itemHeight );
            }
        }
        super.renderItem( graphics, mouseX, mouseY, partialTick, itemIndex, rowLeft, rowTop, rowWidth, itemHeight, tesselator, buf );
    }
    
    @Override
    protected void renderHeader( GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, Tesselator tesselator, BufferBuilder buf ) {
        graphics.setColor( 0.25F, 0.25F, 0.25F, 1.0F );
        //x, y, u, v, width, height, tex width, tex height
        graphics.blit( Screen.BACKGROUND_LOCATION, 0, 0,
                0.0F, 0.0F, width, headerHeight, 32, 32 );
        
        graphics.setColor( 1.0F, 1.0F, 1.0F, 1.0F );
        graphics.fillGradient( RenderType.guiOverlay(), 0, headerHeight,
                width, headerHeight + 4, 0xFF_000000, 0, 0 );
    }
    
    protected void renderFooter( GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, Tesselator tesselator, BufferBuilder buf ) {
        graphics.setColor( 0.25F, 0.25F, 0.25F, 1.0F );
        int footerY = height - footerHeight;
        //x, y, u, v, width, height, tex width, tex height
        graphics.blit( Screen.BACKGROUND_LOCATION, 0, footerY,
                0.0F, footerY, width, footerHeight, 32, 32 );
        
        graphics.setColor( 1.0F, 1.0F, 1.0F, 1.0F );
        graphics.fillGradient( RenderType.guiOverlay(), 0, footerY - 4,
                width, footerY, 0, 0xFF_000000, 0 );
    }
    
    @Override
    protected void renderScrollbar( GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, Tesselator tesselator, BufferBuilder buf ) {
        // Hijack the scrollbar render to insert the footer render immediately after the header render
        if( renderFooter ) renderFooter( graphics, mouseX, mouseY, partialTicks, tesselator, buf );
        
        // Render the actual scrollbar
        int maxScroll = getMaxScrollDistance();
        if( maxScroll > 0 ) {
            int scrollX0 = getScrollbarLeft();
            int scrollX1 = scrollX0 + SCROLLBAR_WIDTH;
            int handleH = getScrollHandleHeight();
            int handleY = headerHeight + (height - headerHeight - footerHeight - handleH) * (int) getScrollDistance() / maxScroll;
            if( handleY < headerHeight ) {
                handleY = headerHeight;
            }
            
            graphics.fill( scrollX0, headerHeight, scrollX1, height - footerHeight, 0xFF_000000 );
            graphics.fill( scrollX0, handleY, scrollX1, handleY + handleH, 0xFF_808080 );
            graphics.fill( scrollX0, handleY, scrollX1 - 1, handleY + handleH - 1, 0xFF_C0C0C0 );
        }
        
        // Then, render any added widgets not attached to a list entry
        renderExtras( graphics, mouseX, mouseY, partialTicks, tesselator, buf );
    }
    
    protected void renderExtras( GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, Tesselator tesselator, BufferBuilder buf ) {
        for( AbstractWidget widget : children() ) {
            widget.render( graphics, mouseX, mouseY, partialTicks );
        }
    }
}