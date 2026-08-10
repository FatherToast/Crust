package fathertoast.crust.client.renderer.entryview;

import fathertoast.crust.api.config.client.gui.widget.field.EntryViewWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * An entry view renderer implementation that renders the icon of a mob effect.
 */
public class MobEffectEntryViewRenderer implements EntryViewWidget.EntryViewRenderer<MobEffect> {
    /**
     * Called from {@link EntryViewWidget#renderWidget(GuiGraphics, int, int, float)}
     * to render something based on the widget's field's value.
     */
    @Override
    public void render( MobEffect displayValue, GuiGraphics graphics,
                        int widgetX, int widgetY, int mouseX, int mouseY, float partialTick ) {
        final TextureAtlasSprite sprite = Minecraft.getInstance().getMobEffectTextures().get( displayValue );
        
        graphics.pose().pushPose();
        graphics.blit( widgetX + 2, widgetY + 2, 0, 16, 16, sprite );
        graphics.pose().popPose();
    }
    
    /** Called when the display value is changed to populate the widget's tooltip. */
    @Override
    public void updateTooltip( List<FormattedCharSequence> tooltip, MobEffect displayValue ) {
        addLine( tooltip, Component.translatable( displayValue.getDescriptionId() )
                .withStyle( displayValue.getCategory().getTooltipFormatting() ) );
        
        Map<Attribute, AttributeModifier> attributeModifiers = displayValue.getAttributeModifiers();
        if( !attributeModifiers.isEmpty() ) {
            tooltip.add( FormattedCharSequence.EMPTY );
            addLine( tooltip, Component.translatable( "potion.whenDrank" ).withStyle( ChatFormatting.DARK_PURPLE ) );
            
            attributeModifiers.forEach( ( attribute, modifier ) -> {
                double modifierValue = displayValue.getAttributeModifierValue( 0, modifier );
                AttributeModifier.Operation operation = modifier.getOperation();
                double displayNumber = operation == AttributeModifier.Operation.ADDITION ? modifierValue :
                        modifierValue * 100.0; // Show as percent
                
                if( modifierValue > 0.0 ) {
                    addLine( tooltip, Component.translatable( "attribute.modifier.plus." + operation.toValue(),
                            ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format( displayNumber ),
                            Component.translatable( attribute.getDescriptionId() ) ).withStyle( ChatFormatting.BLUE ) );
                }
                else if( modifierValue < 0.0 ) {
                    addLine( tooltip, Component.translatable( "attribute.modifier.take." + operation.toValue(),
                            ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format( -displayNumber ),
                            Component.translatable( attribute.getDescriptionId() ) ).withStyle( ChatFormatting.RED ) );
                }
            } );
        }
    }
}