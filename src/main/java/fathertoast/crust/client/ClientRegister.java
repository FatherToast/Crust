package fathertoast.crust.client;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.config.client.ClientConfigUtil;
import fathertoast.crust.api.config.common.ConfigManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = ICrustApi.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class ClientRegister {
    
    /** File for configuring in-game config edit button client preferences. */
    public static final CfgEditorCrustConfigFile CONFIG_EDITOR = new CfgEditorCrustConfigFile(
            ConfigManager.getRequired( ICrustApi.MOD_ID ), "client_config_editor" );
    /** File for configuring extra inventory button client preferences. */
    public static final ExtraInvButtonsCrustConfigFile EXTRA_INV_BUTTONS = new ExtraInvButtonsCrustConfigFile(
            ConfigManager.getRequired( ICrustApi.MOD_ID ), "client_extra_inv_buttons" );
    
    /** Called after common setup to perform client-side-only setup. */
    @SubscribeEvent
    static void onClientSetup( FMLClientSetupEvent event ) {
        // Perform first-time loading of the client-only configs
        CONFIG_EDITOR.SPEC.initialize();
        EXTRA_INV_BUTTONS.SPEC.initialize();
        
        // Tell Forge to open the config editor when our mod's "Config" button is clicked in the Mods screen
        ClientConfigUtil.registerConfigButtonAsEditScreen();
        
        KeyBindingEvents.register();
    }
}