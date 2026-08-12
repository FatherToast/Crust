package fathertoast.crust.api.config.client.gui.widget.provider;

import fathertoast.crust.api.config.client.gui.widget.entry.ConfigFieldGuiEntry;
import fathertoast.crust.api.config.client.gui.widget.field.EntryViewWidget;
import fathertoast.crust.api.config.common.file.TomlHelper;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Displays a text box with an icon to the right of it that renders
 * something based on the current value of the text box.
 *
 * @param <T> The type of value the field provides.
 * @param <V> The type of value the renderer displays.
 */
public class EntryViewWidgetProvider<T, V> implements IConfigFieldWidgetProvider<T> {
    
    /** Converts the field's value type to the renderer's value type. */
    protected final Function<T, V> VALUE_MAPPER;
    /** The entry renderer use to render a config value. */
    private final EntryViewWidget.EntryViewRenderer<V> RENDERER;
    /** An optional line validator. */
    @Nullable
    protected final Predicate<String> VALIDATOR;
    
    /**
     * @param valueMapper   Maps a field value to the value we want to render.
     * @param renderer      The renderer we use.
     * @param lineValidator An optional line validator for the text box provided.
     */
    public EntryViewWidgetProvider( Function<T, V> valueMapper, EntryViewWidget.EntryViewRenderer<V> renderer,
                                    @Nullable Predicate<String> lineValidator ) {
        VALUE_MAPPER = valueMapper;
        RENDERER = renderer;
        VALIDATOR = lineValidator;
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
        EntryViewWidget<T, V> entryViewWidget = new EntryViewWidget<>( VALUE_MAPPER, RENDERER, displayValue,
                VALUE_WIDTH - EntryViewWidget.DEFAULT_SIZE, 0 );
        
        EditBox editBox = new EditBox( listEntry.client().font, 1, 1,
                VALUE_WIDTH - 3 - EntryViewWidget.DEFAULT_SIZE, VALUE_HEIGHT - 2, // Account for ~1px frame
                Component.literal( "" ) );
        editBox.setMaxLength( Integer.MAX_VALUE );
        editBox.setValue( TomlHelper.toTomlString( displayValue ) );
        editBox.setResponder( VALIDATOR == null ?
                text -> {
                    listEntry.updateInput( text );
                    entryViewWidget.updateDisplay( listEntry.getValue() );
                } :
                text -> {
                    if( text == null || !VALIDATOR.test( text ) ) {
                        editBox.setTextColor( INVALID_COLOR );
                    }
                    else {
                        editBox.setTextColor( DEFAULT_COLOR );
                        listEntry.updateInput( text );
                        entryViewWidget.updateDisplay( listEntry.getValue() );
                    }
                } );
        editBox.active = listEntry.isEditable();
        
        components.add( entryViewWidget );
        components.add( editBox );
    }
    
    
    /**
     * Simple implementation for field types that exactly match their renderer type.
     *
     * @param <T> The type of value the field provides.
     */
    public static class Simple<T> extends EntryViewWidgetProvider<T, T> {
        /**
         * Constructs a new instance of this widget provider with the specified supplier and an optional line validator.
         *
         * @param renderer      The entry view renderer we use.
         * @param lineValidator An optional line validator for the text box provided by this provider.
         */
        public Simple( EntryViewWidget.EntryViewRenderer<T> renderer, @Nullable Predicate<String> lineValidator ) {
            super( value -> value, renderer, lineValidator );
        }
    }
}