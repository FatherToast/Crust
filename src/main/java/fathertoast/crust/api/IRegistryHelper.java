package fathertoast.crust.api;

import fathertoast.crust.api.portal.IPortalBuilder;
import fathertoast.crust.api.portal.IPortalGenerator;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Helper interface for registering/adding
 * various stuff to be used by Crust
 */
public interface IRegistryHelper {

    /**
     * Registers a portal builder with the specified ID, icon texture, valid dimensions
     * and generation logic.<br>
     * <br>
     * The registered portal builder can then be used with the Crust "portal build" command,
     * and optionally bound to a custom Crust inventory button for easy access.<br>
     * <br>
     *
     * @param id              The ID of this portal builder. Should consist of the namespace of the mod adding the portal builder, and a unique name.<br>
     *                        <br>
     * @param textureLocation A {@link ResourceLocation} pointing to this portal builder's Crust button icon (Optional).<br>
     *                        <br>
     * @param validDimensions A List of dimension/world IDs where this portal builder can be used.<br>
     *                        <br>
     * @param portalGenerator The {@link IPortalGenerator} implementation for this portal builder. Responsible for actually placing
     *                        the portal in the world.<br>
     *                        <br>
     *
     * @return The registered IPortalBuilder if nothing went wrong (e.g. portal builder with the same ID already exists),
     *         returns null otherwise.
     */
    @Nullable
    IPortalBuilder registerPortalBuilder( ResourceLocation id, @Nullable ResourceLocation textureLocation, List<ResourceLocation> validDimensions, IPortalGenerator portalGenerator );
}
