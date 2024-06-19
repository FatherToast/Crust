package fathertoast.crust.common.core;

import fathertoast.crust.api.CrustPlugin;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.ICrustPlugin;
import fathertoast.crust.api.config.common.value.environment.compat.ApocalypseDifficultyEnvironment;
import fathertoast.crust.common.api.impl.CrustApi;
import fathertoast.crust.common.config.CrustConfig;
import fathertoast.crust.common.network.CrustPacketHandler;
import fathertoast.crust.common.portal.CrustPortals;
import fathertoast.crust.common.potion.CrustEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod( ICrustApi.MOD_ID )
public class Crust {
    
    /* Feature List:
     * (KEY: - = complete in current version, o = incomplete feature from previous version,
     *       + = incomplete new feature, ? = feature to consider adding)
     *  - configs
     *      - config button opens config folder or in-game editor
     *      - in-game config editor
     *          - menu buttons and hotkey to access
     *          + raw toml text box for default field widget
     *          + list builder widgets for list fields
     *              + attribute list
     *              + block list
     *              + entity list
     *              + environment list
     *              + reg entry list
     *              + string list
     *  - helpers
     *      - advancement load event
     *      - tile entity bounding box renderer
     *      - portal registry
     *      - data gen
     *          - loot table
     *      - set block flags
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
     *      + hotkey to equip from creative inv - ideally MMB by default
     *      - extra inventory buttons (command-driven)
     *          - can have hotkey assigned
     *          - built-in buttons
     *          - custom buttons (user-defined)
     *          ? registry for mod-added buttons
     *      - configure default game rules
     *      - configure default 'modes' (see below)
     *      ? in-game nbt editor gui (does the mod still exist?)
     *  - modes
     *      - magnet - pulls nearby items toward you
     *      ? multi-mine - break multiple blocks at once; perhaps compat to an existing mod instead (like "Ore Excavation")
     *      - undying - fully heal if you would have died
     *      - unbreaking - fully repair items periodically
     *          ? grant 'instant build' player ability (infinite block placement)
     *      - uneating - restore food level when it drops below a threshold
     *      - destroy-on-pickup - items are not added to inventory when picked up
     *      - super vision - continuous night vision, removes fog/blindness
     *          ? make all entities glow
     *      - super speed - move very fast
     *          ? grant instant (or very fast) mining
     */
    
    /** Logger instance for the mod. */
    public static final Logger LOG = LogManager.getLogger( ICrustApi.MOD_ID );
    
    /** Mod instance. */
    public static Crust INSTANCE;
    
    /** API instance. */
    private final CrustApi apiInstance;
    
    
    public Crust() {
        INSTANCE = this;
        apiInstance = new CrustApi();
        ApocalypseDifficultyEnvironment.register( apiInstance );
        CrustPacketHandler.registerMessages();
        
        // Perform first-time loading of the common configs for this mod
        CrustConfig.initialize();
        
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        modBus.addListener( CrustPortals::onRegistryCreate );
        CrustEffects.register( modBus );
        CrustPortals.register( modBus );
        
        modBus.addListener( this::onCommonSetup );
    }
    
    private void onCommonSetup( FMLCommonSetupEvent event ) { event.enqueueWork( this::processPlugins ); }
    
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
                                plugin.onLoad( apiInstance );
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
    
    public static ResourceLocation resLoc( String path ) { return new ResourceLocation( ICrustApi.MOD_ID, path ); }
}