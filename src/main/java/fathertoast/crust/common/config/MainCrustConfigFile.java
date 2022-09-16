package fathertoast.crust.common.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.DoubleField;

/**
 *
 */
public class MainCrustConfigFile extends AbstractConfigFile {
    
    public final General GENERAL;
    
    /**
     * @param cfgManager The mod's config manager.
     * @param cfgName    Name for the new config file. May include a file path (e.g. "folder/subfolder/filename").
     */
    MainCrustConfigFile( ConfigManager cfgManager, String cfgName ) {
        super( cfgManager, cfgName,
                "This config contains options that apply to the Crust mod as a whole." );
        
        GENERAL = new General( this );
    }
    
    /**
     *
     */
    public static class General extends AbstractConfigCategory<MainCrustConfigFile> {
        
        public final BooleanField boolField;
        
        public final DoubleField numberField;
        
        General( MainCrustConfigFile parent ) {
            super( parent, "general",
                    "Options that apply to the Special Mobs mod as a whole.",
                    "Also includes several 'master toggles' for convenience." );
            
            boolField = SPEC.define( new BooleanField( "test_boolean_field", false,
                    "Test field with a true/false value.",
                    "Woo woo wee wee!" ) );
            
            SPEC.newLine();
            
            numberField = SPEC.define( new DoubleField( "test_double_field", 0.07, DoubleField.Range.PERCENT,
                    "This is a test field that has a number!" ) );
        }
    }
}