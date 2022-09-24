package fathertoast.crust.common.network;

import fathertoast.crust.common.core.Crust;
import fathertoast.crust.common.mode.CrustModesData;
import fathertoast.crust.common.network.message.S2CUpdateCrustModes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CrustPacketHandler {
    
    private static final String PROTOCOL_VERSION = "0";
    
    /** The network channel our mod will be using when sending messages. */
    public static final SimpleChannel CHANNEL = createChannel();
    
    private static int messageIndex;
    
    /** Sends Crust modes data to its owner's client. */
    public static void sendCrustModesUpdate( ServerPlayerEntity player ) {
        sendToClient( player, new S2CUpdateCrustModes( CrustModesData.of( player ).getSaveTag() ) );
    }
    
    /**
     * Sends the specified message to the client.
     *
     * @param message The message to send to the client.
     * @param player  The player client that should receive this message.
     * @param <MSG>   Packet type.
     */
    private static <MSG> void sendToClient( ServerPlayerEntity player, MSG message ) {
        CHANNEL.sendTo( message, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT );
    }
    
    /** Registers this mod's messages. */
    public static void registerMessages() {
        registerMessage( S2CUpdateCrustModes.class, S2CUpdateCrustModes::encode, S2CUpdateCrustModes::decode, S2CUpdateCrustModes::handle );
    }
    
    /** Registers a message with an auto-assigned 'message index'. */
    private static <MSG> void registerMessage( Class<MSG> messageType, BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder,
                                               BiConsumer<MSG, Supplier<NetworkEvent.Context>> handler ) {
        CHANNEL.registerMessage( messageIndex++, messageType, encoder, decoder,
                handler, Optional.empty() );
    }
    
    private static SimpleChannel createChannel() {
        return NetworkRegistry.ChannelBuilder
                .named( new ResourceLocation( Crust.MOD_ID, "channel" ) )
                .serverAcceptedVersions( PROTOCOL_VERSION::equals )
                .clientAcceptedVersions( PROTOCOL_VERSION::equals )
                .networkProtocolVersion( () -> PROTOCOL_VERSION )
                .simpleChannel();
    }
}