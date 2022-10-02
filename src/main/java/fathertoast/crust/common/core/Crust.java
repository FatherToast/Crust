package fathertoast.crust.common.core;

import fathertoast.crust.api.CrustPlugin;
import fathertoast.crust.api.ICrustPlugin;
import fathertoast.crust.api.impl.CrustApi;
import fathertoast.crust.common.config.CrustConfig;
import fathertoast.crust.common.event.EventListener;
import fathertoast.crust.common.network.CrustPacketHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod( Crust.MOD_ID )
public class Crust {
    
    /* Feature List:
     * (KEY: - = complete in current version, o = incomplete feature from previous version,
     *       + = incomplete new feature, ? = feature to consider adding)
     *  - configs
     *      - config button opens config folder
     *      ? in-game config editor gui
     *      + variable field min/max limits
     *  - helpers
     *      - advancement load event
     *      + data gen
     *          + loot table
     *      - set block flags
     *      - nbt
     *      - entity/level events
     *      - math library
     *  - commands
     *      - crustclean pointer [<player>] - destroy item on pointer
     *      ? crustclean [<players>] - reset inventory to starting inventory
     *      - crustmode [<player>] - check active modes
     *      - crustmode <mode> (disable|<value>) [<players>] - enable/disable mode
     *      - crustportal (nether|end|<other portal>) [<target>] - create dimension portal
     *      - crustrecover [all|health|hunger|effects] [<targets>]
     *  - tools
     *      + starting inventory
     *      + hotkey to equip from creative inv - ideally MMB by default
     *      - extra inventory buttons (command-driven)
     *          - can have hotkey assigned
     *          - built-in buttons
     *          - custom buttons (user-defined)
     *      - configure default game rules
     *      ? in-game nbt editor gui (does the mod still exist?)
     *  - modes
     *      - magnet - pulls nearby items toward you
     *      ? multi-mine - break multiple blocks at once; perhaps compat to an existing mod instead (like "Ore Excavation")
     *      - undying - fully heal if you would have died
     *      - unbreaking - fully repair items periodically
     *          ? grant 'instant build' player ability
     *      - uneating - restore food level when it drops below a threshold
     *      - destroy-on-pickup - items are not added to inventory when picked up
     *      - super vision - continuous night vision, removes fog/blindness
     *          ? make all entities glow
     *      - super speed - move very fast
     *          ? grant instant (or very fast) mining
     */
    
    /** The mod's id. */
    public static final String MOD_ID = "crust";
    
    /** Logger instance for the mod. */
    public static final Logger LOG = LogManager.getLogger( MOD_ID );
    
    /** API instance */
    private final CrustApi apiInstance;
    
    
    public Crust() {
        apiInstance = new CrustApi();
        CrustPacketHandler.registerMessages();
        
        // Perform first-time loading of the configs for this mod
        CrustConfig.DEFAULT_GAME_RULES.SPEC.initialize();
        CrustConfig.MODES.SPEC.initialize();
        
        
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        modBus.addListener( this::onCommonSetup );
        
        MinecraftForge.EVENT_BUS.register( new EventListener() );
    }
    
    void onCommonSetup( FMLCommonSetupEvent event ) {
        event.enqueueWork( this::processPlugins );
    }
    
    private void processPlugins() {
        // Load mod plugins
        ModList.get().getAllScanData().forEach( scanData -> {
            scanData.getAnnotations().forEach( annotationData -> {
                
                // Look for classes annotated with @CrustPlugin
                if( annotationData.getAnnotationType().getClassName().equals( CrustPlugin.class.getName() ) ) {
                    try {
                        Class<?> pluginClass = Class.forName( annotationData.getMemberName() );
                        
                        if( ICrustPlugin.class.isAssignableFrom( pluginClass ) ) {
                            ICrustPlugin plugin = (ICrustPlugin) pluginClass.newInstance();
                            plugin.onLoad( apiInstance );
                            LOG.info( "Found Crust plugin at {} with plugin ID: {}", annotationData.getMemberName(), plugin.getId() );
                        }
                    }
                    catch( Exception e ) {
                        LOG.error( "Failed to load a Crust plugin! Plugin class: {}", annotationData.getMemberName() );
                        e.printStackTrace();
                    }
                }
            } );
        } );
    }
    
    public static ResourceLocation resLoc( String path ) {
        return new ResourceLocation( MOD_ID, path );
    }
}