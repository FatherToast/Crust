package fathertoast.crust.test.client;

import fathertoast.crust.common.core.Crust;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = Crust.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class TestClientModEventHandler {
    
    /** Called after common setup to perform client-side-only setup. */
    @SubscribeEvent
    static void onClientSetup( FMLClientSetupEvent event ) {
        TestClientForgeEventHandler.register();
    }
}