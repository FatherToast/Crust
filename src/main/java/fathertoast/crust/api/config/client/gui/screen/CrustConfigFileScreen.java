package fathertoast.crust.api.config.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import fathertoast.crust.api.config.client.gui.widget.CrustConfigFieldList;
import fathertoast.crust.api.config.client.gui.widget.field.IPopupWidget;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.CrustConfigFormat;
import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

/**
 * Screen that displays the contents of a single config file for the user to view and edit.
 *
 * @see fathertoast.crust.api.config.common.AbstractConfigFile
 * @see CrustConfigSpec
 */
public class CrustConfigFileScreen extends Screen {
    
    /** @return The spec's display name. */
    public static String getSpecName( CrustConfigSpec spec ) {
        String name = spec.getFile().getName();
        return ConfigUtil.properCase( decodeString( name.substring( 0, name.length() - CrustConfigFormat.FILE_EXT.length() ) ) );
    }
    
    /** @return The string made into something more readable. */
    public static String decodeString( String str ) {
        return ConfigUtil.camelCaseToLowerSpace( str.replace( '_', ' ' ).replace( ".", " > " ).trim() );
    }
    
    
    /** The screen open under this one. */
    private final Screen LAST_SCREEN;
    
    /** The spec of the 'opened' config file. */
    private final CrustConfigSpec SPEC;
    
    /** The text to render below the title. */
    private final Component SUBTITLE;
    
    /** The list generated by the config spec to represent the file contents. */
    private CrustConfigFieldList fieldList;
    /** Tooltip to render this frame. */
    private List<FormattedCharSequence> tooltip;
    
    /** The "open file" or "discard changes" button. */
    private Button bottomLeftButton;
    /** The "done" or "save changes" button. */
    private Button bottomRightButton;
    
    /** The currently focused text box, if any. */
    private EditBox focusedTextBox;
    
    /** The currently open popup widget, if any. */
    private AbstractWidget popupWidget;
    
    /** Creates a new config file screen. */
    public CrustConfigFileScreen( Screen parent, CrustConfigSpec spec ) {
        super( Component.translatable( "menu.crust.config.file.title",
                CrustConfigSelectScreen.getModName( spec.MANAGER.MOD_ID ), getSpecName( spec ) ) );
        LAST_SCREEN = parent;
        SPEC = spec;
        SUBTITLE = Component.translatable( "menu.crust.config.file.subtitle",
                ConfigUtil.toRelativePath( spec.getFile() ) );
    }
    
    /** Called to set the currently focused text box. */
    public void setFocusedTextBox( EditBox textBox ) { focusedTextBox = textBox; }
    
    /** Sets the current scroll position. */
    public void setScrollAmount( double scroll ) { fieldList.setScrollAmount( scroll ); }
    
    /** @return The current scroll position. */
    public double getScrollAmount() { return fieldList.getScrollAmount(); }
    
    /** Called to open a 'popup widget'. Setting to null closes any open popup. */
    public void setPopupWidget( @Nullable AbstractWidget popup ) { popupWidget = popup; }
    
    /** Sets the tooltip to render this tick. */
    public void setTooltip( @Nullable List<FormattedCharSequence> text ) { tooltip = text; }
    
    /** Closes this screen and reopens it to hard-refresh everything. */
    public void resetScreen() {
        if( minecraft != null ) {
            CrustConfigFileScreen newScreen = new CrustConfigFileScreen( LAST_SCREEN, SPEC );
            minecraft.setScreen( newScreen );
            newScreen.setScrollAmount( getScrollAmount() );
        }
    }
    
    /** Called to close the screen. */
    @Override
    public void onClose() { if( minecraft != null ) minecraft.setScreen( LAST_SCREEN ); }
    
    /** Called to setup the screen before displaying it. */
    @Override
    protected void init() {
        if( minecraft == null ) return;
        
        // Header content
        // Nothing to init
        
        // Primary screen content
        fieldList = new CrustConfigFieldList( this, minecraft, SPEC );
        addRenderableWidget( fieldList );
        
        // Footer content
        addRenderableWidget( bottomLeftButton = new Button( width / 2 - 155, height - 29,
                150, 20, Component.translatable( "menu.crust.config.open_folder" ),
                ( button ) -> {
                    if( fieldList.isChanged() ) resetScreen();
                    else Util.getPlatform().openFile( SPEC.getFile().getParentFile() );
                },
                Supplier::get ) );
        addRenderableWidget( bottomRightButton = new Button( width / 2 - 155 + 160, height - 29,
                150, 20, CommonComponents.GUI_DONE,
                ( button ) -> {
                    if( fieldList.isChanged() ) {
                        fieldList.saveChanges();
                        resetScreen();
                    }
                    else minecraft.setScreen( LAST_SCREEN );
                },
                Supplier::get ) );
    }
    
    /** Called when the footer text might need to be changed. */
    public void updateFooterButtonText() {
        if( fieldList.isChanged() ) {
            bottomLeftButton.setMessage( Component.translatable( "menu.crust.config.discard_changes" )
                    .withStyle( ChatFormatting.RED ) );
            bottomRightButton.setMessage( Component.translatable( "menu.crust.config.save_changes" )
                    .withStyle( ChatFormatting.GREEN ) );
        }
        else {
            bottomLeftButton.setMessage( Component.translatable( "menu.crust.config.open_folder" ) );
            bottomRightButton.setMessage( CommonComponents.GUI_DONE );
        }
    }
    
    /** Called when the mouse is moved. */
    @Override
    public void mouseMoved( double x, double y ) {
        if( popupWidget != null ) popupWidget.mouseMoved( x, y );
        else super.mouseMoved( x, y );
    }
    
    /**
     * Called when a mouse button is clicked.
     *
     * @param mouseKey The mouse key that was clicked (see {@link InputConstants.Type#MOUSE}).
     * @return True if the event has been handled.
     */
    @Override
    public boolean mouseClicked( double x, double y, int mouseKey ) {
        if( focusedTextBox != null ) {
            focusedTextBox.setFocused( false );
            focusedTextBox = null;
        }
        
        if( popupWidget != null ) {
            if( popupWidget.isMouseOver( x, y ) ) {
                popupWidget.mouseClicked( x, y, mouseKey );
            }
            else if( popupWidget instanceof IPopupWidget ) {
                if( ((IPopupWidget) popupWidget).mouseClickedOutOfBounds( x, y, mouseKey ) )
                    popupWidget = null;
            }
            else popupWidget = null;
            return true;
        }
        return super.mouseClicked( x, y, mouseKey );
    }
    
    /**
     * Called when a mouse button is released.
     *
     * @param mouseKey The mouse key that was released (see {@link InputConstants.Type#MOUSE}).
     * @return True if the event has been handled.
     */
    @Override
    public boolean mouseReleased( double x, double y, int mouseKey ) {
        if( popupWidget != null ) {
            popupWidget.mouseReleased( x, y, mouseKey );
            return true;
        }
        return super.mouseReleased( x, y, mouseKey );
    }
    
    /** Called when the mouse is moved while a mouse button is held. */
    @Override
    public boolean mouseDragged( double x, double y, int mouseKey, double deltaX, double deltaY ) {
        if( popupWidget != null ) {
            popupWidget.mouseDragged( x, y, mouseKey, deltaX, deltaY );
            return true;
        }
        return super.mouseDragged( x, y, mouseKey, deltaX, deltaY );
    }
    
    /** Called when the mouse wheel is scrolled. */
    @Override
    public boolean mouseScrolled( double x, double y, double deltaScroll ) {
        if( popupWidget != null ) {
            popupWidget.mouseScrolled( x, y, deltaScroll );
            return true;
        }
        return super.mouseScrolled( x, y, deltaScroll );
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
        if( popupWidget != null ) {
            if( key == InputConstants.getKey( "key.keyboard.escape" ).getValue() &&
                    (!(popupWidget instanceof IPopupWidget) || ((IPopupWidget) popupWidget).shouldCloseOnEsc()) ) {
                popupWidget = null;
                return true;
            }
            popupWidget.keyPressed( key, scancode, mods );
            return true;
        }
        return super.keyPressed( key, scancode, mods );
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
        if( popupWidget != null ) {
            popupWidget.keyReleased( key, scancode, mods );
            return true;
        }
        return super.keyReleased( key, scancode, mods );
    }
    
    /** Called when a character is typed. */
    @Override
    public boolean charTyped( char codePoint, int mods ) {
        if( popupWidget != null ) {
            popupWidget.charTyped( codePoint, mods );
            return true;
        }
        return super.charTyped( codePoint, mods );
    }
    
    // TODO - check out how this actually works
    @Nullable
    @Override
    public ComponentPath nextFocusPath( FocusNavigationEvent event ) {
        return super.nextFocusPath( event );
    }
    
    /**
     * Called when focus change is requested (for example, tab or shift+tab).
     *
     * @param forward Whether focus should move forward. Typically, forward means left-to-right then top-to-bottom.
     * @return This gui's new focus state.
     */
    /*
    @Override
    public boolean changeFocus( boolean forward ) {
        if( popupWidget != null ) {
            if( !changeFocus( forward ) ) popupWidget = null;
            return true;
        }
        return super.changeFocus( forward );
    }

     */
    
    /** Called each tick to update animations. */
    @Override
    public void tick() {
        if( focusedTextBox != null ) focusedTextBox.tick();
        if( popupWidget instanceof IPopupWidget ) ((IPopupWidget) popupWidget).tick();
    }
    
    /** Called to render the screen. */
    @Override
    public void render( GuiGraphics graphics, int mouseX, int mouseY, float partialTicks ) {
        if( popupWidget == null ) {
            renderMain( graphics, mouseX, mouseY, partialTicks );
        }
        else {
            renderMain( graphics, Integer.MIN_VALUE, Integer.MIN_VALUE, partialTicks );
            renderPopup( graphics, popupWidget, mouseX, mouseY, partialTicks );
        }
        
        if( tooltip != null ) {
            graphics.renderTooltip( font, tooltip, mouseX, mouseY );
        }
    }
    
    /** Called to render the primary screen content. */
    protected void renderMain( GuiGraphics graphics, int mouseX, int mouseY, float partialTicks ) {
        renderBackground( graphics );
        
        setTooltip( null );
        fieldList.render( graphics, mouseX, mouseY, partialTicks );
        
        graphics.drawCenteredString( font, SUBTITLE, width / 2,
                24, 0x777777 );
        graphics.drawCenteredString( font, title, width / 2,
                8, 0xFFFFFF );
        
        super.render( graphics, mouseX, mouseY, partialTicks );
    }
    
    /** Called to render a popup widget overlay. */
    protected void renderPopup( GuiGraphics graphics, AbstractWidget popup, int mouseX, int mouseY, float partialTicks ) {
        graphics.pose().pushPose();
        graphics.pose().translate( 0.0, 0.0, 40.0 );
        
        graphics.fillGradient( 0, 0, width, height,
                0xC0_101010, 0xD0_101010 );
        
        setTooltip( null );
        popup.render( graphics, mouseX, mouseY, partialTicks );
        
        graphics.pose().popPose();
    }
}