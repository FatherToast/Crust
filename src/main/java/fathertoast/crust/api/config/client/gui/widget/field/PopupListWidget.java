package fathertoast.crust.api.config.client.gui.widget.field;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * A popup that displays a scrollable list of row entries.
 *
 * @see AbstractList
 * @see net.minecraft.client.gui.widget.list.AbstractOptionList
 */
public class PopupListWidget<E extends PopupListWidget.AbstractListEntry<E>> extends Widget implements IPopupWidget {
    
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
    private final List<E> children = new SimpleArrayList();
    
    /** When true, the selection box will be rendered. */
    private boolean renderSelectionBox = true;
    
    @SuppressWarnings( "unused" )
    public PopupListWidget( int x, int y, int width, int height, ITextComponent message ) {
        this( x, y, width, height, DEFAULT_ROW_HEIGHT, message );
    }
    
    public PopupListWidget( int x, int y, int width, int height, int rowHeight, ITextComponent message ) {
        super( x, y, width, height, message );
        itemHeight = rowHeight;
        
        y0 = y;
        y1 = y + height;
        x0 = x;
        x1 = x + width;
    }
    
    /** Set visibility of the selection box. */
    @SuppressWarnings( "unused" )
    public void setRenderSelectionBox( boolean visible ) { renderSelectionBox = visible; }
    
    /** When true, the header will be rendered. */
    private boolean renderHeader;
    /** The render height of the header. */
    protected int headerHeight;
    
    /** Set visibility and height of the list header. */
    @SuppressWarnings( "unused" )
    public void setRenderHeader( boolean visible, int height ) {
        renderHeader = visible;
        headerHeight = visible ? height : 0;
    }
    
    /** @param entry A new entry to append to the end of this list. */
    public void addEntry( E entry ) { children.add( entry ); }
    
    /** Removes all entries in this list. */
    public final void clearEntries() { children.clear(); }
    
    /** Replaces all entries in this list with the entries of a collection, in the order returned by its iterator. */
    @SuppressWarnings( "unused" )
    public void replaceEntries( Collection<E> newEntries ) {
        clearEntries();
        children.addAll( newEntries );
    }
    
    /** Sets the x position for the left edge of this widget. */
    @SuppressWarnings( "unused" )
    public void setLeftPos( int x ) {
        x0 = x;
        x1 = x + width;
    }
    
    //    public void updateSize( int w, int h, int top, int bottom ) { TODO This seems useless
    //        width = w;
    //        height = h;
    //        y0 = top;
    //        y1 = bottom;
    //        x0 = 0;
    //        x1 = w;
    //    }
    
    /** Removes the entry at a specified index and returns the removed value if successful. */
    @SuppressWarnings( "unused" )
    @Nullable
    public E remove( int index ) {
        E entry = children.get( index );
        return removeEntry( children.get( index ) ) ? entry : null;
    }
    
    /** Removes the entry and returns true if successful. */
    public boolean removeEntry( E entry ) {
        boolean success = children.remove( entry );
        if( success && entry == getSelected() ) setSelected( null );
        return success;
    }
    
    /** Called to set the entry's parent to this when it is added. */
    private void bindEntryToSelf( PopupListWidget.AbstractListEntry<E> entry ) { entry.parent = this; }
    
    
    // ---- Collection Methods ---- //
    
    /** @return The underlying list containing all entries from top to bottom. */
    public final List<E> entries() { return children; }
    
    /** @return The entry at a particular index. */
    protected E getEntry( int index ) { return entries().get( index ); }
    
    /** @return The number of entries in this list. */
    protected int getItemCount() { return entries().size(); }
    
    
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
    @SuppressWarnings( "unused" )
    public int getBottom() { return y1; }
    
    /** @return The x position for the left edge of entry rows. */
    public int getRowLeft() { return x0 + (width - getRowWidth()) / 2 + 2; }
    
    /** @return The x position for the right edge of entry rows. */
    public int getRowRight() { return getRowLeft() + getRowWidth(); }
    
    /** @return The y position for the top edge of a specific entry row. May be outside the widget/render bounds. */
    protected int getRowTop( int index ) { return y0 + ENTRY_PADDING - (int) getScrollDistance() + index * itemHeight + headerHeight; }
    
    /** @return The y position for the bottom edge of a specific entry row. May be outside the widget/render bounds. */
    @SuppressWarnings( "unused" )
    protected int getRowBottom( int index ) { return getRowTop( index ) + itemHeight; }
    
    /** @return The width of each list entry. Note that entries are centered in the widget, ignoring the scrollbar. */
    public int getRowWidth() { return width; }
    
    /** @return The total render height of the list contents, ignoring widget and render bounds. */
    protected int getListContentHeight() { return getItemCount() * itemHeight + headerHeight; }
    
    /** @return The height of the scrollbar handle. */
    protected int getScrollHandleHeight() { return MathHelper.clamp( height * height / getListContentHeight(), 32, height - 8 ); }
    
    /** @return The x position for the left edge of the scrollbar. */
    protected int getScrollbarLeft() { return x1 - 2 - SCROLLBAR_WIDTH; }
    
    
    // ---- Input Handling ---- //
    
    /** The currently selected list entry. */
    private E selected;
    
    /** @return The currently selected list entry. */
    @Nullable
    public E getSelected() { return selected; }
    
    /** Sets the currently selected list entry. */
    public void setSelected( @Nullable E value ) { selected = value; }
    
    /** The item currently being dragged. */
    private E dragging;
    
    /** Called when an entry is clicked. */
    public void setDragging( @Nullable E entry ) { dragging = entry; }
    
    /** Return the entry currently being dragged. */
    @Nullable
    public E getDragging() { return dragging; }
    
    /** @return True if the entry at a particular index is the selected entry. */
    protected boolean isSelectedItem( int index ) { return getEntry( index ).equals( getSelected() ); }
    
    /** @return The list entry at the given screen coordinates, if any. */
    @Nullable
    protected final E getEntryAtPosition( double x, double y ) {
        // Check bounds
        if( y < y0 || y > y1 || x >= getScrollbarLeft() || x < getRowLeft() || x > getRowRight() ) return null;
        
        // Calculate entry index
        int yFromListTop = MathHelper.floor( y - y0 ) - headerHeight + (int) getScrollDistance() - ENTRY_PADDING;
        int index = yFromListTop / itemHeight;
        return index >= 0 && index < getItemCount() ? entries().get( index ) : null;
    }
    
    /** The current scroll distance. */
    private double scrollDistance;
    
    /** @return The scroll distance (from 0 to maxScroll). */
    public double getScrollDistance() { return scrollDistance; }
    
    /** Sets the scroll distance (from 0 to maxScroll). */
    public void setScrollDistance( double value ) {
        scrollDistance = MathHelper.clamp( value, 0.0, getMaxScrollDistance() );
    }
    
    /** Adds to the scroll distance (from 0 to maxScroll). */
    public void addScrollDistance( int y ) { setScrollDistance( getScrollDistance() + y ); }
    
    /** @return The maximum scroll distance. */
    public int getMaxScrollDistance() { return Math.max( 0, getListContentHeight() - (height - ENTRY_PADDING) ); }
    
    /** Centers the scroll position on a specific entry. */
    @SuppressWarnings( "unused" )
    protected void centerScrollOn( E entry ) {
        setScrollDistance( entries().indexOf( entry ) * itemHeight + (itemHeight - height) / 2.0 );
    }
    
    /** Scrolls, if needed, to make sure a specific entry can be seen. */
    protected void ensureVisible( E entry ) {
        int rowTop = getRowTop( entries().indexOf( entry ) );
        
        int scrollAmount = rowTop - (y0 + ENTRY_PADDING + itemHeight);
        if( scrollAmount < 0 ) addScrollDistance( scrollAmount );
        
        scrollAmount = rowTop + itemHeight - (y1 - itemHeight);
        if( scrollAmount > 0 ) addScrollDistance( scrollAmount );
    }
    
    /** @return True if the mouse is over this widget. */
    @Override
    public boolean isMouseOver( double x, double y ) { return y >= y0 && y <= y1 && x >= x0 && x <= x1; }
    
    /** True if currently scrolling (from clicking on the scrollbar). */
    private boolean scrolling;
    
    /**
     * Called when a mouse button is clicked.
     *
     * @param mouseKey The mouse key that was clicked (see {@link InputMappings.Type#MOUSE}).
     * @return True if the event has been handled.
     */
    @Override
    public boolean mouseClicked( double x, double y, int mouseKey ) {
        updateScrollingState( x, mouseKey );
        if( !isMouseOver( x, y ) ) return false;
        
        // Find the entry being clicked on
        E entry = getEntryAtPosition( x, y );
        if( entry != null ) {
            if( entry.mouseClicked( x, y, mouseKey ) ) {
                setDragging( entry );
                return true;
            }
        }
        // Otherwise, we assume the header was clicked
        else if( mouseKey == 0 ) {
            clickedHeader( (int) (x - x0) + (width - getRowWidth()) / 2,
                    (int) (y - y0) + (int) getScrollDistance() - ENTRY_PADDING );
            return true;
        }
        
        return scrolling;
    }
    
    /** Called when a mouse button is clicked to update scrolling state. */
    protected void updateScrollingState( double mouseX, int mouseKey ) {
        scrolling = mouseKey == 0 && mouseX >= getScrollbarLeft() && mouseX < getScrollbarLeft() + SCROLLBAR_WIDTH;
    }
    
    /** Called when the header is clicked. */
    @SuppressWarnings( "unused" )
    protected void clickedHeader( int headerX, int headerY ) { }
    
    /**
     * Called when a mouse button is released.
     *
     * @param mouseKey The mouse key that was released (see {@link InputMappings.Type#MOUSE}).
     * @return True if the event has been handled.
     */
    @Override
    public boolean mouseReleased( double x, double y, int mouseKey ) {
        if( getDragging() != null ) {
            getDragging().mouseReleased( x, y, mouseKey );
            setDragging( null );
        }
        return false;
    }
    
    /** Called when the mouse is moved while a mouse button is held. */
    @Override
    public boolean mouseDragged( double x, double y, int mouseKey, double deltaX, double deltaY ) {
        if( getDragging() != null && mouseKey == 0 && getDragging().mouseDragged( x, y, mouseKey, deltaX, deltaY ) ||
                super.mouseDragged( x, y, mouseKey, deltaX, deltaY ) )
            return true;
        
        if( mouseKey == 0 && scrolling ) {
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
        return false;
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
     * @param key      The keyboard key that was pressed (see {@link InputMappings.Type#KEYSYM}).
     * @param scancode The system-specific scancode of the key (see {@link InputMappings.Type#SCANCODE}).
     * @param mods     Bitfield describing which modifier keys were held down.
     * @return True if the event has been handled.
     * @see org.lwjgl.glfw.GLFWKeyCallbackI#invoke(long, int, int, int, int)
     */
    @Override
    public boolean keyPressed( int key, int scancode, int mods ) {
        if( super.keyPressed( key, scancode, mods ) )
            return true;
        
        if( key == InputMappings.getKey( "key.keyboard.down" ).getValue() ) {
            moveSelection( AbstractList.Ordering.DOWN );
            return true;
        }
        if( key == InputMappings.getKey( "key.keyboard.up" ).getValue() ) {
            moveSelection( AbstractList.Ordering.UP );
            return true;
        }
        
        return false;
    }
    
    /** Moves the selection based on the ordering given. */
    protected void moveSelection( AbstractList.Ordering ordering ) {
        moveSelection( ordering, ( entry ) -> true );
    }
    
    /** Moves the selection based on the ordering given, optionally skipping entries based on a filter. */
    protected void moveSelection( AbstractList.Ordering ordering, Predicate<E> filter ) {
        if( entries().isEmpty() ) return;
        
        int dir = ordering == AbstractList.Ordering.UP ? -1 : 1;
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
    @SuppressWarnings( "unused" )
    protected void refreshSelection() {
        E entry = getSelected();
        if( entry != null ) {
            setSelected( entry );
            ensureVisible( entry );
        }
    }
    
    //    @Override
    //    public boolean isFocused() { return false; } TODO why was this forced false on AbstractList?
    
    
    // ---- Rendering ---- //
    
    /** Renders this widget. */
    @Override
    public void render( MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks ) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buf = tessellator.getBuilder();
        
        renderBackground( matrixStack, mouseX, mouseY, partialTicks, tessellator, buf );
        renderList( matrixStack, mouseX, mouseY, partialTicks, tessellator, buf );
        if( renderHeader ) renderHeader( matrixStack, mouseX, mouseY, partialTicks, tessellator, buf );
        renderScrollbar( matrixStack, mouseX, mouseY, partialTicks, tessellator, buf );
        renderDecorations( matrixStack, mouseX, mouseY, partialTicks, tessellator, buf );
        
        //noinspection deprecation
        RenderSystem.shadeModel( 0x1D00 );
        //noinspection deprecation
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
    }
    
    /** Renders the list's background. */
    @SuppressWarnings( "unused" )
    protected void renderBackground( MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, Tessellator tessellator, BufferBuilder buf ) {
        // Default background is solid dark gray
        RenderSystem.disableTexture();
        //noinspection deprecation
        buf.begin( 7, DefaultVertexFormats.POSITION_TEX_COLOR );
        drawBox( buf, x0, x1, y0, y1, 0x20 );
        tessellator.end();
        RenderSystem.enableTexture();
    }
    
    /** Renders the list content (entries). */
    protected void renderList( MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, Tessellator tessellator, BufferBuilder buf ) {
        int rowWidth = getRowWidth();
        int rowLeft = getRowLeft();
        int rowRight = rowLeft + rowWidth;
        
        int length = getItemCount();
        for( int i = 0; i < length; i++ ) {
            int rowTop = getRowTop( i );
            int rowBottom = rowTop + itemHeight;
            if( rowBottom < y0 || rowTop > y1 ) continue; // Skip entries that are scrolled out of view
            
            // Selection highlight
            if( renderSelectionBox && isSelectedItem( i ) ) {
                RenderSystem.disableTexture();
                buf.begin( 7, DefaultVertexFormats.POSITION );
                drawBox( buf, rowLeft, rowRight, rowTop - 2, rowBottom - ENTRY_PADDING + 2, isFocused() ? 0xFF : 0x7F );
                drawBox( buf, rowLeft + 1, rowRight - 1, rowTop - 1, rowBottom - ENTRY_PADDING + 1, 0x00 );
                tessellator.end();
                RenderSystem.enableTexture();
            }
            
            // The list entry itself
            E entry = getEntry( i );
            entry.render( matrixStack, i, rowLeft, rowTop, rowWidth, itemHeight - ENTRY_PADDING, mouseX, mouseY,
                    isMouseOver( mouseX, mouseY ) && entry.equals( getEntryAtPosition( mouseX, mouseY ) ),
                    partialTicks );
        }
    }
    
    @SuppressWarnings( "unused" )
    protected void renderHeader( MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, Tessellator tessellator, BufferBuilder buf ) { }
    
    @SuppressWarnings( "unused" )
    protected void renderScrollbar( MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, Tessellator tessellator, BufferBuilder buf ) {
        RenderSystem.disableTexture();
        int maxScroll = getMaxScrollDistance();
        if( maxScroll > 0 ) {
            int scrollX0 = getScrollbarLeft();
            int scrollX1 = scrollX0 + SCROLLBAR_WIDTH;
            int handleH = getScrollHandleHeight();
            int handleY = y0 + (height - handleH) * (int) getScrollDistance() / maxScroll;
            if( handleY < y0 ) {
                handleY = y0;
            }
            
            //noinspection deprecation
            buf.begin( 7, DefaultVertexFormats.POSITION_TEX_COLOR );
            drawBox( buf, scrollX0, scrollX1, y0, y1, 0x00 ); // Bar background
            drawBox( buf, scrollX0, scrollX1, handleY, handleH, 0x80 ); // Handle shadow
            drawBox( buf, scrollX0, scrollX1 - 1, handleY, handleH - 1, 0xC0 ); // Handle
            tessellator.end();
        }
        RenderSystem.enableTexture();
    }
    
    @SuppressWarnings( "unused" )
    protected void renderDecorations( MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, Tessellator tessellator, BufferBuilder buf ) { }
    
    /** Draws a gray box with brightness 0x00-0xFF. */
    protected static void drawBox( BufferBuilder buf, double x0, double x1, double y0, double y1, int b ) {
        drawBox( buf, x0, x1, y0, y1, 0.0, 0.0F, 1.0F, 0.0F, 1.0F, b, b, b, 0xFF );
    }
    
    /** Draws a box with RGB channels 0x00-0xFF. */
    @SuppressWarnings( "unused" )
    protected static void drawBox( BufferBuilder buf, double x0, double x1, double y0, double y1, int r, int g, int b ) {
        drawBox( buf, x0, x1, y0, y1, 0.0, 0.0F, 1.0F, 0.0F, 1.0F, r, g, b, 0xFF );
    }
    
    /** Draws a box with depth and UV coords with RGBA channels 0x00-0xFF. */
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
        
        private SimpleArrayList() { }
        
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
    
    /** The class all entries in popup lists must extend. */
    public abstract static class AbstractListEntry<E extends AbstractListEntry<E>> implements IGuiEventListener {
        protected PopupListWidget<E> parent;
        
        /** Called each tick to update animations. */
        @SuppressWarnings( "unused" )
        public void tick() { }
        
        /** Called to render the list entry. */
        public abstract void render( MatrixStack matrixStack, int index, int x, int y, int width, int height,
                                     int mouseX, int mouseY, boolean mouseOver, float partialTicks );
        
        @Override
        public boolean isMouseOver( double mouseX, double mouseY ) {
            return equals( parent.getEntryAtPosition( mouseX, mouseY ) );
        }
    }
    
    /**
     * A simple implementation of a list entry that handles an arbitrary number of widgets (ordered front-to-back).
     * Widget x and y coordinates are defined relative to the list entry; (0,0) is the list entry's top-left corner.
     */
    public static class WidgetListEntry extends AbstractListEntry<WidgetListEntry> {
        
        private final Widget[] WIDGETS;
        private final OffsetWidget[] RENDER_WIDGETS;
        
        public WidgetListEntry( Widget... widgets ) {
            WIDGETS = widgets;
            RENDER_WIDGETS = new OffsetWidget[widgets.length];
            int offset = widgets.length - 1; // Render in reverse order, so lower priority widgets render in the back
            for( int i = 0; i <= offset; i++ ) RENDER_WIDGETS[offset - i] = new OffsetWidget( WIDGETS[i] );
        }
        
        @SuppressWarnings( "unused" )
        public WidgetListEntry( Collection<Widget> widgets ) { this( widgets.toArray( new Widget[0] ) ); }
        
        /** Called when the mouse is moved. */
        @Override
        public void mouseMoved( double x, double y ) {
            for( Widget w : WIDGETS ) w.mouseMoved( x, y );
        }
        
        /**
         * Called when a mouse button is clicked.
         *
         * @param mouseKey The mouse key that was clicked (see {@link InputMappings.Type#MOUSE}).
         * @return True if the event has been handled.
         */
        @Override
        public boolean mouseClicked( double x, double y, int mouseKey ) {
            for( Widget w : WIDGETS ) {
                if( w.mouseClicked( x, y, mouseKey ) ) return true;
            }
            return false;
        }
        
        /**
         * Called when a mouse button is released.
         *
         * @param mouseKey The mouse key that was released (see {@link InputMappings.Type#MOUSE}).
         * @return True if the event has been handled.
         */
        @Override
        public boolean mouseReleased( double x, double y, int mouseKey ) {
            for( Widget w : WIDGETS ) {
                if( w.mouseReleased( x, y, mouseKey ) ) return true;
            }
            return false;
        }
        
        /** Called when the mouse is moved while a mouse button is held. */
        @Override
        public boolean mouseDragged( double x, double y, int mouseKey, double deltaX, double deltaY ) {
            for( Widget w : WIDGETS ) {
                if( w.mouseDragged( x, y, mouseKey, deltaX, deltaY ) ) return true;
            }
            return false;
        }
        
        /** Called when the mouse wheel is scrolled. */
        @Override
        public boolean mouseScrolled( double x, double y, double deltaScroll ) {
            for( Widget w : WIDGETS ) {
                if( w.mouseScrolled( x, y, deltaScroll ) ) return true;
            }
            return false;
        }
        
        /**
         * Called when a keyboard key is pressed.
         *
         * @param key      The keyboard key that was pressed (see {@link InputMappings.Type#KEYSYM}).
         * @param scancode The system-specific scancode of the key (see {@link InputMappings.Type#SCANCODE}).
         * @param mods     Bitfield describing which modifier keys were held down.
         * @return True if the event has been handled.
         * @see org.lwjgl.glfw.GLFWKeyCallbackI#invoke(long, int, int, int, int)
         */
        @Override
        public boolean keyPressed( int key, int scancode, int mods ) {
            for( Widget w : WIDGETS ) {
                if( w.keyPressed( key, scancode, mods ) ) return true;
            }
            return false;
        }
        
        /**
         * Called when a keyboard key is released.
         *
         * @param key      The keyboard key that was released (see {@link InputMappings.Type#KEYSYM}).
         * @param scancode The system-specific scancode of the key (see {@link InputMappings.Type#SCANCODE}).
         * @param mods     Bitfield describing which modifier keys were held down.
         * @return True if the event has been handled.
         * @see org.lwjgl.glfw.GLFWKeyCallbackI#invoke(long, int, int, int, int)
         */
        @Override
        public boolean keyReleased( int key, int scancode, int mods ) {
            for( Widget w : WIDGETS ) {
                if( w.keyReleased( key, scancode, mods ) ) return true;
            }
            return false;
        }
        
        /** Called when a character is typed. */
        @Override
        public boolean charTyped( char codePoint, int mods ) {
            for( Widget w : WIDGETS ) {
                if( w.charTyped( codePoint, mods ) ) return true;
            }
            return false;
        }
        
        /**
         * Called when focus change is requested (for example, tab or shift+tab).
         *
         * @param forward Whether focus should move forward. Typically, forward means left-to-right then top-to-bottom.
         * @return This gui's new focus state.
         */
        @Override
        public boolean changeFocus( boolean forward ) {
            for( Widget w : WIDGETS ) {
                if( w.changeFocus( forward ) ) return true;
            }
            return false;
        }
        
        /** Called to render the list entry. */
        @Override
        public void render( MatrixStack matrixStack, int index, int x, int y, int width, int height,
                            int mouseX, int mouseY, boolean mouseOver, float partialTicks ) {
            for( OffsetWidget off : RENDER_WIDGETS ) {
                off.WIDGET.x = x + off.X_OFFSET;
                off.WIDGET.y = y + off.Y_OFFSET;
                off.WIDGET.render( matrixStack, mouseX, mouseY, partialTicks );
            }
        }
        
        /** Simple wrapper used to save the offsets of provided gui components. */
        private static class OffsetWidget {
            
            final Widget WIDGET;
            final int X_OFFSET;
            final int Y_OFFSET;
            
            OffsetWidget( Widget widget ) {
                WIDGET = widget;
                X_OFFSET = widget.x;
                Y_OFFSET = widget.y;
            }
        }
    }
}