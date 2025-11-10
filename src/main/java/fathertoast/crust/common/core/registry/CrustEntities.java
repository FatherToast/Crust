package fathertoast.crust.common.core.registry;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.entity.CrustFishingHook;
import fathertoast.crust.api.lib.CrustEntityHelper;
import fathertoast.crust.api.lib.CrustObjects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

public class CrustEntities {
    
    private static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create( ForgeRegistries.ENTITY_TYPES, ICrustApi.MOD_ID );
    
    static {
        register( CrustObjects.Entities.FISH_HOOK, CrustEntityHelper.fishHookType( CrustFishingHook::new ) );
    }
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    /** Registers an entity to the deferred register. */
    private static <T extends Entity> void register( RegistryObject<EntityType<T>> regObj, EntityType.Builder<T> builder ) {
        final String name = Objects.requireNonNull( regObj.getId() ).getPath();
        REGISTRY.register( name, () -> builder.build( name ) );
    }
}