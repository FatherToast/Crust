package fathertoast.crust.client.button;

import com.mojang.blaze3d.systems.RenderSystem;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.lib.CrustMath;
import fathertoast.crust.client.ClientRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.function.Supplier;

public class ExtraInventoryButton extends Button {
    
    public static final ResourceLocation BUTTON_TEXTURE = new ResourceLocation( ICrustApi.MOD_ID, "textures/button.png" );
    public static final ResourceLocation BUTTON_TEXTURE_ON = new ResourceLocation( ICrustApi.MOD_ID, "textures/button_on.png" );
    
    public static final int ICON_SIZE = 9;
    public static final int ICON_BORDER = 3;
    
    public static final int BUTTON_SIZE = ICON_SIZE + 2 * ICON_BORDER;
    
    public static final int BUTTON_PADDING = 1;
    public static final int BUTTON_SPACING = BUTTON_SIZE + BUTTON_PADDING;
    
    public static final int TEXT_X = Mth.ceil( BUTTON_SIZE / 2.0F );
    public static final int TEXT_Y = Mth.ceil( (BUTTON_SIZE - 8) / 2.0F );
    
    
    private final AbstractContainerScreen<?> PARENT;
    private final boolean HAS_RECIPE_BOOK;
    private final ButtonInfo INFO;
    
    public ExtraInventoryButton( AbstractContainerScreen<?> screen, int leftPos, int topPos, ButtonInfo info ) {
        super( leftPos, topPos, BUTTON_SIZE, BUTTON_SIZE,
                Component.literal( info.TEXT ), info.ON_PRESS,
                Supplier::get );
        setTooltip( createButtonTooltip( info.TOOLTIP ) );
        PARENT = screen;
        HAS_RECIPE_BOOK = screen instanceof RecipeUpdateListener;
        INFO = info;
    }
    
    @Override
    public void onPress() {
        super.onPress();
        PARENT.skipNextRelease = true;
    }
    
    @Override
    public void render( GuiGraphics graphics, int mouseX, int mouseY, float partialTicks ) {
        if( ClientRegister.EXTRA_INV_BUTTONS.GENERAL.hideForRecipeBook.get() && HAS_RECIPE_BOOK ) {
            visible = !((RecipeUpdateListener) PARENT).getRecipeBookComponent().isVisible();
        }
        active = INFO.canBeActive() && (!ClientRegister.EXTRA_INV_BUTTONS.GENERAL.disableInvalid.get() || INFO.canEnable());
        super.render( graphics, mouseX, mouseY, partialTicks );
    }
    
    @Override
    public void renderWidget( GuiGraphics graphics, int mouseX, int mouseY, float partialTicks ) {
        Minecraft mc = Minecraft.getInstance();

        graphics.pose().pushPose();
        graphics.pose().translate( 0.0, 0.0, 31.0 ); // Right behind item on pointer
        
        // Draw button tile
        RenderSystem.setShaderColor( 1.0F, 1.0F, 1.0F, alpha );
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        
        graphics.blit( INFO.isToggledOn() ? BUTTON_TEXTURE_ON : BUTTON_TEXTURE, getX(), getY(), 0.0F, BUTTON_SIZE * getTextureY( ),
                BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE * 3 );
        
        // Draw button detail (icon/text)
        float colorFac = active ? 1.0F : 0.666F; // darken detail if not clickable
        if( INFO.ICON == null ) {
            RenderSystem.setShaderColor( colorFac, colorFac, colorFac, alpha );
            graphics.drawCenteredString( mc.font, getMessage(),
                    getX() + TEXT_X, getY() + TEXT_Y,
                    INFO.COLOR | Mth.ceil( alpha * 0xFF ) << 24 );
        }
        else {
            RenderSystem.setShaderColor( CrustMath.getRed( INFO.COLOR ) * colorFac, CrustMath.getGreen( INFO.COLOR ) * colorFac,
                    CrustMath.getBlue( INFO.COLOR ) * colorFac, alpha );
            
            graphics.blit( INFO.ICON, getX() + ICON_BORDER, getY() + ICON_BORDER, 0.0F, 0.0F,
                    ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE );
        }
        
        graphics.pose().popPose();
        
        // Draw tooltip
        // TODO - Check if this is still needed
        /*
        if( isHovered() ) {
            graphics.renderTooltip( mc.font, mouseX, mouseY );
        }

         */
    }
    
    public static Tooltip createButtonTooltip( String tooltip ) {
        return Tooltip.create( Component.translatable(tooltip) );
    }
}