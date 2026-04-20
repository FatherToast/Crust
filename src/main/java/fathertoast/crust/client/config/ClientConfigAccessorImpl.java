package fathertoast.crust.client.config;

import fathertoast.crust.api.client.accessor.IClientConfigAccessor;
import fathertoast.crust.api.config.client.gui.widget.field.searchbar.Searchbar;

import static fathertoast.crust.client.ClientRegister.CONFIG_EDITOR;
import static fathertoast.crust.client.ClientRegister.RENDER_SETTINGS;

public class ClientConfigAccessorImpl implements IClientConfigAccessor {
    
    //------------------------------------------------------
    //          CONFIG "client_render_settings"
    //------------------------------------------------------
    
    @Override
    public boolean getFancyFishingEnabled() {
        return RENDER_SETTINGS.fancyFishing.get();
    }
    
    @Override
    public boolean getBlockEntityShapesEnabled() {
        return RENDER_SETTINGS.blockEntityShapes.get();
    }
    
    @Override
    public int getBlockEntityShapesDistance() {
        return RENDER_SETTINGS.blockEntityShapesDistance.get();
    }
    
    @Override
    public boolean getEntityShapesEnabled() {
        return RENDER_SETTINGS.entityShapes.get();
    }
    
    @Override
    public double getEntityShapesDistance() {
        return RENDER_SETTINGS.entityShapesDistanceSqr.get();
    }
    
    
    //------------------------------------------------------
    //          CONFIG "client_config_editor"
    //------------------------------------------------------
    
    @Override
    public Searchbar.Orientation getSearchbarOrientation() {
        return CONFIG_EDITOR.SEARCHBAR.orientation.get();
    }
    
    @Override
    public boolean getShowSearchHighlights() {
        return CONFIG_EDITOR.SEARCHBAR.showSearchHighlights.get();
    }
    
    @Override
    public int getHighlightColor() {
        return CONFIG_EDITOR.SEARCHBAR.highlightColor.get();
    }
    
    @Override
    public boolean getIgnoreBrokenConfigs() {
        return CONFIG_EDITOR.MISC.ignoreBrokenConfigs.get();
    }
}
