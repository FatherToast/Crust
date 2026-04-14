package fathertoast.crust.common.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;

/**
 * File for configuring properties for misc Crust utilities.
 */
public class CrustUtilitiesConfigFile extends AbstractConfigFile {
    
    public final FeatureGenerator FEATURE_GEN;
    
    /**
     * @param cfgManager The mod's config manager.
     * @param cfgName    Name for the new config file. May include a file path (e.g. "folder/subfolder/filename").
     */
    CrustUtilitiesConfigFile( ConfigManager cfgManager, String cfgName ) {
        super( cfgManager, cfgName,
                "This config contains options related to verious tools and utilities provided by Crust." );
        
        FEATURE_GEN = new FeatureGenerator( this );
    }
    
    /**
     * Category for feature generator related thingies.
     */
    public static class FeatureGenerator extends AbstractConfigCategory<CrustUtilitiesConfigFile> {
        
        public final BooleanField debugMode;
        
        
        FeatureGenerator( CrustUtilitiesConfigFile parent ) {
            super( parent, "feature_generator",
                    "Options that apply to the 'feature generator' structure block." );
            
            SPEC.increaseIndent();
            
            debugMode = SPEC.define( new BooleanField( "debug_mode", false,
                    "If enabled, the feature generator will print console warnings for debug when " +
                            "something might have gone wrong during placement." ) );
            
            SPEC.decreaseIndent();
        }
    }
}
