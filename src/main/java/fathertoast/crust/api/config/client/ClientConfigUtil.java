package fathertoast.crust.api.config.client;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.common.core.Crust;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

import java.io.File;

/**
 * This class contains static helper methods for client-side config functions.
 */
@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = Crust.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE )
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
            Crust.LOG.warn( "Mod '{}' attempted to assign a config button action, but has no config!", modId );
        }
        else {
            ctx.registerExtensionPoint( ExtensionPoint.CONFIGGUIFACTORY,
                    () -> ( game, parent ) -> new OpenFolderScreen( cfgManager.DIR ) );
        }
    }
    
    
    // ---- Internal Methods ---- //
    
    /** Called before a GUI opens. */
    @SuppressWarnings( "ProtectedMemberInFinalClass" ) // Forge won't let us use private, so we do this instead
    @SubscribeEvent
    protected static void onGuiOpen( GuiOpenEvent event ) {
        if( event.getGui() instanceof OpenFolderScreen ) {
            event.setCanceled( true );
            Util.getPlatform().openFile( ((OpenFolderScreen) event.getGui()).dirToOpen );
        }
    }
    
    /**
     * This "screen" is effectively a redirect. Attempting to open this screen instead opens the specified folder.
     */
    private static class OpenFolderScreen extends Screen {
        final File dirToOpen;
        
        OpenFolderScreen( File dir ) {
            // We don't need to localize the name or do anything since the opening of this screen is always canceled
            super( new StringTextComponent( "Opening folder" ) );
            dirToOpen = dir;
        }
    }
}