package fathertoast.crust.client.renderer.entryview;

import fathertoast.crust.api.config.client.gui.widget.field.EntryViewWidget;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.Nullable;

/** An entry view renderer implementation that doesn't render anything. */
public class EmptyEntryViewRenderer implements EntryViewWidget.EntryViewRenderer<Object> {
    /**
     * Called from {@link EntryViewWidget#renderWidget(GuiGraphics, int, int, float)}
     * to render something based on the widget's field's value.
     */
    @Override
    public void render( @Nullable Object valueSupplier, GuiGraphics graphics,
                        int widgetX, int widgetY, int mouseX, int mouseY, float partialTick ) {
        // NOOP
    }
}