package fathertoast.crust.api.client.accessor;

import fathertoast.crust.api.config.client.gui.widget.field.Searchbar;

/**
 * Exposes some of Crust's client configs' values for reading only (no write access).
 */
public interface IClientConfigAccessor {
    
    //------------------------------------------------------
    //          CONFIG "client_render_settings"
    //------------------------------------------------------
    
    boolean getFancyFishingEnabled();
    
    /** Category: "block_entity_debug_shapes". */
    boolean getBlockEntityShapesEnabled();
    
    /** Category: "block_entity_debug_shapes". */
    int getBlockEntityShapesDistance();
    
    
    /** Category: "entity_debug_shapes". */
    boolean getEntityShapesEnabled();
    
    /** Category: "entity_debug_shapes". */
    double getEntityShapesDistance();
    
    
    //------------------------------------------------------
    //          CONFIG "client_config_editor"
    //------------------------------------------------------
    
    /** Category: "searchbar_properties". */
    Searchbar.Orientation getSearchbarOrientation();
    
    /** Category: "searchbar_properties". */
    boolean getShowSearchHighlights();
    
    /**
     * Category: "searchbar_properties"
     *
     * @return The configured highlight color, as a hexadecimal color int.
     */
    int getHighlightColor();
    
    
    /** Category: "misc". */
    boolean getIgnoreBrokenConfigs();
}
