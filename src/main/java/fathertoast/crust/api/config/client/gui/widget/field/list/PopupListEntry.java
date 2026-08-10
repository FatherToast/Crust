package fathertoast.crust.api.config.client.gui.widget.field.list;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * A simple implementation of a popup list entry that handles an arbitrary number of widgets (ordered front-to-back).
 * Widget x and y coordinates are defined relative to the list entry; (0,0) is the list entry's top-left corner.
 */
public class PopupListEntry extends AbstractPopupListEntry<PopupListEntry> {
    
    private AbstractWidget[] WIDGETS;
    private OffsetWidget[] RENDER_WIDGETS;
    
    /** Creates a new popup list entry containing the given widgets (ordered front-to-back). */
    public PopupListEntry( AbstractWidget... widgets ) { setWidgets( widgets ); }
    
    /** Creates a new popup list entry containing the given widgets (ordered front-to-back). */
    @SuppressWarnings( "unused" )
    public PopupListEntry( Collection<AbstractWidget> widgets ) { setWidgets( widgets ); }
    
    protected void setWidgets( Collection<AbstractWidget> widgets ) { setWidgets( widgets.toArray( new AbstractWidget[0] ) ); }
    
    protected void setWidgets( AbstractWidget... widgets ) {
        WIDGETS = widgets;
        RENDER_WIDGETS = new OffsetWidget[widgets.length];
        int offset = widgets.length - 1; // Render in reverse order, so lower priority widgets render in the back
        for( int i = 0; i <= offset; i++ ) RENDER_WIDGETS[offset - i] = new OffsetWidget( WIDGETS[i] );
    }
    
    /** Called when the mouse is moved. */
    @Override
    public void mouseMoved( double x, double y ) { for( AbstractWidget w : WIDGETS ) w.mouseMoved( x, y ); }
    
    /**
     * Called when a mouse button is clicked.
     *
     * @param mouseKey The mouse key that was clicked (see {@link InputConstants.Type#MOUSE}).
     * @return True if the event has been handled.
     */
    @Override
    public boolean mouseClicked( double x, double y, int mouseKey ) {
        for( AbstractWidget w : WIDGETS ) {
            if( w.mouseClicked( x, y, mouseKey ) ) {
                w.setFocused( true );
                popup.setFocused( w );
                return true;
            }
        }
        return false;
    }
    
    /**
     * Called when a mouse button is released.
     *
     * @param mouseKey The mouse key that was released (see {@link InputConstants.Type#MOUSE}).
     * @return True if the event has been handled.
     */
    @Override
    public boolean mouseReleased( double x, double y, int mouseKey ) {
        for( AbstractWidget w : WIDGETS ) {
            if( w.mouseReleased( x, y, mouseKey ) ) return true;
        }
        return false;
    }
    
    /** Called when the mouse is moved while a mouse button is held. */
    @Override
    public boolean mouseDragged( double x, double y, int mouseKey, double deltaX, double deltaY ) {
        for( AbstractWidget w : WIDGETS ) {
            if( w.mouseDragged( x, y, mouseKey, deltaX, deltaY ) ) return true;
        }
        return false;
    }
    
    /** Called when the mouse wheel is scrolled. */
    @Override
    public boolean mouseScrolled( double x, double y, double deltaScroll ) {
        for( AbstractWidget w : WIDGETS ) {
            if( w.mouseScrolled( x, y, deltaScroll ) ) return true;
        }
        return false;
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
        for( AbstractWidget w : WIDGETS ) {
            if( w.isFocused() && w.keyPressed( key, scancode, mods ) ) return true;
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
        for( AbstractWidget w : WIDGETS ) {
            if( w.isFocused() && w.keyReleased( key, scancode, mods ) ) return true;
        }
        return false;
    }
    
    /** Called when a character is typed. */
    @Override
    public boolean charTyped( char codePoint, int mods ) {
        for( AbstractWidget w : WIDGETS ) {
            if( w.isFocused() && w.charTyped( codePoint, mods ) ) return true;
        }
        return false;
    }
    
    /**
     * Called when focus change is requested (for example, tab or shift+tab).
     *
     * @param event Represents the type of focus shift. In vanilla, this is always
     *              one of the three following types: {@code ArrowNavigation}, {@code InitialFocus} or {@code TabNavigation}.
     * @return This GUI's new focus state.
     */
    @Override
    @Nullable
    public ComponentPath nextFocusPath( FocusNavigationEvent event ) {
        for( AbstractWidget w : WIDGETS ) {
            ComponentPath path = w.nextFocusPath( event );
            if( path != null ) return path;
        }
        return null;
    }
    
    /** Called to render the list entry. */
    @Override
    public void render( GuiGraphics graphics, int index, int x, int y, int width, int height,
                        int mouseX, int mouseY, boolean mouseOver, float partialTicks ) {
        for( OffsetWidget off : RENDER_WIDGETS ) {
            off.WIDGET.setX( x + off.X_OFFSET );
            off.WIDGET.setY( y + off.Y_OFFSET );
            off.WIDGET.render( graphics, mouseX, mouseY, partialTicks );
        }
    }
    
    
    /** Simple wrapper used to save the offsets of provided gui components. */
    private static class OffsetWidget {
        
        final AbstractWidget WIDGET;
        final int X_OFFSET;
        final int Y_OFFSET;
        
        OffsetWidget( AbstractWidget widget ) {
            WIDGET = widget;
            X_OFFSET = widget.getX();
            Y_OFFSET = widget.getY();
        }
    }
}