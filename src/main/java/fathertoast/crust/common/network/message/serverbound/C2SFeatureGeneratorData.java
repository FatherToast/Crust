package fathertoast.crust.common.network.message.serverbound;

import fathertoast.crust.common.network.work.CrustServerWork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

@SuppressWarnings( "ClassCanBeRecord" )
public final class C2SFeatureGeneratorData {
    
    public final CompoundTag DATA_TAG;
    public final GlobalPos POS;
    
    public C2SFeatureGeneratorData( @Nullable CompoundTag dataTag, Level level, BlockPos pos ) {
        this( dataTag, GlobalPos.of( level.dimension(), pos ) );
    }
    
    public C2SFeatureGeneratorData( @Nullable CompoundTag dataTag, GlobalPos pos ) {
        DATA_TAG = dataTag == null ? new CompoundTag() : dataTag;
        POS = pos;
    }
    
    /** Handles receipt of the message. */
    public static void handle( C2SFeatureGeneratorData message, Supplier<NetworkEvent.Context> contextSupplier ) {
        NetworkEvent.Context context = contextSupplier.get();
        
        if( context.getDirection().getReceptionSide().isServer() ) {
            context.enqueueWork( () -> CrustServerWork.handleFeatureGeneratorData( message ) );
        }
        context.setPacketHandled( true );
    }
    
    /** Reads the message from a data buffer. */
    public static C2SFeatureGeneratorData decode( FriendlyByteBuf buffer ) {
        return new C2SFeatureGeneratorData( buffer.readNbt(), buffer.readGlobalPos() );
    }
    
    /** Writes the message to a data buffer. */
    public static void encode( C2SFeatureGeneratorData message, FriendlyByteBuf buffer ) {
        buffer.writeNbt( message.DATA_TAG );
        buffer.writeGlobalPos( message.POS );
    }
}
