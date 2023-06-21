package fathertoast.crust.common.portal;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.lib.CrustObjects;
import fathertoast.crust.api.portal.PortalBuilder;
import fathertoast.crust.common.core.Crust;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class CrustPortals {
    
    /** Deferred register used to initialize the portal registry and populate vanilla portals. */
    private static final DeferredRegister<PortalBuilder> PORTAL_REGISTER = DeferredRegister.create( PortalBuilder.class, ICrustApi.MOD_ID );
    
    /** Registry for portals. */
    public static final Supplier<IForgeRegistry<PortalBuilder>> PORTAL_REGISTRY = PORTAL_REGISTER.makeRegistry( "portal_builder",
            () -> new RegistryBuilder<PortalBuilder>().setDefaultKey( Crust.resLoc( "empty" ) ) );
    
    
    public static final ResourceLocation NETHER_PORTAL = register( CrustObjects.ID.NETHER_PORTAL, NetherPortalBuilder::new );
    public static final ResourceLocation END_PORTAL = register( CrustObjects.ID.END_PORTAL, EndPortalBuilder::new );
    
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { PORTAL_REGISTER.register( bus ); }
    
    /** Registers a portal builder to the deferred register. */
    private static ResourceLocation register( String name, Supplier<PortalBuilder> factory ) {
        return PORTAL_REGISTER.register( name, factory ).getId();
    }
}