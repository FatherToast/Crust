package fathertoast.crust.api.config.client.gui.widget.field.list;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import fathertoast.crust.api.config.client.gui.widget.field.IPopupWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * A popup that displays a scrollable list of row entries.
 *
 * @see net.minecraft.client.gui.components.AbstractSelectionList
 * @see net.minecraft.client.gui.components.ContainerObjectSelectionList
 */
@SuppressWarnings( "unused" )
public class PopupListWidget<E extends AbstractPopupListEntry<E>> extends AbstractWidget implements IPopupWidget {
    
    /** The default vertical size of each entry in the list, including padding. */
    public static final int DEFAULT_ROW_HEIGHT = 20;
    /** The vertical padding between each entry in the list. */
    public static final int ENTRY_PADDING = 4;
    /** The horizontal size of the scrollbar. */
    public static final int SCROLLBAR_WIDTH = 6;
    
    
    // ---- Setup ---- //
    
    /** The height of each list entry, including the built-in padding of 4. */
    protected final int itemHeight;
    
    /** The entries contained in this list widget. */
    private final List<E> entries = new SimpleArrayList();
    
    /** When true, the selection box will be rendered. */
    private boolean renderSelectionBox = true;
    
    public PopupListWidget( int x, int y, int width, int height, Component message ) {
        this( x, y, width, height, DEFAULT_ROW_HEIGHT, message );
    }
    
    public PopupListWidget( int x, int y, int width, int height, int rowHeight, Component message ) {
        super( x, y, width, height, message );
        itemHeight = rowHeight;
        
        y0 = y;
        y1 = y + height;
        x0 = x;
        x1 = x + width;
    }
    
    /** Set visibility of the selection box. */
    public void setRenderSelectionBox( boolean visible ) { renderSelectionBox = visible; }
    
    /** When true, the header will be rendered. */
    private boolean renderHeader;
    /** The render height of the header. */
    protected int headerHeight;
    
    /** Set visibility and height of the list header. */
    public void setRenderHeader( boolean visible, int height ) {
        renderHeader = visible;
        headerHeight = visible ? height : 0;
    }
    
    /** Sets the x position for the left edge of this widget. */
    public void setLeftPos( int x ) {
        x0 = x;
        x1 = x + width;
    }
    
    /** @param entry A new entry to append to the end of this list. */
    public void addEntry( E entry ) { entries.add( entry ); }
    
    /** @param entry A new entry to add to this list. */
    public void addEntry( int index, E entry ) { entries.add( index, entry ); }
    
    /** Removes all entries in this list. */
    public void clearEntries() { entries.clear(); }
    
    /** Replaces all entries in this list with the entries of a collection, in the order returned by its iterator. */
    public void replaceEntries( Collection<? extends E> newEntries ) {
        clearEntries();
        entries.addAll( newEntries );
    }
    
    /** Removes the entry at a specified index and returns the removed value if successful. */
    @Nullable
    public E remove( int index ) {
        E entry = entries.get( index );
        return removeEntry( entries.get( index ) ) ? entry : null;
    }
    
    /** Removes the entry and returns true if successful. */
    public boolean removeEntry( E entry ) {
        boolean success = entries.remove( entry );
        if( success && entry == getSelected() ) setSelected( null );
        return success;
    }
    
    /** @return The index of the entry in this list, or -1 if it is not present. */
    public int indexOf( E entry ) { return entries.indexOf( entry ); }
    
    /** Called to set the entry's parent to this when it is added. */
    private void bindEntryToSelf( AbstractPopupListEntry<E> entry ) { entry.popup = this; }
    
    
    // ---- Collection Methods ---- //
    
    /** @return The underlying list containing all entries from top to bottom. */
    public final List<E> entries() { return entries; }
    
    /** @return The entry at a particular index. */
    public E getEntry( int index ) { return entries().get( index ); }
    
    /** @return The number of entries in this list. */
    public int getItemCount() { return entries().size(); }
    
    
    // ---- Dimensions ---- //
    
    /** Top edge y-coord of this widget. */
    protected int y0;
    /** Bottom edge y-coord of this widget. */
    protected int y1;
    /** Left edge x-coord of this widget. */
    protected int x0;
    /** Right edge x-coord of this widget. */
    protected int x1;
    
    /** @return The x position for the left edge of this widget. */
    public int getLeft() { return x0; }
    
    /** @return The x position for the right edge of this widget. */
    public int getRight() { return x1; }
    
    /** @return The y position for the top edge of this widget. */
    public int getTop() { return y0; }
    
    /** @return The y position for the bottom edge of this widget. */
    public int getBottom() { return y1; }
    
    /** @return The x position for the left edge of entry rows. */
    public int getRowLeft() { return x0 + ((width - getRowWidth()) >> 1) + 2; }
    
    /** @return The x position for the right edge of entry rows. */
    public int getRowRight() { return getRowLeft() + getRowWidth(); }
    
    /** @return The y position for the top edge of a specific entry row. May be outside the widget/render bounds. */
    protected int getRowTop( int index ) { return y0 + ENTRY_PADDING - (int) getScrollDistance() + index * itemHeight + headerHeight; }
    
    /** @return The y position for the bottom edge of a specific entry row. May be outside the widget/render bounds. */
    protected int getRowBottom( int index ) { return getRowTop( index ) + itemHeight; }
    
    /** @return The width of each list entry. Note that entries are centered in the widget, ignoring the scrollbar. */
    public int getRowWidth() { return width; }
    
    /** @return The total render height of the list contents, ignoring widget and render bounds. */
    protected int getListContentHeight() { return getItemCount() * itemHeight + headerHeight; }
    
    /** @return The height of the scrollbar handle. */
    protected int getScrollHandleHeight() {
        return Mth.clamp( (int) ((float) height * height / (float) getListContentHeight()),
                32, height - 8 );
    }
    
    /** @return The x position for the left edge of the scrollbar. */
    protected int getScrollbarLeft() { return x1 - 2 - SCROLLBAR_WIDTH; }
    
    
    // ---- Input Handling ---- //
    
    /** The currently focused element. */
    @Nullable
    private GuiEventListener focused;
    
    /** @return The currently focused element. */
    @Nullable
    public GuiEventListener getFocused() { return focused; }
    
    /** Sets the currently focused element. */
    public void setFocused( @Nullable GuiEventListener value ) {
        if( focused != null ) focused.setFocused( false );
        if( value != null ) value.setFocused( true );
        focused = value;
    }
    
    /** The currently selected list entry. */
    @Nullable
    private E selected;
    
    /** @return The currently selected list entry. */
    @Nullable
    public E getSelected() { return selected; }
    
    /** Sets the currently selected list entry. */
    public void setSelected( @Nullable E value ) { selected = value; }
    
    /** The widget currently being dragged, if any. */
    @Nullable
    private GuiEventListener dragging;
    
    /** Called when a widget is clicked. */
    public void setDragging( @Nullable GuiEventListener entry ) { dragging = entry; }
    
    /** @return The widget currently being dragged. */
    @Nullable
    public GuiEventListener getDragging() { return dragging; }
    
    /** @return True if the entry at a particular index is the selected entry. */
    protected boolean isSelectedItem( int index ) { return getEntry( index ).equals( getSelected() ); }
    
    /** @return The list entry at the given screen coordinates, if any. */
    @Nullable
    protected final E getEntryAtPosition( double x, double y ) {
        // Check bounds
        if( y < y0 || y > y1 || x >= getScrollbarLeft() || x < getRowLeft() || x > getRowRight() ) return null;
        
        // Calculate entry index
        int yFromListTop = Mth.floor( y - y0 ) - headerHeight + (int) getScrollDistance() - ENTRY_PADDING;
        int index = yFromListTop / itemHeight;
        return index >= 0 && index < getItemCount() ? entries().get( index ) : null;
    }
    
    /** The current scroll distance. */
    private double scrollDistance;
    
    /** @return The scroll distance (from 0 to maxScroll). */
    public double getScrollDistance() { return scrollDistance; }
    
    /** Sets the scroll distance (from 0 to maxScroll). */
    public void setScrollDistance( double value ) {
        scrollDistance = Mth.clamp( value, 0.0, getMaxScrollDistance() );
    }
    
    /** Adds to the scroll distance (from 0 to maxScroll). */
    public void addScrollDistance( int y ) { setScrollDistance( getScrollDistance() + y ); }
    
    /** @return The maximum scroll distance. */
    public int getMaxScrollDistance() { return Math.max( 0, getListContentHeight() - (height - ENTRY_PADDING) ); }
    
    /** Centers the scroll position on a specific entry. */
    public void centerScrollOn( E entry ) { scrollToIndex( entries().indexOf( entry ) ); }
    
    /** Scrolls, if needed, to make sure a specific entry can be seen. */
    public void ensureVisible( E entry ) {
        int rowTop = getRowTop( entries().indexOf( entry ) );
        
        int scrollAmount = rowTop - (y0 + ENTRY_PADDING + itemHeight);
        if( scrollAmount < 0 ) addScrollDistance( scrollAmount );
        
        scrollAmount = rowTop + itemHeight - (y1 - itemHeight);
        if( scrollAmount > 0 ) addScrollDistance( scrollAmount );
    }
    
    /** Tells this searchbar's selection list to scroll to the element at the given index. */
    public void scrollToIndex( int index ) {
        // Negative index, assume it is intentional for defocusing the focused search candidate.
        if( index < 0 ) setScrollDistance( 0.0 );
        else setScrollDistance( index * itemHeight + (itemHeight - height) / 2.0 );
    }
    
    /** @return True if the mouse is over this widget. */
    @Override
    public boolean isMouseOver( double x, double y ) { return y >= y0 && y <= y1 && x >= x0 && x <= x1; }
    
    /** Called when building narration elements for this widget. */
    @Override
    protected void updateWidgetNarration( NarrationElementOutput output ) {}
    
    /** True if currently scrolling (from clicking on the scrollbar). */
    private boolean scrolling;
    
    /** @return True if currently scrolling (from clicking on the scrollbar). */
    public boolean isDraggingScrollbar() { return scrolling; }
    
    /**
     * Called when a mouse button is clicked.
     *
     * @param mouseKey The mouse key that was clicked (see {@link InputConstants.Type#MOUSE}).
     * @return True if the event has been handled.
     */
    @Override
    public boolean mouseClicked( double x, double y, int mouseKey ) {
        setFocused( null );
        
        updateScrollingState( x, mouseKey );
        if( !isMouseOver( x, y ) ) return false;
        if( isDraggingScrollbar() ) return true;
        
        // Find the entry being clicked on
        E entry = getEntryAtPosition( x, y );
        if( entry != null ) {
            return entry.mouseClicked( x, y, mouseKey );
        }
        // Otherwise, we assume the header was clicked
        else if( mouseKey == 0 ) {
            clickedHeader( (int) (x - x0) + ((width - getRowWidth()) >> 1),
                    (int) (y - y0) + (int) getScrollDistance() - ENTRY_PADDING );
            return true;
        }
        
        return false;
    }
    
    /** Called when a mouse button is clicked to update scrolling state. */
    protected void updateScrollingState( double mouseX, int mouseKey ) {
        scrolling = mouseKey == 0 && mouseX >= getScrollbarLeft() && mouseX < getScrollbarLeft() + SCROLLBAR_WIDTH;
    }
    
    /** Called when the header is clicked. */
    protected void clickedHeader( int headerX, int headerY ) {}
    
    /**
     * Called when a mouse button is released.
     *
     * @param mouseKey The mouse key that was released (see {@link InputConstants.Type#MOUSE}).
     * @return True if the event has been handled.
     */
    @Override
    public boolean mouseReleased( double x, double y, int mouseKey ) {
        if( getDragging() != null ) {
            getDragging().mouseReleased( x, y, mouseKey );
            setDragging( null );
            return true;
        }
        return false;
    }
    
    /** Called when the mouse is moved while a mouse button is held. */
    @Override
    public boolean mouseDragged( double x, double y, int mouseKey, double deltaX, double deltaY ) {
        if( getDragging() != null ) {
            getDragging().mouseDragged( x, y, mouseKey, deltaX, deltaY );
            return true;
        }
        
        if( mouseKey == 0 && isDraggingScrollbar() ) {
            int maxScroll = getMaxScrollDistance();
            if( y < y0 || maxScroll <= 0 ) {
                setScrollDistance( 0.0 );
            }
            else if( y > y1 ) {
                setScrollDistance( maxScroll );
            }
            else {
                double scrollScale = Math.max( 1.0, maxScroll / (double) (height - getScrollHandleHeight()) );
                setScrollDistance( getScrollDistance() + deltaY * scrollScale );
            }
            return true;
        }
        
        return super.mouseDragged( x, y, mouseKey, deltaX, deltaY );
    }
    
    /** Called when the mouse wheel is scrolled. */
    @Override
    public boolean mouseScrolled( double x, double y, double deltaScroll ) {
        setScrollDistance( getScrollDistance() - deltaScroll * itemHeight / 2.0 );
        return true;
    }
    
    /**
     * Called when a keyboard key is pressed.
     *
     * @param key      The keyboard key that was pressed (see {@link InputConstants.Type#KEYSYM}).
     * @param scancode The system-specific scancode of the key (see {@link InputConstants.Type#SCANCODE}).
     * @param mods     Bitfield describing which modifier keys were held down.
     * @return True if the event has been handled.
     * @see org.lwjgl.glfw.GLFWKeyCallbackI#invoke(long, int, int, int, int)
     */
    @Override
    public boolean keyPressed( int key, int scancode, int mods ) {
        if( getFocused() != null )
            return getFocused().keyPressed( key, scancode, mods );
        
        if( super.keyPressed( key, scancode, mods ) )
            return true;
        
        if( key == InputConstants.getKey( "key.keyboard.down" ).getValue() ) {
            moveSelection( 1 );
            return true;
        }
        if( key == InputConstants.getKey( "key.keyboard.up" ).getValue() ) {
            moveSelection( -1 );
            return true;
        }
        
        return false;
    }
    
    /**
     * Called when a keyboard key is released.
     *
     * @param key      The keyboard key that was released (see {@link InputConstants.Type#KEYSYM}).
     * @param scancode The system-specific scancode of the key (see {@link InputConstants.Type#SCANCODE}).
     * @param mods     Bitfield describing which modifier keys were held down.
     * @return True if the event has been handled.
     * @see org.lwjgl.glfw.GLFWKeyCallbackI#invoke(long, int, int, int, int)
     */
    @Override
    public boolean keyReleased( int key, int scancode, int mods ) {
        if( getFocused() != null ) {
            getFocused().keyReleased( key, scancode, mods );
            return true;
        }
        return super.keyReleased( key, scancode, mods );
    }
    
    /** Called when a character is typed. */
    @Override
    public boolean charTyped( char codePoint, int mods ) {
        if( getFocused() != null ) {
            getFocused().charTyped( codePoint, mods );
            return true;
        }
        return super.charTyped( codePoint, mods );
    }
    
    /** Called each tick to update animations. */
    @Override
    public void tick() {
        if( getFocused() instanceof EditBox editBox ) editBox.tick();
    }
    
    /** Moves the selection based on the ordering given. */
    protected void moveSelection( int dir ) {
        moveSelection( dir, entry -> true );
    }
    
    /** Moves the selection based on the ordering given, optionally skipping entries based on a filter. */
    protected void moveSelection( int dir, Predicate<E> filter ) {
        if( entries().isEmpty() ) return;
        
        for( int i = entries().indexOf( getSelected() ) + dir; i >= 0 && i < getItemCount(); i += dir ) {
            E entry = getEntry( i );
            if( filter.test( entry ) ) {
                setSelected( entry );
                ensureVisible( entry );
                break;
            }
        }
    }
    
    /** Re-selects and focuses the currently selected item, if any. */
    protected void refreshSelection() {
        E entry = getSelected();
        if( entry != null ) {
            setSelected( entry );
            ensureVisible( entry );
        }
    }
    
    
    // ---- Rendering ---- //
    
    /** Renders this widget. */
    @Override
    public void renderWidget( GuiGraphics graphics, int mouseX, int mouseY, float partialTicks ) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buf = tesselator.getBuilder();
        
        renderBackground( graphics, mouseX, mouseY, partialTicks, tesselator, buf );
        renderList( graphics, mouseX, mouseY, partialTicks, tesselator, buf );
        if( renderHeader ) renderHeader( graphics, mouseX, mouseY, partialTicks, tesselator, buf );
        renderScrollbar( graphics, mouseX, mouseY, partialTicks, tesselator, buf );
        renderDecorations( graphics, mouseX, mouseY, partialTicks, tesselator, buf );
        
        //RenderSystem.shadeModel( 0x1D00 );
        //RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
    }
    
    /** Renders the list's background. */
    protected void renderBackground( GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, Tesselator tesselator, BufferBuilder buf ) {
        // Default background is solid dark gray
        //RenderSystem.disableTexture();
        buf.begin( VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR );
        drawBox( buf, x0, x1, y0, y1, 0x20 );
        tesselator.end();
        //RenderSystem.enableTexture();
    }
    
    /** Renders the list content (entries). */
    protected void renderList( GuiGraphics graphics, int mouseX, int mouseY, float partialTick,
                               Tesselator tesselator, BufferBuilder buf ) {
        int rowWidth = getRowWidth();
        int rowLeft = getRowLeft();
        
        int length = getItemCount();
        for( int i = 0; i < length; i++ ) {
            int rowTop = getRowTop( i );
            int rowBottom = rowTop + itemHeight;
            if( rowTop + itemHeight < y0 || rowTop > y1 ) continue; // Skip entries that are scrolled out of view
            
            renderItem( graphics, mouseX, mouseY, partialTick, i, rowLeft, rowTop, rowWidth, itemHeight, tesselator, buf );
        }
    }
    
    /** Renders an individual list entry. */
    protected void renderItem( GuiGraphics graphics, int mouseX, int mouseY, float partialTick,
                               int itemIndex, int rowLeft, int rowTop, int rowWidth, int itemHeight,
                               Tesselator tesselator, BufferBuilder buf ) {
        // Selection highlight
        if( renderSelectionBox && isSelectedItem( itemIndex ) ) {
            buf.begin( VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION );
            drawBox( buf, rowLeft, rowLeft + rowWidth, rowTop - 2, rowTop + itemHeight - ENTRY_PADDING + 2, isFocused() ? 0xFF : 0x7F );
            drawBox( buf, rowLeft + 1, rowLeft + rowWidth - 1, rowTop - 1, rowTop + itemHeight - ENTRY_PADDING + 1, 0x00 );
            tesselator.end();
        }
        
        // The list entry itself
        E entry = getEntry( itemIndex );
        entry.render( graphics, itemIndex, rowLeft, rowTop, rowWidth, itemHeight - ENTRY_PADDING, mouseX, mouseY,
                isMouseOver( mouseX, mouseY ) && entry.equals( getEntryAtPosition( mouseX, mouseY ) ),
                partialTick );
    }
    
    protected void renderHeader( GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, Tesselator tesselator, BufferBuilder buf ) {}
    
    protected void renderScrollbar( GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, Tesselator tesselator, BufferBuilder buf ) {
        int maxScroll = getMaxScrollDistance();
        if( maxScroll > 0 ) {
            int scrollX0 = getScrollbarLeft();
            int scrollX1 = scrollX0 + SCROLLBAR_WIDTH;
            
            int handleH = getScrollHandleHeight();
            int handleY = y0 + (height - handleH) * (int) getScrollDistance() / maxScroll;
            if( handleY < y0 ) {
                handleY = y0;
            }
            
            graphics.fill( scrollX0, y0, scrollX1, y1, 0xFF_000000 );
            graphics.fill( scrollX0, handleY, scrollX1, handleY + handleH, 0xFF_808080 );
            graphics.fill( scrollX0, handleY, scrollX1 - 1, handleY + handleH - 1, 0xFF_C0C0C0 );
        }
    }
    
    protected void renderDecorations( GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, Tesselator tesselator, BufferBuilder buf ) {}
    
    /** Draws a gray box with value/brightness 0x00-0xFF. */
    protected static void drawBox( BufferBuilder buf, double x0, double x1, double y0, double y1, int v ) {
        drawBox( buf, x0, x1, y0, y1, 0.0, 0.0F, 1.0F, 0.0F, 1.0F, v, v, v, 0xFF );
    }
    
    /** Draws a box with RGB channels 0x00-0xFF. */
    protected static void drawBox( BufferBuilder buf, double x0, double x1, double y0, double y1, int r, int g, int b ) {
        drawBox( buf, x0, x1, y0, y1, 0.0, 0.0F, 1.0F, 0.0F, 1.0F, r, g, b, 0xFF );
    }
    
    /** Draws a box with depth and UV coords with RGBA channels 0x00-0xFF. */
    @SuppressWarnings( "SameParameterValue" )
    protected static void drawBox( BufferBuilder buf, double x0, double x1, double y0, double y1, double z,
                                   float u0, float u1, float v0, float v1, int r, int g, int b, int a ) {
        buf.vertex( x0, y1, z ).uv( u0, v1 ).color( r, g, b, a ).endVertex();
        buf.vertex( x1, y1, z ).uv( u1, v1 ).color( r, g, b, a ).endVertex();
        buf.vertex( x1, y0, z ).uv( u1, v0 ).color( r, g, b, a ).endVertex();
        buf.vertex( x0, y0, z ).uv( u0, v0 ).color( r, g, b, a ).endVertex();
    }
    
    
    // ---- Classes ---- //
    
    /** A simple list implementation that enforces connection between the entries and this list widget. */
    class SimpleArrayList extends java.util.AbstractList<E> {
        private final List<E> underlyingList = Lists.newArrayList();
        
        private SimpleArrayList() {}
        
        @Override
        public E get( int index ) { return underlyingList.get( index ); }
        
        @Override
        public int size() { return underlyingList.size(); }
        
        @Override
        public E set( int index, E element ) {
            E previousElement = underlyingList.set( index, element );
            bindEntryToSelf( element );
            return previousElement;
        }
        
        @Override
        public void add( int index, E element ) {
            underlyingList.add( index, element );
            bindEntryToSelf( element );
        }
        
        @Override
        public E remove( int index ) { return underlyingList.remove( index ); }
    }
}