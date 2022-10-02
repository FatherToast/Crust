package fathertoast.crust.common.network.message;

import fathertoast.crust.common.network.work.CrustClientWork;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CDestroyItemOnPointer {
    
    public S2CDestroyItemOnPointer() { }
    
    /** Handles receipt of the message. */
    public static void handle( S2CDestroyItemOnPointer message, Supplier<NetworkEvent.Context> contextSupplier ) {
        NetworkEvent.Context context = contextSupplier.get();
        
        if( context.getDirection().getReceptionSide().isClient() ) {
            context.enqueueWork( () -> CrustClientWork.handleDestroyItemOnPointer( message ) );
        }
        context.setPacketHandled( true );
    }
    
    /** Reads the message from a data buffer. */
    public static S2CDestroyItemOnPointer decode( @SuppressWarnings( "unused" ) PacketBuffer buffer ) { return new S2CDestroyItemOnPointer(); }
    
    /** Writes the message to a data buffer. */
    public static void encode( @SuppressWarnings( "unused" ) S2CDestroyItemOnPointer message, @SuppressWarnings( "unused" ) PacketBuffer buffer ) { }
}