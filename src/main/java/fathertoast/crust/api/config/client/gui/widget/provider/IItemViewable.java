package fathertoast.crust.api.config.client.gui.widget.provider;

import javax.annotation.Nullable;

/** Should be implemented by fields that utilize {@link ItemViewWidgetProvider}. */
public interface IItemViewable {
    
    /**
     * @param displayValue The current raw value of the field.
     * @return A string representing the field's current raw value for GUI display.
     */
    @Nullable
    String asViewedString( Object displayValue );
}
