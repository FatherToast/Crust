package fathertoast.crust.api.config.client.gui.widget.entry;

import fathertoast.crust.api.config.client.gui.widget.SearchableSelectionList;
import fathertoast.crust.api.config.client.gui.widget.field.searchbar.ISearchable;
import fathertoast.crust.api.config.client.gui.widget.field.searchbar.Searchbar;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents one formatting entry in a {@link fathertoast.crust.client.screen.widget.CrustConfigFieldList}.
 * These are the non-field widget entries, which solely exist to provide information or
 * improve readability for the in-game editor.
 */
public abstract class FormatGuiEntry extends ConfigGuiEntry {
    
    // TODO Do we need this?
    //@Nullable
    //@Override
    //public ComponentPath nextFocusPath( FocusNavigationEvent event ) { return super.nextFocusPath( event ); }
    
    @Override
    public List<? extends GuiEventListener> children() { return Collections.emptyList(); }
    
    
    // ---- Implementations ---- //
    
    /** A single new line entry (empty space). */
    public static class NewLine extends FormatGuiEntry {
        /** Renders this list entry. */
        @Override
        public void render( GuiGraphics graphics, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight,
                            int mouseX, int mouseY, boolean mouseOver, float partialTicks ) {}
    }
    
    /**
     * A comment that displays a title, which can be hovered over to see the full text in a tooltip.
     *
     * @see CrustConfigSpec#titledComment(String, String...)
     * @see CrustConfigSpec#titledComment(String, List)
     */
    public static class LeftAlignedString extends FormatGuiEntry {
        
        private final FormattedCharSequence TEXT;
        private final int COLOR;
        
        public LeftAlignedString( FormattedCharSequence text, int color ) {
            TEXT = text;
            COLOR = color;
        }
        
        /** Renders this list entry. */
        @Override
        public void render( GuiGraphics graphics, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight,
                            int mouseX, int mouseY, boolean mouseOver, float partialTicks ) {
            
            graphics.drawString( client().font, TEXT, rowLeft, rowTop + 5, COLOR );
        }
    }
    
    /**
     * A comment displayed as a title that can be hovered over to see the full text in a tooltip.
     *
     * @see CrustConfigSpec#titledComment(String, String...)
     * @see CrustConfigSpec#titledComment(String, List)
     */
    public static class TitledComment extends FormatGuiEntry {
        
        private final Component TEXT;
        private final List<FormattedCharSequence> TOOLTIP;
        private final int COLOR;
        
        public TitledComment( String text, List<String> comment, int color ) {
            TEXT = Component.literal( text );
            COLOR = color;
            
            if( comment.isEmpty() ) TOOLTIP = null;
            else {
                TOOLTIP = new ArrayList<>();
                split( TOOLTIP, Component.literal( text )
                        .withStyle( ChatFormatting.YELLOW ), TOOLTIP_WIDTH );
                for( String line : comment ) {
                    split( TOOLTIP, Component.literal( line ), TOOLTIP_WIDTH );
                }
            }
        }
        
        /** Renders this list entry. */
        @Override
        public void render( GuiGraphics graphics, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight,
                            int mouseX, int mouseY, boolean mouseOver, float partialTicks ) {
            
            graphics.drawString( client().font, TEXT,
                    rowLeft, rowTop + 5, COLOR );
        }
        
        /** @return The tooltip to render when the mouse is over this entry. Null if no tooltip should render. */
        @Override
        @Nullable
        public List<FormattedCharSequence> getTooltip() { return TOOLTIP; }
    }
    
    /**
     * A centered line of text.
     */
    public static class CenteredString extends FormatGuiEntry {
        
        protected final Component TEXT;
        private final int COLOR;
        
        public final int WIDTH;
        
        public CenteredString( Component text, int color ) {
            TEXT = text;
            COLOR = color;
            WIDTH = client().font.width( TEXT );
        }
        
        /** Renders this list entry. */
        @Override
        public void render( GuiGraphics graphics, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight,
                            int mouseX, int mouseY, boolean mouseOver, float partialTicks ) {
            graphics.drawString( client().font, TEXT,
                    screen().width - WIDTH - 5 >> 1, rowTop + 5, COLOR );
        }
    }
    
    /**
     * A centered line of text with a tooltip; used as the start of a new section.
     *
     * @see CrustConfigSpec#category(String, String...)
     * @see CrustConfigSpec#category(String, List)
     * @see CrustConfigSpec#appendixHeader(String...)
     */
    public static class Header extends CenteredString {
        
        private final List<FormattedCharSequence> TOOLTIP;
        
        public Header( String text, @Nullable List<String> comment, int color ) {
            super( Component.literal( ConfigUtil.properCase( text ) ), color );
            
            if( comment == null || comment.isEmpty() ) TOOLTIP = null;
            else {
                TOOLTIP = new ArrayList<>();
                split( TOOLTIP, Component.literal( text )
                        .withStyle( ChatFormatting.YELLOW ), TOOLTIP_WIDTH );
                for( String line : comment ) {
                    split( TOOLTIP, Component.literal( line ), TOOLTIP_WIDTH );
                }
            }
        }
        
        /** @return The tooltip to render when the mouse is over this entry. Null if no tooltip should render. */
        @Override
        @Nullable
        public List<FormattedCharSequence> getTooltip() { return TOOLTIP; }
        
        /** @return An identifying String to be looked up by a {@link Searchbar} */
        @Override // ISearchable
        public String getLookupName() { return TEXT.getString(); }
        
        /**
         * This is used by {@link SearchableSelectionList} to render
         * a highlight behind this searchable if it is said list's
         * currently focused search match.
         *
         * @param isFocused    True if this searchable is the currently focused entry in the underlying searchable list.
         * @param scrollbarPos The position of the underlying searchable list's scrollbar.
         * @see SearchableSelectionList#renderItem(GuiGraphics, int, int, float, int, int, int, int, int)
         */
        @Override // ISearchable
        public void renderHighlight( GuiGraphics graphics, boolean isFocused, int scrollbarPos, int mouseX, int mouseY,
                                     float partialTick, int itemIndex, int rowLeft, int rowTop, int rowWidth, int itemHeight ) {
            int textWidth = client().font.width( TEXT.getString() );
            int x = (screen().width - WIDTH >> 1) - 8;
            int width = Math.min( x + textWidth + 10, x + scrollbarPos );
            int height = rowTop + itemHeight + 2;
            
            ISearchable.drawDefaultHighlight( graphics, isFocused, x, rowTop, width, height );
        }
    }
}