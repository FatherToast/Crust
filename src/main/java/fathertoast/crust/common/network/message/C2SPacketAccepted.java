package fathertoast.crust.common.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 * Reply packet sent from client to server
 * when the server expects a reply from a message
 * sent to the client.
 */
public class C2SPacketAccepted implements IntSupplier {
    
    private int loginIndex;
    
    public C2SPacketAccepted() { }
    
    /** Handles receipt of the message. */
    public void handle( C2SPacketAccepted message, Supplier<NetworkEvent.Context> contextSupplier ) {
        NetworkEvent.Context context = contextSupplier.get();
        context.setPacketHandled( true );
    }
    
    /** Reads the message from a data buffer. */
    public static C2SPacketAccepted decode( FriendlyByteBuf buffer ) {
        return new C2SPacketAccepted();
    }
    
    /** Writes the message to a data buffer. */
    public static void encode( C2SPacketAccepted message, FriendlyByteBuf buffer ) { }
    
    public void setLoginIndex( int loginIndex ) {
        this.loginIndex = loginIndex;
    }
    
    public int getLoginIndex() {
        return loginIndex;
    }
    
    @Override
    public int getAsInt() {
        return getLoginIndex();
    }
}
