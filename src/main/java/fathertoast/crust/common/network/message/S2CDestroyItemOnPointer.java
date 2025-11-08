package fathertoast.crust.common.network.message;

import fathertoast.crust.common.network.work.CrustClientWork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CDestroyItemOnPointer {
    
    @SuppressWarnings( "all" )
    public static final S2CDestroyItemOnPointer INSTANCE = new S2CDestroyItemOnPointer();
    
    private S2CDestroyItemOnPointer() { }
    
    /** Handles receipt of the message. */
    public static void handle( @SuppressWarnings( "unused" ) S2CDestroyItemOnPointer message, Supplier<NetworkEvent.Context> contextSupplier ) {
        NetworkEvent.Context context = contextSupplier.get();
        
        if( context.getDirection().getReceptionSide().isClient() ) {
            context.enqueueWork( CrustClientWork::handleDestroyItemOnPointer );
        }
        context.setPacketHandled( true );
    }
    
    /** Reads the message from a data buffer. */
    public static S2CDestroyItemOnPointer decode( @SuppressWarnings( "unused" ) FriendlyByteBuf buffer ) { return INSTANCE; }
    
    /** Writes the message to a data buffer. */
    public static void encode( @SuppressWarnings( "unused" ) S2CDestroyItemOnPointer message, @SuppressWarnings( "unused" ) FriendlyByteBuf buffer ) { }
}