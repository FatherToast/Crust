package fathertoast.crust.api.config.client.gui.widget.provider;

import fathertoast.crust.api.config.client.gui.widget.entry.ConfigFieldGuiEntry;
import fathertoast.crust.api.config.common.field.IConfigField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Function;

/**
 * Displays a text box for a field that can be serialized to and from a numeric string.
 */
@SuppressWarnings( "ClassCanBeRecord" )
public class NumberFieldWidgetProvider<T extends Number> implements IConfigFieldWidgetProvider<T> {
    
    /** The providing field. */
    protected final IConfigField<T> FIELD;
    /** Converts the input number into the desired type. */
    protected final Function<Number, T> READER;
    /** Returns true when the input number is valid. */
    protected final Function<Number, Boolean> VALIDATOR;
    
    public NumberFieldWidgetProvider( IConfigField<T> field, Function<Number, T> reader, Function<Number, Boolean> validator ) {
        FIELD = field;
        READER = reader;
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
    public void apply( List<AbstractWidget> components, ConfigFieldGuiEntry<T> listEntry, T displayValue ) {
        EditBox editBox = new EditBox( listEntry.client().font,
                1, 1, VALUE_WIDTH - 2, VALUE_HEIGHT - 2, // Account for ~1px frame
                Component.literal( FIELD.getKey() ) );
        editBox.setMaxLength( 127 );
        editBox.setValue( TomlHelper.toLiteral( displayValue ) );
        editBox.setResponder( text -> {
            Number newValue = TomlHelper.parseNumber( text );
            if( newValue == null || !VALIDATOR.apply( newValue ) ) {
                editBox.setTextColor( INVALID_COLOR );
                listEntry.clearValue();
            }
            else {
                editBox.setTextColor( DEFAULT_COLOR );
                listEntry.updateValue( READER.apply( newValue ) );
            }
        } );
        editBox.active = listEntry.isEditable();
        components.add( editBox );
    }
}