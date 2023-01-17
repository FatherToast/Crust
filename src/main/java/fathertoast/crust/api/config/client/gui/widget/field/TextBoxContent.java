package fathertoast.crust.api.config.client.gui.widget.field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A helper class for handling the text string contained in a text box.
 */
public class TextBoxContent {
    
    /** The parent text box. */
    private final TextBoxWidget textBox;
    
    /** The complete string value of the text box. Lines are separated by newline characters. */
    private String plainText;
    /** A logical representation of the text box value for convenience in display and manipulation. */
    private final ArrayList<WrappedLine> wrappedLines = new ArrayList<>();
    
    /** The number of lines rendered, accounting for word wrap. If -1, it must be recalculated. */
    private int renderLines = -1;
    
    TextBoxContent( TextBoxWidget parent, String text ) {
        textBox = parent;
        setText( text );
    }
    
    /** Sets the full text and completely recalculates accordingly. */
    public void setText( String text ) {
        plainText = text;
        recalculateLines();
    }
    
    /** @return The full text. */
    public String getText() { return plainText; }
    
    /** @return A read-only list of the text's lines. */
    public List<WrappedLine> getLines() { return Collections.unmodifiableList( wrappedLines ); }
    
    /** @return The number of lines for file writing. */
    public int getLineCount() { return wrappedLines.size(); }
    
    /** @return The number of lines for display, i.e. including word wrap. */
    public int getLineCountAfterWrap() {
        if( renderLines < 0 ) {
            int count = 0;
            for( WrappedLine line : wrappedLines ) count += line.wrappedValue.size();
            renderLines = count;
        }
        return renderLines;
    }
    
    /** @return The index of the line containing a particular position. Returns -1 if the position is out of bounds. */
    public int getLineAtPosition( int pos ) {
        int i = wrappedLines.size() - 1;
        WrappedLine lastLine = wrappedLines.get( i ); // Special case for the last line
        if( pos >= lastLine.position ) return pos > lastLine.length() ? -1 : i;
        for( i--; i >= 0; i-- ) {
            if( pos >= wrappedLines.get( i ).position ) return i;
        }
        return -1;
    }
    
    /** Removes the line at the specified index. */
    public void removeLine( int index ) {
        WrappedLine removedLine = wrappedLines.remove( index );
        renderLines -= removedLine.getLineCountAfterWrap();
        int lineCount = getLineCount();
        int posOffset = removedLine.length() + 1;
        for( int i = index; i < lineCount; i++ ) wrappedLines.get( i ).position -= posOffset;
        
        recalculateText();
    }
    
    /** @return The line at the specified index. */
    public WrappedLine getLine( int index ) { return wrappedLines.get( index ); }
    
    /** Appends a new line to the end of the text. */
    public void addLine( String newLine ) { addLine( getLineCount(), newLine ); }
    
    /** Inserts a new line at the specified index. If the index is equal to the line count, the new line is appended. */
    public void addLine( int index, String newLine ) {
        if( newLine.contains( "\n" ) ) {
            String[] splitLine = newLine.split( "\n" );
            int i = index;
            for( String subLine : splitLine ) {
                addSingleLineNoRecalculation( i, subLine );
                i++;
            }
        }
        else addSingleLineNoRecalculation( index, newLine );
        
        recalculateText();
    }
    
    /**
     * Inserts a new line at the specified index. If the index is equal to the line count, the new line is appended.
     * The full text must be recalculated after calling this via {@link #recalculateText()}.
     */
    private void addSingleLineNoRecalculation( int index, String newLine ) {
        int lineCount = getLineCount();
        if( index < lineCount ) {
            // Insert
            wrappedLines.add( index, new WrappedLine( newLine, getLine( index ).position ) );
            int posOffset = newLine.length() + 1;
            for( int i = index + 1; i <= lineCount; i++ ) wrappedLines.get( i ).position += posOffset;
        }
        else {
            // Append
            WrappedLine lastLine = getLine( lineCount - 1 );
            wrappedLines.add( index, new WrappedLine( newLine, lastLine.position + lastLine.length() + 1 ) );
        }
    }
    
    /** Recalculates word wrap for each line using the current text box width. */
    public void recalculateWrap() { for( WrappedLine line : wrappedLines ) line.recalculateWrap(); }
    
    /** Recalculates all lines, their positions, and word wrap from the full text string. */
    private void recalculateLines() {
        String[] rawLines = plainText.split( "\n" );
        int currentPos = 0;
        renderLines = 0;
        wrappedLines.clear();
        for( String line : rawLines ) {
            wrappedLines.add( new WrappedLine( line, currentPos ) );
            currentPos += line.length() + 1; // +1 accounts for newline char
        }
    }
    
    /** Recalculates the full text string from the lines. */
    private void recalculateText() {
        StringBuilder text = new StringBuilder();
        boolean newline = false;
        for( WrappedLine line : wrappedLines ) {
            text.append( line.getValue() );
            if( newline ) text.append( '\n' );
            else newline = true;
        }
        plainText = text.toString();
    }
    
    
    /**
     * Stores data about a single line in the text box and applies word wrap.
     */
    public class WrappedLine {
        
        /** The line itself, without word wrap applied. */
        private final String value;
        /** The line split by render width. */
        private final ArrayList<String> wrappedValue = new ArrayList<>();
        
        /** The position of this line's first character within the overall plain text. */
        private int position;
        
        private WrappedLine( String str, int pos ) {
            position = pos;
            value = str;
            recalculateWrap();
        }
        
        /** @return The line position translated to text box position. */
        public int localPosToGlobal( int localPos ) { return localPos + position; }
        
        /** @return The text box position translated to line position. */
        public int globalPosToLocal( int globalPos ) { return globalPos - position; }
        
        /** @return The number of characters in this line. */
        public int length() { return value.length(); }
        
        /** @return The full contents of this line. */
        public String getValue() { return value; }
        
        /** @return The number of lines for display, i.e. including word wrap. */
        public int getLineCountAfterWrap() { return wrappedValue.size(); }
        
        /** Recalculates this line's word wrap using the current text box width. */
        private void recalculateWrap() {
            int width = textBox.getInnerWidth();
            int index = 0;
            renderLines -= getLineCountAfterWrap();
            wrappedValue.clear();
            while( index < value.length() ) {
                String wrapped = textBox.font.plainSubstrByWidth( value.substring( index ), width );
                index += wrapped.length();
            }
            renderLines += getLineCountAfterWrap();
        }
    }
}