package fathertoast.crust.api.config.client.gui.widget.provider;

import fathertoast.crust.api.config.client.gui.widget.CrustConfigFieldList;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;

import java.util.List;
import java.util.function.Supplier;

/**
 * Displays an on/off toggle button for a boolean value.
 */
public class BooleanFieldWidgetProvider implements IConfigFieldWidgetProvider {
    
    /** The providing field. */
    protected final BooleanField FIELD;
    
    public BooleanFieldWidgetProvider( BooleanField field ) { FIELD = field; }
    
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
        Button toggleButton = new Button( 0, 0, VALUE_WIDTH, VALUE_HEIGHT,
                CommonComponents.optionStatus( cast( displayValue ) ), (button ) -> {
            boolean newValue = !cast( listEntry.getValue() );
            button.setMessage( CommonComponents.optionStatus( newValue ) );
            listEntry.updateValue( newValue );
        }, Supplier::get );
        
        components.add( toggleButton );
    }
    
    private static boolean cast( Object raw ) {
        Boolean value = TomlHelper.asBoolean( raw );
        return value != null && value;
    }
}