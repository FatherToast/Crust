package fathertoast.crust.api.config.client.gui.widget.field;

import fathertoast.crust.api.config.client.gui.widget.CrustConfigFieldList;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.EnumField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

/**
 * Displays a button that opens a dropdown menu for an enum value.
 */
public class EnumFieldWidgetProvider<T extends Enum<T>> implements IConfigFieldWidgetProvider {
    
    /** The providing field. */
    protected final EnumField<T> FIELD;
    
    public EnumFieldWidgetProvider( EnumField<T> field ) { FIELD = field; }
    
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
        Button dropdownButton = new Button( 0, 0, VALUE_WIDTH, VALUE_HEIGHT,
                rawToText( displayValue ), ( button ) -> openDropdownMenu( button, listEntry, this ) );
        
        components.add( dropdownButton );
    }
    
    /** Called when the button is pressed to open a dropdown menu popup. */
    protected void openDropdownMenu( Button openingButton, CrustConfigFieldList.FieldEntry listEntry, EnumFieldWidgetProvider<T> provider ) {
        T[] validValues = provider.FIELD.validValues();
        int screenHeight = listEntry.PARENT.getHeight();
        int rowHeight = 20 + PopupListWidget.ENTRY_PADDING;
        int y;
        int height = validValues.length * rowHeight + PopupListWidget.ENTRY_PADDING;
        boolean hasScrollbar = false;
        
        if( height > screenHeight ) {
            // List cannot be displayed without scrolling
            y = 0;
            height = screenHeight;
            hasScrollbar = true;
        }
        else if( openingButton.y + height <= screenHeight ) {
            // Can fit as a traditional dropdown
            y = openingButton.y;
        }
        else if( openingButton.y + openingButton.getHeight() - height >= 0 ) {
            // Can fit when flipped upward
            y = openingButton.y + openingButton.getHeight() - height;
        }
        else if( openingButton.y + openingButton.getHeight() / 2 < screenHeight / 2 ) {
            // Push from the top
            y = 0;
        }
        else {
            // Push from the bottom
            y = screenHeight - height;
        }
        
        PopupListWidget<PopupListWidget.WidgetListEntry> dropdownMenu = new PopupListWidget<>( openingButton.x - 2, y,
                openingButton.getWidth() + 4 + (hasScrollbar ? PopupListWidget.SCROLLBAR_WIDTH + 2 : 0),
                height, rowHeight, new StringTextComponent( provider.FIELD.getKey() ) );
        for( T value : validValues ) {
            Button selectButton = new Button( 0, 0,
                    openingButton.getWidth(), rowHeight - PopupListWidget.ENTRY_PADDING,
                    toText( value, TomlHelper.equals( value, listEntry.getValue() ) ? TextFormatting.GREEN : null ),
                    ( button ) -> {
                        openingButton.setMessage( toText( value ) );
                        listEntry.updateValue( TomlHelper.enumToString( value ) );
                        listEntry.setPopupWidget( null );
                    } );
            
            dropdownMenu.addEntry( new PopupListWidget.WidgetListEntry( selectButton ) );
        }
        
        listEntry.setPopupWidget( dropdownMenu );
    }
    
    /** Converts the enum into its display string. Assumes the enum's declared name is in UPPER_UNDERSCORE format. */
    protected String toReadable( T value ) {
        return ConfigUtil.properCase( value.name().toLowerCase( Locale.ROOT ).replace( '_', ' ' ) );
    }
    
    /** Converts the enum into its display text component. Assumes the enum's declared name is in UPPER_UNDERSCORE format. */
    protected ITextComponent toText( T value ) {
        return new StringTextComponent( toReadable( value ) );
    }
    
    /** Converts the enum into its display text component. Assumes the enum's declared name is in UPPER_UNDERSCORE format. */
    protected ITextComponent toText( T value, @Nullable TextFormatting format ) {
        if( format == null ) return toText( value );
        return new StringTextComponent( toReadable( value ) ).withStyle( format );
    }
    
    /**
     * Converts the raw enum string into its display text component. Assumes the enum string is in lower_underscore format.
     *
     * @see TomlHelper#enumToString(Enum)
     */
    protected ITextComponent rawToText( Object value ) {
        if( value instanceof String ) {
            return new StringTextComponent( ConfigUtil.properCase( ((String) value)
                    .toLowerCase( Locale.ROOT ).replace( '_', ' ' ) ) );
        }
        return StringTextComponent.EMPTY;
    }
}