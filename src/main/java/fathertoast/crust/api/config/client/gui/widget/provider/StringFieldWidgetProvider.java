package fathertoast.crust.api.config.client.gui.widget.provider;

import fathertoast.crust.api.config.client.gui.widget.CrustConfigFieldList;
import fathertoast.crust.api.config.common.field.StringField;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

/**
 * Displays a text box for a string value.
 */
public class StringFieldWidgetProvider implements IConfigFieldWidgetProvider {
    
    /** The providing field. */
    protected final StringField FIELD;
    
    public StringFieldWidgetProvider( StringField field ) { FIELD = field; }
    
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
    public void apply( List<Widget> components, CrustConfigFieldList.FieldEntry listEntry, Object displayValue ) {
        TextFieldWidget textWidget = new TextFieldWidget( listEntry.minecraft().font,
                1, 1, VALUE_WIDTH - 2, VALUE_HEIGHT - 2, // Account for ~1px frame
                new StringTextComponent( FIELD.getKey() ) );
        textWidget.setMaxLength( Integer.MAX_VALUE );
        
        textWidget.setValue( displayValue.toString() );
        textWidget.setResponder( listEntry::updateValue );
        
        components.add( textWidget );
    }
}