package fathertoast.crust.api.config.client.gui.widget.provider;

import fathertoast.crust.api.config.client.gui.widget.CrustConfigFieldList;
import fathertoast.crust.api.config.client.gui.widget.field.EntryViewWidget;
import fathertoast.crust.api.config.client.gui.widget.field.SoundPlayerWidget;
import fathertoast.crust.api.config.common.field.RegObjectField;
import fathertoast.crust.api.config.common.value.collection.value.SoundInstanceStats;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Displays a text box with a button to the right of it that plays
 * a sound event depending on the value of the text box. This
 * is primarily used
 */
public class SoundPlayerWidgetProvider implements IConfigFieldWidgetProvider {
    
    /** A supplier that provides the sound player widget with sound data. */
    @Nullable
    protected final Supplier<SoundInstanceStats> VALUE_SUPPLIER;
    /** An optional line validator. */
    @Nullable
    protected final Predicate<String> VALIDATOR;
    
    
    /**
     * Constructs a new instance of this widget provider
     * with the specified value supplier, and optionally a line validator.
     *
     * @param valueSupplier A supplier providing the value to display. Usually a config field.
     * @param lineValidator An optional line validator for the text box provided by this provider.
     */
    public SoundPlayerWidgetProvider( @Nullable Supplier<SoundInstanceStats> valueSupplier, @Nullable Predicate<String> lineValidator ) {
        VALUE_SUPPLIER = valueSupplier;
        VALIDATOR = lineValidator;
    }
    
    
    /** Helper method for creating a new instance for a sound event registry object field. */
    public static SoundPlayerWidgetProvider ofField( RegObjectField<SoundEvent> field ) {
        // noinspection ConstantConditions
        return new SoundPlayerWidgetProvider( null, field.lineValidator() );
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
        final SoundPlayerWidget soundPlayer = new SoundPlayerWidget( displayValue.toString(), VALUE_WIDTH - EntryViewWidget.DEFAULT_SIZE, 0, 20, 20 );
        
        // noinspection resource
        EditBox editBox = new EditBox( listEntry.minecraft().font,
                1, 1, VALUE_WIDTH - 3 - EntryViewWidget.DEFAULT_SIZE, VALUE_HEIGHT - 2, // Account for ~1px frame
                Component.literal( "" ) );
        editBox.setMaxLength( Integer.MAX_VALUE );
        
        editBox.setValue( displayValue.toString() );
        editBox.setResponder( listEntry::updateValue );
        
        if( VALIDATOR != null ) {
            editBox.setResponder( ( value ) -> {
                if( value == null || !VALIDATOR.test( value ) ) {
                    editBox.setTextColor( INVALID_COLOR );
                    listEntry.clearValue();
                    // Update sound player widget
                    soundPlayer.setSound( null );
                }
                else {
                    editBox.setTextColor( DEFAULT_COLOR );
                    listEntry.updateValue( value );
                    // Update sound player widget
                    soundPlayer.setSoundFromId( value );
                }
            } );
        }
        else {
            editBox.setResponder( listEntry::updateValue );
        }
        components.add( soundPlayer );
        components.add( editBox );
    }
}
