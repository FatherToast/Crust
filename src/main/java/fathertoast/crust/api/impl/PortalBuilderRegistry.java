package fathertoast.crust.api.impl;

import fathertoast.crust.api.PortalBuilder;
import fathertoast.crust.common.core.Crust;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PortalBuilderRegistry {

    private static final Map<ResourceLocation, PortalBuilder> PORTAL_BUILDERS = new HashMap<>();

    protected static void registerPortalBuilder(ResourceLocation id, PortalBuilder portalBuilder) {
        if (PORTAL_BUILDERS.containsKey(id)) {
            Crust.LOG.warn("Tried to register portal builder with an ID that already exists in the registry: {}", id.toString());
            return;
        }
        PORTAL_BUILDERS.put(id, Objects.requireNonNull(portalBuilder));
    }

    @Nullable
    protected static PortalBuilder getBuilder(ResourceLocation id) {
        return PORTAL_BUILDERS.get(id);
    }
}
