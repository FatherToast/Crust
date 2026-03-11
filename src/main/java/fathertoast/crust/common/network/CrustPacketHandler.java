package fathertoast.crust.common.network;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.common.block.entity.FeatureGeneratorBlockEntity;
import fathertoast.crust.common.mode.CrustModesData;
import fathertoast.crust.common.network.message.C2SFeatureGeneratorData;
import fathertoast.crust.common.network.message.S2CDestroyItemOnPointer;
import fathertoast.crust.common.network.message.S2COpenFeatureGeneratorScreen;
import fathertoast.crust.common.network.message.S2CUpdateCrustModes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class CrustPacketHandler {
    
    //
    // Protocol version history (mod version -> protocol version)
    //
    // ver <= 4.12.20 -> 0
    // ver >  4.12.20 -> 1
    //
    private static final String PROTOCOL_VERSION = "1";
    
    /** The network channel our mod will be using when sending messages. */
    public static final SimpleChannel CHANNEL = createChannel();
    
    private static int messageIndex;
    
    /** Sends Crust modes data to its owner's client. */
    public static void sendDestroyItemOnPointerUpdate( ServerPlayer player ) {
        sendToClient( player, S2CDestroyItemOnPointer.INSTANCE );
    }
    
    /** Sends Crust modes data to its owner's client. */
    public static void sendCrustModesUpdate( ServerPlayer player ) {
        sendToClient( player, new S2CUpdateCrustModes( CrustModesData.of( player ).getSaveTag() ) );
    }
    
    public static void openFeatureGeneratorScreen( ServerPlayer player, BlockEntity blockEntity ) {
        sendToClient( player, new S2COpenFeatureGeneratorScreen( blockEntity.getBlockPos() ) );
    }
    
    /** Sends a feature generator data update to the server for the given feature generator block entity. */
    public static void sendFeatureGeneratorData( FeatureGeneratorBlockEntity featureGenerator ) {
        Objects.requireNonNull( featureGenerator );
        Objects.requireNonNull( featureGenerator.getLevel() );
        
        final CompoundTag tag = new CompoundTag();
        featureGenerator.getData().saveTo( tag );
        
        CHANNEL.sendToServer( new C2SFeatureGeneratorData(
                tag,
                featureGenerator.getLevel(),
                featureGenerator.getBlockPos()
        ) );
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
        // Server -> Client
        registerMessage( S2CUpdateCrustModes.class, S2CUpdateCrustModes::encode, S2CUpdateCrustModes::decode, S2CUpdateCrustModes::handle );
        registerMessage( S2CDestroyItemOnPointer.class, S2CDestroyItemOnPointer::encode, S2CDestroyItemOnPointer::decode, S2CDestroyItemOnPointer::handle );
        registerMessage( S2COpenFeatureGeneratorScreen.class, S2COpenFeatureGeneratorScreen::encode, S2COpenFeatureGeneratorScreen::decode, S2COpenFeatureGeneratorScreen::handle );
        
        // Client -> Server
        registerMessage( C2SFeatureGeneratorData.class, C2SFeatureGeneratorData::encode, C2SFeatureGeneratorData::decode, C2SFeatureGeneratorData::handle );
    }
    
    /** Registers a message with an auto-assigned 'message index'. */
    private static <MSG> void registerMessage( Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder,
                                               BiConsumer<MSG, Supplier<NetworkEvent.Context>> handler ) {
        CHANNEL.registerMessage( messageIndex++, messageType, encoder, decoder,
                handler, Optional.empty() );
    }
    
    private static SimpleChannel createChannel() {
        return NetworkRegistry.ChannelBuilder
                .named( ResourceLocation.fromNamespaceAndPath( ICrustApi.MOD_ID, "channel" ) )
                .serverAcceptedVersions( PROTOCOL_VERSION::equals )
                .clientAcceptedVersions( PROTOCOL_VERSION::equals )
                .networkProtocolVersion( () -> PROTOCOL_VERSION )
                .simpleChannel();
    }
}