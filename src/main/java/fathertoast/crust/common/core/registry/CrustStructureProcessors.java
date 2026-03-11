package fathertoast.crust.common.core.registry;

import com.mojang.serialization.Codec;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.lib.CrustObjects;
import fathertoast.crust.common.worldgen.structure.processor.FeatureGeneratorProcessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.function.Supplier;

public final class CrustStructureProcessors {
    
    private static final DeferredRegister<StructureProcessorType<?>> REGISTRY = DeferredRegister.create( Registries.STRUCTURE_PROCESSOR, ICrustApi.MOD_ID );
    
    static {
        register( CrustObjects.StructureProcessors.FEATURE_GEN_ACTIVATOR, () -> type( FeatureGeneratorProcessor.CODEC ) );
    }
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    /** Registers a structure processor type to the deferred register. */
    @SuppressWarnings( "SameParameterValue" )
    private static void register( RegistryObject<StructureProcessorType<?>> regObj, Supplier<StructureProcessorType<?>> sup ) {
        REGISTRY.register( Objects.requireNonNull( regObj.getId() ).getPath(), sup );
    }
    
    /** @return The given structure processor codec as a {@link StructureProcessorType}. */
    @SuppressWarnings( "SameParameterValue" )
    private static <T extends StructureProcessor> StructureProcessorType<T> type( Codec<T> codec ) {
        return () -> codec;
    }
    
    
    // Utility class
    private CrustStructureProcessors() { }
}
