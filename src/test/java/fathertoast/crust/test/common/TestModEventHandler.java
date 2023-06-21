package fathertoast.crust.test.common;

import fathertoast.crust.api.ICrustApi;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber( modid = ICrustApi.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class TestModEventHandler {
    
    @SubscribeEvent
    static void onCommonSetup( FMLCommonSetupEvent event ) {
        TestCrust.CONFIG.SPEC.initialize();
    }
}