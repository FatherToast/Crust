package fathertoast.crust.common.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.config.common.field.RestartNote;
import fathertoast.crust.api.config.common.field.ScaledDoubleField;
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
                "This config contains options to control the 'modes' added by Crust.",
                "Some examples of Crust modes are magnet mode, multi-mine mode, and undying mode.",
                "",
                "This config is for server-side settings. Client preferences are requested by using",
                "/crustmode or Crust's extra inventory buttons (client_extra_inv_buttons.toml)." );
        
        GENERAL = new General( this );
        
        MAGNET = new Magnet( this );
        SPEED = new Speed( this );
    }
    
    /**
     * Category for modes overall.
     */
    public static class General extends AbstractConfigCategory<CrustModesConfigFile> {
        
        public final IntField magnetOpLevel;
        public final IntField multiMineOpLevel;
        public final IntField noPickupOpLevel;
        public final IntField undyingOpLevel;
        public final IntField unbreakingOpLevel;
        public final IntField uneatingOpLevel;
        public final IntField visionOpLevel;
        public final IntField speedOpLevel;
        
        General( CrustModesConfigFile parent ) {
            super( parent, "general",
                    "Options that apply to the 'modes' added by Crust, in general." );
            
            magnetOpLevel = SPEC.define( new IntField( "op_level.magnet",
                    CommandUtil.PERMISSION_NONE, IntField.Range.ANY,
                    "The op levels (aka permission levels) required to enable/disable Crust's various modes.",
                    "Vanilla op levels used are:",
                    "  " + CommandUtil.PERMISSION_NONE + " - Chat/whispers, Access to limited info",
                    "  " + CommandUtil.PERMISSION_TRUSTED + " - Can bypass spawn protection",
                    "  " + CommandUtil.PERMISSION_CHEAT + " - Can use cheats, Access to info that can be used to cheat",
                    "  " + CommandUtil.PERMISSION_MODERATE + " - Can ban/whitelist players, 'Moderator'",
                    "  " + CommandUtil.PERMISSION_SERVER_OP + " - All permissions, Server management" ), RestartNote.WORLD );
            multiMineOpLevel = SPEC.define( new IntField( "op_level.multi_mine",
                    CommandUtil.PERMISSION_NONE, IntField.Range.ANY ) );
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
        }
    }
    
    /**
     * Category for magnet mode.
     */
    public static class Magnet extends AbstractConfigCategory<CrustModesConfigFile> {
        
        public final DoubleField maxRangeLimit;
        public final DoubleField maxSpeed;
        
        Magnet( CrustModesConfigFile parent ) {
            super( parent, "magnet_mode",
                    "Options that apply to Crust's magnet mode." );
            
            maxRangeLimit = SPEC.define( new DoubleField( "max_range_limit", 8.0, DoubleField.Range.NON_NEGATIVE,
                    "The highest maximum range (blocks) allowed for magnet mode. Max range is a client preference." ) );
            maxSpeed = SPEC.define( new ScaledDoubleField.Rate( "max_speed", 10.0, DoubleField.Range.NON_NEGATIVE,
                    "The maximum speed (blocks/sec) for items pulled by magnet mode.",
                    "Speed is higher the closer the item is to the player, scaling down to 0 m/s at the player's max range." ) );
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
            
            speedLimit = SPEC.define( new DoubleField( "speed_limit", 10.0, DoubleField.Range.NON_NEGATIVE,
                    "The highest maximum speed multiplier allowed for super-speed mode. Actual speed is a client preference." ) );
        }
    }
}