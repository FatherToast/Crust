package fathertoast.crust.api.portal;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

/**
 *  This interface represents a portal builder.
 *  Portal builders are used by Crust to generate
 *  a specific type of portal with the <strong>/crustportal</strong>
 *  command, or by binding the command to a Crust inventory button.
 */
public interface IPortalBuilder {

    /** @return The ID of this portal builder. */
    ResourceLocation getId();

    /** @return A ResourceLocation pointing to this portal builder's button icon (can be null). */
    @Nullable
    ResourceLocation getTextureLocation();

    /**
     *  @return A List of ResourceLocations representing the dimensions/worlds
     *  that this portal builder can be used in.
     */
    List<ResourceLocation> getValidDimensions();

    /** @return The IPortalGenerator implementation for this portal builder. */
    IPortalGenerator getGenerator();
}
