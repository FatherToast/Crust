package fathertoast.crust.common.network.message.clientbound;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.common.network.work.CrustClientWork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public record S2CConfigSync( CrustConfigSpec spec, List<Object> values ) {
    
    public S2CConfigSync( CrustConfigSpec spec ) { this( spec, spec.buildValueList( false, true ) ); }
    
    /** Sets all synced remote values from the value list. */
    public void apply() { spec().applyValueListRemote( values(), true ); }
    
    /** Handles receipt of the message. */
    public static void handle( S2CConfigSync message, Supplier<NetworkEvent.Context> contextSupplier ) {
        NetworkEvent.Context context = contextSupplier.get();
        
        if( context.getDirection().getReceptionSide().isClient() ) {
            context.enqueueWork( () -> CrustClientWork.handleConfigSync( message ) );
        }
        context.setPacketHandled( true );
    }
    
    /** Reads the message from a data buffer. */
    public static S2CConfigSync decode( FriendlyByteBuf buffer ) {
        CrustConfigSpec spec = CrustConfigSpec.readSpec( buffer );
        return new S2CConfigSync( spec, spec.deserialize( buffer, true ) );
    }
    
    /** Writes the message to a data buffer. */
    public static void encode( S2CConfigSync message, FriendlyByteBuf buffer ) {
        message.spec().serialize( message.values(), buffer, true );
    }
}