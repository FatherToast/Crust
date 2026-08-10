package fathertoast.crust.api.config.client.gui.widget.field;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.config.client.gui.EntryViewRendererRegistry;
import fathertoast.crust.api.config.client.gui.widget.entry.ConfigGuiEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Displays something based on the current value of an associated config field.
 */
public class EntryViewWidget<T, V> extends AbstractWidget implements ITooltipWidget {
    
    /** The default size of the space this widget occupies. */
    public static final int DEFAULT_SIZE = 20;
    
    /** Converts the field's value type to the renderer's value type. */
    private final Function<T, V> VALUE_MAPPER;
    /** The entry renderer used to render a config value. */
    private final EntryViewRenderer<V> RENDERER;
    private final List<FormattedCharSequence> TOOLTIP = new ArrayList<>();
    
    private V display;
    
    public EntryViewWidget( Function<T, V> valueMapper, EntryViewRenderer<V> renderer, T displayValue, int x, int y ) {
        super( x, y, DEFAULT_SIZE, DEFAULT_SIZE, Component.literal( "" ) );
        VALUE_MAPPER = valueMapper;
        RENDERER = renderer;
        updateDisplay( displayValue );
    }
    
    /** Called to update the rendered display. */
    public void updateDisplay( T displayValue ) {
        display = VALUE_MAPPER.apply( displayValue );
        
        TOOLTIP.clear();
        RENDERER.updateTooltip( TOOLTIP, display );
    }
    
    /** Renders this widget. */
    @Override
    protected void renderWidget( GuiGraphics graphics, int mouseX, int mouseY, float partialTick ) {
        RENDERER.renderBackground( display, graphics, getX(), getY(), mouseX, mouseY, partialTick );
        RENDERER.render( display, graphics, getX(), getY(), mouseX, mouseY, partialTick );
    }
    
    /** @return The tooltip to render when the mouse is over this entry. Null if no tooltip should render. */
    @Override // ITooltipWidget
    @Nullable
    public List<FormattedCharSequence> getTooltip( int mouseX, int mouseY ) { return TOOLTIP.isEmpty() ? null : TOOLTIP; }
    
    /** Called when building narration elements for this widget. */
    @Override
    protected void updateWidgetNarration( NarrationElementOutput output ) {}
    
    /**
     * Called when a mouse button is clicked.
     *
     * @param mouseKey The mouse key that was clicked (see {@link InputConstants.Type#MOUSE}).
     * @return True if the event has been handled.
     */
    @Override
    public boolean mouseClicked( double x, double y, int mouseKey ) { return false; }
    
    
    /**
     * A simple renderer interface meant to be used by {@link EntryViewWidget}.
     * <br><br>
     * Instances can be registered via {@link EntryViewRendererRegistry}.
     */
    public interface EntryViewRenderer<V> {
        
        ResourceLocation BACKGROUND_TEXTURE =
                ResourceLocation.fromNamespaceAndPath( ICrustApi.MOD_ID, "textures/slot_background.png" );
        
        /**
         * Called from {@link EntryViewWidget#renderWidget(GuiGraphics, int, int, float)}
         * to render the widget background.
         */
        default void renderBackground( V displayValue, GuiGraphics graphics, int widgetX, int widgetY,
                                       int mouseX, int mouseY, float partialTick ) {
            // We force render this very far back, so it's like a hole in the UI we can render inside to clip
            // large entries via depth test (e.g., to prevent entity models from overflowing the widget bounds)
            RenderSystem.depthFunc( GL11.GL_ALWAYS );
            // texture, x, y, z, u, v, width, height, texWidth, texHeight
            graphics.blit( BACKGROUND_TEXTURE, widgetX, widgetY, -150, 0.0F, 0.0F,
                    DEFAULT_SIZE, DEFAULT_SIZE, DEFAULT_SIZE, DEFAULT_SIZE );
            RenderSystem.depthFunc( GL11.GL_LEQUAL );
        }
        
        /**
         * Called from {@link EntryViewWidget#renderWidget(GuiGraphics, int, int, float)}
         * to render something based on the widget's field's value.
         */
        void render( V displayValue, GuiGraphics graphics, int widgetX, int widgetY,
                     int mouseX, int mouseY, float partialTick );
        
        /** Called when the display value is changed to populate the widget's tooltip. */
        default void updateTooltip( List<FormattedCharSequence> tooltip, V displayValue ) {}
        
        /** Helper method for generating a tooltip in {@link #updateTooltip(List, Object)}. */
        default void addLine( List<FormattedCharSequence> tooltip, String line ) {
            addLine( tooltip, Component.literal( line ) );
        }
        
        /** Helper method for generating a tooltip in {@link #updateTooltip(List, Object)}. */
        default void addLine( List<FormattedCharSequence> tooltip, FormattedText line ) {
            tooltip.addAll( Minecraft.getInstance().font.split( line, ConfigGuiEntry.TOOLTIP_WIDTH ) );
        }
        
        /** Called after mod loading has completed to perform any required setup before use. */
        default void setup() {}
    }
}