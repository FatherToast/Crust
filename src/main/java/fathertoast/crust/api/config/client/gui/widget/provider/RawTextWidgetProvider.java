package fathertoast.crust.api.config.client.gui.widget.provider;

import fathertoast.crust.api.config.client.gui.widget.CrustConfigFieldList;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Supplier;

/**
 * Displays a button that opens a popup text editor for a generic value.
 *
 * @see net.minecraft.client.gui.components.EditBox
 */
public class RawTextWidgetProvider implements IConfigFieldWidgetProvider {
    
    /** The providing field. */
    protected final AbstractConfigField FIELD;
    
    public RawTextWidgetProvider( AbstractConfigField field ) { FIELD = field; }
    
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
    public void apply( List<AbstractWidget> components, CrustConfigFieldList.FieldEntry listEntry, Object displayValue ) {
        Button editButton = new Button( 0, 0, VALUE_WIDTH, VALUE_HEIGHT,
                Component.literal( "Edit..." ),
                ( button ) -> openTextBoxMenu( button, listEntry, this ), Supplier::get );
        
        components.add( editButton );
        
        EditBox editBox = new EditBox( listEntry.minecraft().font,
                1, 1, VALUE_WIDTH - 2, VALUE_HEIGHT - 2, // Account for ~1px frame
                Component.literal( FIELD.getKey() ) );
        editBox.setMaxLength( Integer.MAX_VALUE );

        editBox.setValue( displayValue.toString() );
        editBox.setResponder( listEntry::updateValue );
        
        components.add( editBox );
    }
    
    /** Called when the button is pressed to open a text box popup. */
    protected void openTextBoxMenu( Button openingButton, CrustConfigFieldList.FieldEntry listEntry, RawTextWidgetProvider provider ) {
        //TODO create text box widget (needs to be created)
        //TODO create accept and cancel buttons
    }
}