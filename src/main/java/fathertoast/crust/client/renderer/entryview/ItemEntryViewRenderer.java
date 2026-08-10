package fathertoast.crust.client.renderer.entryview;

import fathertoast.crust.api.config.client.gui.EntryViewRendererRegistry;
import fathertoast.crust.api.config.client.gui.widget.field.EntryViewWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * An entry view renderer implementation that renders a flat item.
 * <br><br>
 * All this renderer does is create an item stack from the item value and calling
 * on Crust's item stack entry view renderer to actually do the rendering.
 */
public class ItemEntryViewRenderer implements EntryViewWidget.EntryViewRenderer<Item> {
    /**
     * Called from {@link EntryViewWidget#renderWidget(GuiGraphics, int, int, float)}
     * to render something based on the widget's field's value.
     */
    @Override
    public void render( Item displayValue, GuiGraphics graphics, int widgetX, int widgetY,
                        int mouseX, int mouseY, float partialTick ) {
        try {
            if( displayValue instanceof BlockItem blockItem ) {
                EntryViewRendererRegistry.getRendererOrThrow( EntryViewRendererRegistry.BLOCK_STATE )
                        .render( blockItem.getBlock().defaultBlockState(), graphics, widgetX, widgetY,
                                mouseX, mouseY, partialTick );
            }
            else {
                EntryViewRendererRegistry.getRendererOrThrow( EntryViewRendererRegistry.ITEM_STACK )
                        .render( new ItemStack( displayValue ), graphics, widgetX, widgetY,
                                mouseX, mouseY, partialTick );
            }
        }
        catch( Exception e ) {
            // noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
    
    /** Called when the display value is changed to populate the widget's tooltip. */
    @Override
    public void updateTooltip( List<FormattedCharSequence> tooltip, Item displayValue ) {
        EntryViewRendererRegistry.getRendererOrThrow( EntryViewRendererRegistry.ITEM_STACK )
                .updateTooltip( tooltip, new ItemStack( displayValue ) );
    }
}