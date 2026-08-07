package fathertoast.crust.api.config.client.gui.widget.provider;

import fathertoast.crust.api.config.client.gui.widget.entry.ConfigFieldGuiEntry;
import fathertoast.crust.api.config.client.gui.widget.field.list.PopupStringListWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Displays a button to open a string list editor for a field that can be serialized to and from a string list.
 */
public class StringListFieldWidgetProvider<T> implements IConfigFieldWidgetProvider<T> {
    
    Function<T, List<String>> STRING_LIST_MAPPER;
    @Nullable
    protected final Predicate<String> VALIDATOR;
    
    public StringListFieldWidgetProvider( Function<T, List<String>> stringListMapper, @Nullable Predicate<String> validator ) {
        STRING_LIST_MAPPER = stringListMapper;
        VALIDATOR = validator;
    }
    
    /**
     * Called to initialize the field's gui components.
     * <p>
     * For this field type in particular, we open a new screen
     * entirely.
     *
     * @param components   The list to populate with widgets.
     * @param listEntry    The field component (widget "row" within a scrollable list).
     * @param displayValue The current raw value to display in the GUI.
     */
    @Override
    public void apply( List<AbstractWidget> components, ConfigFieldGuiEntry<T> listEntry, T displayValue ) {
        MutableComponent component = listEntry.isEditable() ? Component.translatable( "menu.crust.config.edit" ) :
                Component.translatable( "menu.crust.config.view" );
        Button editButton = new Button( 0, 0, VALUE_WIDTH, VALUE_HEIGHT,
                component,
                button -> listEntry.setPopupWidget( new PopupStringListWidget<>(
                        listEntry, STRING_LIST_MAPPER.apply( listEntry.getValue() ), VALIDATOR ) ),
                narratable -> component );
        components.add( editButton );
    }
}