package fathertoast.crust.common.network.message.clientbound;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.common.network.work.CrustClientWork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public record S2CConfigData( CrustConfigSpec spec, List<Object> values ) {
    
    public S2CConfigData( CrustConfigSpec spec ) { this( spec, spec.buildValueList( false, false ) ); }
    
    /** Sets all remote values from the value list. */
    public void apply() { spec().applyValueListRemote( values(), false ); }
    
    /** Handles receipt of the message. */
    public static void handle( S2CConfigData message, Supplier<NetworkEvent.Context> contextSupplier ) {
        NetworkEvent.Context context = contextSupplier.get();
        
        if( context.getDirection().getReceptionSide().isClient() ) {
            context.enqueueWork( () -> CrustClientWork.handleConfigData( message ) );
        }
        context.setPacketHandled( true );
    }
    
    /** Reads the message from a data buffer. */
    public static S2CConfigData decode( FriendlyByteBuf buffer ) {
        CrustConfigSpec spec = CrustConfigSpec.readSpec( buffer );
        return new S2CConfigData( spec, spec.deserialize( buffer, false ) );
    }
    
    /** Writes the message to a data buffer. */
    public static void encode( S2CConfigData message, FriendlyByteBuf buffer ) {
        message.spec().serialize( message.values(), buffer, false );
    }
}