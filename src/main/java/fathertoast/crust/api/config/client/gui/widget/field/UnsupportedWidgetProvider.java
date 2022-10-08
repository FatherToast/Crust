package fathertoast.crust.api.config.client.gui.widget.field;

import fathertoast.crust.api.config.client.gui.widget.CrustConfigFieldList;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

/**
 * Represents a field that is not allowed to be modified by the in-game editor.
 * <p>
 * Displays a grayed-out button with customizable text.
 */
public class UnsupportedWidgetProvider implements IConfigFieldWidgetProvider {
    
    /** The message to display on the disabled button. */
    private final ITextComponent TEXT;
    
    public UnsupportedWidgetProvider() { this( new StringTextComponent( "In-Game Edit NYI" ) ); }
    
    public UnsupportedWidgetProvider( ITextComponent text ) { TEXT = text; }
    
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
        Button dummyButton = new Button( 0, 0, VALUE_WIDTH, VALUE_HEIGHT,
                TEXT, ( button ) -> { } );
        dummyButton.active = false;
        components.add( dummyButton );
    }
}