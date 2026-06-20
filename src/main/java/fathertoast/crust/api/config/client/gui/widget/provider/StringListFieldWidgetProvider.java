package fathertoast.crust.api.config.client.gui.widget.provider;

import fathertoast.crust.api.config.client.gui.screen.CrustConfigFileScreen;
import fathertoast.crust.api.config.client.gui.screen.EditStringListScreen;
import fathertoast.crust.api.config.client.gui.widget.CrustConfigFieldList;
import fathertoast.crust.api.config.common.field.GenericField;
import fathertoast.crust.api.config.common.field.IStringListScreenEditable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

/**
 * Provides a button to open a new screen entirely for
 * editing strings in a string list.
 */
@SuppressWarnings( "ClassCanBeRecord" )
public class StringListFieldWidgetProvider<V, T extends GenericField<V> & IStringListScreenEditable> implements
        IConfigFieldWidgetProvider {
    
    /** The providing field. */
    protected final T FIELD;
    
    public StringListFieldWidgetProvider( T field ) { FIELD = field; }
    
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
    public void apply( List<AbstractWidget> components, CrustConfigFieldList.FieldEntry listEntry, Object displayValue ) {
        MutableComponent component = Component.translatable( "menu.crust.config.edit" );
        
        Button editButton = new Button( 0, 0, VALUE_WIDTH, VALUE_HEIGHT,
                component,
                ( button ) -> {
                    if( Minecraft.getInstance().screen instanceof CrustConfigFileScreen screen ) {
                        Screen editScreen = new EditStringListScreen<>( screen, listEntry,
                                FIELD.get(),//TODO use displayValue instead once we fix screen transition
                                FIELD );
                        Minecraft.getInstance().setScreen( editScreen );
                    }
                },
                ( narratable ) -> component
        );
        components.add( editButton );
    }
}