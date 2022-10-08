package fathertoast.crust.common.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.*;
import fathertoast.crust.common.command.CommandUtil;

/**
 * File for configuring rules & limitations for Crust's modes.
 */
public class CrustModesConfigFile extends AbstractConfigFile {
    
    public final General GENERAL;
    
    public final Magnet MAGNET;
    public final Speed SPEED;
    
    /**
     * @param cfgManager The mod's config manager.
     * @param cfgName    Name for the new config file. May include a file path (e.g. "folder/subfolder/filename").
     */
    CrustModesConfigFile( ConfigManager cfgManager, String cfgName ) {
        super( cfgManager, cfgName,
                "This config contains options to control the 'modes' added by Crust. Some examples of Crust " +
                        "modes are magnet mode, super speed mode, and undying mode.",
                "",
                "This config is for server-side settings. Client preferences are requested by using /crustmode or Crust's " +
                        "extra inventory buttons (client_extra_inv_buttons.toml)." );
        
        GENERAL = new General( this );
        
        MAGNET = new Magnet( this );
        SPEED = new Speed( this );
    }
    
    /**
     * Category for modes overall.
     */
    public static class General extends AbstractConfigCategory<CrustModesConfigFile> {
        
        public final IntField magnetOpLevel;
        //public final IntField multiMineOpLevel;
        public final IntField undyingOpLevel;
        public final IntField unbreakingOpLevel;
        public final IntField uneatingOpLevel;
        public final IntField visionOpLevel;
        public final IntField speedOpLevel;
        public final IntField noPickupOpLevel;
        
        public final DoubleField magnetDefault;
        //public final ??? multiMineDefault;
        public final BooleanField undyingDefault;
        public final BooleanField unbreakingDefault;
        public final IntField uneatingDefault;
        public final BooleanField visionDefault;
        public final DoubleField speedDefault;
        public final BooleanField noPickupDefault;
        
        General( CrustModesConfigFile parent ) {
            super( parent, "general",
                    "Options that apply to the 'modes' added by Crust, in general." );
            
            SPEC.increaseIndent();
            
            SPEC.subcategory( "op_level",
                    "The op levels (aka permission levels) required to enable/disable Crust's various modes. You can " +
                            "disable any mode by setting this level very high (e.g., " + (CommandUtil.PERMISSION_SERVER_OP + 1) + ").",
                    "Vanilla op levels used are:",
                    "  " + CommandUtil.PERMISSION_NONE + " - Chat/whispers, Access to limited info",
                    "  " + CommandUtil.PERMISSION_TRUSTED + " - Can bypass spawn protection",
                    "  " + CommandUtil.PERMISSION_CHEAT + " - Can use cheats, Access to info that can be used to cheat",
                    "  " + CommandUtil.PERMISSION_MODERATE + " - Can ban/whitelist players, 'Moderator'",
                    "  " + CommandUtil.PERMISSION_SERVER_OP + " - All permissions, Server management",
                    RestartNote.WORLD.COMMENT );
            magnetOpLevel = SPEC.define( new IntField( "op_level.magnet",
                    CommandUtil.PERMISSION_NONE, IntField.Range.ANY ) );
            //multiMineOpLevel = SPEC.define( new IntField( "op_level.multi_mine",
            //        CommandUtil.PERMISSION_NONE, IntField.Range.ANY ) );
            undyingOpLevel = SPEC.define( new IntField( "op_level.undying",
                    CommandUtil.PERMISSION_CHEAT, IntField.Range.ANY ) );
            unbreakingOpLevel = SPEC.define( new IntField( "op_level.unbreaking",
                    CommandUtil.PERMISSION_CHEAT, IntField.Range.ANY ) );
            uneatingOpLevel = SPEC.define( new IntField( "op_level.uneating",
                    CommandUtil.PERMISSION_CHEAT, IntField.Range.ANY ) );
            visionOpLevel = SPEC.define( new IntField( "op_level.super_vision",
                    CommandUtil.PERMISSION_CHEAT, IntField.Range.ANY ) );
            speedOpLevel = SPEC.define( new IntField( "op_level.super_speed",
                    CommandUtil.PERMISSION_CHEAT, IntField.Range.ANY ) );
            noPickupOpLevel = SPEC.define( new IntField( "op_level.destroy_on_pickup",
                    CommandUtil.PERMISSION_CHEAT, IntField.Range.ANY ) );
            
            SPEC.subcategory( "default",
                    "The default settings for Crust's various modes initially applied to players. Note that these " +
                            "mode settings will be applied regardless of op level; if the player does not have permission to " +
                            "enable/disable the mode, they will be 'stuck' with whatever is assigned here." );
            magnetDefault = SPEC.define( new DoubleField( "default.magnet", 10.0, DoubleField.Range.NON_NEGATIVE,
                    "The maximum range (blocks) for magnet mode. If 0, magnet mode will be off by default." ) );
            //multiMineDefault = SPEC.define( new ???( "default.multi_mine", 0, 0, 0 ) );
            undyingDefault = SPEC.define( new BooleanField( "default.undying", false ) );
            unbreakingDefault = SPEC.define( new BooleanField( "default.unbreaking", false ) );
            uneatingDefault = SPEC.define( new IntField( "default.uneating", 0, 0, 20,
                    "When dropping below this food level (half-drumsticks), uneating mode restores hunger. If 0, " +
                            "uneating mode will be off by default." ) );
            visionDefault = SPEC.define( new BooleanField( "default.super_vision", false ) );
            speedDefault = SPEC.define( new DoubleField( "default.super_speed", 1.0, 1.0, Double.POSITIVE_INFINITY,
                    "The speed multiplier applied while sprinting. If 1, super speed mode will be off by default." ) );
            noPickupDefault = SPEC.define( new BooleanField( "default.destroy_on_pickup", false ) );
            
            SPEC.decreaseIndent();
        }
    }
    
    /**
     * Category for magnet mode.
     */
    public static class Magnet extends AbstractConfigCategory<CrustModesConfigFile> {
        
        public final DoubleField maxRangeLimit;
        public final DoubleField maxSpeed;
        public final IntField delay;
        
        Magnet( CrustModesConfigFile parent ) {
            super( parent, "magnet_mode",
                    "Options that apply to Crust's magnet mode." );
            
            maxRangeLimit = SPEC.define( new DoubleField( "max_range_limit", 10.0, DoubleField.Range.NON_NEGATIVE,
                    "The highest maximum range (blocks) allowed for magnet mode. Max range is a client preference." ) );
            maxSpeed = SPEC.define( new ScaledDoubleField.Rate( "max_speed", 10.0, DoubleField.Range.NON_NEGATIVE,
                    "The maximum speed (blocks/sec) for items pulled by magnet mode. Speed is higher the closer " +
                            "the item is to the player, scaling down to 0 m/s at the player's max range." ) );
            delay = SPEC.define( new IntField( "delay", 40, IntField.Range.NON_NEGATIVE,
                    "The time delay (ticks) before freshly dropped items are pulled by magnet mode.",
                    "Setting this to a low value will cause items to fly around your face until their pickup delay expires. " +
                            "The default prevents face-flying for vanilla drops, but many drops only have a pickup delay of 10." ) );
        }
    }
    
    /**
     * Category for super-speed mode.
     */
    public static class Speed extends AbstractConfigCategory<CrustModesConfigFile> {
        
        public final DoubleField speedLimit;
        
        Speed( CrustModesConfigFile parent ) {
            super( parent, "super_speed_mode",
                    "Options that apply to Crust's super-speed mode." );
            
            speedLimit = SPEC.define( new DoubleField( "speed_limit", 12.0, 1.0, Double.POSITIVE_INFINITY,
                    "The highest maximum speed multiplier allowed for super-speed mode. Actual speed is a client preference.",
                    "Warning: Very large speed multipliers might break the game's physics." ) );
        }
    }
}