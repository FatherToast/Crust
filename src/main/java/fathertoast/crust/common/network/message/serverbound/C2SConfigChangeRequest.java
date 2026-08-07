package fathertoast.crust.common.network.message.serverbound;

import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.common.network.work.CrustServerWork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public record C2SConfigChangeRequest( CrustConfigSpec spec, List<Object> values ) {
    
    public C2SConfigChangeRequest( CrustConfigSpec spec ) { this( spec, spec.buildValueList( true, false ) ); }
    
    /** Sets all local values from the value list and then reloads the config to apply changes and sync. */
    public void apply() {
        spec().applyValueListLocal( values() );
        spec().onLoad();
    }
    
    /** Handles receipt of the message. */
    public static void handle( C2SConfigChangeRequest message, Supplier<NetworkEvent.Context> contextSupplier ) {
        NetworkEvent.Context context = contextSupplier.get();
        
        if( context.getDirection().getReceptionSide().isServer() ) {
            context.enqueueWork( () -> CrustServerWork.handleConfigChangeRequest( message, context.getSender() ) );
        }
        context.setPacketHandled( true );
    }
    
    /** Reads the message from a data buffer. */
    public static C2SConfigChangeRequest decode( FriendlyByteBuf buffer ) {
        CrustConfigSpec spec = CrustConfigSpec.readSpec( buffer );
        return new C2SConfigChangeRequest( spec, spec.deserialize( buffer, false ) );
    }
    
    /** Writes the message to a data buffer. */
    public static void encode( C2SConfigChangeRequest message, FriendlyByteBuf buffer ) {
        message.spec().serialize( message.values(), buffer, false );
    }
}