package fathertoast.crust.api.config.client.gui.widget.provider;

import fathertoast.crust.api.config.client.gui.widget.entry.ConfigFieldGuiEntry;
import fathertoast.crust.api.config.common.field.IConfigField;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.Supplier;

/**
 * Displays a button that opens a popup text editor for a generic value.
 *
 * @see net.minecraft.client.gui.components.EditBox
 */
@ApiStatus.Experimental // WIP Does not work yet
@SuppressWarnings( { "ClassCanBeRecord", "unused" } )
public class RawTextWidgetProvider<T> implements IConfigFieldWidgetProvider<T> {
    
    /** The providing field. */
    protected final IConfigField<T> FIELD;
    
    public RawTextWidgetProvider( IConfigField<T> field ) { FIELD = field; }
    
    /**
     * Called to initialize the field's gui components.
     * <p>
     * Positions of the widgets provided (x, y) are relative to the top-left corner of the "field value widget" space.
     * The space available for field value widgets is a {@link #VALUE_WIDTH} by {@link #VALUE_HEIGHT} rectangle
     * (in GUI pixels) that is right-aligned in the parent list widget.
     *
     * @param components   The list to populate with widgets.
     * @param listEntry    The field component (widget "row" within a scrollable list).
     * @param displayValue The current raw value to display in the GUI.
     */
    @Override
    public void apply( List<AbstractWidget> components, ConfigFieldGuiEntry<T> listEntry,//TODO remove reference to non-api package
                       T displayValue ) {
        Button editButton = new Button( 0, 0, VALUE_WIDTH, VALUE_HEIGHT,
                Component.literal( "Edit..." ),
                button -> openTextBoxMenu( button, listEntry, this ), Supplier::get );
        editButton.active = listEntry.isEditable();
        
        EditBox editBox = new EditBox( listEntry.client().font,
                1, 1, VALUE_WIDTH - 2, VALUE_HEIGHT - 2, // Account for ~1px frame
                Component.literal( FIELD.getKey() ) );
        editBox.setMaxLength( Integer.MAX_VALUE );
        
        editBox.setValue( displayValue.toString() );
        editBox.setResponder( listEntry::updateInput );
        editBox.active = listEntry.isEditable();
        
        components.add( editButton );
        components.add( editBox );
    }
    
    /** Called when the button is pressed to open a text box popup. */
    protected void openTextBoxMenu( Button openingButton, ConfigFieldGuiEntry<T> listEntry, RawTextWidgetProvider<T> provider ) {
        //TODO create text box widget (needs to be created)
        //TODO create accept and cancel buttons
    }
}