package fathertoast.crust.test.client;

import fathertoast.crust.api.ICrustApi;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = ICrustApi.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class TestClientModEventHandler {
    
    /** Called after common setup to perform client-side-only setup. */
    @SubscribeEvent
    static void onClientSetup( FMLClientSetupEvent event ) {
        TestClientForgeEventHandler.register( );

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        modBus.addListener( TestClientForgeEventHandler::registerKeyBindings );
    }
}