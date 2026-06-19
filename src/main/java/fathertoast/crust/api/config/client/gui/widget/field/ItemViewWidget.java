package fathertoast.crust.api.config.client.gui.widget.field;

import fathertoast.crust.api.config.client.gui.widget.provider.IItemViewable;
import fathertoast.crust.api.config.common.field.GenericField;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Draws a "button frame" that displays something inside itself
 * based on the current value of an associated config field.
 */
public class ItemViewWidget<V, T extends GenericField<V> & IItemViewable> extends AbstractWidget {
    
    /** The default size of the space this widget occupies. */
    public static final int DEFAULT_SIZE = 20;
    
    private final ItemViewRenderer<V> renderer;
    private final T field;
    
    
    public ItemViewWidget( ItemViewRenderer<V> renderer, T field, int x, int y ) {
        this( renderer, field, x, y, DEFAULT_SIZE );
    }
    
    public ItemViewWidget( ItemViewRenderer<V> renderer, T field, int x, int y, int size ) {
        super( x, y, size, size, Component.literal( "" ) );
        this.renderer = Objects.requireNonNull( renderer );
        this.field = Objects.requireNonNull( field );
    }
    
    
    @Override
    protected void renderWidget( GuiGraphics graphics, int mouseX, int mouseY, float partialTick ) {
        renderer.render( new RenderContext<>( field.get(), graphics, getX(), getY(), mouseX, mouseY, partialTick ) );
    }
    
    @Override
    protected void updateWidgetNarration( NarrationElementOutput output ) {
        // Nothing to narrate
    }
    
    @Override
    public boolean mouseClicked( double x, double y, int mouseKey ) {
        return false;
    }
    
    
    /**
     * A simple renderer interface meant to be used by {@link ItemViewWidget}.
     * <br><br>
     * Instances can be registered via {@link fathertoast.crust.api.config.client.gui.ItemViewRendererRegistry}.
     */
    public interface ItemViewRenderer<V> {
        
        /**
         * Called from {@link ItemViewWidget#renderWidget(GuiGraphics, int, int, float)}
         * to render something based on the widget's field's value.
         */
        void render( RenderContext<V> renderContext );
        
        /**
         * Called after mod loading has completed to
         * perform any required setup before use.
         */
        default void setup() { }
    }
    
    /** Contains information required by item view renderers to draw. */
    public record RenderContext<V>(@Nullable V value, GuiGraphics graphics, int widgetX, int widgetY, int mouseX,
                                   int mouseY, float partialTick) { }
}
