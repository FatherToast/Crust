package fathertoast.crust.client.renderer.entryview;


import com.mojang.blaze3d.vertex.PoseStack;
import fathertoast.crust.api.config.client.gui.widget.field.EntryViewWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import java.util.List;

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
    public void render( ItemStack displayValue, GuiGraphics graphics,
                        int widgetX, int widgetY, int mouseX, int mouseY, float partialTick ) {
        if( displayValue == ItemStack.EMPTY ) return;
        
        final int stackSize = displayValue.getCount();
        final PoseStack stack = graphics.pose();
        
        stack.pushPose();
        
        graphics.renderItem( displayValue, widgetX + 2, widgetY + 2, 0, -100 );
        // Render "sub-widgets" such as durability bar and stack size
        graphics.renderItemDecorations( Minecraft.getInstance().font, displayValue,
                widgetX, widgetY, stackSize > 1 ? String.valueOf( stackSize ) : null );
        
        stack.popPose();
    }
    
    /** Called when the display value is changed to populate the widget's tooltip. */
    @Override
    public void updateTooltip( List<FormattedCharSequence> tooltip, ItemStack displayValue ) {
        if( !displayValue.isEmpty() ) {
            for( Component line : Screen.getTooltipFromItem( Minecraft.getInstance(), displayValue ) ) {
                addLine( tooltip, line );
            }
        }
    }
}