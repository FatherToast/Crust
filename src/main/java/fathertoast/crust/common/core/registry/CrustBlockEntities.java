package fathertoast.crust.common.core.registry;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.lib.CrustObjects;
import fathertoast.crust.common.block.entity.FeaturePlacerBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Objects;


public final class CrustBlockEntities {
    
    private static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create( ForgeRegistries.BLOCK_ENTITY_TYPES, ICrustApi.MOD_ID );
    
    static {
        // noinspection ConstantConditions
        register( CrustObjects.BlockEntities.FEATURE_PLACER, FeaturePlacerBlockEntity::new, List.of( CrustObjects.Blocks.FEATURE_PLACER ) );
    }
    
    /** Called to register this class. */
    public static void register( IEventBus bus ) { REGISTRY.register( bus ); }
    
    /** Registers a block entity type to the deferred register. */
    @SuppressWarnings( { "SameParameterValue", "ConstantConditions" } )
    private static void register( RegistryObject<BlockEntityType<?>> regObj, BlockEntityType.BlockEntitySupplier<?> supplier, List<RegistryObject<Block>> block ) {
        REGISTRY.register( Objects.requireNonNull( regObj.getId() ).getPath(), () -> BlockEntityType.Builder.of( supplier, toBlockArray( block ) ).build( null ) );
    }
    
    /** Convenience method for returning a list of block registry objects as an array of blocks. */
    private static Block[] toBlockArray( List<RegistryObject<Block>> blocks ) {
        // Sanity checks
        Objects.requireNonNull( blocks );
        if( blocks.isEmpty() ) {
            throw new IllegalArgumentException( "Attempted to convert empty block registry object list into a block array! Boo." );
        }
        // Collect in array and return
        Block[] blockArray = new Block[blocks.size()];
        for( int i = 0; i < blocks.size(); i++ ) {
            blockArray[i] = blocks.get( i ).get();
        }
        return blockArray;
    }
    
    
    // Utility class
    private CrustBlockEntities() { }
}
