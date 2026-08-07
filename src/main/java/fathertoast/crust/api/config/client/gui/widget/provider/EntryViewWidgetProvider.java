package fathertoast.crust.api.config.client.gui.widget.provider;

import fathertoast.crust.api.config.client.gui.widget.entry.ConfigFieldGuiEntry;
import fathertoast.crust.api.config.client.gui.widget.field.EntryViewWidget;
import fathertoast.crust.api.config.common.file.TomlHelper;
import net.minecraft.client.gui.GuiGraphics;
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
 */
public abstract class EntryViewWidgetProvider<T> implements IConfigFieldWidgetProvider<T> {
    
    /** An optional line validator. */
    @Nullable
    protected final Predicate<String> VALIDATOR;
    
    /**
     * Constructs a new instance of this widget provider with the specified supplier and an optional line validator.
     *
     * @param lineValidator An optional line validator for the text box provided by this provider.
     */
    public EntryViewWidgetProvider( @Nullable Predicate<String> lineValidator ) {
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
        EntryViewWidget<T> entryViewWidget = new EntryViewWidget<>( this::renderEntry, displayValue,
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
                        listEntry.clearValue();
                    }
                    else {
                        editBox.setTextColor( DEFAULT_COLOR );
                        listEntry.updateInput( text );
                    }
                    entryViewWidget.updateDisplay( listEntry.getValue() );
                } );
        editBox.active = listEntry.isEditable();
        
        components.add( entryViewWidget );
        components.add( editBox );
    }
    
    /**
     * Allows rendering something based on the
     * current value of this provider's field.
     */
    public abstract void renderEntry( @Nullable T displayValue, GuiGraphics graphics,
                                      int widgetX, int widgetY, int mouseX, int mouseY, float partialTick );
    
    
    /**
     * Simple implementation that allows passing an {@link EntryViewWidget.EntryViewRenderer}
     * instance in the constructor instead of having to override
     * {@link EntryViewWidgetProvider#renderEntry(Object, GuiGraphics, int, int, int, int, float)}.
     *
     * @param <T> The type of value the field provides.
     */
    public static class Simple<T> extends EntryViewWidgetProvider<T> {
        
        /** The entry renderer used to render a config value. */
        private final EntryViewWidget.EntryViewRenderer<T> RENDERER;
        
        /**
         * Constructs a new instance of this widget provider with the specified supplier and an optional line validator.
         *
         * @param renderer      The entry view renderer we use.
         * @param lineValidator An optional line validator for the text box provided by this provider.
         */
        public Simple( EntryViewWidget.EntryViewRenderer<T> renderer, @Nullable Predicate<String> lineValidator ) {
            super( lineValidator );
            RENDERER = renderer;
        }
        
        @Override
        public void renderEntry( @Nullable T displayValue, GuiGraphics graphics,
                                 int widgetX, int widgetY, int mouseX, int mouseY, float partialTick ) {
            RENDERER.render( displayValue, graphics, widgetX, widgetY, mouseX, mouseY, partialTick );
        }
    }
    
    /**
     * Simple implementation that allows passing an {@link EntryViewWidget.EntryViewRenderer}
     * instance in the constructor and a mapping function instead of having to override
     * {@link EntryViewWidgetProvider#renderEntry(Object, GuiGraphics, int, int, int, int, float)}.
     *
     * @param <T> The type of value the field provides.
     * @param <V> The renderer type.
     */
    public static class SimpleMapped<T, V> extends EntryViewWidgetProvider<T> {
        
        /** The providing field. */
        protected final Function<T, V> VALUE_MAPPER;
        /** The entry renderer used to render a config value. */
        private final EntryViewWidget.EntryViewRenderer<V> RENDERER;
        
        /**
         * Constructs a new instance of this widget provider with the specified supplier and an optional line validator.
         *
         * @param valueMapper   Maps a field value to the thing we need to render.
         * @param renderer      The entry view renderer we use.
         * @param lineValidator An optional line validator for the text box provided by this provider.
         */
        public SimpleMapped( Function<T, V> valueMapper, EntryViewWidget.EntryViewRenderer<V> renderer, @Nullable Predicate<String> lineValidator ) {
            super( lineValidator );
            VALUE_MAPPER = valueMapper;
            RENDERER = renderer;
        }
        
        @Override
        public void renderEntry( @Nullable T displayValue, GuiGraphics graphics,
                                 int widgetX, int widgetY, int mouseX, int mouseY, float partialTick ) {
            if( displayValue == null ) return;
            RENDERER.render( VALUE_MAPPER.apply( displayValue ), graphics,
                    widgetX, widgetY, mouseX, mouseY, partialTick );
        }
    }
}