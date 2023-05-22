package fathertoast.crust.client;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.EnumField;
import fathertoast.crust.api.config.common.field.IntField;

/**
 * File for configuring the in-game config editor button.
 */
public class CfgEditorCrustConfigFile extends AbstractConfigFile {
    
    //public final General GENERAL;
    public final Button BUTTON;
    
    /**
     * @param cfgManager The mod's config manager.
     * @param cfgName    Name for the new config file. May include a file path (e.g. "folder/subfolder/filename").
     */
    CfgEditorCrustConfigFile( ConfigManager cfgManager, String cfgName ) {
        super( cfgManager, cfgName,
                "In-game config editor client preferences." );
        
        //GENERAL = new General( this );
        BUTTON = new Button( this );
    }
    
    /**
     * Category for the pause menu config editor button display.
     */
    public static class Button extends AbstractConfigCategory<CfgEditorCrustConfigFile> {
        
        public final BooleanField enabled;
        
        public final EnumField<Anchor> anchorY;
        public final EnumField<Anchor> anchorX;
        
        public final IntField offsetY;
        public final IntField offsetX;
        
        Button( CfgEditorCrustConfigFile parent ) {
            super( parent, "pause_menu_button",
                    "Options to modify the in-game config editor button on the main pause menu." );
            
            enabled = SPEC.define( new BooleanField( "enabled", true,
                    "Set this to false to hide the in-game config editor button.",
                    "You may assign a hotkey to the editor in your options, whether or not you choose to display a button." ) );
            
            SPEC.newLine();
            
            anchorY = SPEC.define( new EnumField<>( "anchor.vertical", Anchor.BOTTOM, Anchor.VERTICAL,
                    "The anchor position for the config editor button. That is, where it should be positioned " +
                            "relative to the screen or vanilla pause menu buttons." ) );
            anchorX = SPEC.define( new EnumField<>( "anchor.horizontal", Anchor.LEFT, Anchor.HORIZONTAL,
                    (String[]) null ) );
            
            SPEC.newLine();
            
            offsetY = SPEC.define( new IntField( "offset.vertical", -44, IntField.Range.ANY,
                    "The position offset for the config editor button from the anchor position, in GUI pixels. " +
                            "Negative values move the button toward the top/left, positive move it toward the bottom/right." ) );
            offsetX = SPEC.define( new IntField( "offset.horizontal", -4, IntField.Range.ANY,
                    (String[]) null ) );
        }
    }
    
    public enum Anchor {
        CENTER, SCREEN_LEFT, LEFT, SCREEN_RIGHT, RIGHT, SCREEN_TOP, TOP, SCREEN_BOTTOM, BOTTOM;
        
        public static final Anchor[] VERTICAL = { SCREEN_TOP, TOP, CENTER, BOTTOM, SCREEN_BOTTOM };
        public static final Anchor[] HORIZONTAL = { SCREEN_LEFT, LEFT, CENTER, RIGHT, SCREEN_RIGHT };
        
        /**
         * @param screenSize X- or Y-size of the entire game screen.
         * @param guiSize    X- or Y-size of the active GUI window.
         * @param size       X- or Y-size of the anchored GUI element.
         * @return The anchored position to render at; that is, the position of the top-left corner.
         */
        public int pos( int screenSize, int guiSize, int size ) {
            switch( this ) {
                case SCREEN_TOP: case SCREEN_LEFT:
                    return 0;
                case SCREEN_BOTTOM: case SCREEN_RIGHT:
                    return screenSize - size;
                case TOP:
                    return screenSize / 4 + 8 - size;
                case LEFT:
                    return (screenSize - guiSize) / 2 - size;
                case BOTTOM:
                    return screenSize / 4 + 8 + guiSize;
                case RIGHT:
                    return (screenSize + guiSize) / 2;
                default:
                    return (screenSize - size) / 2;
            }
        }
    }
}