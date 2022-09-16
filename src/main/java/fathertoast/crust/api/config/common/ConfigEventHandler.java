package fathertoast.crust.api.config.common;

import fathertoast.crust.common.core.Crust;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;

@Mod.EventBusSubscriber( modid = Crust.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE )
public class ConfigEventHandler {
    
    /** The current "version" of the dynamic registries. This is incremented each time dynamic registries are loaded. */
    public static byte DYNAMIC_REGISTRY_VERSION;
    
    /** Called when a server (integrated or dedicated) is about to start. */
    @SubscribeEvent
    public static void onServerAboutToStart( FMLServerAboutToStartEvent event ) { DYNAMIC_REGISTRY_VERSION++; }
}