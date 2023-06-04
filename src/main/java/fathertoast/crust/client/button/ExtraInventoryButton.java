package fathertoast.crust.client.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import fathertoast.crust.api.lib.CrustMath;
import fathertoast.crust.client.ClientRegister;
import fathertoast.crust.common.core.Crust;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ExtraInventoryButton extends Button {
    
    public static final ResourceLocation BUTTON_TEXTURE = new ResourceLocation( Crust.MOD_ID, "textures/button.png" );
    public static final ResourceLocation BUTTON_TEXTURE_ON = new ResourceLocation( Crust.MOD_ID, "textures/button_on.png" );
    
    public static final int ICON_SIZE = 9;
    public static final int ICON_BORDER = 3;
    
    public static final int BUTTON_SIZE = ICON_SIZE + 2 * ICON_BORDER;
    
    public static final int BUTTON_PADDING = 1;
    public static final int BUTTON_SPACING = BUTTON_SIZE + BUTTON_PADDING;
    
    public static final int TEXT_X = MathHelper.ceil( BUTTON_SIZE / 2.0F );
    public static final int TEXT_Y = MathHelper.ceil( (BUTTON_SIZE - 8) / 2.0F );
    
    
    private final ContainerScreen<?> PARENT;
    private final boolean HAS_RECIPE_BOOK;
    private final ButtonInfo INFO;
    
    public ExtraInventoryButton( ContainerScreen<?> screen, int leftPos, int topPos, ButtonInfo info ) {
        super( leftPos, topPos, BUTTON_SIZE, BUTTON_SIZE,
                new StringTextComponent( info.TEXT ), info.ON_PRESS,
                new ButtonTooltip( screen, info.TOOLTIP ) );
        PARENT = screen;
        HAS_RECIPE_BOOK = screen instanceof IRecipeShownListener;
        INFO = info;
    }
    
    @Override
    public void onPress() {
        super.onPress();
        PARENT.skipNextRelease = true;
    }
    
    @Override
    public void render( MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks ) {
        if( ClientRegister.EXTRA_INV_BUTTONS.GENERAL.hideForRecipeBook.get() && HAS_RECIPE_BOOK ) {
            visible = !((IRecipeShownListener) PARENT).getRecipeBookComponent().isVisible();
        }
        active = INFO.canBeActive() && (!ClientRegister.EXTRA_INV_BUTTONS.GENERAL.disableInvalid.get() || INFO.canEnable());
        super.render( matrixStack, mouseX, mouseY, partialTicks );
    }
    
    @Override
    public void renderButton( MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks ) {
        Minecraft mc = Minecraft.getInstance();
        
        matrixStack.pushPose();
        matrixStack.translate( 0.0, 0.0, 31.0 ); // Right behind item on pointer
        
        // Draw button tile
        mc.getTextureManager().bind( INFO.isToggledOn() ? BUTTON_TEXTURE_ON : BUTTON_TEXTURE );
        //noinspection deprecation
        RenderSystem.color4f( 1.0F, 1.0F, 1.0F, alpha );
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        
        blit( matrixStack, x, y, 0.0F, BUTTON_SIZE * getYImage( isHovered() ),
                BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE * 3 );
        
        // Draw button detail (icon/text)
        float colorFac = active ? 1.0F : 0.666F; // darken detail if not clickable
        if( INFO.ICON == null ) {
            //noinspection deprecation
            RenderSystem.color4f( colorFac, colorFac, colorFac, alpha );
            drawCenteredString( matrixStack, mc.font, getMessage(),
                    x + TEXT_X, y + TEXT_Y,
                    INFO.COLOR | MathHelper.ceil( alpha * 0xFF ) << 24 );
        }
        else {
            mc.getTextureManager().bind( INFO.ICON );
            //noinspection deprecation
            RenderSystem.color4f( CrustMath.getRed( INFO.COLOR ) * colorFac, CrustMath.getGreen( INFO.COLOR ) * colorFac,
                    CrustMath.getBlue( INFO.COLOR ) * colorFac, alpha );
            
            blit( matrixStack, x + ICON_BORDER, y + ICON_BORDER, 0.0F, 0.0F,
                    ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE );
        }
        
        matrixStack.popPose();
        
        // Draw tooltip
        if( isHovered() ) {
            renderToolTip( matrixStack, mouseX, mouseY );
        }
    }
    
    
    private static class ButtonTooltip implements Button.ITooltip {
        
        private final Screen SCREEN;
        private final String DISPLAY;
        
        ButtonTooltip( Screen screen, String display ) {
            SCREEN = screen;
            DISPLAY = display;
        }
        
        @Override
        public void onTooltip( Button button, MatrixStack matrixStack, int x, int y ) {
            SCREEN.renderTooltip( matrixStack, new TranslationTextComponent( DISPLAY ),
                    x, y );
        }
    }
}