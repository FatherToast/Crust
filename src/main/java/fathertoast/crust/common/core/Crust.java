package fathertoast.crust.common.core;

import fathertoast.crust.api.CrustPlugin;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.ICrustPlugin;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.value.environment.compat.ApocalypseDifficultyEnvironment;
import fathertoast.crust.common.api.impl.CrustApi;
import fathertoast.crust.common.api.impl.PlayerVelocityWatcher;
import fathertoast.crust.common.command.CrustArgumentTypes;
import fathertoast.crust.common.compat.naturalabsorption.NaturalAbsorptionPlugin;
import fathertoast.crust.common.config.CrustConfig;
import fathertoast.crust.common.core.registry.*;
import fathertoast.crust.common.network.CrustPacketHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod( ICrustApi.MOD_ID )
public class Crust {
    
    /* Feature List:
     * (KEY: '-' = complete in current version, 'o' = incomplete feature from previous version,
     *       '+' = incomplete new feature, '?' = feature to consider adding)
     *  - configs
     *      - config button opens config folder or in-game editor
     *      - automated syncing of specified server-side fields
     *      - in-game config editor
     *          - menu buttons and hotkey to access
     *          + raw toml text box for default field widget
     *          - list builder widgets for list fields
     *              - string list (default list field widget)
     *              + attribute list
     *              + block list
     *              + entity list
     *              + environment list
     *              + reg entry list
     *              + dimension ID based sub-lists
     *  - events
     *      - Crust config lifecycle events
     *      - advancement load event
     *      ? structure generating event
     *  - helpers
     *      - shape renderer (block entity, entity)
     *      - portal registry
     *      - data gen
     *          - loot table
     *      - nbt
     *      - entity/level events
     *      - math library
     *  - commands
     *      - crustclean pointer [<player>] - destroy item on pointer
     *      + crustclean [<players>] - reset inventory to starting inventory
     *      - crustmode [<player>] - check active modes
     *      - crustmode <mode> (disable|<value>) [<players>] - enable/disable mode
     *      - crustportal (<portal_type>) [<target>] - create dimension portal
     *      - crustrecover [all|health|hunger|effects] [<targets>]
     *  - tools
     *      + starting inventory
     *      ? inventory presets/loadouts
     *      - hotkey to equip from creative inv
     *      - extra inventory buttons (command-driven)
     *          - can have hotkey assigned
     *          - built-in buttons
     *          - custom buttons (user-defined)
     *          ? registry for mod-added buttons
     *      - configure default game rules
     *      - configure default 'modes' (see below)
     *      ? in-game nbt editor gui (does the mod still exist?)
     *      - feature generator blocks
     *          - structure gen variant (with processor)
     *  - modes
     *      - magnet - pulls nearby items toward you
     *      ? multi-mine - break multiple blocks at once; perhaps compat to an existing mod instead (like "Ore Excavation")
     *      - undying - fully heal if you would have died
     *      - unbreaking - fully repair items periodically
     *          ? grant 'instant build' player ability (infinite block placement)
     *      - uneating - restore food level when it drops below a threshold
     *      - destroy-on-pickup - items are not added to inventory when picked up
     *      - super vision - continuous night vision, removes fog/blindness
     *          ? make all entities glow (maybe enable fullbright texture somehow? The glow shader will kill performance)
     *      - super speed - move very fast
     *          ? grant instant (or very fast) mining
     */
    
    /** Logger instance for the mod. */
    public static final Logger LOG = LogManager.getLogger( ICrustApi.MOD_ID );
    
    /** Mod instance. */
    public static Crust INSTANCE;
    /** Mod container. */
    public final FMLModContainer CONTAINER;
    /** API instance. */
    public final CrustApi API;
    
    /** True if Natural Absorption is installed. */
    public static boolean NA_INSTALLED;
    
    
    public Crust( FMLJavaModLoadingContext context ) {
        INSTANCE = this;
        CONTAINER = context.getContainer();
        API = new CrustApi();
        ApocalypseDifficultyEnvironment.register( API );
        new CrustPacketHandler().registerMessages();
        
        ModLoadingStage.CONSTRUCT.getDeferredWorkQueue().enqueueWork( CONTAINER, () -> {
            // Crust's config manager; defines the mod config folder
            ConfigManager.create( "Crust", ICrustApi.MOD_ID );
            // Perform first-time loading of the common configs for this mod
            CrustConfig.initialize();
        } );
        
        final IEventBus modBus = context.getModEventBus();
        
        modBus.addListener( CrustPortals::onRegistryCreate );
        CrustBlocks.register( modBus );
        CrustItems.register( modBus );
        CrustBlockEntities.register( modBus );
        CrustPortals.register( modBus );
        CrustEffects.register( modBus );
        CrustEntities.register( modBus );
        CrustArgumentTypes.register( modBus );
        CrustStructureProcessors.register( modBus );
        
        modBus.addListener( this::onCommonSetup );
        modBus.addListener( this::sendIMCMessages );
        
        MinecraftForge.EVENT_BUS.register( PlayerVelocityWatcher.INSTANCE );
    }
    
    private void onCommonSetup( FMLCommonSetupEvent event ) {
        event.enqueueWork( this::processPlugins );
    }
    
    public void sendIMCMessages( InterModEnqueueEvent event ) {
        NA_INSTALLED = InterModComms.sendTo( "naturalabsorption", "getNaturalAbsorptionAPI",
                () -> NaturalAbsorptionPlugin.RECEIVER );
    }
    
    @SuppressWarnings( "all" )
    private void processPlugins() {
        // Load mod plugins
        ModList.get().getAllScanData().forEach( ( scanData ) ->
                scanData.getAnnotations().forEach( ( annotationData ) -> {
                    // Look for classes annotated with @CrustPlugin
                    if( annotationData.annotationType().getClassName().equals( CrustPlugin.class.getName() ) ) {
                        try {
                            Class<?> pluginClass = Class.forName( annotationData.memberName() );
                            
                            if( ICrustPlugin.class.isAssignableFrom( pluginClass ) ) {
                                ICrustPlugin plugin = (ICrustPlugin) pluginClass.getDeclaredConstructor().newInstance();
                                plugin.onLoad( API );
                                LOG.info( "Found Crust plugin at {} with plugin ID: {}",
                                        annotationData.memberName(), plugin.getId() );
                            }
                        }
                        catch( Exception ex ) {
                            LOG.error( "Failed to load a Crust plugin! Plugin class: {}",
                                    annotationData.memberName() );
                            ex.printStackTrace();
                        }
                    }
                } ) );
    }
    
    public static ResourceLocation rl( String path ) { return ResourceLocation.fromNamespaceAndPath( ICrustApi.MOD_ID, path ); }
}