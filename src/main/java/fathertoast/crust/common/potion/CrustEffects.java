package fathertoast.crust.common.potion;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.lib.CrustObjects;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class CrustEffects {
    
    private static final DeferredRegister<Effect> REGISTRY = DeferredRegister.create( ForgeRegistries.POTIONS, ICrustApi.MOD_ID );
    
    static {
        register( CrustObjects.ID.VULNERABILITY, EffectType.HARMFUL, 0x96848D );
        register( CrustObjects.ID.WEIGHT, () -> new WeightEffect( EffectType.HARMFUL, 0x353A6B ) );
    }
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    /** Registers a simple effect to the deferred register. */
    private static void register( String name, EffectType type, int color ) { register( name, () -> new SimpleEffect( type, color ) ); }
    
    /** Registers a custom effect to the deferred register. */
    private static <T extends Effect> void register( String name, Supplier<T> effect ) { REGISTRY.register( name, effect ); }
    
    /** Really just here to allow access to the Effect::new. */
    private static class SimpleEffect extends Effect {
        SimpleEffect( EffectType type, int color ) { super( type, color ); }
    }
}