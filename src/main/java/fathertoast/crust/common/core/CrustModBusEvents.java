package fathertoast.crust.common.core;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.config.common.ConfigManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

@Mod.EventBusSubscriber( modid = ICrustApi.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public final class CrustModBusEvents {
    
    /** Called when mod loading is completed. */
    @SubscribeEvent
    static void onLoadComplete( FMLLoadCompleteEvent event ) {
        ConfigManager.GLOBAL_FREEZE_FILE_WATCHERS = false;
    }
    
    
    // Static listener, no instantiation
    private CrustModBusEvents() { }
}