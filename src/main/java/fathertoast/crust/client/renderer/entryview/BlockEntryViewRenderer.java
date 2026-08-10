package fathertoast.crust.client.renderer.entryview;

import fathertoast.crust.api.config.client.gui.EntryViewRendererRegistry;
import fathertoast.crust.api.config.client.gui.widget.field.EntryViewWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.List;

/**
 * An entry view renderer implementation that renders a block's default state.
 * <br><br>
 * All this renderer does is fetch a block's default state and calling
 * on Crust's block state entry view renderer to actually do the rendering.
 */
public class BlockEntryViewRenderer implements EntryViewWidget.EntryViewRenderer<Block> {
    /**
     * Called from {@link EntryViewWidget#renderWidget(GuiGraphics, int, int, float)}
     * to render something based on the widget's field's value.
     */
    @Override
    public void render( Block displayValue, GuiGraphics graphics, int widgetX, int widgetY,
                        int mouseX, int mouseY, float partialTick ) {
        try {
            EntryViewRendererRegistry.getRendererOrThrow( EntryViewRendererRegistry.BLOCK_STATE ).render(
                    displayValue.defaultBlockState(), graphics, widgetX, widgetY, mouseX, mouseY, partialTick );
        }
        catch( Exception e ) {
            // noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
    
    /** Called when the display value is changed to populate the widget's tooltip. */
    @Override
    public void updateTooltip( List<FormattedCharSequence> tooltip, Block displayValue ) {
        if( !displayValue.defaultBlockState().isAir() ) {
            Item item = displayValue.asItem();
            if( item == Items.AIR ) {
                // Block has no item, simply display the block name
                addLine( tooltip, displayValue.getName() );
            }
            else {
                EntryViewRendererRegistry.getRendererOrThrow( EntryViewRendererRegistry.ITEM_STACK )
                        .updateTooltip( tooltip, new ItemStack( displayValue ) );
            }
        }
    }
}