package fathertoast.crust.common.potion;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.lib.CrustObjects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class CrustEffects {
    
    private static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create( ForgeRegistries.MOB_EFFECTS, ICrustApi.MOD_ID );
    
    static {
        register( CrustObjects.ID.VULNERABILITY, MobEffectCategory.HARMFUL, 0x96848D );
        register( CrustObjects.ID.WEIGHT, () -> new WeightEffect( MobEffectCategory.HARMFUL, 0x353A6B ) );
    }
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    /** Registers a simple effect to the deferred register. */
    private static void register( String name, MobEffectCategory category, int color ) { register( name, () -> new SimpleEffect( category, color ) ); }
    
    /** Registers a custom effect to the deferred register. */
    private static <T extends MobEffect> void register( String name, Supplier<T> effect ) { REGISTRY.register( name, effect ); }
    
    /** Really just here to allow access to the Effect::new. */
    private static class SimpleEffect extends MobEffect {
        SimpleEffect( MobEffectCategory type, int color ) { super( type, color ); }
    }
}