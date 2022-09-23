package fathertoast.crust.api.impl;

import fathertoast.crust.api.IRegistryHelper;
import fathertoast.crust.api.PortalBuilder;
import net.minecraft.util.ResourceLocation;

public class RegistryHelper implements IRegistryHelper {

    @Override
    public void registerPortalBuilder(ResourceLocation iconPath, PortalBuilder portalBuilder) {
        PortalBuilderRegistry.registerPortalBuilder(iconPath, portalBuilder);
    }

    /** Internal mod registration */
    public void registerInternal() {
        //registerPortalBuilder(NETHER PORTAL GRRR);
        //registerPortalBuilder(END PORTAL GGRRR);
    }
}
