package fathertoast.crust.api.config.client.gui.widget.field.list;

import fathertoast.crust.api.config.client.gui.widget.SearchableSelectionList;
import fathertoast.crust.api.config.client.gui.widget.entry.ConfigFieldGuiEntry;
import fathertoast.crust.api.config.client.gui.widget.field.DeleteButton;
import fathertoast.crust.api.config.client.gui.widget.field.ResetButton;
import fathertoast.crust.api.config.client.gui.widget.field.TextWithSubtitle;
import fathertoast.crust.api.config.client.gui.widget.field.searchbar.ISearchable;
import fathertoast.crust.api.config.client.gui.widget.field.searchbar.Searchbar;
import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.TomlHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A full-screen popup that displays a scrollable list of editable text boxes.
 */
public class PopupStringListWidget<T> extends FullScreenPopupListWidget<PopupListEntry> {
    
    /** @return The given string field's key as a more easily readable title. */
    private static Component title( ConfigFieldGuiEntry<?> listEntry ) {
        String name = ConfigUtil.decodeBareKeyString(
                listEntry.getField().getKey().startsWith( listEntry.getField().getSpec().loadingCategory ) ?
                        listEntry.getField().getKey().substring( listEntry.getField().getSpec().loadingCategory.length() ) :
                        listEntry.getField().getKey() );
        
        return Component.literal( name );
    }
    
    
    protected final ConfigFieldGuiEntry<T> PARENT;
    /** The value provided to this widget when opened, used to build the initial GUI list. */
    protected final List<String> DISPLAY_VALUE;
    @Nullable
    protected final Predicate<String> VALIDATOR;
    
    private boolean changed;
    
    /** The "open file" or "discard changes" button. */
    private Button bottomLeftButton;
    /** The "done" or "save changes" button. */
    private Button bottomRightButton;
    
    public PopupStringListWidget( ConfigFieldGuiEntry<T> listEntry, List<String> displayValue, @Nullable Predicate<String> validator ) {
        super( title( listEntry ) );
        PARENT = listEntry;
        DISPLAY_VALUE = displayValue;
        VALIDATOR = validator;
    }
    
    /** Called when the widget is opened to perform standard setup. */
    @Override // IPopupWidget
    public void init() {
        // Header content
        setRenderHeader( true, 43 );
        addChild( TextWithSubtitle.create( PARENT.screen(), PARENT.client().font, width / 2, 8,
                true, getMessage(), null ) );
        
        // Primary screen content
        for( String value : DISPLAY_VALUE ) {
            addEntry( new PopupStringListWidget.Entry( this, value ) );
        }
        // Create a special entry at the end containing the "new entry" button.
        addEntry( new PopupStringListWidget.AddEntry( this ) );
        updateReorderingButtons();
        
        Searchbar.Orientation orientation = Searchbar.orientation.get();
        int searchbarX = orientation.getX( 8, 115, width );
        Searchbar.create( this, orientation.getOpposite(), PARENT.client().font,
                searchbarX, 20, 115, null );
        
        // Footer content
        setRenderFooter( true, 32 );
        addChild( bottomLeftButton = new Button( width / 2 - 155, height - 29,
                150, 20, CommonComponents.GUI_DONE,
                button -> closePopup(), Supplier::get ) );
        addChild( bottomRightButton = new Button( width / 2 - 155 + 160, height - 29,
                150, 20, Component.translatable( "menu.crust.config.confirm_changes" ),
                button -> {
                    applyChanges();
                    closePopup();
                }, Supplier::get ) );
        bottomRightButton.active = false;
    }
    
    /** Called when the footer text might need to be changed. */
    public void updateFooterButtonText() {
        if( isChanged() ) {
            bottomLeftButton.setMessage( Component.translatable( "menu.crust.config.discard_changes" )
                    .withStyle( ChatFormatting.RED ) );
            bottomRightButton.setMessage( Component.translatable( "menu.crust.config.confirm_changes" )
                    .withStyle( ChatFormatting.AQUA ) );
            bottomRightButton.active = true;
        }
        else {
            bottomLeftButton.setMessage( CommonComponents.GUI_DONE );
            bottomRightButton.setMessage( Component.translatable( "menu.crust.config.confirm_changes" ) );
            bottomRightButton.active = false;
        }
    }
    
    private void updateReorderingButtons() {
        if( !PARENT.isEditable() ) return;
        for( PopupListEntry entry : entries() ) {
            if( entry instanceof Entry e ) {
                e.UP_BUTTON.active = true;
                e.DOWN_BUTTON.active = true;
            }
        }
        if( entries().size() > 1 ) {
            ((Entry) getEntry( 0 )).UP_BUTTON.active = false;
            ((Entry) getEntry( entries().size() - 2 )).DOWN_BUTTON.active = false;
        }
    }
    
    /** @return The x position for the left edge of entry rows. */
    @Override
    public int getRowLeft() { return (width - getRowWidth() >> 1); }
    
    /** @return The width of each list entry. Note that entries are centered in the widget, ignoring the scrollbar. */
    @Override
    public int getRowWidth() { return Entry.WIDTH; }
    
    /** @return The height of the scrollbar handle. */
    @Override
    protected int getScrollHandleHeight() {
        int contentHeight = height - headerHeight - footerHeight;
        return Mth.clamp( (int) ((float) contentHeight * contentHeight / (float) getListContentHeight()),
                32, contentHeight - 8 );
    }
    
    /** @return The x position for the left edge of the scrollbar. */
    @Override
    protected int getScrollbarLeft() { return getRowRight() + ENTRY_PADDING; }
    
    /** Closes this popup widget. */
    public void closePopup() { PARENT.setPopupWidget( null ); }
    
    /** @return True if any entries have been changed. */
    public boolean isChanged() { return changed; }
    
    /** Forcibly flags this list as changed and updates change state. */
    public void setChanged() {
        if( PARENT.isEditable() ) {
            changed = true;
            updateFooterButtonText();
        }
    }
    
    /** Called by fields to verify changed state. */
    private void updateChangedState() {
        if( !PARENT.isEditable() ) return;
        // Account for the index taken up by the add entry button
        if( entries().size() != DISPLAY_VALUE.size() + 1 ) {
            changed = true;
        }
        else {
            update:
            {
                for( int i = 0; i < DISPLAY_VALUE.size(); i++ ) {
                    Entry entry = (Entry) entries().get( i );
                    if( entry.changed || !DISPLAY_VALUE.get( i ).equals( entry.INITIAL_VALUE ) ) {
                        changed = true;
                        break update;
                    }
                }
                changed = false;
            }
        }
        updateFooterButtonText();
    }
    
    /** Called when the "confirm changes" button is pressed to apply all pending changes. */
    public void applyChanges() {
        if( PARENT.isEditable() && isChanged() ) {
            List<String> values = new ArrayList<>();
            for( PopupListEntry entry : entries() ) {
                if( entry instanceof Entry e ) values.add( e.EDIT_BOX.getValue() );
            }
            PARENT.updateInput( values );
        }
    }
    
    /**
     * An entry in the popup string list editor corresponding to a single string entry in the list.
     */
    public static class Entry extends PopupListEntry implements ISearchable {
        
        private static final int EDIT_BOX_WIDTH = 240;
        private static final int WIDTH = Searchbar.ARROW_BUTTON_WIDTH + EDIT_BOX_WIDTH + ResetButton.WIDTH + DeleteButton.WIDTH + ENTRY_PADDING * 3;
        
        private final PopupStringListWidget<?> PARENT;
        /** Null for newly added entries; forces this to always be flagged as "changed". */
        @Nullable
        private final String INITIAL_VALUE;
        
        private final Button UP_BUTTON;
        private final Button DOWN_BUTTON;
        private final EditBox EDIT_BOX;
        private final Button RESET_BUTTON;
        
        private boolean changed;
        
        public Entry( PopupStringListWidget<?> parent, @Nullable String value ) {
            PARENT = parent;
            INITIAL_VALUE = value;
            
            List<AbstractWidget> widgets = new ArrayList<>();
            boolean editable = parent.PARENT.isEditable();
            
            UP_BUTTON = new ImageButton( 0, 2, Searchbar.ARROW_BUTTON_WIDTH, 7, 11, 0, 7,
                    Searchbar.SEARCH_BAR_ICONS, button -> {
                int index = parent.indexOf( this );
                if( index > 0 ) {
                    parent.remove( index );
                    parent.addEntry( index - 1, this );
                    parent.scrollToIndex( index - 1 );
                }
                parent.updateReorderingButtons();
                parent.updateChangedState();
                button.setFocused( false );
            } );
            UP_BUTTON.active = editable;
            widgets.add( UP_BUTTON );
            DOWN_BUTTON = new ImageButton( 0, 11, Searchbar.ARROW_BUTTON_WIDTH, 7, 0, 0, 7,
                    Searchbar.SEARCH_BAR_ICONS, button -> {
                int index = parent.indexOf( this );
                if( index >= 0 && index < parent.entries().size() - 2 ) {
                    parent.remove( index );
                    parent.addEntry( index + 1, this );
                    parent.scrollToIndex( index + 1 );
                }
                parent.updateReorderingButtons();
                parent.updateChangedState();
                button.setFocused( false );
            } );
            DOWN_BUTTON.active = editable;
            widgets.add( DOWN_BUTTON );
            
            Predicate<String> validator = parent.VALIDATOR;
            EDIT_BOX = new EditBox( parent.PARENT.client().font, Searchbar.ARROW_BUTTON_WIDTH + ENTRY_PADDING, 2,
                    EDIT_BOX_WIDTH, 18, Component.literal( "" ) );
            EDIT_BOX.setMaxLength( Short.MAX_VALUE );
            EDIT_BOX.setValue( value == null ? "" : value );
            EDIT_BOX.setResponder( validator == null ? this::updateValue :
                    text -> {
                        if( validator.test( text ) ) {
                            EDIT_BOX.setTextColor( IConfigFieldWidgetProvider.DEFAULT_COLOR );
                            updateValue( text );
                        }
                        else {
                            EDIT_BOX.setTextColor( IConfigFieldWidgetProvider.INVALID_COLOR );
                            clearValue();
                        }
                    } );
            EDIT_BOX.active = editable;
            widgets.add( EDIT_BOX );
            
            RESET_BUTTON = new ResetButton( button -> clearValue() );
            RESET_BUTTON.setX( WIDTH - ResetButton.WIDTH - ENTRY_PADDING - DeleteButton.WIDTH );
            RESET_BUTTON.setY( 1 );
            RESET_BUTTON.active = false;
            widgets.add( RESET_BUTTON );
            
            Button deleteButton = new DeleteButton( button -> {
                parent.entries().remove( this );
                parent.updateReorderingButtons();
                parent.updateChangedState();
                parent.rerunSearch();
                if( !parent.entries().isEmpty() ) {
                    parent.setScrollDistance( parent.getScrollDistance() );
                }
            } );
            deleteButton.setX( WIDTH - DeleteButton.WIDTH );
            deleteButton.setY( 1 );
            deleteButton.active = editable;
            widgets.add( deleteButton );
            
            setWidgets( widgets );
        }
        
        /** Call this to change the field's pending "new" value. */
        public void updateValue( String value ) {
            if( !PARENT.PARENT.isEditable() ) return;
            changed = INITIAL_VALUE == null || !TomlHelper.equals( INITIAL_VALUE, value );
            RESET_BUTTON.active = changed;
            PARENT.updateChangedState();
            ensureVisible();
        }
        
        /** Call this to delete the field's pending "new" value. */
        public void clearValue() {
            changed = INITIAL_VALUE == null;
            EDIT_BOX.setValue( INITIAL_VALUE == null ? "" : INITIAL_VALUE );
            RESET_BUTTON.active = false;
            PARENT.updateChangedState();
            ensureVisible();
        }
        
        /** Ensures this list entry is on-screen by scrolling the list up or down. */
        public void ensureVisible() { PARENT.ensureVisible( this ); }
        
        /** @return An identifying String to be looked up by a {@link Searchbar} */
        @Override // ISearchable
        @Nullable
        public String getLookupName() { return EDIT_BOX.getValue(); }
        
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
            ISearchable.drawDefaultHighlight( graphics, isFocused, rowLeft - 2, rowTop,
                    rowLeft + WIDTH + 2, rowTop + itemHeight - 2 );
        }
    }
    
    /**
     * An entry in the popup string list editor corresponding to an "add entry" button.
     * There should be exactly one of these in the list, as the last element of it.
     */
    public static class AddEntry extends PopupListEntry {
        
        public AddEntry( PopupStringListWidget<?> parent ) {
            if( !parent.PARENT.isEditable() ) return; // Entry will still exist, but invisible
            
            Button addEntryButton = new Button( 0, 0, 100, 20,
                    Component.translatable( "menu.crust.config.add_entry" ),
                    button -> {
                        parent.addEntry( parent.entries().size() - 1,
                                new Entry( parent, null ) );
                        parent.updateReorderingButtons();
                        parent.setChanged();
                    },
                    Supplier::get );
            addEntryButton.setX( parent.getRowWidth() - addEntryButton.getWidth() >> 1 );
            setWidgets( addEntryButton );
        }
    }
}