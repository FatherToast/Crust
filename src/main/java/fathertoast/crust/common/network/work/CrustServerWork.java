package fathertoast.crust.common.network.work;

import fathertoast.crust.common.block.entity.FeatureGeneratorBlockEntity;
import fathertoast.crust.common.core.Crust;
import fathertoast.crust.common.network.message.serverbound.C2SFeatureGeneratorData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.server.ServerLifecycleHooks;


public final class CrustServerWork {
    
    public static void handleFeatureGeneratorData( C2SFeatureGeneratorData message ) {
        ServerLevel level = ServerLifecycleHooks.getCurrentServer().getLevel( message.POS.dimension() );
        
        // Check if level exists
        if( level == null ) {
            Crust.LOG.warn( "Received {} packet with invalid position data! Dimension '{}' does not exist on the server!",
                    C2SFeatureGeneratorData.class.getSimpleName(), message.POS.dimension().location() );
            return;
        }
        final BlockPos pos = message.POS.pos();
        final BlockEntity blockEntity = level.getExistingBlockEntity( pos );
        
        if( blockEntity instanceof FeatureGeneratorBlockEntity featureGenerator ) {
            FeatureGeneratorBlockEntity.FeatureData data = FeatureGeneratorBlockEntity.FeatureData.newEmpty();
            data.loadFrom( message.DATA_TAG );
            featureGenerator.setData( data );
        }
        // No feature generator block entity at position, log warning
        else {
            Crust.LOG.warn( "Received {} packet for position {}, but no feature generator block entity was found!",
                    C2SFeatureGeneratorData.class.getSimpleName(), message.POS.toString() );
        }
    }
}