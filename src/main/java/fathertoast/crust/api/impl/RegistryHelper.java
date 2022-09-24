package fathertoast.crust.api.impl;

import fathertoast.crust.api.IRegistryHelper;
import fathertoast.crust.api.portal.IPortalBuilder;
import fathertoast.crust.api.portal.IPortalGenerator;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

public class RegistryHelper implements IRegistryHelper {

    @Override
    public IPortalBuilder registerPortalBuilder(ResourceLocation id, @Nullable ResourceLocation textureLocation, List<ResourceLocation> validDimensions, IPortalGenerator portalGenerator) {
        return PortalBuilderRegistry.registerPortalBuilder(id, textureLocation, validDimensions, portalGenerator);
    }
}
