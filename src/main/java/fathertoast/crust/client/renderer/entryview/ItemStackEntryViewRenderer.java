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
    public void render( @Nullable ItemStack displayValue, GuiGraphics graphics,
                        int widgetX, int widgetY, int mouseX, int mouseY, float partialTick ) {
        if( displayValue == null || displayValue == ItemStack.EMPTY ) return;
        
        final int stackSize = displayValue.getCount();
        final PoseStack stack = graphics.pose();
        
        stack.pushPose();
        stack.translate( 0.0, 0.0, -150.0 );
        
        graphics.renderItem( displayValue, widgetX + 2, widgetY + 2 );
        // Render "sub-widgets" such as durability bar and stack size
        graphics.renderItemDecorations( Minecraft.getInstance().font, displayValue,
                widgetX, widgetY, stackSize > 1 ? String.valueOf( stackSize ) : null );
        
        stack.popPose();
    }
}