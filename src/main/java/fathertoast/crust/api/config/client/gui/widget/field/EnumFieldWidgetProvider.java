package fathertoast.crust.api.config.client.gui.widget.field;

import fathertoast.crust.api.config.client.gui.widget.CrustConfigFieldList;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.EnumField;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;
import java.util.Locale;

/**
 * Displays a button that opens a dropdown menu for an enum value.
 */
public class EnumFieldWidgetProvider<T extends Enum<T>> implements IConfigFieldWidgetProvider {
    
    /** The providing field. */
    protected final EnumField<T> FIELD;
    
    public EnumFieldWidgetProvider( EnumField<T> field ) { FIELD = field; }
    
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
        Button dropdownButton = new Button( 0, 0, VALUE_WIDTH, VALUE_HEIGHT,
                rawToText( displayValue ), ( button ) -> openDropdownMenu( button, listEntry ) );
        
        components.add( dropdownButton );
    }
    
    /** Called when the button is pressed to open a dropdown menu popup. */
    protected void openDropdownMenu( Button openingButton, CrustConfigFieldList.FieldEntry listEntry ) {
        Button testbutton = new Button( openingButton.x, openingButton.y + openingButton.getHeight(),
                openingButton.getWidth(), openingButton.getHeight(),
                rawToText( listEntry.getValue() ), ( button ) -> { listEntry.setPopupWidget( null ); } );
        
        listEntry.setPopupWidget( testbutton );
    }
    
    /** Converts the enum into its display string. Assumes the enum's declared name is in UPPER_UNDERSCORE format. */
    protected String toReadable( T value ) {
        return ConfigUtil.properCase( value.name().toLowerCase( Locale.ROOT ).replace( '_', ' ' ) );
    }
    
    /** Converts the enum into its display text component. Assumes the enum's declared name is in UPPER_UNDERSCORE format. */
    protected ITextComponent toText( T value ) { return new StringTextComponent( toReadable( value ) ); }
    
    /** Converts the enum into its display text component. Assumes the enum's declared name is in UPPER_UNDERSCORE format. */
    protected ITextComponent rawToText( Object value ) {
        try {
            //noinspection unchecked
            return toText( (T) value );
        }
        catch( ClassCastException ex ) {
            return StringTextComponent.EMPTY;
        }
    }
}