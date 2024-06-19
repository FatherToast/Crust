package fathertoast.crust.common.network.message;

import fathertoast.crust.common.network.work.CrustClientWork;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class S2CUpdateCrustModes {
    
    public final CompoundTag CRUST_MODES_TAG;
    
    public S2CUpdateCrustModes( @Nullable CompoundTag crustModesTag ) {
        CRUST_MODES_TAG = crustModesTag == null ? new CompoundTag() : crustModesTag;
    }
    
    /** Handles receipt of the message. */
    public static void handle( S2CUpdateCrustModes message, Supplier<NetworkEvent.Context> contextSupplier ) {
        NetworkEvent.Context context = contextSupplier.get();
        
        if( context.getDirection().getReceptionSide().isClient() ) {
            context.enqueueWork( () -> CrustClientWork.handleCrustModesUpdate( message ) );
        }
        context.setPacketHandled( true );
    }
    
    /** Reads the message from a data buffer. */
    public static S2CUpdateCrustModes decode( FriendlyByteBuf buffer ) {
        return new S2CUpdateCrustModes( buffer.readNbt() );
    }
    
    /** Writes the message to a data buffer. */
    public static void encode( S2CUpdateCrustModes message, FriendlyByteBuf buffer ) {
        buffer.writeNbt( message.CRUST_MODES_TAG );
    }
}