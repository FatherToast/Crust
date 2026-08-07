package fathertoast.crust.client;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.client.accessor.IClientConfigAccessor;
import fathertoast.crust.api.client.renderer.CrustFishingHookRenderer;
import fathertoast.crust.api.client.util.shape.*;
import fathertoast.crust.api.config.client.ClientConfigUtil;
import fathertoast.crust.api.config.client.gui.EntryViewRendererRegistry;
import fathertoast.crust.api.config.client.gui.widget.field.EntryViewWidget;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.lib.CrustObjects;
import fathertoast.crust.api.util.BoxShape;
import fathertoast.crust.api.util.shape.CircleShape;
import fathertoast.crust.api.util.shape.CylinderShape;
import fathertoast.crust.api.util.shape.QuadShape;
import fathertoast.crust.api.util.shape.SphereShape;
import fathertoast.crust.client.config.CfgEditorCrustConfig;
import fathertoast.crust.client.config.ClientConfigAccessorImpl;
import fathertoast.crust.client.config.ExtraInvButtonsCrustConfig;
import fathertoast.crust.client.config.RenderSettingsCrustConfig;
import fathertoast.crust.client.renderer.entryview.*;
import fathertoast.crust.client.screen.CrustConfigSelectScreen;
import fathertoast.crust.common.api.impl.CrustApi;
import fathertoast.crust.common.core.Crust;
import fathertoast.crust.common.core.registry.CrustItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = ICrustApi.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public final class ClientRegister {
    
    static {
        // Provide the config button screen factory; we do it here so it's ready before client setup starts
        ClientConfigUtil.configScreenFactory = cfgManager -> new CrustConfigSelectScreen( null, cfgManager );
    }
    
    /** File for configuring in-game config edit button client preferences. */
    public static CfgEditorCrustConfig CONFIG_EDITOR;
    /** File for configuring extra inventory button client preferences. */
    public static ExtraInvButtonsCrustConfig EXTRA_INV_BUTTONS;
    /** File for misc rendering settings. */
    public static RenderSettingsCrustConfig RENDER_SETTINGS;
    /**
     * Client config accessor instance for the API.
     *
     * @see CrustApi#getClientConfigAccessor()
     */
    public static IClientConfigAccessor CONFIG_ACCESSOR = null;
    
    
    /** Called after common setup to perform client-side-only setup. */
    @SubscribeEvent
    static void onClientSetup( FMLClientSetupEvent event ) {
        // Perform first-time loading of the client-only configs
        // EXTRA_INV_BUTTONS is loaded in KeyBindingEvents
        CONFIG_EDITOR = new CfgEditorCrustConfig(
                ConfigManager.getRequired( ICrustApi.MOD_ID ), "client/config_editor" );
        RENDER_SETTINGS = new RenderSettingsCrustConfig(
                ConfigManager.getRequired( ICrustApi.MOD_ID ), "client/render_settings" );
        
        CONFIG_EDITOR.SPEC.initialize();
        EXTRA_INV_BUTTONS.SPEC.initialize();
        RENDER_SETTINGS.SPEC.initialize();
        
        CONFIG_ACCESSOR = new ClientConfigAccessorImpl();
        
        // Inject our own fishing rod animation
        if( RENDER_SETTINGS.fancyFishing.get() ) {
            event.enqueueWork( () -> ItemProperties.register( Items.FISHING_ROD,
                    ResourceLocation.withDefaultNamespace( "cast" ), new FishingRodItemPropertyGetter() ) );
        }
        
        // Register our debug shape renderers
        registerShapeRenderers();
        // Register our GUI entry view renderers
        registerEntryViewRenderers();
        
        // Tell Forge to open the config editor when our mod's "Config" button is clicked in the Mods screen
        ClientConfigUtil.registerConfigButtonAsEditScreen( Crust.INSTANCE.CONTAINER );
        
        // Run setup for all registered item view renderers
        ModLoadingStage.COMPLETE.getDeferredWorkQueue().enqueueWork( ModList.get().getModContainerById( ICrustApi.MOD_ID ).orElseThrow(),
                () -> EntryViewRendererRegistry.allRenderers().forEach( EntryViewWidget.EntryViewRenderer::setup ) );
    }
    
    /** Registers this mod's additional key bindings. */
    @SubscribeEvent
    static void onRegisterKeyMappings( RegisterKeyMappingsEvent event ) {
        KeyBindingEvents.register( event );
    }
    
    /** Registers this mod's entity renderers. */
    @SubscribeEvent
    static void registerEntityRenderers( EntityRenderersEvent.RegisterRenderers event ) {
        event.registerEntityRenderer( CrustObjects.Entities.FISH_HOOK.get(), CrustFishingHookRenderer::new );
    }
    
    /**
     * Registers this mod's debug shape renderers.
     *
     * @see IDebugShapeRenderer
     */
    private static void registerShapeRenderers() {
        DebugShapeRenderManager.register( BoxShape::new, new BoxShapeRenderer() );
        DebugShapeRenderManager.register( QuadShape::new, new QuadShapeRenderer() );
        DebugShapeRenderManager.register( CircleShape::new, new CircleShapeRenderer() );
        DebugShapeRenderManager.register( SphereShape::new, new SphereShapeRenderer() );
        DebugShapeRenderManager.register( CylinderShape::new, new CylinderShapeRenderer() );
    }
    
    /**
     * Registers this mod's entry view renderers.
     *
     * @see EntryViewWidget.EntryViewRenderer
     */
    private static void registerEntryViewRenderers() {
        // SPECIAL
        EntryViewRendererRegistry.registerRenderer( EntryViewRendererRegistry.EMPTY, new EmptyEntryViewRenderer() );
        EntryViewRendererRegistry.registerRenderer( EntryViewRendererRegistry.BLOCK_STATE, new BlockStateEntryViewRenderer() );
        EntryViewRendererRegistry.registerRenderer( EntryViewRendererRegistry.ITEM_STACK, new ItemStackEntryViewRenderer() );
        
        // REGISTRY
        EntryViewRendererRegistry.registerRenderer( EntryViewRendererRegistry.BLOCK, new BlockEntryViewRenderer() );
        EntryViewRendererRegistry.registerRenderer( EntryViewRendererRegistry.ITEM, new ItemEntryViewRenderer() );
        EntryViewRendererRegistry.registerRenderer( EntryViewRendererRegistry.MOB_EFFECT, new MobEffectEntryViewRenderer() );
        EntryViewRendererRegistry.registerRenderer( EntryViewRendererRegistry.ENTITY_TYPE, new EntityTypeEntryViewRenderer() );
    }
    
    @SubscribeEvent
    public static void buildCreativeContents( BuildCreativeModeTabContentsEvent event ) {
        CrustItems.buildCreativeContents( event );
    }
    
    // Static listener, no instantiation
    private ClientRegister() {}
}