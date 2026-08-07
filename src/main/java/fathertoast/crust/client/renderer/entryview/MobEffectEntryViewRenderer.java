package fathertoast.crust.client.renderer.entryview;

import fathertoast.crust.api.config.client.gui.widget.field.EntryViewWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.Nullable;

/**
 * An entry view renderer implementation that renders the icon of a mob effect.
 */
public class MobEffectEntryViewRenderer implements EntryViewWidget.EntryViewRenderer<MobEffect> {
    /**
     * Called from {@link EntryViewWidget#renderWidget(GuiGraphics, int, int, float)}
     * to render something based on the widget's field's value.
     */
    @Override
    public void render( @Nullable MobEffect displayValue, GuiGraphics graphics,
                        int widgetX, int widgetY, int mouseX, int mouseY, float partialTick ) {
        if( displayValue == null ) return;
        
        final TextureAtlasSprite sprite = Minecraft.getInstance().getMobEffectTextures().get( displayValue );
        
        graphics.pose().pushPose();
        graphics.blit( widgetX + 2, widgetY + 2, 0, 16, 16, sprite );
        graphics.pose().popPose();
    }
}