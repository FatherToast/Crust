package fathertoast.crust.common.network.message;

import fathertoast.crust.common.network.work.CrustClientWork;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class S2CUpdateCrustModes {
    
    public final CompoundNBT CRUST_MODES_TAG;
    
    public S2CUpdateCrustModes( @Nullable CompoundNBT crustModesTag ) {
        CRUST_MODES_TAG = crustModesTag == null ? new CompoundNBT() : crustModesTag;
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
    public static S2CUpdateCrustModes decode( PacketBuffer buffer ) {
        return new S2CUpdateCrustModes( buffer.readNbt() );
    }
    
    /** Writes the message to a data buffer. */
    public static void encode( S2CUpdateCrustModes message, PacketBuffer buffer ) {
        buffer.writeNbt( message.CRUST_MODES_TAG );
    }
}