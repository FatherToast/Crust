package fathertoast.crust.client;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.EnumField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.config.common.value.CrustAnchor;
import net.minecraftforge.fml.ModList;

/**
 * File for configuring the in-game config editor button.
 */
public class CfgEditorCrustConfigFile extends AbstractConfigFile {
    
    //public final General GENERAL;
    public final Button MAIN_BUTTON;
    public final Button PAUSE_BUTTON;
    
    /**
     * @param cfgManager The mod's config manager.
     * @param cfgName    Name for the new config file. May include a file path (e.g. "folder/subfolder/filename").
     */
    CfgEditorCrustConfigFile( ConfigManager cfgManager, String cfgName ) {
        super( cfgManager, cfgName,
                "In-game config editor client preferences." );
        
        // Move the default button position to the right side if Quark is installed to avoid conflicting with its default
        boolean buttonConflict = ModList.get().isLoaded( "quark" );
        
        //GENERAL = new General( this );
        MAIN_BUTTON = new Button( this, "main_menu_button",
                "Options to modify the in-game config editor button on the main menu.",
                -56, buttonConflict,
                "Set this to false to hide the config editor button on the main menu." );
        PAUSE_BUTTON = new Button( this, "pause_menu_button",
                "Options to modify the in-game config editor button on the pause menu.",
                -44, buttonConflict,
                "Set this to false to hide the in-game config editor button.",
                "You may assign a hotkey to the editor in your options, whether or not you choose to display a button." );
    }
    
    /**
     * Category for config editor buttons.
     */
    public static class Button extends AbstractConfigCategory<CfgEditorCrustConfigFile> {
        
        public final BooleanField enabled;
        
        public final EnumField<CrustAnchor> anchorY;
        public final EnumField<CrustAnchor> anchorX;
        
        public final IntField offsetY;
        public final IntField offsetX;
        
        Button( CfgEditorCrustConfigFile parent, String category, String categoryDescription,
                int offV, boolean buttonConflict, String... enabledComment ) {
            super( parent, category, categoryDescription );
            
            enabled = SPEC.define( new BooleanField( "enabled", true, enabledComment ) );
            
            SPEC.newLine();
            
            anchorY = SPEC.define( new EnumField<>( "anchor.vertical", CrustAnchor.BOTTOM, CrustAnchor.VERTICAL_GUI,
                    "The anchor position for the config editor button. That is, where it should be positioned " +
                            "relative to the screen or vanilla menu buttons." ) );
            anchorX = SPEC.define( new EnumField<>( "anchor.horizontal",
                    buttonConflict ? CrustAnchor.RIGHT : CrustAnchor.LEFT, CrustAnchor.HORIZONTAL_GUI,
                    (String[]) null ) );
            
            SPEC.newLine();
            
            offsetY = SPEC.define( new IntField( "offset.vertical", offV, IntField.Range.ANY,
                    "The position offset for the config editor button from the anchor position, in GUI pixels. " +
                            "Negative values move the button toward the top/left, positive move it toward the bottom/right." ) );
            offsetX = SPEC.define( new IntField( "offset.horizontal", buttonConflict ? 4 : -4, IntField.Range.ANY,
                    (String[]) null ) );
        }
    }
}