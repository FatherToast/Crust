package fathertoast.crust.api.config.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import fathertoast.crust.api.config.client.gui.widget.CrustConfigFieldList;
import fathertoast.crust.api.config.client.gui.widget.field.IPopupWidget;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.CrustConfigFormat;
import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.List;

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
        return ConfigUtil.camelCaseToLowerSpace( str.replace( '_', ' ' ).replace( ".", " > " ) );
    }
    
    
    /** The screen open under this one. */
    private final Screen LAST_SCREEN;
    
    /** The spec of the 'opened' config file. */
    private final CrustConfigSpec SPEC;
    
    /** The text to render below the title. */
    private final ITextComponent SUBTITLE;
    
    /** The list generated by the config spec to represent the file contents. */
    private CrustConfigFieldList fieldList;
    /** Tooltip to render this frame. */
    private List<IReorderingProcessor> tooltip;
    
    /** The "open file" or "discard changes" button. */
    private Button bottomLeftButton;
    /** The "done" or "save changes" button. */
    private Button bottomRightButton;
    
    /** The currently focused text box, if any. */
    private TextFieldWidget focusedTextBox;
    
    /** The currently open popup widget, if any. */
    private Widget popupWidget;
    
    /** Creates a new config file screen. */
    public CrustConfigFileScreen( Screen parent, CrustConfigSpec spec ) {
        super( new TranslationTextComponent( "menu.crust.config.file.title",
                CrustConfigSelectScreen.getModName( spec.MANAGER.MOD_ID ), getSpecName( spec ) ) );
        LAST_SCREEN = parent;
        SPEC = spec;
        SUBTITLE = new TranslationTextComponent( "menu.crust.config.file.subtitle",
                ConfigUtil.toRelativePath( spec.getFile() ) );
    }
    
    /** Called to set the currently focused text box. */
    public void setFocusedTextBox( TextFieldWidget textBox ) { focusedTextBox = textBox; }
    
    /** Called to open a 'popup widget'. Setting to null closes any open popup. */
    public void setPopupWidget( @Nullable Widget popup ) { popupWidget = popup; }
    
    /** Sets the tooltip to render this tick. */
    public void setTooltip( @Nullable List<IReorderingProcessor> text ) { tooltip = text; }
    
    /** Closes this screen and reopens it to hard-refresh everything. */
    public void resetScreen() {
        if( minecraft != null ) minecraft.setScreen( new CrustConfigFileScreen( LAST_SCREEN, SPEC ) );
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
        children.add( fieldList );
        
        // Footer content
        addButton( bottomLeftButton = new Button( width / 2 - 155, height - 29,
                150, 20, new TranslationTextComponent( "menu.crust.config.open_folder" ),
                ( button ) -> {
                    if( fieldList.isChanged() ) resetScreen();
                    else Util.getPlatform().openFile( SPEC.getFile().getParentFile() );
                } ) );
        addButton( bottomRightButton = new Button( width / 2 - 155 + 160, height - 29,
                150, 20, DialogTexts.GUI_DONE,
                ( button ) -> {
                    if( fieldList.isChanged() ) {
                        fieldList.saveChanges();
                        resetScreen();
                    }
                    else minecraft.setScreen( LAST_SCREEN );
                } ) );
    }
    
    /** Called when the footer text might need to be changed. */
    public void updateFooterButtonText() {
        if( fieldList.isChanged() ) {
            bottomLeftButton.setMessage( new TranslationTextComponent( "menu.crust.config.discard_changes" )
                    .withStyle( TextFormatting.RED ) );
            bottomRightButton.setMessage( new TranslationTextComponent( "menu.crust.config.save_changes" )
                    .withStyle( TextFormatting.GREEN ) );
        }
        else {
            bottomLeftButton.setMessage( new TranslationTextComponent( "menu.crust.config.open_folder" ) );
            bottomRightButton.setMessage( DialogTexts.GUI_DONE );
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
     * @param mouseKey The mouse key that was clicked (see {@link InputMappings.Type#MOUSE}).
     * @return True if the event has been handled.
     */
    @Override
    public boolean mouseClicked( double x, double y, int mouseKey ) {
        if( focusedTextBox != null ) {
            focusedTextBox.setFocus( false );
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
     * @param mouseKey The mouse key that was released (see {@link InputMappings.Type#MOUSE}).
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
     * @param key      The keyboard key that was pressed (see {@link InputMappings.Type#KEYSYM}).
     * @param scancode The system-specific scancode of the key (see {@link InputMappings.Type#SCANCODE}).
     * @param mods     Bitfield describing which modifier keys were held down.
     * @return True if the event has been handled.
     * @see org.lwjgl.glfw.GLFWKeyCallbackI#invoke(long, int, int, int, int)
     */
    @Override
    public boolean keyPressed( int key, int scancode, int mods ) {
        if( popupWidget != null ) {
            if( key == InputMappings.getKey( "key.keyboard.escape" ).getValue() &&
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
     * @param key      The keyboard key that was released (see {@link InputMappings.Type#KEYSYM}).
     * @param scancode The system-specific scancode of the key (see {@link InputMappings.Type#SCANCODE}).
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
    
    /**
     * Called when focus change is requested (for example, tab or shift+tab).
     *
     * @param forward Whether focus should move forward. Typically, forward means left-to-right then top-to-bottom.
     * @return This gui's new focus state.
     */
    @Override
    public boolean changeFocus( boolean forward ) {
        if( popupWidget != null ) {
            if( !changeFocus( forward ) ) popupWidget = null;
            return true;
        }
        return super.changeFocus( forward );
    }
    
    /** Called each tick to update animations. */
    @Override
    public void tick() {
        if( focusedTextBox != null ) focusedTextBox.tick();
        if( popupWidget instanceof IPopupWidget ) ((IPopupWidget) popupWidget).tick();
    }
    
    /** Called to render the screen. */
    @Override
    public void render( MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks ) {
        if( popupWidget == null ) {
            renderMain( matrixStack, mouseX, mouseY, partialTicks );
        }
        else {
            renderMain( matrixStack, Integer.MIN_VALUE, Integer.MIN_VALUE, partialTicks );
            renderPopup( matrixStack, popupWidget, mouseX, mouseY, partialTicks );
        }
        
        if( tooltip != null ) {
            renderTooltip( matrixStack, tooltip, mouseX, mouseY );
        }
    }
    
    /** Called to render the primary screen content. */
    protected void renderMain( MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks ) {
        renderBackground( matrixStack );
        
        setTooltip( null );
        fieldList.render( matrixStack, mouseX, mouseY, partialTicks );
        
        drawCenteredString( matrixStack, font, SUBTITLE, width / 2,
                24, 0x777777 );
        drawCenteredString( matrixStack, font, title, width / 2,
                8, 0xFFFFFF );
        
        super.render( matrixStack, mouseX, mouseY, partialTicks );
    }
    
    /** Called to render a popup widget overlay. */
    protected void renderPopup( MatrixStack matrixStack, Widget popup, int mouseX, int mouseY, float partialTicks ) {
        matrixStack.pushPose();
        matrixStack.translate( 0.0, 0.0, 40.0 );
        
        fillGradient( matrixStack, 0, 0, width, height,
                0xC0_101010, 0xD0_101010 );
        
        setTooltip( null );
        popup.render( matrixStack, mouseX, mouseY, partialTicks );
        
        matrixStack.popPose();
    }
}