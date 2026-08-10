package fathertoast.crust.api.config.client.gui.widget.provider;

import fathertoast.crust.api.config.client.gui.widget.entry.ConfigFieldGuiEntry;
import fathertoast.crust.api.config.common.file.TomlHelper;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Displays a text box for a field that can be serialized to and from a single-line string.
 */
public class StringFieldWidgetProvider<T> implements IConfigFieldWidgetProvider<T> {
    
    Function<T, String> WRITER;
    @Nullable
    protected final Predicate<String> VALIDATOR;
    
    public StringFieldWidgetProvider( @Nullable Predicate<String> validator ) { this( null, validator ); }
    
    public StringFieldWidgetProvider( @Nullable Function<T, String> writer, @Nullable Predicate<String> validator ) {
        WRITER = writer == null ? TomlHelper::toTomlString : writer;
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
                Component.literal( listEntry.getField().getKey() ) );
        editBox.setMaxLength( Integer.MAX_VALUE );
        editBox.setValue( TomlHelper.toTomlString( displayValue ) );
        editBox.setResponder( VALIDATOR == null ? listEntry::updateInput :
                text -> {
                    if( text == null || !VALIDATOR.test( text ) ) {
                        editBox.setTextColor( INVALID_COLOR );
                        listEntry.clearValue();
                    }
                    else {
                        editBox.setTextColor( DEFAULT_COLOR );
                        listEntry.updateInput( text );
                    }
                } );
        editBox.active = listEntry.isEditable();
        components.add( editBox );
    }
}