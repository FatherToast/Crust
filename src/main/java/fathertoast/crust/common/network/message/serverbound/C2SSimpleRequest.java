package fathertoast.crust.common.network.message.serverbound;

import fathertoast.crust.common.core.Crust;
import fathertoast.crust.common.network.work.CrustServerWork;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * A simple serverbound packet that requests the server execute a simple
 * task associated with the byte ID that is sent.
 */
public record C2SSimpleRequest(Type type) {
    
    /** Represents a request type, using each enum's ordinal as ID. */
    public enum Type {
        NULL,
        RECEIVE_PATHFINDER_DATA,
        UNSUBSCRIBE_PATHFINDER_DATA;
        
        public byte serialize() { return (byte) ordinal(); }
        
        public static Type deserialize( byte b ) { return b >= 0 && b < values().length ? values()[b] : NULL; }
    }
    
    /** Handles receipt of the message. */
    public static void handle( C2SSimpleRequest message, Supplier<NetworkEvent.Context> contextSupplier ) {
        NetworkEvent.Context context = contextSupplier.get();
        
        if( context.getDirection().getReceptionSide().isServer() ) {
            context.enqueueWork( () -> CrustServerWork.handleSimpleRequest( message, context.getSender() ) );
        }
        context.setPacketHandled( true );
    }
    
    /** Reads the message from a data buffer. */
    public static C2SSimpleRequest decode( FriendlyByteBuf buffer ) {
        try {
            return new C2SSimpleRequest(
                    Type.deserialize( buffer.readByte() ) );
        }
        catch( IndexOutOfBoundsException | DecoderException e ) {
            Crust.LOG.error( e );
            // noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        return new C2SSimpleRequest( Type.NULL );
    }
    
    /** Writes the message to a data buffer. */
    public static void encode( C2SSimpleRequest message, FriendlyByteBuf buffer ) {
        buffer.writeByte( message.type.serialize() );
    }
}
