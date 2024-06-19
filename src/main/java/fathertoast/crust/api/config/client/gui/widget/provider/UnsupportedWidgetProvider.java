package fathertoast.crust.api.config.client.gui.widget.provider;

import fathertoast.crust.api.config.client.gui.widget.CrustConfigFieldList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Supplier;

/**
 * Represents a field that is not allowed to be modified by the in-game editor.
 * <p>
 * Displays a grayed-out button with customizable text.
 */
public class UnsupportedWidgetProvider implements IConfigFieldWidgetProvider {
    
    /** The message to display on the disabled button. */
    private final Component TEXT;
    
    public UnsupportedWidgetProvider() { this( Component.literal( "In-Game Edit NYI" ) ); }
    
    public UnsupportedWidgetProvider( Component text ) { TEXT = text; }
    
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
        Button dummyButton = new Button( 0, 0, VALUE_WIDTH, VALUE_HEIGHT,
                TEXT, ( button ) -> { }, Supplier::get );
        dummyButton.active = false;
        components.add( dummyButton );
    }
}