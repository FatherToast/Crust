package fathertoast.crust.client.config;

import fathertoast.crust.api.config.client.ClientConfigUtil;
import fathertoast.crust.api.config.client.gui.widget.SearchableSelectionList;
import fathertoast.crust.api.config.client.gui.widget.field.searchbar.Searchbar;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.*;
import fathertoast.crust.api.config.common.value.CrustAnchor;
import fathertoast.crust.client.screen.widget.CrustConfigFileList;
import net.minecraftforge.fml.ModList;

/**
 * File for configuring various things related to the in-game config editor GUI.
 */
public class CfgEditorCrustConfig extends AbstractConfigFile {
    
    public final Button MAIN_BUTTON;
    public final Button PAUSE_BUTTON;
    public final EditScreen EDIT_SCREEN;
    public final Misc MISC;
    
    /**
     * @param cfgManager The mod's config manager.
     * @param cfgName    Name for the new config file. May include a file path (e.g. "folder/subfolder/filename").
     */
    public CfgEditorCrustConfig( ConfigManager cfgManager, String cfgName ) {
        super( cfgManager, cfgName, true,
                "In-game config editor client preferences." );
        
        // Move the default button position to the right side if Quark is installed to avoid conflicting with its default
        boolean buttonConflict = ModList.get().isLoaded( "quark" );
        
        MAIN_BUTTON = new Button( this, "main_menu_button",
                "Options to modify the in-game config editor button on the main menu.",
                -56, buttonConflict,
                "Set this to false to hide the config editor button on the main menu." );
        PAUSE_BUTTON = new Button( this, "pause_menu_button",
                "Options to modify the in-game config editor button on the pause menu.",
                -44, buttonConflict,
                "Set this to false to hide the in-game config editor button.",
                "You may assign a hotkey to the editor in your options, whether or not you choose to display a button." );
        EDIT_SCREEN = new EditScreen( this, "editor_screen" );
        MISC = new Misc( this, "misc" );
    }
    
    
    /**
     * Category for config editor buttons.
     */
    public static class Button extends AbstractConfigCategory<CfgEditorCrustConfig> {
        
        public final BooleanField enabled;
        
        public final EnumField<CrustAnchor> anchorY;
        public final EnumField<CrustAnchor> anchorX;
        
        public final IntField offsetY;
        public final IntField offsetX;
        
        Button( CfgEditorCrustConfig parent, String category, String categoryDescription,
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
    
    /**
     * Category for config editor buttons.
     */
    public static class EditScreen extends AbstractConfigCategory<CfgEditorCrustConfig> {
        
        public final EnumField<CrustConfigFileList.Visibility> fileVisibility;
        
        public final EnumField<Searchbar.Orientation> searchBarOrientation;
        public final BooleanField showSearchHighlights;
        public final ColorIntField highlightColor;
        
        EditScreen( CfgEditorCrustConfig parent, String category ) {
            super( parent, category,
                    "Contains settings for the in-game editor appearance and function." );
            
            fileVisibility = SPEC.define( new EnumField<>( "file_visibility",
                    CrustConfigFileList.Visibility.HIDE_UNREADABLE,
                    "Whether to show all files, or hide files you don't have permission to read or edit." ) );
            
            SPEC.newLine();
            
            Searchbar.orientation = searchBarOrientation = SPEC.define( new EnumField<>( "search_bar.orientation", Searchbar.Orientation.LEFT,
                    "Determines the orientation and placement of the searchbar and its navigation buttons on the screen." ) );
            Searchbar.showHighlights = showSearchHighlights = SPEC.define( new BooleanField(
                    "search_bar.show_highlights", true,
                    "If true, search results found by the searchbar will be highlighted.",
                    "The searchbar will still auto-scroll to the first match even if this is disabled." ) );
            Searchbar.highlightColor = highlightColor = SPEC.define( new ColorIntField(
                    "search_bar.highlight_color", 0x45_FFFF5D, true,
                    "If the above setting is enabled, this field determines the highlight color for search results." ) );
        }
    }
    
    /**
     * Category for settings that don't fit elsewhere.
     */
    public static class Misc extends AbstractConfigCategory<CfgEditorCrustConfig> {
        
        public final BooleanField ignoreBrokenConfigs;
        
        Misc( CfgEditorCrustConfig parent, String category ) {
            super( parent, category,
                    "Contains settings that don't fit in other categories." );
            
            ignoreBrokenConfigs = SPEC.define( new BooleanField( "ignore_broken_configs", false,
                    "If true, the \"broken configs\" screen will not open when trying to load a world if any Crust-based configs are broken." ) );
        }
    }
}