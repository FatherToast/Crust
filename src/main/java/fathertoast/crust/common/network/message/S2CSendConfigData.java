package fathertoast.crust.common.network.message;

import fathertoast.crust.common.network.work.CrustClientWork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class S2CSendConfigData implements IntSupplier {
    
    private int loginIndex;
    
    public final String message;
    
    public S2CSendConfigData( String message ) {
        this.message = message;
    }
    
    /** Handles receipt of the message. */
    public void handle( S2CSendConfigData message, Supplier<NetworkEvent.Context> contextSupplier ) {
        NetworkEvent.Context context = contextSupplier.get();
        
        if( context.getDirection().getReceptionSide().isClient() ) {
            CrustClientWork.handleReceivedConfigData( message, context );
        }
    }
    
    /** Reads the message from a data buffer. */
    public static S2CSendConfigData decode( FriendlyByteBuf buffer ) {
        return new S2CSendConfigData( buffer.readUtf() );
    }
    
    /** Writes the message to a data buffer. */
    public static void encode( S2CSendConfigData message, FriendlyByteBuf buffer ) {
        buffer.writeUtf( message.message );
    }
    
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
