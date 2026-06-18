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
        renderer.render( field.getValue(), graphics, getX(), getY(), mouseX, mouseY, partialTick );
    }
    
    @Override
    protected void updateWidgetNarration( NarrationElementOutput output ) {
        // Nothing to narrate
    }
    
    @Override
    public boolean mouseClicked( double x, double y, int mouseKey ) {
        return false;
    }
    
    public interface ItemViewRenderer<V> {
        void render( @Nullable V value, GuiGraphics graphics, int widgetX, int widgetY, int mouseX, int mouseY, float partialTick );
    }
}
