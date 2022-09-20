package fathertoast.crust.common.core;

import fathertoast.crust.common.config.CrustConfig;
import net.minecraftforge.common.MinecraftForge;
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
     *      + variable field min/max limits
     *  - helpers
     *      - advancement load event
     *      + data gen
     *          + loot table
     *      - set block flags
     *      - nbt
     *      - entity/level events
     *      - math library
     *  + commands
     *      + crustrecover (health|hunger|effects|all) [<value>] [<target>]
     *      + crustcap <mode name> [<value>] [<target>] - set/toggle mode
     *      + crustportal (nether|end) [<pos>] - create dimension portal
     *      + crustclean [start|all] [<target>] - clear inventory to starting or empty
     *  - tools
     *      + starting inventory
     *      + hotkey to equip from creative inv - MMB by default
     *      - extra inventory buttons (command-driven)
     *          + can have hotkey assigned
     *          - built-in buttons
     *          - custom buttons (user-defined)
     *      - configure default game rules
     *      ? in-game nbt editor gui (does the mod still exist?)
     *  + modes
     *      + magnet - pulls nearby items toward you
     *      + multi-mine - break multiple blocks at once
     *      + undying - fully heal if you would have died
     *      + unbreaking - fully repair items if they would have broken
     *      + uneating - restore hunger when a threshold is reached
     *      + destroy-on-pickup - items are not added to inventory when picked up
     *      + super vision - continuous night vision to you and glowing to all mobs
     *      + super speed - move very fast and instant mine
     */
    
    /** The mod's id. */
    public static final String MOD_ID = "crust";
    
    /** Logger instance for the mod. */
    public static final Logger LOG = LogManager.getLogger( MOD_ID );
    
    
    public Crust() {
        // Perform first-time loading of the config for this mod
        CrustConfig.DEFAULT_GAME_RULES.SPEC.initialize();
    }
}