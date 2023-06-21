package fathertoast.crust.api.lib;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.portal.PortalBuilder;
import net.minecraft.potion.Effect;
import net.minecraftforge.registries.ObjectHolder;

/**
 * This helper class contains references/getters for all registry objects provided by Crust.
 */
@SuppressWarnings( { "FieldMayBeFinal", "unused" } )
@ObjectHolder( ICrustApi.MOD_ID )
public final class CrustObjects {
    
    /** The Vulnerability potion effect. Take 25% more damage per level (vs. Damage Resistance's 20% less per level). */
    public static Effect vulnerability() { return VULNERABILITY; }
    
    /**
     * The Weight potion effect. Increases effective fall distance by ~33% per level.
     * Applies downward acceleration, up to a maximum equivalent to one downward bubble column per level.
     */
    public static Effect weight() { return WEIGHT; }
    
    /** Vanilla Nether portal; 3x3 configuration. */
    public static PortalBuilder netherPortal() { return NETHER_PORTAL; }
    
    /** Vanilla End portal; stronghold configuration. */
    public static PortalBuilder endPortal() { return END_PORTAL; }
    
    
    /** The 'path' portion of each Crust object's registry name. */
    public interface ID {
        String VULNERABILITY = "vulnerability";
        String WEIGHT = "weight";
        
        String NETHER_PORTAL = "nether_portal";
        String END_PORTAL = "end_portal";
    }
    
    
    @ObjectHolder( ID.VULNERABILITY )
    private static Effect VULNERABILITY;
    @ObjectHolder( ID.WEIGHT )
    private static Effect WEIGHT;
    
    @ObjectHolder( ID.NETHER_PORTAL )
    private static PortalBuilder NETHER_PORTAL;
    @ObjectHolder( ID.END_PORTAL )
    private static PortalBuilder END_PORTAL;
}