package fathertoast.crust.common.network.message.serverbound;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.common.network.work.CrustServerWork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record C2SConfigDataRequest( CrustConfigSpec spec ) {
    
    /** Handles receipt of the message. */
    public static void handle( C2SConfigDataRequest message, Supplier<NetworkEvent.Context> contextSupplier ) {
        NetworkEvent.Context context = contextSupplier.get();
        
        if( context.getDirection().getReceptionSide().isServer() ) {
            context.enqueueWork( () -> CrustServerWork.handleConfigDataRequest( message, context.getSender() ) );
        }
        context.setPacketHandled( true );
    }
    
    /** Reads the message from a data buffer. */
    public static C2SConfigDataRequest decode( FriendlyByteBuf buffer ) {
        return new C2SConfigDataRequest( CrustConfigSpec.readSpec( buffer ) );
    }
    
    /** Writes the message to a data buffer. */
    public static void encode( C2SConfigDataRequest message, FriendlyByteBuf buffer ) {
        message.spec().writeSpec( buffer );
    }
}