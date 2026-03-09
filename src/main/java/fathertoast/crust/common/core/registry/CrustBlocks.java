package fathertoast.crust.common.core.registry;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.lib.CrustObjects;
import fathertoast.crust.common.block.FeaturePlacerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.function.Supplier;

public final class CrustBlocks {
    
    private static final DeferredRegister<Block> REGISTRY = DeferredRegister.create( ForgeRegistries.BLOCKS, ICrustApi.MOD_ID );
    
    static {
        register( CrustObjects.Blocks.FEATURE_PLACER, FeaturePlacerBlock::new );
    }
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    /** Registers a block with no block item to the deferred register. */
    @SuppressWarnings( "SameParameterValue" )
    private static void registerNoItem( RegistryObject<Block> regObj, Supplier<Block> supplier ) {
        REGISTRY.register( Objects.requireNonNull( regObj.getId() ).getPath(), supplier );
    }
    
    /** Registers a block with a simple block item to the deferred register. */
    @SuppressWarnings( "SameParameterValue" )
    private static void register( RegistryObject<Block> regObj, Supplier<Block> supplier ) {
        REGISTRY.register( Objects.requireNonNull( regObj.getId() ).getPath(), supplier );
        CrustItems.registerBlockItem( regObj );
    }
    
    // Utility class
    private CrustBlocks() { }
}
