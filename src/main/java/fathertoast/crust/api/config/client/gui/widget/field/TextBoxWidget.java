package fathertoast.crust.api.config.client.gui.widget.field;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * TODO WIP: copy/pasted from text field
 * <p>
 * A multiline text field that allows unlimited string length and becomes scrollable upon overflow.
 * <p>
 * TODO: Consider text size controls (zoom in/out) and horizontal scroll vs. wordwrap
 *
 * @see net.minecraft.client.gui.widget.TextFieldWidget
 */
public class TextBoxWidget extends Widget implements IRenderable, IGuiEventListener {
    
    /** @return True if the character is allowed in text boxes; i.e. is not a control code other than newline (\n). */
    public static boolean isAllowedTextBoxCharacter( char c ) { return c == '\n' || c > 159 || c < 127 && c > 31; }
    
    /** @return The input, stripped of all disallowed characters. Based on {@link net.minecraft.util.SharedConstants#filterText(String)}. */
    public static String filterText( String text ) {
        StringBuilder filteredText = new StringBuilder();
        for( char c : text.toCharArray() ) {
            if( isAllowedTextBoxCharacter( c ) ) filteredText.append( c );
        }
        return filteredText.toString();
    }
    
    /** The client's font renderer. */
    public final FontRenderer font;
    
    /** The text currently held in this text box. */
    private String value = "";
    
    /** Color to render the text box value in while flagged as editable. */
    private int textColor = 0xFF_E0E0E0;
    /** Color to render the text box value in while flagged as NOT editable. */
    private int textColorUneditable = 0xFF_707070;
    
    /** True if a border should be rendered around this text box. */
    private boolean bordered = true;
    /** True if this text box can lose focus. */
    private boolean canLoseFocus = true;
    /** True if this text box can receive user input. */
    private boolean isEditable = true;
    
    private String suggestion;
    /** Called when this text box's value is changed. */
    private Consumer<String> responder;
    private Predicate<String> filter = Objects::nonNull;
    private BiFunction<String, Integer, IReorderingProcessor> formatter;
    
    /** Counter to play animations. */
    private int frame;
    /** True if the shift key is being held. */
    private boolean shiftPressed;
    
    /** The position of the first visible character. */
    @Deprecated
    private int displayPos;
    
    /**
     * The current cursor position. If the highlight position is different from this, then it is also the start position for the highlight.
     * Position can be anything from 0 to the length of the text box value.
     */
    private int cursorPos;
    /** The end position for the current highlight. Equal to the cursor position if no text is highlighted. */
    private int highlightPos;
    
    public TextBoxWidget( FontRenderer fontRenderer, int x, int y, int width, int height, ITextComponent message ) {
        this( fontRenderer, x, y, width, height, "", message );
    }
    
    public TextBoxWidget( FontRenderer fontRenderer, int x, int y, int width, int height, String initialValue, ITextComponent message ) {
        super( x, y, width, height, message );
        font = fontRenderer;
        formatter = ( p_195610_0_, p_195610_1_ ) -> IReorderingProcessor.forward( p_195610_0_, Style.EMPTY );
        if( !initialValue.isEmpty() ) setValue( initialValue );
    }
    
    /** Sets the text color to render while the text box is flagged as editable. Note that alpha channel of 0x03 or less is set to 0xFF. */
    @SuppressWarnings( "unused" )
    public TextBoxWidget setTextColor( int argb ) {
        textColor = argb;
        return this;
    }
    
    /** Sets the text color to render while the text box is flagged as NOT editable. Note that alpha channel of 0x03 or less is set to 0xFF. */
    @SuppressWarnings( "unused" )
    public TextBoxWidget setTextColorUneditable( int argb ) {
        textColorUneditable = argb;
        return this;
    }
    
    /** Enables or disables the text box border. Default is enabled. */
    @SuppressWarnings( "unused" )
    public TextBoxWidget setBordered( boolean enable ) {
        bordered = enable;
        return this;
    }
    
    /** Enables or disables the ability to click off of the text box. Default is enabled. */
    @SuppressWarnings( "unused" )
    public TextBoxWidget setCanLoseFocus( boolean enable ) {
        canLoseFocus = enable;
        return this;
    }
    
    /** Enables or disables the player's ability to modify the text box contents. Default is enabled. */
    @SuppressWarnings( "unused" )
    public TextBoxWidget setEditable( boolean enable ) {
        isEditable = enable;
        return this;
    }
    
    @SuppressWarnings( "unused" )
    public TextBoxWidget setSuggestion( @Nullable String newSuggestion ) {
        suggestion = newSuggestion;
        return this;
    }
    
    /** Registers the update responder. This will be called whenever the text box contents are updated. */
    @SuppressWarnings( "unused" )
    public TextBoxWidget setResponder( Consumer<String> newResponder ) {
        responder = newResponder;
        return this;
    }
    
    /**
     * Sets a filter that determines what strings are allowed in the text box. Default is any non-null string.
     * Note that you should ensure your filter does not allow null strings.
     */
    @SuppressWarnings( "unused" )
    public TextBoxWidget setFilter( Predicate<String> newFilter ) {
        filter = newFilter;
        return this;
    }
    
    @SuppressWarnings( "unused" )
    public TextBoxWidget setFormatter( BiFunction<String, Integer, IReorderingProcessor> newFormatter ) {
        formatter = newFormatter;
        return this;
    }
    
    /** Sets the visibility of this text box. */
    @SuppressWarnings( "unused" )
    public TextBoxWidget setVisible( boolean enable ) {
        visible = enable;
        return this;
    }
    
    //    public int getScreenX( int index ) {
    //        return index > value.length() ? x : x + font.width( value.substring( 0, index ) );//TODO account for multiline - if this is even needed
    //    }
    
    @SuppressWarnings( "unused" )
    public int getCursorPosition() { return cursorPos; }
    
    /** Called each tick to update animations. */
    public void tick() { frame++; }
    
    @Override
    protected IFormattableTextComponent createNarrationMessage() {
        return new TranslationTextComponent( "gui.narrate.editBox", getMessage(), value );
    }
    
    /** Sets the text to hold in this text box. */
    public void setValue( String text ) {
        if( filter.test( text ) ) {
            value = text;
            moveCursorToEnd();
            setHighlightPos( cursorPos );
            onValueChange();
        }
    }
    
    /** @return The text contained in this text box. */
    public String getValue() { return value; }
    
    /** @return True if any text is currently highlighted. */
    public boolean isHighlighted() { return cursorPos != highlightPos; }
    
    /** @return The text that is currently highlighted. */
    public String getHighlighted() {
        if( !isHighlighted() ) return "";
        return cursorPos < highlightPos ? value.substring( cursorPos, highlightPos ) : value.substring( highlightPos, cursorPos );
    }
    
    /** Inserts a string at the current cursor position. Replaces the currently highlighted text, if any. */
    public void insertText( String text ) {
        String filteredText = filterText( text );
        
        int p0 = Math.min( cursorPos, highlightPos );
        int p1 = Math.max( cursorPos, highlightPos );
        String newValue = new StringBuilder( value ).replace( p0, p1, filteredText ).toString();
        if( filter.test( newValue ) ) {
            value = newValue;
            setCursorPosition( p0 + filteredText.length() );
            setHighlightPos( cursorPos );
            onValueChange();
        }
    }
    
    /** Called when the text box's value have been changed to trigger related events. */
    private void onValueChange() {
        if( responder != null ) responder.accept( value );
        nextNarration = Util.getMillis() + 500L;
    }
    
    /** Deletes the specified number of words from the cursor position. */
    public void deleteWords( int count ) {
        if( !value.isEmpty() ) {
            if( isHighlighted() ) insertText( "" );
            else deleteChars( getWordPosition( count ) - cursorPos );
        }
    }
    
    /** Deletes the specified number of characters from the cursor position. */
    public void deleteChars( int count ) {
        if( value.isEmpty() ) return;
        
        if( isHighlighted() ) insertText( "" );
        else {
            int deletePos = getCursorPos( count );
            if( deletePos != cursorPos ) {
                int p0 = Math.min( deletePos, cursorPos );
                int p1 = Math.max( deletePos, cursorPos );
                
                String newValue = new StringBuilder( value ).delete( p0, p1 ).toString();
                if( filter.test( newValue ) ) {
                    value = newValue;
                    moveCursorTo( p0 );
                }
            }
        }
    }
    
    /** @return The position a specified number of words away from the cursor position. */
    public int getWordPosition( int count ) { return getWordPosition( count, cursorPos ); }
    
    /** @return The position a specified number of words away from a particular position. */
    private int getWordPosition( int count, int pos ) {
        int currentPos = pos;
        boolean forward = count > 0;
        int max = Math.abs( count );
        for( int i = 0; i < max; i++ ) {
            if( forward ) {
                while( currentPos < value.length() && !Character.isWhitespace( value.charAt( currentPos ) ) ) {
                    currentPos++;
                }
                while( currentPos < value.length() && Character.isWhitespace( value.charAt( currentPos ) ) ) {
                    currentPos++;
                }
            }
            else {
                while( currentPos > 0 && Character.isWhitespace( value.charAt( currentPos - 1 ) ) ) {
                    currentPos--;
                }
                while( currentPos > 0 && !Character.isWhitespace( value.charAt( currentPos - 1 ) ) ) {
                    currentPos--;
                }
            }
        }
        return currentPos;
    }
    
    /** Moves the cursor position a specified number of characters. */
    public void moveCursor( int count ) { moveCursorTo( getCursorPos( count ) ); }
    
    /** @return A position the specified number of characters from the cursor position. */
    private int getCursorPos( int count ) {
        return Util.offsetByCodepoints( value, cursorPos, count );
    }
    
    /** Moves the cursor position, also moving the highlight position unless the user is holding shift. */
    public void moveCursorTo( int pos ) {
        setCursorPosition( pos );
        if( !shiftPressed ) setHighlightPos( cursorPos );
        onValueChange();
    }
    
    /** Sets the cursor position. Note this does not move the highlight position, so that must be handled separately. */
    public void setCursorPosition( int pos ) {
        cursorPos = MathHelper.clamp( pos, 0, value.length() );
    }
    
    /** Moves the cursor position before the first character, also moving the highlight position unless the user is holding shift. */
    public void moveCursorToStart() { moveCursorTo( 0 ); }
    
    /** Moves the cursor position behind the last character, also moving the highlight position unless the user is holding shift. */
    public void moveCursorToEnd() { moveCursorTo( value.length() ); }
    
    /** Sets the highlight position. Note this does not move the cursor position, so that must be handled separately. */
    public void setHighlightPos( int pos ) {
        //int maxPos = value.length();
        highlightPos = MathHelper.clamp( pos, 0, value.length() );
        //        if( font != null ) { TODO Replace this with something that scrolls the text box as needed
        //            if( displayPos > maxPos ) displayPos = maxPos;
        //
        //            int lvt_3_1_ = getInnerWidth();
        //            String lvt_4_1_ = font.plainSubstrByWidth( value.substring( displayPos ), lvt_3_1_ );
        //            int lvt_5_1_ = lvt_4_1_.length() + displayPos;
        //            if( highlightPos == displayPos ) {
        //                displayPos -= font.plainSubstrByWidth( value, lvt_3_1_, true ).length();
        //            }
        //
        //            if( highlightPos > lvt_5_1_ ) {
        //                displayPos += highlightPos - lvt_5_1_;
        //            }
        //            else if( highlightPos <= displayPos ) {
        //                displayPos -= displayPos - highlightPos;
        //            }
        //
        //            displayPos = MathHelper.clamp( displayPos, 0, maxPos );
        //        }
    }
    
    /**
     * Called when a mouse button is clicked.
     *
     * @param mouseKey The mouse key that was clicked (see {@link InputMappings.Type#MOUSE}).
     * @return True if the event has been handled.
     */
    @Override
    public boolean mouseClicked( double mouseX, double mouseY, int mouseKey ) {
        if( !visible ) return false;
        
        boolean hovered = isMouseOver( mouseX, mouseY );
        if( canLoseFocus ) setFocused( hovered );
        if( isFocused() && hovered && mouseKey == 0 ) {
            //TODO Account for multiline & scrolling
            int boxX = MathHelper.floor( mouseX ) - x;
            int boxY = MathHelper.floor( mouseY ) - y;
            if( bordered ) {
                boxX -= 4;
                boxY -= 4;
            }
            
            String visibleText = font.plainSubstrByWidth( value.substring( displayPos ), getInnerWidth() );
            moveCursorTo( font.plainSubstrByWidth( visibleText, boxX ).length() + displayPos );
            return true;
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
        if( !canConsumeInput() ) return false;
        
        boolean controlPressed = Screen.hasControlDown();
        shiftPressed = Screen.hasShiftDown();
        
        // Handle commands
        if( Screen.isSelectAll( key ) ) {
            moveCursorToEnd();
            setHighlightPos( 0 );
            return true;
        }
        if( Screen.isCopy( key ) ) {
            Minecraft.getInstance().keyboardHandler.setClipboard( getHighlighted() );
            return true;
        }
        if( Screen.isPaste( key ) ) {
            if( isEditable ) insertText( Minecraft.getInstance().keyboardHandler.getClipboard() );
            return true;
        }
        if( Screen.isCut( key ) ) {
            Minecraft.getInstance().keyboardHandler.setClipboard( getHighlighted() );
            if( isEditable ) insertText( "" );
            return true;
        }
        
        // Handle special keys
        if( key == InputMappings.getKey( "key.keyboard.backspace" ).getValue() ) {
            if( isEditable ) {
                shiftPressed = false;
                if( controlPressed ) deleteWords( -1 );
                else deleteChars( -1 );
                shiftPressed = Screen.hasShiftDown();
            }
            return true;
        }
        if( key == InputMappings.getKey( "key.keyboard.delete" ).getValue() ) {
            if( isEditable ) {
                shiftPressed = false;
                if( controlPressed ) deleteWords( 1 );
                else deleteChars( 1 );
                shiftPressed = Screen.hasShiftDown();
            }
            return true;
        }
        if( key == InputMappings.getKey( "key.keyboard.right" ).getValue() ) {
            if( controlPressed ) moveCursorTo( getWordPosition( 1 ) );
            else moveCursor( 1 );
            return true;
        }
        if( key == InputMappings.getKey( "key.keyboard.left" ).getValue() ) {
            if( controlPressed ) moveCursorTo( getWordPosition( -1 ) );
            else moveCursor( -1 );
            return true;
        }
        // TODO implement up/down key actions? "key.keyboard.up" / "key.keyboard.down"
        if( key == InputMappings.getKey( "key.keyboard.home" ).getValue() ) {
            moveCursorToStart();
            return true;
        }
        if( key == InputMappings.getKey( "key.keyboard.end" ).getValue() ) {
            moveCursorToEnd();
            return true;
        }
        // TODO implement enter key here? "key.keyboard.enter"
        return false;
    }
    
    /** Called when a character is typed. */
    @Override
    public boolean charTyped( char codePoint, int mods ) {
        if( !canConsumeInput() ) return false;
        if( isAllowedTextBoxCharacter( codePoint ) ) {
            if( isEditable ) {
                insertText( Character.toString( codePoint ) );
            }
            return true;
        }
        return false;
    }
    
    public boolean canConsumeInput() { return visible && isFocused() && isEditable; }
    
    @Override
    public void renderButton( MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks ) {
        if( !visible ) return;
        
        if( bordered ) {
            fill( matrixStack, x - 1, y - 1,
                    x + width + 1, y + height + 1, isFocused() ? 0xFF_FFFFFF : 0xFF_A0A0A0 );
            fill( matrixStack, x, y,
                    x + width, y + height, 0xFF_000000 );
        }
        
        int color = isEditable ? textColor : textColorUneditable;
        //TODO adjust this for multiline/scrolling
        String visibleText = font.plainSubstrByWidth( value.substring( displayPos ), getInnerWidth() );
        
        int boxCursorPos = cursorPos - displayPos;
        int boxHighlightPos = MathHelper.clamp( highlightPos - displayPos, 0, visibleText.length() );
        boolean cursorInBox = boxCursorPos >= 0 && boxCursorPos <= visibleText.length();
        boolean drawCursor = isFocused() && frame / 6 % 2 == 0 && cursorInBox;
        
        int fontX = bordered ? x + 4 : x;
        int fontY = bordered ? y + (height - 8) / 2 : y;
        
        int renderedToX = fontX;
        if( !visibleText.isEmpty() ) {
            String textBeforeCursor = cursorInBox ? visibleText.substring( 0, boxCursorPos ) : visibleText;
            renderedToX = font.drawShadow( matrixStack, formatter.apply( textBeforeCursor, displayPos ),
                    (float) fontX, (float) fontY, color );
        }
        
        boolean cursorIsAtEnd = cursorPos >= value.length();
        int cursorX;
        if( cursorInBox ) {
            if( !cursorIsAtEnd ) renderedToX--;
            cursorX = renderedToX;
        }
        else {
            cursorX = boxCursorPos > 0 ? fontX + width : fontX;
        }
        
        // Render any text after the cursor
        if( !visibleText.isEmpty() && cursorInBox && boxCursorPos < visibleText.length() ) {
            font.drawShadow( matrixStack, formatter.apply( visibleText.substring( boxCursorPos ), cursorPos ),
                    (float) renderedToX, (float) fontY, color );
        }
        
        // Render the suggestion
        if( cursorIsAtEnd && suggestion != null ) {
            font.drawShadow( matrixStack, suggestion,
                    (float) (cursorX - 1), (float) fontY, 0xFF_808080 );
        }
        
        // Render the cursor
        if( drawCursor ) {
            if( cursorIsAtEnd ) {
                font.drawShadow( matrixStack, "_",
                        (float) cursorX, (float) fontY, color );
            }
            else {
                //font.getClass(); // Why was this in the vanilla code?
                AbstractGui.fill( matrixStack, cursorX, fontY - 1,
                        cursorX + 1, fontY + 1 + 9, 0xFF_D0D0D0 );
            }
        }
        
        // Render the selection highlight
        if( boxHighlightPos != boxCursorPos ) {
            int lvt_16_1_ = fontX + font.width( visibleText.substring( 0, boxHighlightPos ) );
            //font.getClass(); // Why was this in the vanilla code?
            renderHighlight( cursorX, fontY - 1, lvt_16_1_ - 1, fontY + 1 + 9 );
        }
    }
    
    /** Renders the selected text highlight box using two points. */
    private void renderHighlight( int x0, int y0, int x1, int y1 ) {
        if( x0 > x1 ) {
            int dum = x1;
            x1 = x0;
            x0 = dum;
        }
        if( y0 > y1 ) {
            int dum = y1;
            y1 = y0;
            y0 = dum;
        }
        if( x0 < x ) {
            x0 = x;
            if( x1 < x ) x1 = x;
        }
        if( x1 > x + width ) {
            x1 = x + width;
            if( x0 > x + width ) x0 = x + width;
        }
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buf = tessellator.getBuilder();
        //noinspection deprecation
        RenderSystem.color4f( 0.0F, 0.0F, 255.0F, 255.0F );
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp( GlStateManager.LogicOp.OR_REVERSE );
        buf.begin( 7, DefaultVertexFormats.POSITION );
        buf.vertex( x1, y0, 0.0 ).endVertex();
        buf.vertex( x0, y0, 0.0 ).endVertex();
        buf.vertex( x0, y1, 0.0 ).endVertex();
        buf.vertex( x1, y1, 0.0 ).endVertex();
        tessellator.end();
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }
    
    /**
     * Called when focus change is requested (for example, tab or shift+tab).
     *
     * @param forward Whether focus should move forward. Typically, forward means left-to-right then top-to-bottom.
     * @return This gui's new focus state.
     */
    @Override
    public boolean changeFocus( boolean forward ) { return visible && isEditable && super.changeFocus( forward ); }
    
    @Override
    public boolean isMouseOver( double mouseX, double mouseY ) {
        return visible && mouseX >= (double) x && mouseX < (double) (x + width) && mouseY >= (double) y && mouseY < (double) (y + height);
    }
    
    @Override
    protected void onFocusedChanged( boolean focus ) { if( focus ) frame = 0; }
    
    public int getInnerWidth() { return bordered ? width - 8 : width; }
}