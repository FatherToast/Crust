package fathertoast.crust.test.common;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.EnvironmentListField;
import fathertoast.crust.api.config.common.field.collection.*;

/**
 * Readme config dedicated to printing detailed/verbose descriptions
 * for every field type that provides it.
 */
public class TestConfigReadme extends AbstractConfigFile.Simple {
    
    /**
     * @param cfgName Name for the new config file. May include a file path (e.g. "folder/subfolder/filename").
     */
    public TestConfigReadme( String cfgName ) {
        super( ICrustApi.MOD_ID, cfgName, false,
                "Contains detailed/verbose descriptions of every field type that provides it." );
        
        // Field descriptions
        EnvironmentListField.describe1of2( SPEC );
        
        BlockStateListField.describe( SPEC );
        BlockStateMapField.describe( SPEC );
        BlockStateSetField.describe( SPEC );
        BlockStateValueListField.describe( SPEC );
        BlockStateWeightedListField.describe( SPEC );
        BlockStateWeightedValueListField.describe( SPEC );
        
        EntityMapField.describe( SPEC );
        EntitySetField.describe( SPEC );
        
        RegistryListField.describe( SPEC );
        RegistryMapField.describe( SPEC );
        RegistrySetField.describe( SPEC );
        RegistryValueListField.describe( SPEC );
        RegistryWeightedListField.describe( SPEC );
        RegistryWeightedValueListField.describe( SPEC );
        
        SPEC.define( new BooleanField( "toast_mode", true,
                "This field only exists to prevent the spec from getting angry." ) );
        
        SPEC.fileOnlyNewLine();
        EnvironmentListField.describe2of2( SPEC );
    }
}