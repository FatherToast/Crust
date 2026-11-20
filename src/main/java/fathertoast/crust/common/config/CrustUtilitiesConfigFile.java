package fathertoast.crust.common.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.lib.CrustCmdHelper;

/**
 * File for configuring properties for misc Crust utilities.
 */
public class CrustUtilitiesConfigFile extends AbstractConfigFile {
    
    public final Configs CONFIGS;
    public final FeatureGenerator FEATURE_GEN;
    public final OptionalDataSharing OPTIONAL_DATA_SHARING;
    
    /**
     * @param cfgManager The mod's config manager.
     * @param cfgName    Name for the new config file. May include a file path (e.g. "folder/subfolder/filename").
     */
    CrustUtilitiesConfigFile( ConfigManager cfgManager, String cfgName ) {
        super( cfgManager, cfgName, false,
                "This config contains options related to various tools and utilities provided by Crust." );
        
        CONFIGS = new Configs( this );
        FEATURE_GEN = new FeatureGenerator( this );
        OPTIONAL_DATA_SHARING = new OptionalDataSharing( this );
    }
    
    /**
     * Category for server-sided config related dongles.
     */
    public static class Configs extends AbstractConfigCategory<CrustUtilitiesConfigFile> {
        
        public final IntField viewConfigsOpLevel;
        
        Configs( CrustUtilitiesConfigFile parent ) {
            super( parent, "configs",
                    "Options that apply to Crust configs, in general." );
            
            viewConfigsOpLevel = SPEC.define( new IntField( "op_level.view_server_configs",
                    CrustCmdHelper.PERMISSION_MODERATE, IntField.Range.ANY,
                    "The op level (aka permission level) required for read-only access to server configs " +
                            "through the in-game config editor. Only server operators (op level = " +
                            CrustCmdHelper.PERMISSION_SERVER_OP + ") have edit access to server configs, regardless of " +
                            "this setting.",
                    "Vanilla op levels used are:",
                    "  " + CrustCmdHelper.PERMISSION_NONE + " - Chat/whispers, Access to limited info",
                    "  " + CrustCmdHelper.PERMISSION_TRUSTED + " - Can bypass spawn protection",
                    "  " + CrustCmdHelper.PERMISSION_CHEAT + " - Can use cheats, Access to info that can be used to cheat",
                    "  " + CrustCmdHelper.PERMISSION_MODERATE + " - Can ban/whitelist players, 'Moderator'",
                    "  " + CrustCmdHelper.PERMISSION_SERVER_OP + " - All permissions, Server management" ), true );
        }
    }
    
    /**
     * Category for feature generator related thingies.
     */
    public static class FeatureGenerator extends AbstractConfigCategory<CrustUtilitiesConfigFile> {
        
        public final BooleanField debugMode;
        
        FeatureGenerator( CrustUtilitiesConfigFile parent ) {
            super( parent, "feature_generator",
                    "Options that apply to the 'feature generator' structure block." );
            
            debugMode = SPEC.define( new BooleanField( "debug_mode", false,
                    "If enabled, the feature generator will print console warnings for debug when " +
                            "something might have gone wrong during placement." ) );
        }
    }
    
    /**
     * Category for misc optional data sharing to clients
     */
    public static class OptionalDataSharing extends AbstractConfigCategory<CrustUtilitiesConfigFile> {
        
        public final BooleanField pathfinderData;
        public final IntField pathfinderDataInterval;
        public final IntField pathfinderDataDistance;
        
        OptionalDataSharing( CrustUtilitiesConfigFile parent ) {
            super( parent, "optional_data_sharing",
                    "Options that apply to various types of optional data being sent from server to clients." );
            
            SPEC.category( "pathfinder_data" );
            pathfinderData = SPEC.define( new BooleanField( "enabled", false,
                    "If enabled, the server will send pathfinder data to all connected clients that ask for it.",
                    "This is currently only used for allowing clients to visualize mob's pathfinding for debug purposes.",
                    "Unless you are actively using Crust's pathfinder visualizer, this option should be disabled as it can be resource intensive." ) );
            
            pathfinderDataInterval = SPEC.define( new IntField( "interval", 20, IntField.Range.NON_NEGATIVE,
                    "If the above option is enabled, this setting determines the interval (in ticks) between each time the server " +
                            "calculates and sends pathfinder data to clients.",
                    "Lower numbers result in higher precision, but can cause lag." ) );
            
            pathfinderDataDistance = SPEC.define( new IntField( "distance", 128, 16, 1024,
                    "If 'pathfinder_data.enabled' is true, this setting limits how far away a player can be from " +
                            "a mob and still receive pathfinder data for it.",
                    "For example, a value of 64 would mean the player has to be within 64 blocks of a mob to receive data for it.",
                    "Note that this value is capped by factors such as simulation distance and chunk loading distance; " +
                            "a mob must be loaded in order to have its data sent." ) );
        }
    }
}