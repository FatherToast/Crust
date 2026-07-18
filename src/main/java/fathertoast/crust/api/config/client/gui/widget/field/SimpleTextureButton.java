package fathertoast.crust.api.config.client.gui.widget.field;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A simple 3-state (disabled, normal, highlighted) textured button designed to work
 * with any texture that has a raster size of <b>width * (height * 3)</b>.
 */
public class SimpleTextureButton extends Button {
    
    /** A resource location pointing to the texture to use for this button. */
    private final ResourceLocation TEXTURE;
    
    public SimpleTextureButton( int x, int y, int width, int height, @Nullable Component tooltip, ResourceLocation texture, Button.OnPress onPress ) {
        super( x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION );
        Objects.requireNonNull( texture );
        TEXTURE = texture;
        
        if( tooltip != null ) {
            setTooltip( Tooltip.create( tooltip ) );
        }
    }
    
    @Override
    public void renderWidget( GuiGraphics graphics, int mouseX, int mouseY, float partialTick ) {
        RenderSystem.enableDepthTest();
        
        graphics.blit( TEXTURE, getX(), getY(), 0.0F, getTextureY(),
                width, height, width, height * 3 );
    }
    
    @Override
    public int getTextureY() { return height * (!active ? 0 : isHoveredOrFocused() ? 2 : 1); }
}
