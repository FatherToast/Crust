package fathertoast.crust.api.config.client;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.ConfigUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;

/**
 * This class contains static helper methods for client-side config functions.
 */
public final class ClientConfigUtil {
    
    /**
     * Call this during client setup event to allow users to open your mod's config directory from the mods
     * screen by pressing the "Config" button.
     *
     * @see net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
     */
    public static void registerConfigButtonAsOpenFolder() {
        ModLoadingContext ctx = ModLoadingContext.get();
        String modId = ctx.getActiveNamespace();
        ConfigManager cfgManager = ConfigManager.get( modId );
        
        if( cfgManager == null ) {
            ConfigUtil.LOG.warn( "Mod '{}' attempted to assign a config button action, but has no config!", modId );
        }
        else {
            ctx.registerExtensionPoint( ExtensionPoint.CONFIGGUIFACTORY,
                    () -> ( game, parent ) -> new OpenFolderScreen( cfgManager ) );
            
            MinecraftForge.EVENT_BUS.addListener( new OpenFolderHandler( cfgManager )::onGuiOpen );
        }
    }
    
    
    // ---- Internal Methods ---- //
    
    /** Watches opening screens to actually open the config folder when it detects the right screen. */
    private static class OpenFolderHandler {
        
        final ConfigManager CFG_MANAGER;
        
        OpenFolderHandler( ConfigManager cfgManager ) { CFG_MANAGER = cfgManager; }
        
        /** Called before a GUI opens. */
        void onGuiOpen( GuiOpenEvent event ) {
            if( event.getGui() instanceof OpenFolderScreen &&
                    CFG_MANAGER.equals( ((OpenFolderScreen) event.getGui()).CFG_MANAGER ) ) {
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
            super( new StringTextComponent( "Opening folder" ) );
            CFG_MANAGER = cfgManager;
        }
    }
}