package fathertoast.crust.common.core.registry;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.lib.CrustObjects;
import fathertoast.crust.common.potion.WeightEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.function.Supplier;

public final class CrustEffects {
    
    private static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create( ForgeRegistries.MOB_EFFECTS, ICrustApi.MOD_ID );
    
    static {
        register( CrustObjects.Effects.VULNERABILITY, MobEffectCategory.HARMFUL, 0x96848D );
        register( CrustObjects.Effects.WEIGHT, () -> new WeightEffect( MobEffectCategory.HARMFUL, 0x353A6B ) );
    }
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    /** Registers a simple effect to the deferred register. */
    @SuppressWarnings( "SameParameterValue" )
    private static void register( RegistryObject<MobEffect> regObj, MobEffectCategory category, int color ) {
        register( regObj, () -> new SimpleEffect( category, color ) );
    }
    
    /** Registers a custom effect to the deferred register. */
    private static void register( RegistryObject<MobEffect> regObj, Supplier<? extends MobEffect> effect ) {
        REGISTRY.register( Objects.requireNonNull( regObj.getId() ).getPath(), effect );
    }
    
    /** Really just here to allow access to the Effect::new. */
    private static class SimpleEffect extends MobEffect {
        SimpleEffect( MobEffectCategory type, int color ) { super( type, color ); }
    }
    
    
    // Utility class
    private CrustEffects() { }
}