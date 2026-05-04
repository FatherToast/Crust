package fathertoast.crust.api.config.client.gui.widget.provider;

import fathertoast.crust.api.config.client.gui.screen.ColorPickerScreen;
import fathertoast.crust.api.config.client.gui.screen.CrustConfigFileScreen;
import fathertoast.crust.api.config.client.gui.widget.CrustConfigFieldList;
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
public class ColorFieldWidgetProvider implements IConfigFieldWidgetProvider {
    
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
    public void apply( List<AbstractWidget> components, CrustConfigFieldList.FieldEntry listEntry, Object displayValue ) {
        ColorPreviewWidget previewWidget = new ColorPreviewWidget( VALUE_WIDTH - ColorPreviewWidget.DEFAULT_SIZE, 0 );
        
        Number startValue = TomlHelper.asNumber( displayValue );
        previewWidget.setColor( startValue == null ? 0 : startValue.intValue(), FIELD.usesAlpha() );
        
        // TODO - Add back once the color picker screen is done
        /*
        Button openPickerButton = new Button( VALUE_WIDTH - ColorPreviewWidget.DEFAULT_SIZE - 20, 0, 20, 20,
                Component.literal( ":)" ), ( button ) -> openColorPickerScreen( listEntry ), Supplier::get );
        
        // noinspection resource
        EditBox editBox = new EditBox( listEntry.minecraft().font, 1, 1,
                VALUE_WIDTH - 3 - ColorPreviewWidget.DEFAULT_SIZE - openPickerButton.getWidth(), VALUE_HEIGHT - 2, // Account for 1px frame
                Component.literal( FIELD.getKey() ) );
        editBox.setMaxLength( FIELD.getMinDigits() );
         */
        
        // noinspection resource
        EditBox editBox = new EditBox( listEntry.minecraft().font, 1, 1,
                VALUE_WIDTH - 3 - ColorPreviewWidget.DEFAULT_SIZE, VALUE_HEIGHT - 2, // Account for 1px frame
                Component.literal( FIELD.getKey() ) );
        editBox.setMaxLength( FIELD.getMinDigits() );
        
        editBox.setValue( startValue == null ? "" :
                TomlHelper.toLiteral( FIELD.wrap( startValue.intValue() ) ).substring( 2 ) );
        
        editBox.setResponder( ( value ) -> {
            Integer newValue = TomlHelper.parseHexInt( value );
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
        
        components.add( previewWidget );
        //components.add( openPickerButton );
        components.add( editBox );
    }
    
    // TODO - Use when the color picker screen is done
    
    /** Opens a color picker screen when the "open color picker" button is pressed. */
    @SuppressWarnings( "unused" )
    private void openColorPickerScreen( CrustConfigFieldList.FieldEntry listEntry ) {
        if( Minecraft.getInstance().screen instanceof CrustConfigFileScreen screen ) {
            Screen editScreen = new ColorPickerScreen( screen, listEntry, FIELD );
            Minecraft.getInstance().setScreen( editScreen );
        }
    }
    
    /** Returns true when the input number is valid. */
    public static boolean isValid( ColorIntField field, Integer value ) { return field.usesAlpha() || 0 <= value && value <= 0xFFFFFF; }
}