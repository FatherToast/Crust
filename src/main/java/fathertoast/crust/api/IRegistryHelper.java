package fathertoast.crust.api;

import net.minecraft.util.ResourceLocation;

public interface IRegistryHelper {

    /**
     * Registers a portal builder with the specified Resource Location (id)
     * and List of valid dimensions to build said portal.<br>
     * <br>
     * The registered portal builder can then be used with the Crust "portal build" command,
     * and optionally bound to a custom Crust inventory button for easy access.<br>
     * <br>
     *
     * @param id The ID of this portal builder. Should consist of the namespace of the mod adding the portal builder, and a unique name.<br>
     *           <br>
     * @param portalBuilder The {@link PortalBuilder} implementation to be registered.
     */
    void registerPortalBuilder( ResourceLocation id, PortalBuilder portalBuilder );
}
