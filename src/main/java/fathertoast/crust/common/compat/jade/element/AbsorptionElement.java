package fathertoast.crust.common.compat.jade.element;

import fathertoast.crust.common.compat.jade.CrustJadePlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.ui.Element;
import snownee.jade.api.ui.IDisplayHelper;

/**
 * An entity element that draws the inspected entity's absorption amount
 * as absorption hearts below the entity's health element.
 * <br><br>
 * If Natural Absorption is installed, this element can also render the inspected entity's
 * absorption capacity, if enabled in the config.
 * <br><br>
 * Modified copy-paste of Jade's heart element.
 */
public class AbsorptionElement extends Element {
    
    /** Resource location pointing to the vanilla GUI icons texture. */
    private static final ResourceLocation GUI_ICONS = ResourceLocation.withDefaultNamespace( "textures/gui/icons.png" );
    
    
    /** The absorption amount to draw. */
    private final float absorptionAmount;
    /** The absorption amount as readable text. */
    private final String amountAsText;
    /** The absorption capacity to draw (when Natural Absorption is installed). */
    private final float absorptionCapacity;
    
    /** Provides access to Jade's config values. */
    private final IPluginConfig cfgAccess;
    
    
    /**
     * Creates a new instance of this element.
     *
     * @param cfgAccess          The config access object from Jade.
     * @param absorptionAmount   The absorption amount, which determines how many absorption hearts to draw.
     * @param absorptionCapacity The absorption capacity, which is used to draw empty heart containers behind the absorption hearts.
     *                           This is a compatibility feature for the Natural Absorption mod, and will normally always be 0.0
     *                           if the mod is not installed.
     */
    public AbsorptionElement( IPluginConfig cfgAccess, float absorptionAmount, float absorptionCapacity ) {
        this.cfgAccess = cfgAccess;
        this.absorptionAmount = absorptionAmount;
        this.absorptionCapacity = absorptionCapacity;
        this.amountAsText = "x" + IDisplayHelper.get().humanReadableNumber( absorptionAmount, "", false );
    }
    
    
    /**
     * Calculates the default reserved area of this element.
     * <p>
     * Modders call {@link #getCachedSize} instead.
     */
    @Override
    public Vec2 getSize() {
        final float maxAbsorption = cfgAccess.getFloat( CrustJadePlugin.Config.ENTITY_MAX_ABSORPTION_FOR_RENDER );
        final int iconsPerLine = cfgAccess.getInt( CrustJadePlugin.Config.ENTITY_ABSORPTION_ICONS_PER_LINE );
        
        // Render text instead of icons if absorption amount
        // is greater than the configured max.
        if( absorptionAmount > maxAbsorption ) {
            return new Vec2( (float) (10 + Minecraft.getInstance().font.width( amountAsText )), 10.0F );
        }
        else {
            float heartCount = absorptionAmount * 0.5F;
            int heartsPerLine = (int) Math.min( iconsPerLine, Math.ceil( heartCount ) );
            int lineCount = (int) Math.ceil( heartCount / iconsPerLine );
            
            return new Vec2( (float) (8 * heartsPerLine), (float) (10 * lineCount) );
        }
    }
    
    /**
     * Draws this element.
     *
     * @param guiGraphics The GUI graphics instance.
     * @param x           The X-position of this element.
     * @param y           The Y-position of this element.
     * @param maxX        Max width this element can expand to.
     * @param maxY        Max height this element can expand to.
     */
    @Override
    public void render( GuiGraphics guiGraphics, float x, float y, float maxX, float maxY ) {
        final float maxHeartIcons = cfgAccess.getFloat( CrustJadePlugin.Config.ENTITY_MAX_ABSORPTION_FOR_RENDER );
        final int iconsPerLine = cfgAccess.getInt( CrustJadePlugin.Config.ENTITY_ABSORPTION_ICONS_PER_LINE );
        final int heartsPerLine = (int) Math.min( iconsPerLine, Math.ceil( absorptionAmount ) );
        
        int xOffset = 0;
        
        // Draw absorption as text if amount is greater than
        // the configured maximum that can be drawn with icons
        if( absorptionAmount > maxHeartIcons ) {
            renderHeart( guiGraphics, Gui.HeartType.CONTAINER, x, y, false );
            renderHeart( guiGraphics, Gui.HeartType.ABSORBING, x, y, false );
            IDisplayHelper.get().drawText( guiGraphics, amountAsText, x + 10.5F, y, IThemeHelper.get().getNormalColor() );
        }
        else {
            final float absorption = absorptionAmount * 0.5F;
            final int heartCount = Mth.ceil( absorption );
            
            for( int i = 1; i <= heartCount; ++i ) {
                // Draw empty hearts first based on absorption capacity
                if( i <= absorptionCapacity ) {
                    renderHeart( guiGraphics, Gui.HeartType.CONTAINER, x + xOffset, y, false );
                }
                // Draw absorption hearts based on absorption amount
                if( i <= Mth.floor( absorption ) ) {
                    renderHeart( guiGraphics, Gui.HeartType.ABSORBING, x + xOffset, y, false );
                    xOffset += 8;
                }
                // Draw a half of an absorption heart, if needed
                if( (float) i > absorption && (float) i < absorption + 1.0F ) {
                    renderHeart( guiGraphics, Gui.HeartType.ABSORBING, x + xOffset, y, true );
                    xOffset += 8;
                }
                // Increment Y-offset when we need to go to a new row
                if( i % heartsPerLine == 0 ) {
                    y += 10.0F;
                    xOffset = 0;
                }
            }
        }
    }
    
    /**
     * Renders a heart icon based on the specified heart type.
     *
     * @param graphics  The GUI graphics object we are drawing with.
     * @param heartType The heart type to draw.
     * @param x         The X position to draw the heart icon at.
     * @param y         The Y position to draw the heart icon at.
     * @param half      Ture if half a heart should be rendered instead of a whole heart.
     */
    private void renderHeart( GuiGraphics graphics, Gui.HeartType heartType, float x, float y, boolean half ) {
        // Texture, x, y, width, height, u, v, regWidth, regHeight, textureWidth, textureHeight
        graphics.blit( GUI_ICONS, (int) x, (int) y,
                8, 8,
                heartType.getX( half, false ), 0,
                9, 9,
                256, 256
        );
    }
}
