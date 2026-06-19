package fathertoast.crust.api.config.client.gui;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.config.client.gui.widget.field.ItemViewWidget;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Contains all {@link fathertoast.crust.api.config.client.gui.widget.field.ItemViewWidget.ItemViewRenderer}
 * implementations provided by Crust as well as access to all registered instances from other mods.
 * <br><br>
 * Instances can be registered here via {@link ItemViewRendererRegistry#registerRenderer(ResourceLocation, ItemViewWidget.ItemViewRenderer)}.
 * There are no restrictions for when to register an instance, but it is advisable to do so
 * before mod loading has completed and the user is able to access the config GUI.
 * <br><br>
 * If you are registering an instance and depend on {@link ItemViewWidget.ItemViewRenderer#setup()}
 * being called, make sure to register it before the mod loading cycle completes, as the setup method
 * will be called on the main thread via {@code ModLoadingStage.COMPLETE}'s deferred work queue.
 *
 * @see fathertoast.crust.api.config.client.gui.widget.provider.ItemViewWidgetProvider
 * @see fathertoast.crust.api.config.client.gui.widget.field.ItemViewWidget.ItemViewRenderer
 * @see fathertoast.crust.api.config.client.gui.widget.provider.IItemViewable
 * @see ItemViewWidget
 */
@ApiStatus.Experimental
public class ItemViewRendererRegistry {
    
    /** Internal map of renderers by ID. */
    @ApiStatus.Internal
    private static final Map<ResourceLocation, ItemViewWidget.ItemViewRenderer<?>> RENDERERS_BY_ID = new ConcurrentHashMap<>();
    
    
    /** The ID for Crust's block state type renderer . */
    public static final ResourceLocation BLOCK_STATE = id( "block_state" );
    
    
    /**
     * Registers the given renderer instance under the specified ID.
     *
     * @throws IllegalStateException If a registered instance with the same ID already exists.
     */
    public static <V> ItemViewWidget.ItemViewRenderer<V> registerRenderer( ResourceLocation id, ItemViewWidget.ItemViewRenderer<V> renderer ) {
        if( RENDERERS_BY_ID.containsKey( id ) ) {
            throw new IllegalStateException( "A renderer instance with duplicate id '" + id + "' already exists in the renderer map!" );
        }
        RENDERERS_BY_ID.put( id, renderer );
        return renderer;
    }
    
    
    /**
     * @return The renderer instance associated with the specified ID.
     * @throws NullPointerException If no renderer with the given ID exists in the renderer registry.
     * @throws ClassCastException   If the returned renderer cannot be cast to the inferred type.
     */
    @SuppressWarnings( "unchecked" )
    public static <V> ItemViewWidget.ItemViewRenderer<V> getRendererOrThrow( ResourceLocation id ) {
        if( !RENDERERS_BY_ID.containsKey( id ) )
            throw new NullPointerException( "No item view renderer with ID '" + id + "' exists in the registry!" );
        return (ItemViewWidget.ItemViewRenderer<V>) RENDERERS_BY_ID.get( id );
    }
    
    /**
     * @return The renderer instance associated with the specified ID, if one exists.
     * Returns null otherwise.
     * @throws ClassCastException If the returned renderer cannot be cast to the inferred type.
     */
    @Nullable
    @SuppressWarnings( "unchecked" )
    public static <V> ItemViewWidget.ItemViewRenderer<V> getRenderer( ResourceLocation id ) {
        if( RENDERERS_BY_ID.containsKey( id ) ) {
            return (ItemViewWidget.ItemViewRenderer<V>) RENDERERS_BY_ID.get( id );
        }
        return null;
    }
    
    /** @return An iterable of all registered renderers. */
    @ApiStatus.Internal
    public static Iterable<ItemViewWidget.ItemViewRenderer<?>> allRenderers() {
        return RENDERERS_BY_ID.values();
    }
    
    
    @ApiStatus.Internal
    private static ResourceLocation id( String name ) {
        return ResourceLocation.fromNamespaceAndPath( ICrustApi.MOD_ID, name );
    }
}
