package fathertoast.crust.api.impl;

import fathertoast.crust.api.portal.IPortalBuilder;
import fathertoast.crust.api.portal.IPortalGenerator;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

public class PortalBuilder implements IPortalBuilder {

    private final ResourceLocation id;
    private final ResourceLocation iconPath;
    private final List<ResourceLocation> validDimensions;
    private final IPortalGenerator portalGenerator;


    public PortalBuilder(ResourceLocation id, @Nullable ResourceLocation iconPath, List<ResourceLocation> validDimensions, IPortalGenerator portalGenerator) {
        this.id = id;
        this.iconPath = iconPath;
        this.validDimensions = validDimensions;
        this.portalGenerator = portalGenerator;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Nullable
    @Override
    public ResourceLocation getTextureLocation() {
        return iconPath;
    }

    @Override
    public List<ResourceLocation> getValidDimensions() {
        return validDimensions;
    }

    @Override
    public IPortalGenerator getGenerator() {
        return portalGenerator;
    }
}
