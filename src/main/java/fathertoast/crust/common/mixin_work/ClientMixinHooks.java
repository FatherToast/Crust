package fathertoast.crust.common.mixin_work;

import fathertoast.crust.api.config.client.gui.screen.BrokenConfigsScreen;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.client.ClientRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

public class ClientMixinHooks {
    
    private static boolean alreadyWarnedBrokenCfgs = false;
    
    /**
     * Checks if any Crust-based configs failed parsing/loading
     * and informs the user about it before world loading.
     */
    public static void handleOnLoadLevel( WorldOpenFlows wof, Screen screen, String worldId, CallbackInfo ci ) {
        // Check if we should even open the screen
        if( alreadyWarnedBrokenCfgs || ClientRegister.CONFIG_EDITOR.MISC.ignoreBrokenConfigs.get() ) return;
        
        final List<String> brokenConfigs = new ArrayList<>();
        
        // Collect names/paths of broken configs from all config managers
        for( ConfigManager cfgManager : ConfigManager.getAll() ) {
            for( AbstractConfigFile config : cfgManager.getConfigs() ) {
                if( !config.SPEC.isInitialized() ) {
                    brokenConfigs.add( config.SPEC.getFilePath() );
                }
            }
        }
        
        // Prompt user about broken configs, if any
        if( !brokenConfigs.isEmpty() ) {
            ci.cancel();
            alreadyWarnedBrokenCfgs = true;
            Minecraft.getInstance().setScreen( new BrokenConfigsScreen( () -> wof.loadLevel( screen, worldId ), brokenConfigs ) );
        }
    }
}
