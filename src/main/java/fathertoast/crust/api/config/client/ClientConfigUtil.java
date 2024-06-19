package fathertoast.crust.api.config.client;

import fathertoast.crust.api.config.client.gui.screen.CrustConfigSelectScreen;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.ConfigUtil;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;

/**
 * This class contains static helper methods for client-side config functions.
 */
public final class ClientConfigUtil {
    
    /**
     * Call this during client setup event to allow users to open your mod's edit config screen from the mods
     * screen by pressing the "Config" button.
     *
     * @see net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
     */
    public static void registerConfigButtonAsEditScreen() {
        ModLoadingContext ctx = ModLoadingContext.get();
        String modId = ctx.getActiveNamespace();
        ConfigManager cfgManager = ConfigManager.get( modId );
        
        if( cfgManager == null ) {
            ConfigUtil.LOG.warn( "Mod '{}' attempted to assign a config button action, but has no config!", modId );
        }
        else {
            ctx.registerExtensionPoint( ConfigScreenHandler.ConfigScreenFactory.class,
                    (  ) -> new ConfigScreenHandler.ConfigScreenFactory( ( mc, parent ) -> new CrustConfigSelectScreen( null, cfgManager ) ) );
        }
    }
    
    /**
     * Call this during client setup event to allow users to open your mod's config directory from the mods
     * screen by pressing the "Config" button.
     *
     * @see net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
     */
    @SuppressWarnings( "unused" )
    public static void registerConfigButtonAsOpenFolder() {
        ModLoadingContext ctx = ModLoadingContext.get();
        String modId = ctx.getActiveNamespace();
        ConfigManager cfgManager = ConfigManager.get( modId );
        
        if( cfgManager == null ) {
            ConfigUtil.LOG.warn( "Mod '{}' attempted to assign a config button action, but has no config!", modId );
        }
        else {
            ctx.registerExtensionPoint( ConfigScreenHandler.ConfigScreenFactory.class,
                    ( ) -> new ConfigScreenHandler.ConfigScreenFactory(( mc, parent ) -> new OpenFolderScreen( cfgManager ) ) );
            
            MinecraftForge.EVENT_BUS.addListener( new OpenFolderHandler( cfgManager )::onGuiOpen );
        }
    }
    
    
    // ---- Internal Methods ---- //
    
    /** Watches opening screens to actually open the config folder when it detects the right screen. */
    private static class OpenFolderHandler {
        
        final ConfigManager CFG_MANAGER;
        
        OpenFolderHandler( ConfigManager cfgManager ) { CFG_MANAGER = cfgManager; }
        
        /** Called before a GUI opens. */
        void onGuiOpen( ScreenEvent.Opening event ) {
            if( event.getNewScreen() instanceof OpenFolderScreen &&
                    CFG_MANAGER.equals( ((OpenFolderScreen) event.getNewScreen()).CFG_MANAGER ) ) {
                event.setCanceled( true );
                Util.getPlatform().openFile( CFG_MANAGER.DIR );
            }
        }
    }
    
    /** This "screen" is effectively a redirect. Attempting to open this screen instead opens the config manager's directory. */
    private static class OpenFolderScreen extends Screen {
        
        final ConfigManager CFG_MANAGER;
        
        OpenFolderScreen( ConfigManager cfgManager ) {
            // We don't need to localize the name or do anything since the opening of this screen is always canceled
            super( Component.literal("Opening folder") );
            CFG_MANAGER = cfgManager;
        }
    }
}