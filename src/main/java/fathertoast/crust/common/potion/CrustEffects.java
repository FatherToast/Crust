package fathertoast.crust.common.potion;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.lib.CrustObjects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class CrustEffects {
    
    private static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create( ForgeRegistries.MOB_EFFECTS, ICrustApi.MOD_ID );

    public static final RegistryObject<MobEffect> VULNEARABILITY = register( CrustObjects.ID.VULNERABILITY_EFFECT.getPath(), MobEffectCategory.HARMFUL, 0x96848D );
    public static final RegistryObject<MobEffect> WEIGHT = register( CrustObjects.ID.WEIGHT_EFFECT.getPath(), () -> new WeightEffect( MobEffectCategory.HARMFUL, 0x353A6B ) );

    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    /** Registers a simple effect to the deferred register. */
    private static RegistryObject<MobEffect> register( String name, MobEffectCategory category, int color ) {
        return register( name, () -> new SimpleEffect( category, color ) );
    }
    
    /** Registers a custom effect to the deferred register. */
    private static <T extends MobEffect> RegistryObject<T> register( String name, Supplier<T> effect ) {
        return REGISTRY.register( name, effect );
    }
    
    /** Really just here to allow access to the Effect::new. */
    private static class SimpleEffect extends MobEffect {
        SimpleEffect( MobEffectCategory type, int color ) { super( type, color ); }
    }
}