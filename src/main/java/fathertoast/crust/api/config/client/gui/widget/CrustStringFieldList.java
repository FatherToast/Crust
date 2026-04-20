package fathertoast.crust.api.config.client.gui.widget;

import com.google.common.collect.ImmutableList;
import fathertoast.crust.api.config.client.gui.screen.EditStringListScreen;
import fathertoast.crust.api.config.client.gui.widget.field.DeleteButton;
import fathertoast.crust.api.config.client.gui.widget.field.ResetButton;
import fathertoast.crust.api.config.client.gui.widget.field.searchbar.ISearchable;
import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.field.IStringListScreenEditable;
import fathertoast.crust.api.config.common.file.TomlHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Widget that displays the items in a {@link fathertoast.crust.api.config.common.field.StringListField}
 * in the same order they exist in the field's internal list.
 */
public class CrustStringFieldList<T extends AbstractConfigField & IStringListScreenEditable> extends
        SearchableSelectionList<CrustStringFieldList.Entry<T>> {
    
    /** The total amount of space available for field widgets. */
    public static final int OVERALL_WIDTH = 310;
    /** The width of the scroll bar, including padding. */
    public static final int SCROLL_WIDTH = 10;
    
    
    /** The field to represent with this selection list. */
    protected final T field;
    /** The field component (widget "row" from previous screen's selection list). */
    protected final CrustConfigFieldList.FieldEntry listEntry;
    /** A predicate used to check if an entry's value is valid. Typically inherited from the config field. */
    @Nullable
    protected final Predicate<String> validator;
    
    /** The config spec this list is displaying contents for. */
    public final EditStringListScreen<T> parent;
    
    /** The amount of entries this list contained when first created. */
    private final int initialEntryCount;
    
    /** True if any entries have been changed since opening. */
    private boolean changed;
    
    
    public CrustStringFieldList( EditStringListScreen<T> parent, Minecraft game, CrustConfigFieldList.FieldEntry listEntry,
                                 Object displayValue, T field ) {
        super( game, parent.width + 45, parent.height,
                43, parent.height - 32, 26 );
        this.parent = parent;
        this.field = field;
        this.listEntry = listEntry;
        this.validator = field.getLineValidator();
        
        List<String> contents = field.rawToStringList( displayValue );
        initialEntryCount = contents.size() + 1;
        
        // Create a new entry for each value in the field's list.
        for( String value : contents ) {
            addEntry( new CrustStringFieldList.Entry<>( this, value ) );
        }
        // Create a special entry at the end containing the "new entry" button.
        addEntry( new AddEntry<>( this ) );
    }
    
    /** @return The width for each row in the list. */
    @Override
    public int getRowWidth() { return OVERALL_WIDTH; }
    
    /** @return The scrollbar position, when visible. */
    @Override
    protected int getScrollbarPosition() { return getRowRight() + 1 - SCROLL_WIDTH; }
    
    /** @return True if any entries have been changed. */
    public boolean isChanged() { return changed; }
    
    /** Forcibly flags this list as changed and updates change state. */
    public void setChanged() {
        changed = true;
        parent.updateFooterButtonText();
    }
    
    /** Called by fields to verify changed state. */
    private void updateChangedState() {
        // Account for the index taken up by the add entry button
        if( children().size() - 1 != initialEntryCount - 1 ) {
            changed = true;
        }
        else {
            update:
            {
                for( CrustStringFieldList.Entry<T> child : children() ) {
                    if( child.changed ) {
                        changed = true;
                        break update;
                    }
                }
                changed = false;
            }
        }
        parent.updateFooterButtonText();
    }
    
    /** Called when the "save changes" button is pressed to apply all pending changes. */
    public void saveChanges() {
        if( changed ) {
            List<String> values = new ArrayList<>();
            
            for( CrustStringFieldList.Entry<T> child : children() ) {
                if( child instanceof AddEntry ) continue;
                
                values.add( child.EDIT_BOX.getValue() );
            }
            
            //TODO change to this once we fix screen transition
            //            listEntry.updateValue( field.stringListToValue( values ) );
            parent.updateValue( field.stringListToValue( values ) );
            listEntry.FIELD.getSpec().getNightConfig().set( field.getKey(), values );
            listEntry.FIELD.getSpec().onLoad();
        }
    }
    
    /** A mod display row for mod selection lists. */
    public static class Entry<T extends AbstractConfigField & IStringListScreenEditable> extends
            ContainerObjectSelectionList.Entry<CrustStringFieldList.Entry<T>> implements ISearchable {
        protected final CrustStringFieldList<T> PARENT;
        
        private final EditBox EDIT_BOX;
        private final Button DELETE_BUTTON;
        private final Button RESET_BUTTON;
        
        private final String INITIAL_VALUE;
        
        private boolean changed;
        
        public Entry( CrustStringFieldList<T> parent, String value ) {
            PARENT = parent;
            
            INITIAL_VALUE = value;
            
            EDIT_BOX = new EditBox( parent.minecraft.font, 0, 0, 240, 18, Component.literal( "" ) );
            EDIT_BOX.setMaxLength( Integer.MAX_VALUE );
            EDIT_BOX.setValue( value );
            
            if( PARENT.validator != null ) {
                EDIT_BOX.setResponder( ( val ) -> {
                    if( val == null || !PARENT.validator.test( val ) ) {
                        EDIT_BOX.setTextColor( IConfigFieldWidgetProvider.INVALID_COLOR );
                        clearValue();
                    }
                    else {
                        EDIT_BOX.setTextColor( IConfigFieldWidgetProvider.DEFAULT_COLOR );
                        updateValue( val );
                    }
                    
                } );
            }
            else {
                EDIT_BOX.setResponder( this::updateValue );
            }
            
            RESET_BUTTON = new ResetButton( ( button ) -> {
                EDIT_BOX.setValue( INITIAL_VALUE );
            } );
            RESET_BUTTON.active = false;
            
            DELETE_BUTTON = new DeleteButton( ( button ) -> {
                PARENT.children().remove( this );
                PARENT.changed = true;
                PARENT.updateChangedState();
                PARENT.rerunSearch();
                
                if( !PARENT.children().isEmpty() ) {
                    PARENT.setScrollAmount( PARENT.getScrollAmount() );
                }
            } );
        }
        
        /** Call this to change the field's pending "new" value. */
        public void updateValue( String value ) {
            changed = !TomlHelper.equals( INITIAL_VALUE, value );
            RESET_BUTTON.active = !value.equals( INITIAL_VALUE );
            PARENT.updateChangedState();
            ensureVisible();
        }
        
        /** Call this to delete the field's pending "new" value. */
        public void clearValue() {
            changed = false;
            RESET_BUTTON.active = false;
            PARENT.updateChangedState();
            ensureVisible();
        }
        
        /** Ensures this list entry is on-screen by scrolling the list up or down. */
        public void ensureVisible() { PARENT.ensureVisible( this ); }
        
        @Override
        public void render( GuiGraphics graphics, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight,
                            int mouseX, int mouseY, boolean mouseOver, float partialTicks ) {
            DELETE_BUTTON.setX( rowLeft );
            DELETE_BUTTON.setY( rowTop + 1 );
            DELETE_BUTTON.render( graphics, mouseX, mouseY, partialTicks );
            
            EDIT_BOX.setX( rowLeft + DELETE_BUTTON.getWidth() + 3 );
            EDIT_BOX.setY( rowTop + 2 );
            EDIT_BOX.render( graphics, mouseX, mouseY, partialTicks );
            
            RESET_BUTTON.setX( EDIT_BOX.getX() + EDIT_BOX.getWidth() + 3 );
            RESET_BUTTON.setY( rowTop + 1 );
            RESET_BUTTON.render( graphics, mouseX, mouseY, partialTicks );
        }
        
        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of( EDIT_BOX, DELETE_BUTTON, RESET_BUTTON );
        }
        
        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }
        
        @Override
        public void setFocused( @Nullable GuiEventListener component ) {
            if( component instanceof EditBox editBox ) PARENT.parent.setFocusedTextBox( editBox );
            super.setFocused( component );
        }
        
        @Override
        @Nullable
        public String getLookupName() {
            return EDIT_BOX.getValue();
        }
        
        @Override
        public void renderHighlight( GuiGraphics graphics, boolean isFocused, int scrollbarPos, int mouseX, int mouseY,
                                     float partialTick, int itemIndex, int rowLeft, int rowTop, int rowWidth, int itemHeight ) {
            int x = rowLeft - 4;
            int y = rowTop - 1;
            int width = RESET_BUTTON.getX() + RESET_BUTTON.getWidth() + 5;
            int height = y + itemHeight + 3;
            
            ISearchable.drawDefaultHighlight( graphics, isFocused, x, y, width, height );
        }
    }
    
    private static class AddEntry<T extends AbstractConfigField & IStringListScreenEditable> extends Entry<T> {
        
        private final Button ADD_ENTRY_BUTTON;
        
        public AddEntry( CrustStringFieldList<T> parent ) {
            super( parent, "" );
            ADD_ENTRY_BUTTON = new Button( 0, 0, 100, 20,
                    Component.translatable( "menu.crust.config.add_entry" ),
                    ( button ) -> {
                        parent.removeEntry( this );
                        parent.addEntry( new Entry<>( parent, "" ) );
                        parent.addEntry( new AddEntry<>( parent ) );
                        parent.setChanged();
                    },
                    Supplier::get );
        }
        
        @Override
        public void render( GuiGraphics graphics, int index, int rowTop, int rowLeft, int rowWidth, int rowHeight,
                            int mouseX, int mouseY, boolean mouseOver, float partialTicks ) {
            
            ADD_ENTRY_BUTTON.setX( (PARENT.parent.width / 2) - ADD_ENTRY_BUTTON.getWidth() / 2 );
            ADD_ENTRY_BUTTON.setY( rowTop );
            ADD_ENTRY_BUTTON.render( graphics, mouseX, mouseY, partialTicks );
        }
        
        @Override
        public List<? extends GuiEventListener> children() {
            return List.of( ADD_ENTRY_BUTTON );
        }
        
        @Override
        @Nullable
        public String getLookupName() {
            return null;
        }
    }
}