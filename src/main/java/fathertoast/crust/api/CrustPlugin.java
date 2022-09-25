package fathertoast.crust.api;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Marker annotation for classes that should be treated as plugins
 * for Crust. To make a plugin, simply decorate your plugin class with this
 * annotation. Crust will then attempt to find it at runtime, during
 * the {@link FMLCommonSetupEvent}.<br>
 * <br>
 * <strong>Note: your plugin class must also implement {@link ICrustPlugin}</strong>
 */
public @interface CrustPlugin {
}
