package fathertoast.crust.api.lib;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.portal.PortalBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

/**
 * This helper class contains references/getters for all registry objects provided by Crust.
 */
@SuppressWarnings( { "unused" } )
public final class CrustObjects {

    /**
     * The Forge registry for Crust portal builders.<br>
     * Populated during {@link net.minecraftforge.registries.NewRegistryEvent}.
     */
    public static Supplier<IForgeRegistry<PortalBuilder>> PORTAL_REGISTRY;
    
    
    /** The registry IDs of misc game objects added by Crust. */
    public interface ID {
        ResourceLocation VULNERABILITY_EFFECT = ResourceLocation.fromNamespaceAndPath( ICrustApi.MOD_ID, "vulnerability" );
        ResourceLocation WEIGHT_EFFECT = ResourceLocation.fromNamespaceAndPath( ICrustApi.MOD_ID, "weight" );

        ResourceLocation NETHER_PORTAL = ResourceLocation.fromNamespaceAndPath( ICrustApi.MOD_ID, "nether_portal" );
        ResourceLocation END_PORTAL = ResourceLocation.fromNamespaceAndPath( ICrustApi.MOD_ID, "end_portal" );
    }
}