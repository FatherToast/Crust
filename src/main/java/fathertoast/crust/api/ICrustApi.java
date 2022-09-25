package fathertoast.crust.api;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * The main interface of Crust's API.<br
 * <br>
 * The API instance gets parsed to
 * all registered Crust plugins during
 * {@link FMLCommonSetupEvent}
 */
public interface ICrustApi {
    
    IRegistryHelper getRegistryHelper();
}