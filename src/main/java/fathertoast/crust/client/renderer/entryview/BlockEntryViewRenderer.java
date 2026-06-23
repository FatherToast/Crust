package fathertoast.crust.client.renderer.entryview;

import fathertoast.crust.api.config.client.gui.EntryViewRendererRegistry;
import fathertoast.crust.api.config.client.gui.widget.field.EntryViewWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * An entry view renderer implementation that renders a block's default state.
 * <br><br>
 * All this renderer does is fetch a block's default state and calling
 * on Crust's block state entry view renderer to actually do the rendering.
 */
public class BlockEntryViewRenderer implements EntryViewWidget.EntryViewRenderer<Block> {
    
    @Override
    public void render( @Nullable Supplier<Block> valueSupplier, GuiGraphics graphics, int widgetX, int widgetY,
                        int mouseX, int mouseY, float partialTick ) {
        final Block block = getValue( valueSupplier );
        
        if( block == null ) return;
        
        try {
            EntryViewRendererRegistry.getRendererOrThrow( EntryViewRendererRegistry.BLOCK_STATE ).render(
                    block::defaultBlockState, graphics, widgetX, widgetY, mouseX, mouseY, partialTick
            );
        }
        catch( Exception e ) {
            // noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
