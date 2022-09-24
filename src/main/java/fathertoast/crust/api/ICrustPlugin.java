package fathertoast.crust.api;

import net.minecraft.util.ResourceLocation;

/**
 * This interface can be implemented into a class
 * to make it a valid Crust mod plugin.<br>
 * <br>
 * <strong>Note: your plugin class must also be annotated with {@link CrustPlugin}</strong>
 */
public interface ICrustPlugin {

    /** Called by Crust during  */
    void onLoad(ICrustApi apiInstance);

    /** @return A ResourceLocation representing the ID of this plugin. */
    ResourceLocation getId();
}
