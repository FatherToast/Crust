package fathertoast.crust.api.config.client.gui.widget.field;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Draws a "button frame" that displays something inside itself
 * based on the current value of an associated config field.
 */
public class ItemViewWidget<V> extends AbstractWidget {
    
    /** The default size of the space this widget occupies. */
    public static final int DEFAULT_SIZE = 20;
    
    private final ItemViewRenderer<V> renderer;
    private final Supplier<V> valueSupplier;
    
    
    public ItemViewWidget( ItemViewRenderer<V> renderer, Supplier<V> valueSupplier, int x, int y ) {
        this( renderer, valueSupplier, x, y, DEFAULT_SIZE );
    }
    
    public ItemViewWidget( ItemViewRenderer<V> renderer, Supplier<V> valueSupplier, int x, int y, int size ) {
        super( x, y, size, size, Component.literal( "" ) );
        this.renderer = Objects.requireNonNull( renderer );
        this.valueSupplier = Objects.requireNonNull( valueSupplier );
    }
    
    
    @Override
    protected void renderWidget( GuiGraphics graphics, int mouseX, int mouseY, float partialTick ) {
        renderer.render( new RenderContext<>( valueSupplier, graphics, getX(), getY(), mouseX, mouseY, partialTick ) );
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
    public record RenderContext<V>(@Nullable Supplier<V> valueSupplier, GuiGraphics graphics, int widgetX, int widgetY,
                                   int mouseX,
                                   int mouseY, float partialTick) {
        /** @return This render context's display value, if it exists. */
        @Nullable
        public V getValue() {
            if( valueSupplier == null ) return null;
            return valueSupplier.get();
        }
    }
}
