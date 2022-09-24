package fathertoast.crust.api.impl;

import fathertoast.crust.api.portal.IPortalBuilder;
import fathertoast.crust.api.portal.IPortalGenerator;
import fathertoast.crust.common.core.Crust;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.*;

public class PortalBuilderRegistry {

    private static final Map<ResourceLocation, IPortalBuilder> PORTAL_BUILDERS = new HashMap<>();

    protected static IPortalBuilder registerPortalBuilder(ResourceLocation id, @Nullable ResourceLocation textureLocation, List<ResourceLocation> validDimensions, IPortalGenerator portalGenerator) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(validDimensions);
        Objects.requireNonNull(portalGenerator);
        final PortalBuilder builder = new PortalBuilder(id, textureLocation, validDimensions, portalGenerator);

        if (PORTAL_BUILDERS.containsKey(id)) {
            Crust.LOG.warn("Tried to register portal builder with an ID that already exists in the registry: {}", id.toString());
            return null;
        }
        PORTAL_BUILDERS.put(id, builder);
        return builder;
    }

    @Nullable
    public static IPortalBuilder getBuilder(ResourceLocation id) {
        return PORTAL_BUILDERS.get(id);
    }

    public static boolean hasBuilder(ResourceLocation id) {
        return PORTAL_BUILDERS.containsKey(id);
    }

    public static Collection<IPortalBuilder> getAllBuilders() {
        return PORTAL_BUILDERS.values();
    }
}
