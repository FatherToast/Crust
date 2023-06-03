package fathertoast.crust.test.common;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.common.core.Crust;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber( modid = Crust.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class TestModEventHandler {
    
    /** File for testing the config api. */
    public static final TestConfigFile TEST_CONFIG = new TestConfigFile(
            ConfigManager.getRequired( Crust.MOD_ID ), "test_config" );
    
    @SubscribeEvent
    static void onCommonSetup( FMLCommonSetupEvent event ) {
        TEST_CONFIG.SPEC.initialize();
    }
}