package fathertoast.crust.client.renderer.entryview;


import com.mojang.blaze3d.vertex.PoseStack;
import fathertoast.crust.api.config.client.gui.widget.field.EntryViewWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * An entry view renderer implementation that renders an item stack,
 * including sub-widgets like the durability bar and stack size number.
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
        
        final int stackSize = itemStack.getCount();
        final PoseStack stack = graphics.pose();
        
        stack.pushPose();
        stack.translate( 0.0D, 0.0D, -150.0D );
        
        graphics.renderItem( itemStack, widgetX + 2, widgetY + 2 );
        // Render "sub-widgets" such as durability bar and stack size
        graphics.renderItemDecorations( Minecraft.getInstance().font, itemStack, widgetX, widgetY, stackSize > 1 ? String.valueOf( stackSize ) : null );
        
        stack.popPose();
    }
}
