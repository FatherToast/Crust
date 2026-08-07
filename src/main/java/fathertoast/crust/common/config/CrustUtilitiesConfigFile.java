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
    
    /**
     * @param cfgManager The mod's config manager.
     * @param cfgName    Name for the new config file. May include a file path (e.g. "folder/subfolder/filename").
     */
    CrustUtilitiesConfigFile( ConfigManager cfgManager, String cfgName ) {
        super( cfgManager, cfgName, false,
                "This config contains options related to various tools and utilities provided by Crust." );
        
        CONFIGS = new Configs( this );
        FEATURE_GEN = new FeatureGenerator( this );
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
}