package fathertoast.crust.client.renderer.entryview;

import fathertoast.crust.api.config.client.gui.EntryViewRendererRegistry;
import fathertoast.crust.api.config.client.gui.widget.field.EntryViewWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * An entry view renderer implementation that renders a flat item.
 * <br><br>
 * All this renderer does is create an item stack from the item value and calling
 * on Crust's item stack entry view renderer to actually do the rendering.
 */
public class ItemEntryViewRenderer implements EntryViewWidget.EntryViewRenderer<Item> {
    @Override
    public void render( @Nullable Supplier<Item> valueSupplier, GuiGraphics graphics, int widgetX, int widgetY,
                        int mouseX, int mouseY, float partialTick ) {
        final Item item = getValue( valueSupplier );
        
        if( item == null ) return;
        
        try {
            EntryViewRendererRegistry.getRendererOrThrow( EntryViewRendererRegistry.ITEM_STACK ).render(
                    () -> new ItemStack( item ), graphics, widgetX, widgetY, mouseX, mouseY, partialTick
            );
        }
        catch( Exception e ) {
            // noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
