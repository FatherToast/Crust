package fathertoast.crust.api.config.client.gui.widget.provider;

import fathertoast.crust.api.config.client.gui.widget.entry.ConfigFieldGuiEntry;
import fathertoast.crust.api.config.client.gui.widget.field.ColorPickerPopupWidget;
import fathertoast.crust.api.config.client.gui.widget.field.ColorPreviewWidget;
import fathertoast.crust.api.config.common.field.ColorIntField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Displays a text box for a hexadecimal color value, plus a color preview swatch.
 */
@SuppressWarnings( "ClassCanBeRecord" )
public class ColorFieldWidgetProvider implements IConfigFieldWidgetProvider<Integer> {
    
    /** The providing field. */
    protected final ColorIntField FIELD;
    
    public ColorFieldWidgetProvider( ColorIntField field ) { FIELD = field; }
    
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
    public void apply( List<AbstractWidget> components, ConfigFieldGuiEntry<Integer> listEntry, Integer displayValue ) {
        ColorPreviewWidget previewWidget = new ColorPreviewWidget( VALUE_WIDTH - ColorPreviewWidget.DEFAULT_SIZE, 0 );
        previewWidget.setColor( displayValue, FIELD.usesAlpha() );
        
        // TODO - Add back once the color picker popup is done
        /*
        Button openPickerButton = new Button( VALUE_WIDTH - ColorPreviewWidget.DEFAULT_SIZE - 20, 0, 20, 20,
                Component.literal( ":)" ), ( button ) -> openColorPickerScreen( listEntry ), Supplier::get );
        
        // noinspection resource
        EditBox editBox = new EditBox( listEntry.minecraft().font, 1, 1,
                VALUE_WIDTH - 3 - ColorPreviewWidget.DEFAULT_SIZE - openPickerButton.getWidth(), VALUE_HEIGHT - 2, // Account for 1px frame
                Component.literal( FIELD.getKey() ) );
        editBox.setMaxLength( FIELD.getMinDigits() );
         */
        
        EditBox editBox = new EditBox( listEntry.client().font, 1, 1,
                VALUE_WIDTH - 3 - ColorPreviewWidget.DEFAULT_SIZE, VALUE_HEIGHT - 2, // Account for 1px frame
                Component.literal( FIELD.getKey() ) );
        editBox.setMaxLength( FIELD.getMinDigits() );
        editBox.setValue( TomlHelper.toLiteral( FIELD.wrap( displayValue ) ).substring( 2 ) );
        editBox.setResponder( text -> {
            Integer newValue = TomlHelper.parseHexInt( text );
            if( newValue == null || !isValid( FIELD, newValue ) ) {
                previewWidget.setColor( 0, true );
                editBox.setTextColor( INVALID_COLOR );
                listEntry.clearValue();
            }
            else {
                previewWidget.setColor( newValue, FIELD.usesAlpha() );
                editBox.setTextColor( DEFAULT_COLOR );
                listEntry.updateValue( newValue );
            }
        } );
        editBox.active = listEntry.isEditable();
        
        components.add( previewWidget );
        //components.add( openPickerButton );
        components.add( editBox );
    }
    
    // TODO - Use when the color picker popup is done
    
    /** Opens a color picker screen when the "open color picker" button is pressed. */
    private void openColorPickerScreen( ConfigFieldGuiEntry<Integer> listEntry ) {
        Screen editScreen = new ColorPickerPopupWidget( listEntry, FIELD );
        Minecraft.getInstance().setScreen( editScreen );
    }
    
    /** Returns true when the input number is valid. */
    public static boolean isValid( ColorIntField field, Integer value ) { return field.usesAlpha() || 0 <= value && value <= 0xFFFFFF; }
}