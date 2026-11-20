package fathertoast.crust.common.network.message.clientbound;

import fathertoast.crust.common.core.Crust;
import fathertoast.crust.common.network.work.CrustClientWork;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * A simple clientbound packet that requests the client execute a simple
 * task associated with the byte ID that is sent.
 */
public record S2CSimpleRequest(Type type) {
    
    /** Represents a request type, using each enum's ordinal as ID. */
    public enum Type {
        NULL,
        DESTROY_ITEM_ON_CURSOR,
        TOGGLE_PATHFINDER_DATA_REQUEST;
        
        public byte serialize() { return (byte) ordinal(); }
        
        public static S2CSimpleRequest.Type deserialize( byte b ) { return b >= 0 && b < values().length ? values()[b] : NULL; }
    }
    
    /** Handles receipt of the message. */
    public static void handle( S2CSimpleRequest message, Supplier<NetworkEvent.Context> contextSupplier ) {
        NetworkEvent.Context context = contextSupplier.get();
        
        if( context.getDirection().getReceptionSide().isClient() ) {
            context.enqueueWork( () -> CrustClientWork.handleSimpleRequest( message ) );
        }
        context.setPacketHandled( true );
    }
    
    /** Reads the message from a data buffer. */
    public static S2CSimpleRequest decode( FriendlyByteBuf buffer ) {
        try {
            return new S2CSimpleRequest( Type.deserialize( buffer.readByte() ) );
        }
        catch( IndexOutOfBoundsException | DecoderException e ) {
            Crust.LOG.error( e );
            // noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        return new S2CSimpleRequest( Type.NULL );
    }
    
    /** Writes the message to a data buffer. */
    public static void encode( S2CSimpleRequest message, FriendlyByteBuf buffer ) {
        buffer.writeByte( message.type.serialize() );
    }
}
