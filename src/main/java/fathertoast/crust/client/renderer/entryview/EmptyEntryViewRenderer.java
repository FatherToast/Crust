package fathertoast.crust.client.renderer.entryview;

import fathertoast.crust.api.config.client.gui.widget.field.EntryViewWidget;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/** An entry view renderer implementation that doesn't render anything. */
public class EmptyEntryViewRenderer implements EntryViewWidget.EntryViewRenderer<Object> {
    
    @Override
    public void render( @Nullable Supplier<Object> valueSupplier, GuiGraphics graphics,
                        int widgetX, int widgetY, int mouseX, int mouseY, float partialTick ) {
        // NOOP
    }
}
