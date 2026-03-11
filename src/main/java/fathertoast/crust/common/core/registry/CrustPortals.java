package fathertoast.crust.common.core.registry;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.lib.CrustObjects;
import fathertoast.crust.api.portal.PortalBuilder;
import fathertoast.crust.common.core.Crust;
import fathertoast.crust.common.portal.EndPortalBuilder;
import fathertoast.crust.common.portal.NetherPortalBuilder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.function.Supplier;

public final class CrustPortals {
    
    /** Deferred register used to initialize the portal registry and populate vanilla portals. */
    private static final DeferredRegister<PortalBuilder> REGISTRY = DeferredRegister.create( CrustObjects.Portals.REGISTRY_KEY, ICrustApi.MOD_ID );
    
    static {
        register( CrustObjects.Portals.NETHER, NetherPortalBuilder::new );
        register( CrustObjects.Portals.END, EndPortalBuilder::new );
    }
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    /** Registers a portal builder to the deferred register. */
    private static void register( RegistryObject<PortalBuilder> regObj, Supplier<? extends PortalBuilder> factory ) {
        REGISTRY.register( Objects.requireNonNull( regObj.getId() ).getPath(), factory );
    }
    
    /**
     * Called when new custom registries are to be created.
     * <br><br>
     * Added as listener from {@link Crust#Crust(FMLJavaModLoadingContext)}.
     */
    public static void onRegistryCreate( NewRegistryEvent event ) {
        RegistryBuilder<PortalBuilder> builder = new RegistryBuilder<>();
        builder.setName( Crust.rl( "portal_builder" ) );
        CrustObjects.PORTAL_REGISTRY = event.create( builder );
    }
    
    
    // Utility class
    private CrustPortals() { }
}