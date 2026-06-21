package fathertoast.crust.api.config.client.gui.widget.provider;

import fathertoast.crust.api.config.client.gui.widget.CrustConfigFieldList;
import fathertoast.crust.api.config.client.gui.widget.field.ItemViewWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Displays a text box with an icon to the right of it that renders
 * something based on the current value of the text box.
 *
 * @param <V> The type of value the field provides.
 */
public abstract class ItemViewWidgetProvider<V> implements IConfigFieldWidgetProvider {
    
    /** The providing field. */
    protected final Supplier<V> VALUE_SUPPLIER;
    /** An optional line validator. */
    @Nullable
    protected final Predicate<String> VALIDATOR;
    
    
    /**
     * Constructs a new instance of this widget provider
     * with the specified field, and optionally a line validator.
     *
     * @param valueSupplier A supplier providing the value to display. Usually a config field.
     * @param lineValidator An optional line validator for the text box provided by this provider.
     */
    public ItemViewWidgetProvider( Supplier<V> valueSupplier, @Nullable Predicate<String> lineValidator ) {
        VALUE_SUPPLIER = valueSupplier;
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
    public void apply( List<AbstractWidget> components, CrustConfigFieldList.FieldEntry listEntry, Object displayValue ) {
        ItemViewWidget<V> itemViewWidget = new ItemViewWidget<>( this::renderItem, VALUE_SUPPLIER, VALUE_WIDTH - ItemViewWidget.DEFAULT_SIZE, 0 );
        
        // noinspection resource
        EditBox editBox = new EditBox( listEntry.minecraft().font,
                1, 1, VALUE_WIDTH - 3 - ItemViewWidget.DEFAULT_SIZE, VALUE_HEIGHT - 2, // Account for ~1px frame
                Component.literal( "" ) );
        editBox.setMaxLength( Integer.MAX_VALUE );
        
        editBox.setValue( displayValue.toString() );
        editBox.setResponder( listEntry::updateValue );
        
        if( VALIDATOR != null ) {
            editBox.setResponder( ( value ) -> {
                if( value == null || !VALIDATOR.test( value ) ) {
                    editBox.setTextColor( INVALID_COLOR );
                    listEntry.clearValue();
                }
                else {
                    editBox.setTextColor( DEFAULT_COLOR );
                    listEntry.updateValue( value );
                }
            } );
        }
        else {
            editBox.setResponder( listEntry::updateValue );
        }
        
        components.add( itemViewWidget );
        components.add( editBox );
    }
    
    /**
     * Allows rendering something based on the
     * current value of this provider's field.
     */
    public abstract void renderItem( ItemViewWidget.RenderContext<V> renderContext );
    
    
    /**
     * Simple implementation that allows passing an
     * {@link fathertoast.crust.api.config.client.gui.widget.field.ItemViewWidget.ItemViewRenderer}
     * instance in the constructor instead of having to override
     * {@link ItemViewWidgetProvider#renderItem(ItemViewWidget.RenderContext)}.
     */
    public static class Simple<V> extends ItemViewWidgetProvider<V> {
        
        /** The item renderer used to render a config value. */
        private final ItemViewWidget.ItemViewRenderer<V> RENDERER;
        
        /**
         * Constructs a new instance of this widget provider
         * with the specified field, and optionally a line validator.
         *
         * @param field         The field to provide widgets for.
         * @param lineValidator An optional line validator for the text box provided by this provider.
         */
        public Simple( Supplier<V> field, ItemViewWidget.ItemViewRenderer<V> renderer, @Nullable Predicate<String> lineValidator ) {
            super( field, lineValidator );
            RENDERER = Objects.requireNonNull( renderer );
        }
        
        @Override
        public void renderItem( ItemViewWidget.RenderContext<V> renderContext ) {
            RENDERER.render( renderContext );
        }
    }
}