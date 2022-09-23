package fathertoast.crust.api;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public final class PortalBuilder {
    @Nullable
    private final ResourceLocation textureLocation;
    private final List<ResourceLocation> validDimensions;
    private final IPortalGenerator portalGenerator;


    public PortalBuilder(@Nullable ResourceLocation textureLocation, List<ResourceLocation> validDimensions, IPortalGenerator portalGenerator) {
        this.textureLocation = textureLocation;
        this.validDimensions = validDimensions;
        this.portalGenerator = portalGenerator;
    }

    public PortalBuilder(@Nullable ResourceLocation textureLocation, IPortalGenerator portalGenerator, ResourceLocation... validDimensions) {
        this(textureLocation, Arrays.asList(validDimensions), portalGenerator);
    }

    @Nullable
    public ResourceLocation getTextureLocation() {
        return textureLocation;
    }

    public List<ResourceLocation> getValidDimensions() {
        return validDimensions;
    }

    public IPortalGenerator getGenerator() {
        return portalGenerator;
    }
}
