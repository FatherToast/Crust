package fathertoast.crust.common.network;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.common.mode.CrustModesData;
import fathertoast.crust.common.network.message.S2CDestroyItemOnPointer;
import fathertoast.crust.common.network.message.S2CUpdateCrustModes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

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
    public static void sendDestroyItemOnPointerUpdate( ServerPlayer player ) {
        sendToClient( player, new S2CDestroyItemOnPointer() );
    }
    
    /** Sends Crust modes data to its owner's client. */
    public static void sendCrustModesUpdate( ServerPlayer player ) {
        sendToClient( player, new S2CUpdateCrustModes( CrustModesData.of( player ).getSaveTag() ) );
    }
    
    /**
     * Sends the specified message to the client.
     *
     * @param message The message to send to the client.
     * @param player  The player client that should receive this message.
     * @param <MSG>   Packet type.
     */
    private static <MSG> void sendToClient( ServerPlayer player, MSG message ) {
        CHANNEL.sendTo( message, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT );
    }
    
    /** Registers this mod's messages. */
    public static void registerMessages() {
        registerMessage( S2CUpdateCrustModes.class, S2CUpdateCrustModes::encode, S2CUpdateCrustModes::decode, S2CUpdateCrustModes::handle );
        registerMessage( S2CDestroyItemOnPointer.class, S2CDestroyItemOnPointer::encode, S2CDestroyItemOnPointer::decode, S2CDestroyItemOnPointer::handle );
    }
    
    /** Registers a message with an auto-assigned 'message index'. */
    private static <MSG> void registerMessage( Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder,
                                              BiConsumer<MSG, Supplier<NetworkEvent.Context>> handler ) {
        CHANNEL.registerMessage( messageIndex++, messageType, encoder, decoder,
                handler, Optional.empty() );
    }
    
    private static SimpleChannel createChannel() {
        return NetworkRegistry.ChannelBuilder
                .named( new ResourceLocation( ICrustApi.MOD_ID, "channel" ) )
                .serverAcceptedVersions( PROTOCOL_VERSION::equals )
                .clientAcceptedVersions( PROTOCOL_VERSION::equals )
                .networkProtocolVersion( () -> PROTOCOL_VERSION )
                .simpleChannel();
    }
}