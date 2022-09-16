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
     *  + tools
     *  - helpers
     *      + data gen
     *          + loot table
     *      - set block flags
     *      - nbt
     *      - entity/level events
     *      - math library
     *  + commands
     *      + crustrecover (health|hunger) [<value>] [<target>]
     *      + crustcap <capability name> [<value>] [<target>]
     *          + capability names: destroyOnPickup, undying, unbreaking, nightVision, superSpeed
     *      + crustportal (nether|end) [<pos>]
     *  - testing tools
     *      + starting inventory
     *      + extra inventory buttons (command-driven)
     *          + full restore health/hunger - /crustrecover
     *          + clear potion effects - /effect clear
     *          + time of day - /time set
     *          + weather - /weather
     *          + clear inventory (or reset to starting inventory) - /clear
     *          ? hot-swap inventories - /item (may be best to use custom command for this)
     *          ? equip gear (hotkey that works from creative inv) - /item
     *          + kill nearby entities - /kill
     *          + undying/unbreaking/destroy-on-pickup/night-vision/super-speed modes - /crustcap
     *          + set spawn point - /spawnpoint
     *          + nether/end portal (or direct teleport) - /crustportal
     *          + "programmable" buttons (simply run custom command?)
     *      - configure default game rules
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
        // Perform first-time loading of the config for this mod
        //CrustConfig.MAIN.SPEC.initialize();
        CrustConfig.DEFAULT_GAME_RULES.SPEC.initialize();
    }
}