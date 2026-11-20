package fathertoast.crust.common.network.message.clientbound;

import fathertoast.crust.common.core.Crust;
import fathertoast.crust.common.network.work.CrustClientWork;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record S2CSendPathfindingData(int entityId, @Nullable Path path) {
    
    /** Handles receipt of the message. */
    public static void handle( S2CSendPathfindingData message, Supplier<NetworkEvent.Context> contextSupplier ) {
        NetworkEvent.Context context = contextSupplier.get();
        
        if( context.getDirection().getReceptionSide().isClient() ) {
            context.enqueueWork( () -> CrustClientWork.handleDebugPath( message ) );
        }
        context.setPacketHandled( true );
    }
    
    /** Reads the message from a data buffer. */
    public static S2CSendPathfindingData decode( FriendlyByteBuf buffer ) {
        int entityId = 0;
        Path path = null;
        
        try {
            entityId = buffer.readInt();
            path = buffer.readNullable( S2CSendPathfindingData::readPath );
        }
        catch( Exception e ) {
            Crust.LOG.warn( "Failed to decode S2CSendPathfindingData packet!" );
            // noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        return new S2CSendPathfindingData( entityId, path );
    }
    
    /** Writes the message to a data buffer. */
    public static void encode( S2CSendPathfindingData message, FriendlyByteBuf buffer ) {
        buffer.writeInt( message.entityId );
        buffer.writeNullable( message.path, S2CSendPathfindingData::writePath );
    }
    
    /**
     * Attempts to read a navigation path from a data buffer.
     * Logs an error if something went wrong.
     */
    @Nullable
    private static Path readPath( FriendlyByteBuf buffer ) {
        try {
            boolean reached = buffer.readBoolean();
            int nextNodeIndex = buffer.readVarInt();
            BlockPos target = buffer.readBlockPos();
            int nodeCount = buffer.readVarInt();
            
            List<Node> nodes = new ArrayList<>();
            for( int i = 0; i < nodeCount; i++ ) {
                // TODO - Depending on the final visual design, we may only need to send the XYZ of each node
                nodes.add( Node.createFromStream( buffer ) );
            }
            Path path = new Path( nodes, target, reached );
            path.setNextNodeIndex( nextNodeIndex );
            return path;
        }
        catch( Exception e ) {
            Crust.LOG.error( "Failed to read navigation path from network!" );
            // noinspection CallToPrintStackTrace
            e.printStackTrace();
            return null;
        }
    }
    
    /** Writes the given navigation path to a data buffer. */
    private static void writePath( FriendlyByteBuf buffer, Path path ) {
        buffer.writeBoolean( path.canReach() );
        buffer.writeVarInt( path.getNextNodeIndex() );
        buffer.writeBlockPos( path.getTarget() );
        buffer.writeVarInt( path.getNodeCount() );
        path.nodes.forEach( ( node ) -> node.writeToStream( buffer ) );
    }
}
