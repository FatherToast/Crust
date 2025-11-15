package fathertoast.crust.api.config.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import fathertoast.crust.api.config.client.gui.ElementOffset;
import fathertoast.crust.api.config.client.gui.widget.CrustStringFieldList;
import fathertoast.crust.api.config.client.gui.widget.field.Searchbar;
import fathertoast.crust.api.config.client.gui.widget.field.TextWithSubtitle;
import fathertoast.crust.api.config.common.field.PredicateStringListField;
import fathertoast.crust.api.config.common.field.StringListField;
import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.client.ClientRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class EditStringListScreen extends Screen {
    
    /** The screen open under this one. */
    private final CrustConfigFileScreen LAST_SCREEN;
    
    /** The scroll amount from the previous screen's selection list. */
    private final double LAST_SCROLL;
    
    /** The spec of the 'opened' config file. */
    private final CrustConfigSpec SPEC;
    
    /** The string list field to edit. */
    private final StringListField FIELD;
    
    /** The selection list for interacting with the underlying {@link StringListField}. */
    private CrustStringFieldList selectionList;
    
    /** The search bar for looking up entries in the underlying {@link StringListField} */
    private Searchbar searchbar;
    
    /** The currently focused edit box, if any. */
    @Nullable
    private EditBox focusedTextBox;
    
    /** A button that adds a new blank entry to the underlying selection list. */
    protected Button addEntryButton;
    /** The "open file" or "discard changes" button. */
    private Button bottomLeftButton;
    /** The "done" or "save changes" button. */
    private Button bottomRightButton;
    
    
    public EditStringListScreen( CrustConfigFileScreen parent, StringListField field ) {
        super( title( field, parent.SPEC ) );
        LAST_SCREEN = parent;
        LAST_SCROLL = parent.getScrollAmount();
        SPEC = parent.SPEC;
        FIELD = field;
    }
    
    /** @return The given string field's key as a more easily readable title. */
    private static Component title( StringListField field, CrustConfigSpec spec ) {
        String name = CrustConfigFileScreen.decodeString( field.getKey().startsWith( spec.loadingCategory ) ?
                field.getKey().substring( spec.loadingCategory.length() ) : field.getKey() );
        
        return Component.literal( name );
    }
    
    /** Called to set the currently focused text box. */
    public void setFocusedTextBox( EditBox textBox ) { focusedTextBox = textBox; }
    
    /** Sets the current scroll position. */
    public void setScrollAmount( double scroll ) { selectionList.setScrollAmount( scroll ); }
    
    /** @return The current scroll position. */
    public double getScrollAmount() { return selectionList.getScrollAmount(); }
    
    /**
     * @return This screen's "add entry" button.
     * This is here so the selection list has
     * access to the button.
     */
    public Button getAddEntryButton() {
        return addEntryButton;
    }
    
    /** Closes this screen and reopens it to hard-refresh everything. */
    public void resetScreen() {
        if( minecraft != null ) {
            EditStringListScreen newScreen = new EditStringListScreen( LAST_SCREEN, FIELD );
            minecraft.setScreen( newScreen );
            newScreen.setScrollAmount( getScrollAmount() );
        }
    }
    
    /** Called to set up the screen before displaying it. */
    @Override
    protected void init() {
        if( minecraft == null ) return;
        
        // Header content
        addRenderableWidget( TextWithSubtitle.create( this, font, width / 2, 8, true, getTitle(), null ) );
        
        // Primary screen content
        Predicate<String> validator = FIELD instanceof PredicateStringListField pslf ? pslf.getLineValidator() : null;
        ElementOffset offset = new ElementOffset( 0, -2, -27, -2 );
        selectionList = new CrustStringFieldList( this, minecraft, FIELD, validator, SPEC, offset );
        addRenderableWidget( selectionList );
        
        Searchbar.Orientation orientation = ClientRegister.CONFIG_EDITOR.SEARCHBAR.orientation.get();
        int searchbarX = orientation == Searchbar.Orientation.LEFT ? 8 : width - 108;
        searchbar = Searchbar.create( this, selectionList, orientation.getOpposite(), font, searchbarX, 20, 100, Searchbar.DEFAULT_MATCHER );
        selectionList.setSearchbar( searchbar );
        
        // Footer content
        addRenderableWidget( bottomLeftButton = new Button( width / 2 - 155, height - 29,
                150, 20, Component.translatable( "menu.crust.config.open_folder" ),
                ( button ) -> {
                    if( selectionList.isChanged() ) resetScreen();
                    else Util.getPlatform().openFile( SPEC.getFile().getParentFile() );
                },
                Supplier::get ) );
        addRenderableWidget( bottomRightButton = new Button( width / 2 - 155 + 160, height - 29,
                150, 20, CommonComponents.GUI_DONE,
                ( button ) -> {
                    if( selectionList.isChanged() ) {
                        selectionList.saveChanges();
                        resetScreen();
                    }
                    else {
                        minecraft.setScreen( LAST_SCREEN );
                        LAST_SCREEN.setScrollAmount( LAST_SCROLL );
                    }
                },
                Supplier::get ) );
    }
    
    /** Called when the footer text might need to be changed. */
    public void updateFooterButtonText() {
        if( selectionList.isChanged() ) {
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
        return super.mouseClicked( x, y, mouseKey );
    }
    
    /** Called to close the screen. */
    @Override
    public void onClose() { if( minecraft != null ) minecraft.setScreen( LAST_SCREEN ); }
    
    /** Called each tick to update animations. */
    @Override
    public void tick() {
        if( focusedTextBox != null ) focusedTextBox.tick();
        if( searchbar != null ) searchbar.tick();
    }
    
    /** Called to render the screen. */
    @Override
    public void render( GuiGraphics graphics, int mouseX, int mouseY, float partialTicks ) {
        renderMain( graphics, mouseX, mouseY, partialTicks );
    }
    
    /** Called to render the primary screen content. */
    protected void renderMain( GuiGraphics graphics, int mouseX, int mouseY, float partialTicks ) {
        renderBackground( graphics );
        
        selectionList.render( graphics, mouseX, mouseY, partialTicks );
        
        super.render( graphics, mouseX, mouseY, partialTicks );
    }
}
