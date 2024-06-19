package fathertoast.crust.api.lib;

import fathertoast.crust.api.portal.PortalBuilder;
import fathertoast.crust.common.api.impl.CrustApi;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ObjectHolder;

/**
 * This helper class contains references/getters for all registry objects provided by Crust.
 */
@SuppressWarnings( { "FieldMayBeFinal", "unused" } )
public final class CrustObjects {
    
    /** The Vulnerability potion effect. Take 25% more damage per level (vs. Damage Resistance's 20% less per level). */
    public static MobEffect vulnerability() { return VULNERABILITY; }
    
    /**
     * The Weight potion effect. Increases effective fall distance by ~33% per level.
     * Applies downward acceleration, up to a maximum equivalent to one downward bubble column per level.
     */
    public static MobEffect weight() { return WEIGHT; }
    
    /** Vanilla Nether portal; 3x3 configuration. */
    public static PortalBuilder netherPortal() { return NETHER_PORTAL; }
    
    /** Vanilla End portal; stronghold configuration. */
    public static PortalBuilder endPortal() { return END_PORTAL; }
    
    
    /** The 'path' portion of each Crust object's registry name. */
    public interface ID {
        String EFFECT_REGISTRY = "minecraft:mob_effect";
        String VULNERABILITY = "vulnerability";
        String WEIGHT = "weight";

        String PORTAL_REGISTRY = "crust:portal_builder";
        String NETHER_PORTAL = "nether_portal";
        String END_PORTAL = "end_portal";
    }
    
    
    @ObjectHolder( registryName = ID.EFFECT_REGISTRY, value = CrustApi.MOD_ID + ":" + ID.VULNERABILITY )
    private static MobEffect VULNERABILITY;
    @ObjectHolder( registryName = ID.EFFECT_REGISTRY, value = CrustApi.MOD_ID + ":" + ID.WEIGHT )
    private static MobEffect WEIGHT;
    
    @ObjectHolder( registryName = ID.PORTAL_REGISTRY, value = CrustApi.MOD_ID + ":" + ID.NETHER_PORTAL )
    private static PortalBuilder NETHER_PORTAL;
    @ObjectHolder( registryName = ID.PORTAL_REGISTRY, value = CrustApi.MOD_ID + ":" + ID.END_PORTAL )
    private static PortalBuilder END_PORTAL;
}