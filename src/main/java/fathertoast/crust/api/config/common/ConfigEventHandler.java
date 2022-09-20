package fathertoast.crust.api.config.common;

import fathertoast.crust.common.core.Crust;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber( modid = Crust.MOD_ID )
public class ConfigEventHandler {
    
    private static byte DYNAMIC_REGISTRY_VERSION;
    
    /** @return The current "version" of the dynamic registries. This is incremented each time resources are loaded. */
    public static byte getDynamicRegVersion() { return DYNAMIC_REGISTRY_VERSION; }
    
    /** Called each time resources are loaded. */
    @SubscribeEvent
    static void onResourceReload( AddReloadListenerEvent event ) { DYNAMIC_REGISTRY_VERSION++; }
}