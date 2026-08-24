package fathertoast.crust.common.core.registry;


import com.mojang.serialization.Codec;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.config.common.value.provider.*;
import fathertoast.crust.api.lib.CrustObjects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviderType;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviderType;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

public final class CrustValueProviders {
    
    public static final DeferredRegister<HeightProviderType<?>> HEIGHT_REGISTRY = DeferredRegister.create( BuiltInRegistries.HEIGHT_PROVIDER_TYPE.key(), ICrustApi.MOD_ID );
    public static final DeferredRegister<IntProviderType<?>> INT_REGISTRY = DeferredRegister.create( BuiltInRegistries.INT_PROVIDER_TYPE.key(), ICrustApi.MOD_ID );
    public static final DeferredRegister<FloatProviderType<?>> FLOAT_REGISTRY = DeferredRegister.create( BuiltInRegistries.FLOAT_PROVIDER_TYPE.key(), ICrustApi.MOD_ID );
    
    static {
        registerHeight( CrustObjects.HeightProviders.CFG_UNIFORM, ConfigHeightProvider.CODEC );
        
        registerInt( CrustObjects.IntProviders.CFG_CONSTANT, ConfigConstantIntProvider.CODEC );
        registerInt( CrustObjects.IntProviders.CFG_UNIFORM, ConfigUniformIntProvider.CODEC );
        registerInt( CrustObjects.IntProviders.CFG_COUNT, ConfigCountIntProvider.CODEC );
        
        registerFloat( CrustObjects.FloatProviders.CFG_CONSTANT, ConfigConstantFloatProvider.CODEC );
        registerFloat( CrustObjects.FloatProviders.CFG_UNIFORM, ConfigUniformFloatProvider.CODEC );
    }
    
    
    /** Called to register this class. */
    public static void register( IEventBus eventBus ) {
        HEIGHT_REGISTRY.register( eventBus );
        INT_REGISTRY.register( eventBus );
        FLOAT_REGISTRY.register( eventBus );
    }
    
    /** Registers a height provider to the height provider registry. */
    @SuppressWarnings( "SameParameterValue" )
    private static <T extends HeightProvider> void registerHeight( RegistryObject<HeightProviderType<?>> regObj, Codec<T> codec ) {
        final HeightProviderType<T> type = () -> codec;
        HEIGHT_REGISTRY.register( Objects.requireNonNull( regObj.getId() ).getPath(), () -> type );
    }
    
    /** Registers an int provider to the int provider registry. */
    @SuppressWarnings( "SameParameterValue" )
    private static <T extends IntProvider> void registerInt( RegistryObject<IntProviderType<?>> regObj, Codec<T> codec ) {
        final IntProviderType<T> type = () -> codec;
        INT_REGISTRY.register( Objects.requireNonNull( regObj.getId() ).getPath(), () -> type );
    }
    
    /** Registers a float provider to the float provider registry. */
    @SuppressWarnings( "SameParameterValue" )
    private static <T extends FloatProvider> void registerFloat( RegistryObject<FloatProviderType<?>> regObj, Codec<T> codec ) {
        final FloatProviderType<T> type = () -> codec;
        FLOAT_REGISTRY.register( Objects.requireNonNull( regObj.getId() ).getPath(), () -> type );
    }
}
