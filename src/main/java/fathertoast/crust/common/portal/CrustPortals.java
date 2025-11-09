package fathertoast.crust.common.portal;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.lib.CrustObjects;
import fathertoast.crust.api.portal.PortalBuilder;
import fathertoast.crust.common.core.Crust;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class CrustPortals {
    
    /** Deferred register used to initialize the portal registry and populate vanilla portals. */
    private static final DeferredRegister<PortalBuilder> PORTAL_REGISTER = DeferredRegister.create( ResourceKey.createRegistryKey( Crust.resLoc( "portal_builder" ) ), ICrustApi.MOD_ID );
    
    
    public static final RegistryObject<PortalBuilder> NETHER_PORTAL = register( CrustObjects.ID.NETHER_PORTAL.getPath(), NetherPortalBuilder::new );
    public static final RegistryObject<PortalBuilder> END_PORTAL = register( CrustObjects.ID.END_PORTAL.getPath(), EndPortalBuilder::new );
    
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { PORTAL_REGISTER.register( bus ); }
    
    /** Registers a portal builder to the deferred register. */
    private static RegistryObject<PortalBuilder> register( String name, Supplier<PortalBuilder> factory ) {
        // noinspection ConstantConditions
        return PORTAL_REGISTER.register( name, factory );
    }
    
    public static void onRegistryCreate( NewRegistryEvent event ) {
        RegistryBuilder<PortalBuilder> builder = new RegistryBuilder<>();
        builder.setName( Crust.resLoc( "portal_builder" ) );
        CrustObjects.PORTAL_REGISTRY = event.create( builder );
    }
    
    
    // Utility class
    private CrustPortals() { }
}