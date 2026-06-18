package fathertoast.crust.api.config.client.gui.widget.provider;

import javax.annotation.Nullable;

/** Should be implemented by fields that utilize {@link ItemViewWidgetProvider}. */
public interface IItemViewable {
    
    /**
     * @return The implementing field's current raw value as a string.
     * This is used to set the value of the widget's edit box.
     */
    @Nullable
    String asViewedString( Object raw );
}
