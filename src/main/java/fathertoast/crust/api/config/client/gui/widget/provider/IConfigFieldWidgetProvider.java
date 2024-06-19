package fathertoast.crust.api.config.client.gui.widget.provider;

import fathertoast.crust.api.config.client.gui.widget.CrustConfigFieldList;
import net.minecraft.client.gui.components.AbstractWidget;
import java.util.List;

public interface IConfigFieldWidgetProvider {
    
    /** The amount of horizontal space available for field widgets' value display/edit components. */
    int VALUE_WIDTH = 120;
    /** The amount of vertical space available for field widgets' value display/edit components. */
    int VALUE_HEIGHT = 20;
    
    /** The default color for input text. */
    int DEFAULT_COLOR = 0xE0E0E0;
    /** The standard color for input text that is unable to be parsed by the field. */
    int INVALID_COLOR = 0xFF0000;
    
    /**
     * Called to initialize the field's gui components.
     * <p>
     * Positions of the widgets provided (x, y) are relative to the top-left corner of the "field value widget" space.
     * The space available for field value widgets is a {@link #VALUE_WIDTH} by {@link #VALUE_HEIGHT} rectangle
     * (in GUI pixels) that is right-aligned to the "set to default" button.
     *
     * @param components   The list to populate with widgets.
     * @param listEntry    The field component (widget "row" within a scrollable list).
     * @param displayValue The current raw value to display in the GUI.
     */
    void apply( List<AbstractWidget> components, CrustConfigFieldList.FieldEntry listEntry, Object displayValue );
}