package fathertoast.crust.common.core;

import fathertoast.crust.common.config.CrustConfig;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod( Crust.MOD_ID )
public class Crust {
    
    /* Feature List:
     * (KEY: - = complete in current version, o = incomplete feature from previous version,
     *       + = incomplete new feature, ? = feature to consider adding)
     *  - configs
     *      - implementable
     *      - config button opens config folder
     *      ? in-game config editor gui
     *  - tools
     *  - helpers
     *      + data gen
     *          + loot table
     *      - set block flags
     *      - nbt
     *      - entity/level events
     *      - math library
     *  - testing tools
     *      + starting inventory
     *      + extra inventory buttons (command-driven)
     *          + full restore health/hunger
     *          + clear potion effects
     *          + time of day
     *          + weather
     *          + clear inventory (or reset to starting inventory)
     *          ? hot-swap inventories
     *          + destroy on pickup
     *          + equip gear (hotkey that works from creative inv?)
     *          + kill nearby entities
     *          + undying/unbreaking/nightvision/superspeed modes
     *          + nether/end portal (or direct teleport)
     *          + "programmable" buttons
     *      + configure default game rules
     *      ? in-game nbt editor gui (does the mod still exist?)
     *  - features
     *      + multi-block mining
     *      + magnet mode
     */
    
    /** The mod's id. */
    public static final String MOD_ID = "crust";
    
    /** Logger instance for the mod. */
    public static final Logger LOG = LogManager.getLogger( MOD_ID );
    
    
    public Crust() {
        // Perform first-time loading of the "main" config for this mod
        CrustConfig.MAIN.SPEC.initialize();
        
        // You can use config values right away
        if( CrustConfig.MAIN.GENERAL.boolField.get() ) { Crust.LOG.warn( "Woah!" ); }
    }
}