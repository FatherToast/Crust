package fathertoast.crust.api.config.client.gui.widget.field;

import fathertoast.crust.api.config.client.gui.widget.CrustConfigFieldList;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;
import java.util.function.Function;

/**
 * Displays a text box for a hexadecimal number value.
 */
public class HexIntFieldWidgetProvider implements IConfigFieldWidgetProvider {
    
    /** The providing field. */
    protected final IntField.Hex FIELD;
    /** Returns true when the input number is valid. */
    protected final Function<Integer, Boolean> VALIDATOR;
    
    public HexIntFieldWidgetProvider( IntField.Hex field, Function<Integer, Boolean> validator ) {
        FIELD = field;
        VALIDATOR = validator;
    }
    
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
                1, 1, VALUE_WIDTH - 2, VALUE_HEIGHT - 2, // Account for 1px frame
                new StringTextComponent( FIELD.getKey() ) );
        textWidget.setMaxLength( 127 );
        
        TomlHelper.HEX_MODE = FIELD.getMinDigits();
        textWidget.setValue( TomlHelper.toLiteral( displayValue ).substring( 2 ) );
        TomlHelper.HEX_MODE = 0;
        
        textWidget.setResponder( ( value ) -> {
            Integer newValue = TomlHelper.parseHexInt( value );
            if( newValue == null || !VALIDATOR.apply( newValue ) ) {
                textWidget.setTextColor( INVALID_COLOR );
                listEntry.clearValue();
            }
            else {
                textWidget.setTextColor( DEFAULT_COLOR );
                listEntry.updateValue( newValue );
            }
        } );
        
        components.add( textWidget );
    }
}