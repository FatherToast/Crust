package fathertoast.crust.api.config.client.gui;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.config.client.gui.widget.field.EntryViewWidget;
import fathertoast.crust.api.config.client.gui.widget.provider.EntryViewWidgetProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Contains all {@link EntryViewWidget.EntryViewRenderer}
 * implementations provided by Crust as well as access to all registered instances from other mods.
 * <br><br>
 * Instances can be registered here via {@link EntryViewRendererRegistry#registerRenderer(ResourceLocation, EntryViewWidget.EntryViewRenderer)}.
 * There are no restrictions for when to register an instance, but it is advisable to do so
 * before mod loading has completed and the user is able to access the config GUI.
 * <br><br>
 * If you are registering an instance and depend on {@link EntryViewWidget.EntryViewRenderer#setup()}
 * being called, make sure to register it before the mod loading cycle completes, as the setup method
 * will be called on the main thread via {@code ModLoadingStage.COMPLETE}'s deferred work queue.
 *
 * @see EntryViewWidgetProvider
 * @see EntryViewWidget.EntryViewRenderer
 * @see EntryViewWidget
 */
public final class EntryViewRendererRegistry {
    
    /** Internal map of renderers by ID. */
    @ApiStatus.Internal
    private static final Map<ResourceLocation, EntryViewWidget.EntryViewRenderer<?>> RENDERERS_BY_ID = new ConcurrentHashMap<>();
    
    
    // Special IDs
    
    /** The ID for Crust's "empty" renderer. This renderer has no strict type and renders nothing. */
    public static final ResourceLocation EMPTY = modId( "empty" );
    /** The ID for Crust's block state entry view renderer that accepts only block state values. */
    public static final ResourceLocation BLOCK_STATE = vanillaId( "block_state" );
    /** The ID for Crust's item stack type renderer that accepts only item stack values. */
    public static final ResourceLocation ITEM_STACK = vanillaId( "item_stack" );
    
    // Registry IDs
    
    /** The ID for Crust's block entry view renderer that accepts only block values. */
    public static final ResourceLocation BLOCK = registryId( ForgeRegistries.BLOCKS );
    /** The ID for Crust's item type renderer that accepts only item values. */
    public static final ResourceLocation ITEM = registryId( ForgeRegistries.ITEMS );
    /** The ID for Crust's mob effect entry view renderer that accepts only mob effect values. */
    public static final ResourceLocation MOB_EFFECT = registryId( ForgeRegistries.MOB_EFFECTS );
    /** The ID for Crust's entity type entry view renderer that accepts only entity type values. */
    public static final ResourceLocation ENTITY_TYPE = registryId( ForgeRegistries.ENTITY_TYPES );
    
    
    /**
     * Registers the given renderer instance under the specified ID.
     *
     * @throws IllegalStateException If a registered instance with the same ID already exists.
     */
    public static <V> EntryViewWidget.EntryViewRenderer<V> registerRenderer( ResourceLocation id, EntryViewWidget.EntryViewRenderer<V> renderer ) {
        if( RENDERERS_BY_ID.containsKey( id ) ) {
            throw new IllegalStateException( "A renderer instance with duplicate id '" + id + "' already exists in the renderer map!" );
        }
        RENDERERS_BY_ID.put( id, renderer );
        return renderer;
    }
    
    /**
     * @return The renderer instance mapped to the specified registry key.
     * Returns the "empty" renderer associated with the ID {@link EntryViewRendererRegistry#EMPTY}
     * if no renderer is associated with the registry key.
     */
    public static <V> EntryViewWidget.EntryViewRenderer<V> getForRegistry( ResourceKey<? extends V> registryKey ) {
        final ResourceLocation id = registryKey.location();
        // noinspection unchecked
        return (EntryViewWidget.EntryViewRenderer<V>) RENDERERS_BY_ID.getOrDefault( id, getRendererOrThrow( EMPTY ) );
    }
    
    /**
     * @return The renderer instance associated with the specified ID.
     * @throws NullPointerException If no renderer with the given ID exists in the renderer registry.
     * @throws ClassCastException   If the returned renderer cannot be cast to the inferred type.
     */
    @SuppressWarnings( "unchecked" )
    public static <V> EntryViewWidget.EntryViewRenderer<V> getRendererOrThrow( ResourceLocation id ) {
        if( !RENDERERS_BY_ID.containsKey( id ) )
            throw new NullPointerException( "No entry view renderer with ID '" + id + "' exists in the registry!" );
        return (EntryViewWidget.EntryViewRenderer<V>) RENDERERS_BY_ID.get( id );
    }
    
    /**
     * @return The renderer instance associated with the specified ID, if one exists.
     * Returns null otherwise.
     * @throws ClassCastException If the returned renderer cannot be cast to the inferred type.
     */
    @Nullable
    @SuppressWarnings( "unchecked" )
    public static <V> EntryViewWidget.EntryViewRenderer<V> getRenderer( ResourceLocation id ) {
        if( RENDERERS_BY_ID.containsKey( id ) ) {
            return (EntryViewWidget.EntryViewRenderer<V>) RENDERERS_BY_ID.get( id );
        }
        return null;
    }
    
    /** @return An iterable of all registered renderers. */
    @ApiStatus.Internal
    public static Iterable<EntryViewWidget.EntryViewRenderer<?>> allRenderers() {
        return RENDERERS_BY_ID.values();
    }
    
    
    /** @return A resource location under Crust's namespace, with the specified path. */
    private static ResourceLocation modId( String path ) {
        return ResourceLocation.fromNamespaceAndPath( ICrustApi.MOD_ID, path );
    }
    
    /** @return A resource location under the default namespace, with the specified path. */
    private static ResourceLocation vanillaId( String path ) {
        return ResourceLocation.withDefaultNamespace( path );
    }
    
    /** @return The registry ID of the given Forge registry. */
    private static ResourceLocation registryId( IForgeRegistry<?> registry ) {
        return registry.getRegistryKey().location();
    }
    
    
    // No instantiation
    private EntryViewRendererRegistry() {}
}