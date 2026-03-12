package fathertoast.crust.common.network.message.clientbound;

import fathertoast.crust.common.network.work.CrustClientWork;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@SuppressWarnings( "ClassCanBeRecord" )
public final class S2COpenFeatureGeneratorScreen {
    
    
    public final BlockPos POS;
    
    
    public S2COpenFeatureGeneratorScreen( BlockPos pos ) {
        POS = pos;
    }
    
    /** Handles receipt of the message. */
    public static void handle( S2COpenFeatureGeneratorScreen message, Supplier<NetworkEvent.Context> contextSupplier ) {
        NetworkEvent.Context context = contextSupplier.get();
        
        if( context.getDirection().getReceptionSide().isClient() ) {
            context.enqueueWork( () -> CrustClientWork.handleOpenFeatureGenScreen( message ) );
        }
        context.setPacketHandled( true );
    }
    
    /** Reads the message from a data buffer. */
    public static S2COpenFeatureGeneratorScreen decode( FriendlyByteBuf buffer ) {
        return new S2COpenFeatureGeneratorScreen( buffer.readBlockPos() );
    }
    
    /** Writes the message to a data buffer. */
    public static void encode( S2COpenFeatureGeneratorScreen message, FriendlyByteBuf buffer ) {
        buffer.writeBlockPos( message.POS );
    }
}
