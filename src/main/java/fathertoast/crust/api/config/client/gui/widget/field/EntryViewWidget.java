package fathertoast.crust.api.config.client.gui.widget.field;

import com.mojang.blaze3d.platform.InputConstants;
import fathertoast.crust.api.config.client.gui.EntryViewRendererRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Displays something based on the current value of an associated config field.
 */
public class EntryViewWidget<V> extends AbstractWidget {
    
    /** The default size of the space this widget occupies. */
    public static final int DEFAULT_SIZE = 20;
    
    private final EntryViewRenderer<V> renderer;
    private final Supplier<V> valueSupplier;
    
    
    public EntryViewWidget( EntryViewRenderer<V> renderer, Supplier<V> valueSupplier, int x, int y ) {
        this( renderer, valueSupplier, x, y, DEFAULT_SIZE );
    }
    
    public EntryViewWidget( EntryViewRenderer<V> renderer, Supplier<V> valueSupplier, int x, int y, int size ) {
        super( x, y, size, size, Component.literal( "" ) );
        this.renderer = Objects.requireNonNull( renderer );
        this.valueSupplier = Objects.requireNonNull( valueSupplier );
    }
    
    /** Renders this widget. */
    @Override
    protected void renderWidget( GuiGraphics graphics, int mouseX, int mouseY, float partialTick ) {
        renderer.render( valueSupplier, graphics, getX(), getY(), mouseX, mouseY, partialTick );
    }
    
    /** Called when building narration elements for this widget. */
    @Override
    protected void updateWidgetNarration( NarrationElementOutput output ) {
        // Nothing to narrate
    }
    
    /**
     * Called when a mouse button is clicked.
     *
     * @param mouseKey The mouse key that was clicked (see {@link InputConstants.Type#MOUSE}).
     * @return True if the event has been handled.
     */
    @Override
    public boolean mouseClicked( double x, double y, int mouseKey ) {
        return false;
    }
    
    
    /**
     * A simple renderer interface meant to be used by {@link EntryViewWidget}.
     * <br><br>
     * Instances can be registered via {@link EntryViewRendererRegistry}.
     */
    public interface EntryViewRenderer<V> {
        
        /**
         * Called from {@link EntryViewWidget#renderWidget(GuiGraphics, int, int, float)}
         * to render something based on the widget's field's value.
         */
        void render( @Nullable Supplier<V> valueSupplier, GuiGraphics graphics, int widgetX, int widgetY,
                     int mouseX, int mouseY, float partialTick );
        
        /** @return Helper method for safely retrieving the value of a nullable supplier. */
        @Nullable
        default V getValue( @Nullable Supplier<V> valueSupplier ) {
            if( valueSupplier == null ) return null;
            return valueSupplier.get();
        }
        
        /**
         * Called after mod loading has completed to
         * perform any required setup before use.
         */
        default void setup() { }
    }
}
