package fathertoast.crust.api.config.client.gui.widget.field;

/**
 * TODO WIP: copy/pasted from text field
 * <p>
 * A multiline text field that allows unlimited string length and becomes scrollable upon overflow.
 * <p>
 * TODO: Consider text size controls (zoom in/out) and horizontal scroll vs. wordwrap
 *
 * @see net.minecraft.client.gui.components.EditBox
 */
public class TextBoxWidget /*extends Widget implements IRenderable, IGuiEventListener*/ {
    //
    //    /** The horizontal size of the scrollbar. */
    //    public static final int SCROLLBAR_WIDTH = 6;
    //
    //    /** @return True if the character is allowed in text boxes; i.e. is not a control code other than newline (\n). */
    //    public static boolean isAllowedTextBoxCharacter( char c ) { return c == '\n' || c > 159 || c < 127 && c > 31; }
    //
    //    /** @return The input, stripped of all disallowed characters. Based on {@link net.minecraft.util.SharedConstants#filterText(String)}. */
    //    public static String filterText( String text ) {
    //        StringBuilder filteredText = new StringBuilder();
    //        for( char c : text.toCharArray() ) {
    //            if( isAllowedTextBoxCharacter( c ) ) filteredText.append( c );
    //        }
    //        return filteredText.toString();
    //    }
    //
    //    /** The client's font renderer. */
    //    public final FontRenderer font;
    //
    //    /** The text currently held in this text box. */
    //    private TextBoxContent content;
    //    //private String value = "";
    //
    //    /** Color to render the text box value in while flagged as editable. */
    //    private int textColor = 0xFF_E0E0E0;
    //    /** Color to render the text box value in while flagged as NOT editable. */
    //    private int textColorUneditable = 0xFF_707070;
    //
    //    /** True if a border should be rendered around this text box. */
    //    private boolean bordered = true;
    //    /** True if this text box can lose focus. */
    //    private boolean canLoseFocus = true;
    //    /** True if this text box can receive user input. */
    //    private boolean isEditable = true;
    //
    //    private String suggestion;
    //    /** Called when this text box's value is changed. */
    //    private Consumer<String> responder;
    //    private BiFunction<String, Integer, IReorderingProcessor> formatter;
    //
    //    /** Counter to play animations. */
    //    private int frame;
    //    /** True if the shift key is being held. */
    //    private boolean shiftPressed;
    //
    //    /** The current scroll distance. */
    //    private double scrollDistance;
    //    /** True if currently scrolling (from clicking on the scrollbar). */
    //    private boolean scrolling;
    //
    //    /** The position of the first visible character. */
    //    @Deprecated
    //    private int displayPos;//TODO replace this with scroll amount
    //
    //    /**
    //     * The current cursor position. If the highlight position is different from this, then it is also the start position for the highlight.
    //     * Position can be anything from 0 to the length of the text box value.
    //     */
    //    private int cursorPos;
    //    /** The end position for the current highlight. Equal to the cursor position if no text is highlighted. */
    //    private int highlightPos;
    //
    //    public TextBoxWidget( FontRenderer fontRenderer, int x, int y, int width, int height, ITextComponent message ) {
    //        this( fontRenderer, x, y, width, height, "", message );
    //    }
    //
    //    public TextBoxWidget( FontRenderer fontRenderer, int x, int y, int width, int height, String initialValue, ITextComponent message ) {
    //        super( x, y, width, height, message );
    //        font = fontRenderer;
    //        formatter = ( p_195610_0_, p_195610_1_ ) -> IReorderingProcessor.forward( p_195610_0_, Style.EMPTY );
    //        setValue( initialValue );
    //    }
    //
    //    /** Sets the text color to render while the text box is flagged as editable. Note that alpha channel of 0x03 or less is set to 0xFF. */
    //    @SuppressWarnings( "unused" )
    //    public TextBoxWidget setTextColor( int argb ) {
    //        textColor = argb;
    //        return this;
    //    }
    //
    //    /** Sets the text color to render while the text box is flagged as NOT editable. Note that alpha channel of 0x03 or less is set to 0xFF. */
    //    @SuppressWarnings( "unused" )
    //    public TextBoxWidget setTextColorUneditable( int argb ) {
    //        textColorUneditable = argb;
    //        return this;
    //    }
    //
    //    /** Enables or disables the text box border. Default is enabled. */
    //    @SuppressWarnings( "unused" )
    //    public TextBoxWidget setBordered( boolean enable ) {
    //        bordered = enable;
    //        return this;
    //    }
    //
    //    /** Enables or disables the ability to click off of the text box. Default is enabled. */
    //    @SuppressWarnings( "unused" )
    //    public TextBoxWidget setCanLoseFocus( boolean enable ) {
    //        canLoseFocus = enable;
    //        return this;
    //    }
    //
    //    /** Enables or disables the player's ability to modify the text box contents. Default is enabled. */
    //    @SuppressWarnings( "unused" )
    //    public TextBoxWidget setEditable( boolean enable ) {
    //        isEditable = enable;
    //        return this;
    //    }
    //
    //    @SuppressWarnings( "unused" )
    //    public TextBoxWidget setSuggestion( @Nullable String newSuggestion ) {
    //        suggestion = newSuggestion;
    //        return this;
    //    }
    //
    //    /** Registers the update responder. This will be called whenever the text box contents are updated. */
    //    @SuppressWarnings( "unused" )
    //    public TextBoxWidget setResponder( Consumer<String> newResponder ) {
    //        responder = newResponder;
    //        return this;
    //    }
    //
    //    //    /**
    //    //     * Sets a filter that determines what strings are allowed in the text box. Default is any non-null string.
    //    //     * Note that you should ensure your filter does not allow null strings.
    //    //     */
    //    //    public TextBoxWidget setFilter( Predicate<String> newFilter ) {
    //    //        filter = newFilter;
    //    //        return this;
    //    //    }
    //
    //    @SuppressWarnings( "unused" )
    //    public TextBoxWidget setFormatter( BiFunction<String, Integer, IReorderingProcessor> newFormatter ) {
    //        formatter = newFormatter;
    //        return this;
    //    }
    //
    //    /** Sets the visibility of this text box. */
    //    @SuppressWarnings( "unused" )
    //    public TextBoxWidget setVisible( boolean enable ) {
    //        visible = enable;
    //        return this;
    //    }
    //
    //    //    public int getScreenX( int index ) {
    //    //        return index > value.length() ? x : x + font.width( value.substring( 0, index ) );//Account for multiline - if this is ever needed
    //    //    }
    //
    //    @SuppressWarnings( "unused" )
    //    public int getCursorPosition() { return cursorPos; }
    //
    //    /** Called each tick to update animations. */
    //    public void tick() { frame++; }
    //
    //    @Override
    //    protected IFormattableTextComponent createNarrationMessage() {
    //        return new TranslationTextComponent( "gui.narrate.editBox", getMessage(), content.getText() );
    //    }
    //
    //    /** Sets the text to hold in this text box. */
    //    public void setValue( String text ) {
    //        if( content == null ) content = new TextBoxContent( this, text );
    //        else content.setText( text );
    //        moveCursorToEnd();
    //        setHighlightPos( cursorPos );
    //        onValueChange();
    //    }
    //
    //    /** @return The text contained in this text box. */
    //    public String getValue() { return content.getText(); }
    //
    //    /** @return True if any text is currently highlighted. */
    //    public boolean isHighlighted() { return cursorPos != highlightPos; }
    //
    //    /** @return The text that is currently highlighted. */
    //    public String getHighlighted() { return content.substring( cursorPos, highlightPos ); }
    //
    //    /** Inserts a string at the current cursor position. Replaces the currently highlighted text, if any. */
    //    public void insertText( String text ) {
    //        String filteredText = filterText( text );
    //
    //        int p0 = Math.min( cursorPos, highlightPos );
    //        int p1 = Math.max( cursorPos, highlightPos );
    //        String newValue = new StringBuilder( content.getText() ).replace( p0, p1, filteredText ).toString();
    //        if( content.setText( newValue ) ) {
    //            setCursorPosition( p0 + filteredText.length() );
    //            setHighlightPos( cursorPos );
    //            onValueChange();
    //        }
    //    }
    //
    //    /** Called when the text box's value have been changed to trigger related events. */
    //    private void onValueChange() {
    //        if( responder != null ) responder.accept( content.getText() );
    //        nextNarration = Util.getMillis() + 500L;
    //    }
    //
    //    /** Deletes the specified number of words from the cursor position. */
    //    public void deleteWords( int count ) {
    //        if( !content.isEmpty() ) {
    //            if( isHighlighted() ) insertText( "" );
    //            else deleteChars( getWordPosition( count ) - cursorPos );
    //        }
    //    }
    //
    //    /** Deletes the specified number of characters from the cursor position. */
    //    public void deleteChars( int count ) {
    //        if( content.isEmpty() ) return;
    //
    //        if( isHighlighted() ) insertText( "" );
    //        else {
    //            int deletePos = getCursorPos( count );
    //            if( deletePos != cursorPos ) {
    //                int p0 = Math.min( deletePos, cursorPos );
    //                int p1 = Math.max( deletePos, cursorPos );
    //
    //                String newValue = new StringBuilder( content.getText() ).delete( p0, p1 ).toString();
    //                if( content.setText( newValue ) ) {
    //                    moveCursorTo( p0 );
    //                }
    //            }
    //        }
    //    }
    //
    //    /** @return The position a specified number of words away from the cursor position. */
    //    public int getWordPosition( int count ) { return getWordPosition( count, cursorPos ); }
    //
    //    /** @return The position a specified number of words away from a particular position. */
    //    private int getWordPosition( int count, int pos ) {
    //        int currentPos = pos;
    //        boolean forward = count > 0;
    //        int max = Math.abs( count );
    //        for( int i = 0; i < max; i++ ) {
    //            if( forward ) {
    //                // Ignore current word, then skip all whitespace to the start of the next word
    //                while( currentPos < content.length() && !content.isWhitespace( currentPos ) ) currentPos++;
    //                while( currentPos < content.length() && content.isWhitespace( currentPos ) ) currentPos++;
    //            }
    //            else {
    //                // Back out of any whitespace, then jump to the start of the word
    //                while( currentPos > 0 && content.isWhitespace( currentPos - 1 ) ) currentPos--;
    //                while( currentPos > 0 && !content.isWhitespace( currentPos - 1 ) ) currentPos--;
    //            }
    //        }
    //        return currentPos;
    //    }
    //
    //    /** Moves the cursor position a specified number of characters. */
    //    public void moveCursor( int count ) { moveCursorTo( getCursorPos( count ) ); }
    //
    //    /** @return A position the specified number of characters from the cursor position. */
    //    private int getCursorPos( int count ) {
    //        return Util.offsetByCodepoints( content.getText(), cursorPos, count );
    //    }
    //
    //    /** Moves the cursor position, also moving the highlight position unless the user is holding shift. */
    //    public void moveCursorTo( int pos ) {
    //        setCursorPosition( pos );
    //        if( !shiftPressed ) setHighlightPos( cursorPos );
    //        onValueChange();
    //    }
    //
    //    /** Sets the cursor position. Note this does not move the highlight position, so that must be handled separately. */
    //    public void setCursorPosition( int pos ) {
    //        cursorPos = MathHelper.clamp( pos, 0, content.length() );
    //    }
    //
    //    /** Moves the cursor position before the first character, also moving the highlight position unless the user is holding shift. */
    //    public void moveCursorToStart() { moveCursorTo( 0 ); }
    //
    //    /** Moves the cursor position behind the last character, also moving the highlight position unless the user is holding shift. */
    //    public void moveCursorToEnd() { moveCursorTo( content.length() ); }
    //
    //    /** Sets the highlight position. Note this does not move the cursor position, so that must be handled separately. */
    //    public void setHighlightPos( int pos ) {
    //        //int maxPos = value.length();
    //        highlightPos = MathHelper.clamp( pos, 0, content.length() );
    //        //        if( font != null ) { TODO Replace this with something that scrolls the text box as needed
    //        //            if( displayPos > maxPos ) displayPos = maxPos;
    //        //
    //        //            int lvt_3_1_ = getInnerWidth();
    //        //            String lvt_4_1_ = font.plainSubstrByWidth( value.substring( displayPos ), lvt_3_1_ );
    //        //            int lvt_5_1_ = lvt_4_1_.length() + displayPos;
    //        //            if( highlightPos == displayPos ) {
    //        //                displayPos -= font.plainSubstrByWidth( value, lvt_3_1_, true ).length();
    //        //            }
    //        //
    //        //            if( highlightPos > lvt_5_1_ ) {
    //        //                displayPos += highlightPos - lvt_5_1_;
    //        //            }
    //        //            else if( highlightPos <= displayPos ) {
    //        //                displayPos -= displayPos - highlightPos;
    //        //            }
    //        //
    //        //            displayPos = MathHelper.clamp( displayPos, 0, maxPos );
    //        //        }
    //    }
    //
    //    /** @return The scroll distance (from 0 to maxScroll). */
    //    public double getScrollDistance() { return scrollDistance; }
    //
    //    /** Sets the scroll distance (from 0 to maxScroll). */
    //    public void setScrollDistance( double value ) {
    //        scrollDistance = MathHelper.clamp( value, 0.0, getMaxScrollDistance() );
    //    }
    //
    //    /** Adds to the scroll distance (from 0 to maxScroll). */
    //    public void addScrollDistance( int y ) { setScrollDistance( getScrollDistance() + y ); }
    //
    //    /** @return The maximum scroll distance. */
    //    public int getMaxScrollDistance() { return Math.max( 0, getListContentHeight() - (height - ENTRY_PADDING) ); }
    //
    //    /** Centers the scroll position on a specific entry. */
    //    @SuppressWarnings( "unused" )
    //    protected void centerScrollOn( E entry ) {
    //        setScrollDistance( entries().indexOf( entry ) * itemHeight + (itemHeight - height) / 2.0 );
    //    }
    //
    //    /** Scrolls, if needed, to make sure a specific entry can be seen. */
    //    protected void ensureVisible( E entry ) {
    //        int rowTop = getRowTop( entries().indexOf( entry ) );
    //
    //        int scrollAmount = rowTop - (y0 + ENTRY_PADDING + itemHeight);
    //        if( scrollAmount < 0 ) addScrollDistance( scrollAmount );
    //
    //        scrollAmount = rowTop + itemHeight - (y1 - itemHeight);
    //        if( scrollAmount > 0 ) addScrollDistance( scrollAmount );
    //    }
    //
    //    /**
    //     * Called when a mouse button is clicked.
    //     *
    //     * @param mouseKey The mouse key that was clicked (see {@link InputMappings.Type#MOUSE}).
    //     * @return True if the event has been handled.
    //     */
    //    @Override
    //    public boolean mouseClicked( double mouseX, double mouseY, int mouseKey ) {
    //        if( !visible ) return false;
    //
    //        updateScrollingState( x, mouseKey );
    //        boolean hovered = isMouseOver( mouseX, mouseY );
    //        if( canLoseFocus ) setFocused( hovered );
    //        if( isFocused() && hovered && mouseKey == 0 ) {
    //            //TODO Account for multiline & scrolling
    //            int boxX = MathHelper.floor( mouseX ) - x;
    //            int boxY = MathHelper.floor( mouseY ) - y;
    //            if( bordered ) {
    //                boxX -= 4;
    //                boxY -= 4;
    //            }
    //
    //            String visibleText = font.plainSubstrByWidth( value.substring( displayPos ), getInnerWidth() );
    //            moveCursorTo( font.plainSubstrByWidth( visibleText, boxX ).length() + displayPos );
    //            return true;
    //        }
    //
    //        return false;
    //    }
    //
    //    /** Called when a mouse button is clicked to update scrolling state. */
    //    protected void updateScrollingState( double mouseX, int mouseKey ) {
    //        scrolling = mouseKey == 0 && mouseX >= getScrollbarLeft() && mouseX < getScrollbarLeft() + SCROLLBAR_WIDTH;
    //    }
    //
    //    /** Called when the mouse is moved while a mouse button is held. */
    //    @Override
    //    public boolean mouseDragged( double x, double y, int mouseKey, double deltaX, double deltaY ) {
    //        if( mouseKey == 0 && scrolling ) {
    //            int maxScroll = getMaxScrollDistance();
    //            if( y < y0 || maxScroll <= 0 ) {
    //                setScrollDistance( 0.0 );
    //            }
    //            else if( y > y1 ) {
    //                setScrollDistance( maxScroll );
    //            }
    //            else {
    //                double scrollScale = Math.max( 1.0, maxScroll / (double) (height - getScrollHandleHeight()) );
    //                setScrollDistance( getScrollDistance() + deltaY * scrollScale );
    //            }
    //            return true;
    //        }
    //        return false;
    //    }
    //
    //    /** Called when the mouse wheel is scrolled. */
    //    @Override
    //    public boolean mouseScrolled( double x, double y, double deltaScroll ) {
    //        setScrollDistance( getScrollDistance() - deltaScroll * itemHeight / 2.0 );
    //        return true;
    //    }
    //
    //    /**
    //     * Called when a keyboard key is pressed.
    //     *
    //     * @param key      The keyboard key that was pressed (see {@link InputMappings.Type#KEYSYM}).
    //     * @param scancode The system-specific scancode of the key (see {@link InputMappings.Type#SCANCODE}).
    //     * @param mods     Bitfield describing which modifier keys were held down.
    //     * @return True if the event has been handled.
    //     * @see org.lwjgl.glfw.GLFWKeyCallbackI#invoke(long, int, int, int, int)
    //     */
    //    @Override
    //    public boolean keyPressed( int key, int scancode, int mods ) {
    //        if( !canConsumeInput() ) return false;
    //
    //        boolean controlPressed = Screen.hasControlDown();
    //        shiftPressed = Screen.hasShiftDown();
    //
    //        // Handle commands
    //        if( Screen.isSelectAll( key ) ) {
    //            moveCursorToEnd();
    //            setHighlightPos( 0 );
    //            return true;
    //        }
    //        if( Screen.isCopy( key ) ) {
    //            Minecraft.getInstance().keyboardHandler.setClipboard( getHighlighted() );
    //            return true;
    //        }
    //        if( Screen.isPaste( key ) ) {
    //            if( isEditable ) insertText( Minecraft.getInstance().keyboardHandler.getClipboard() );
    //            return true;
    //        }
    //        if( Screen.isCut( key ) ) {
    //            Minecraft.getInstance().keyboardHandler.setClipboard( getHighlighted() );
    //            if( isEditable ) insertText( "" );
    //            return true;
    //        }
    //
    //        // Handle special keys
    //        if( key == InputMappings.getKey( "key.keyboard.backspace" ).getValue() ) {
    //            if( isEditable ) {
    //                shiftPressed = false;
    //                if( controlPressed ) deleteWords( -1 );
    //                else deleteChars( -1 );
    //                shiftPressed = Screen.hasShiftDown();
    //            }
    //            return true;
    //        }
    //        if( key == InputMappings.getKey( "key.keyboard.delete" ).getValue() ) {
    //            if( isEditable ) {
    //                shiftPressed = false;
    //                if( controlPressed ) deleteWords( 1 );
    //                else deleteChars( 1 );
    //                shiftPressed = Screen.hasShiftDown();
    //            }
    //            return true;
    //        }
    //        if( key == InputMappings.getKey( "key.keyboard.right" ).getValue() ) {
    //            if( controlPressed ) moveCursorTo( getWordPosition( 1 ) );
    //            else moveCursor( 1 );
    //            return true;
    //        }
    //        if( key == InputMappings.getKey( "key.keyboard.left" ).getValue() ) {
    //            if( controlPressed ) moveCursorTo( getWordPosition( -1 ) );
    //            else moveCursor( -1 );
    //            return true;
    //        }
    //        // TODO implement up/down key actions? "key.keyboard.up" / "key.keyboard.down"
    //        if( key == InputMappings.getKey( "key.keyboard.home" ).getValue() ) {
    //            moveCursorToStart();
    //            return true;
    //        }
    //        if( key == InputMappings.getKey( "key.keyboard.end" ).getValue() ) {
    //            moveCursorToEnd();
    //            return true;
    //        }
    //        // TODO implement enter key here? "key.keyboard.enter"
    //        return false;
    //    }
    //
    //    /** Called when a character is typed. */
    //    @Override
    //    public boolean charTyped( char codePoint, int mods ) {
    //        if( !canConsumeInput() ) return false;
    //        if( isAllowedTextBoxCharacter( codePoint ) ) {
    //            if( isEditable ) {
    //                insertText( Character.toString( codePoint ) );
    //            }
    //            return true;
    //        }
    //        return false;
    //    }
    //
    //    public boolean canConsumeInput() { return visible && isFocused() && isEditable; }
    //
    //    @Override
    //    public void renderButton( MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks ) {
    //        if( !visible ) return;
    //
    //        if( bordered ) {
    //            fill( matrixStack, x - 1, y - 1,
    //                    x + width + 1, y + height + 1, isFocused() ? 0xFF_FFFFFF : 0xFF_A0A0A0 );
    //            fill( matrixStack, x, y,
    //                    x + width, y + height, 0xFF_000000 );
    //        }
    //
    //        int color = isEditable ? textColor : textColorUneditable;
    //        //TODO adjust this for multiline/scrolling
    //        String visibleText = font.plainSubstrByWidth( value.substring( displayPos ), getInnerWidth() );
    //
    //        int boxCursorPos = cursorPos - displayPos;
    //        int boxHighlightPos = MathHelper.clamp( highlightPos - displayPos, 0, visibleText.length() );
    //        boolean cursorInBox = boxCursorPos >= 0 && boxCursorPos <= visibleText.length();
    //        boolean drawCursor = isFocused() && frame / 6 % 2 == 0 && cursorInBox;
    //
    //        int fontX = bordered ? x + 4 : x;
    //        int fontY = bordered ? y + (height - 8) / 2 : y;
    //
    //        int renderedToX = fontX;
    //        if( !visibleText.isEmpty() ) {
    //            String textBeforeCursor = cursorInBox ? visibleText.substring( 0, boxCursorPos ) : visibleText;
    //            renderedToX = font.drawShadow( matrixStack, formatter.apply( textBeforeCursor, displayPos ),
    //                    (float) fontX, (float) fontY, color );
    //        }
    //
    //        TextBoxContent.WrappedLine cursorLine = content.getLineAtPosition( cursorPos );
    //        boolean cursorIsAtEnd = cursorLine == null || cursorLine.globalPosToLocal( cursorPos ) >= cursorLine.length();
    //
    //        int cursorX;
    //        if( cursorInBox ) {
    //            if( !cursorIsAtEnd ) renderedToX--;
    //            cursorX = renderedToX;
    //        }
    //        else {
    //            cursorX = boxCursorPos > 0 ? fontX + width : fontX;
    //        }
    //
    //        // Render any text after the cursor
    //        if( !visibleText.isEmpty() && cursorInBox && boxCursorPos < visibleText.length() ) {
    //            font.drawShadow( matrixStack, formatter.apply( visibleText.substring( boxCursorPos ), cursorPos ),
    //                    (float) renderedToX, (float) fontY, color );
    //        }
    //
    //        // Render the suggestion
    //        if( cursorIsAtEnd && suggestion != null ) {
    //            font.drawShadow( matrixStack, suggestion,
    //                    (float) (cursorX - 1), (float) fontY, 0xFF_808080 );
    //        }
    //
    //        // Render the cursor
    //        if( drawCursor ) {
    //            if( cursorIsAtEnd ) {
    //                font.drawShadow( matrixStack, "_",
    //                        (float) cursorX, (float) fontY, color );
    //            }
    //            else {
    //                //font.getClass(); // Why was this in the vanilla code?
    //                AbstractGui.fill( matrixStack, cursorX, fontY - 1,
    //                        cursorX + 1, fontY + 1 + 9, 0xFF_D0D0D0 );
    //            }
    //        }
    //
    //        // Render the selection highlight
    //        if( boxHighlightPos != boxCursorPos ) {
    //            int lvt_16_1_ = fontX + font.width( visibleText.substring( 0, boxHighlightPos ) );
    //            //font.getClass(); // Why was this in the vanilla code?
    //            renderHighlight( cursorX, fontY - 1, lvt_16_1_ - 1, fontY + 1 + 9 );
    //        }
    //    }
    //
    //    /** Renders the selected text highlight box using two points. */
    //    private void renderHighlight( int x0, int y0, int x1, int y1 ) {
    //        if( x0 > x1 ) {
    //            int dum = x1;
    //            x1 = x0;
    //            x0 = dum;
    //        }
    //        if( y0 > y1 ) {
    //            int dum = y1;
    //            y1 = y0;
    //            y0 = dum;
    //        }
    //        if( x0 < x ) {
    //            x0 = x;
    //            if( x1 < x ) x1 = x;
    //        }
    //        if( x1 > x + width ) {
    //            x1 = x + width;
    //            if( x0 > x + width ) x0 = x + width;
    //        }
    //
    //        Tessellator tessellator = Tessellator.getInstance();
    //        BufferBuilder buf = tessellator.getBuilder();
    //        //noinspection deprecation
    //        RenderSystem.color4f( 0.0F, 0.0F, 255.0F, 255.0F );
    //        RenderSystem.disableTexture();
    //        RenderSystem.enableColorLogicOp();
    //        RenderSystem.logicOp( GlStateManager.LogicOp.OR_REVERSE );
    //        buf.begin( 7, DefaultVertexFormats.POSITION );
    //        buf.vertex( x1, y0, 0.0 ).endVertex();
    //        buf.vertex( x0, y0, 0.0 ).endVertex();
    //        buf.vertex( x0, y1, 0.0 ).endVertex();
    //        buf.vertex( x1, y1, 0.0 ).endVertex();
    //        tessellator.end();
    //        RenderSystem.disableColorLogicOp();
    //        RenderSystem.enableTexture();
    //    }
    //
    //    /**
    //     * Called when focus change is requested (for example, tab or shift+tab).
    //     *
    //     * @param forward Whether focus should move forward. Typically, forward means left-to-right then top-to-bottom.
    //     * @return This gui's new focus state.
    //     */
    //    @Override
    //    public boolean changeFocus( boolean forward ) { return visible && isEditable && super.changeFocus( forward ); }
    //
    //    @Override
    //    public boolean isMouseOver( double mouseX, double mouseY ) {
    //        return visible && mouseX >= (double) x && mouseX < (double) (x + width) && mouseY >= (double) y && mouseY < (double) (y + height);
    //    }
    //
    //    @Override
    //    protected void onFocusedChanged( boolean focus ) { if( focus ) frame = 0; }
    //
    //    public int getInnerWidth() { return bordered ? width - 8 : width; }
}