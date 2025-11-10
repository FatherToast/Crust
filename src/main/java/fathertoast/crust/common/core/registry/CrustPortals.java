package fathertoast.crust.common.core.registry;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.lib.CrustObjects;
import fathertoast.crust.api.portal.PortalBuilder;
import fathertoast.crust.common.core.Crust;
import fathertoast.crust.common.portal.EndPortalBuilder;
import fathertoast.crust.common.portal.NetherPortalBuilder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.function.Supplier;

public final class CrustPortals {
    
    /** Deferred register used to initialize the portal registry and populate vanilla portals. */
    private static final DeferredRegister<PortalBuilder> PORTAL_REGISTER = DeferredRegister.create( CrustObjects.Portals.REGISTRY_KEY, ICrustApi.MOD_ID );
    
    static {
        register( CrustObjects.Portals.NETHER, NetherPortalBuilder::new );
        register( CrustObjects.Portals.END, EndPortalBuilder::new );
    }
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { PORTAL_REGISTER.register( bus ); }
    
    /** Registers a portal builder to the deferred register. */
    private static void register( RegistryObject<PortalBuilder> regObj, Supplier<? extends PortalBuilder> factory ) {
        PORTAL_REGISTER.register( Objects.requireNonNull( regObj.getId() ).getPath(), factory );
    }
    
    public static void onRegistryCreate( NewRegistryEvent event ) {
        RegistryBuilder<PortalBuilder> builder = new RegistryBuilder<>();
        builder.setName( Crust.rl( "portal_builder" ) );
        CrustObjects.PORTAL_REGISTRY = event.create( builder );
    }
    
    
    // Utility class
    private CrustPortals() { }
}