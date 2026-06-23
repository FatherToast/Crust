package fathertoast.crust.client.renderer.entryview;


import fathertoast.crust.api.config.client.gui.widget.field.EntryViewWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * An entry view renderer implementation that renders an item stack.
 * If the item stack's stack size is greater than 1, the stack size number
 * will be rendered as text on top of the item stack icon, similar to
 * how it is displayed in GUIs.
 */
public class ItemStackEntryViewRenderer implements EntryViewWidget.EntryViewRenderer<ItemStack> {
    
    /**
     * Called from {@link EntryViewWidget#renderWidget(GuiGraphics, int, int, float)}
     * to render something based on the widget's field's value.
     */
    @Override
    public void render( @Nullable Supplier<ItemStack> valueSupplier, GuiGraphics graphics,
                        int widgetX, int widgetY, int mouseX, int mouseY, float partialTick ) {
        final ItemStack itemStack = getValue( valueSupplier );
        
        if( itemStack == null ) return;
        
        // Params: stack, x, y, seed, depth
        graphics.renderItem( itemStack, widgetX + 2, widgetY + 2, 0, -10000 );
        
        // TODO - Render stack count number
    }
}
