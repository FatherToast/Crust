package fathertoast.crust.api.config.client.gui.widget.field;

import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Optional interface that can be implemented by widgets
 * {@link fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider provided}
 * for config fields, which allows them to specify a tooltip to render when hovered.
 */
public interface ITooltipWidget {
    /** @return The tooltip to render when the mouse is over this widget. Null if no tooltip should render. */
    @Nullable
    default List<FormattedCharSequence> getTooltip( int mouseX, int mouseY ) { return null; }
}