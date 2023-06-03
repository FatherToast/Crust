package fathertoast.crust.test.common;

import fathertoast.crust.common.core.Crust;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber( modid = Crust.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class TestModEventHandler {
    
    @SubscribeEvent
    static void onCommonSetup( FMLCommonSetupEvent event ) {
        TestCrust.CONFIG.SPEC.initialize();
    }
}