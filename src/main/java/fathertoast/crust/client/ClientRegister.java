package fathertoast.crust.client;

import fathertoast.crust.api.config.client.ClientConfigUtil;
import fathertoast.crust.common.core.Crust;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = Crust.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class ClientRegister {
    
    /** Called after common setup to perform client-side-only setup. */
    @SubscribeEvent
    static void onClientSetup( FMLClientSetupEvent event ) {
        // Tell Forge to open our config folder when our mod's "Config" button is clicked in the Mods screen
        ClientConfigUtil.registerConfigButtonAsOpenFolder();
    }
}