package fathertoast.crust.common.network;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.event.config.CrustConfigEvent;
import fathertoast.crust.common.block.entity.FeatureGeneratorBlockEntity;
import fathertoast.crust.common.mode.CrustModesData;
import fathertoast.crust.common.network.message.clientbound.*;
import fathertoast.crust.common.network.message.serverbound.C2SConfigChangeRequest;
import fathertoast.crust.common.network.message.serverbound.C2SConfigDataRequest;
import fathertoast.crust.common.network.message.serverbound.C2SFeatureGeneratorData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class CrustPacketHandler {
    
    // ---- Message & Channel Setup ---- //
    
    /**
     * Protocol version history (mod version -> protocol version)
     * <p> ver <= 4.12.20 -> 0
     * <p> ver <= 4.17.28 -> 1
     * <p> ver >  4.17.28 -> 2
     */
    private static final String PROTOCOL_VERSION = "2";
    
    /** The network channel our mod will be using when sending messages. */
    private static final SimpleChannel CHANNEL = createChannel();
    
    static {
        ConfigManager.SYNC_SPEC_CONSUMER = CrustPacketHandler::sendConfigSync;
    }
    
    /** Registers this mod's messages. */
    public void registerMessages() {
        int messageIndex = -1;
        
        // Server -> Client
        registerClientboundMessage( ++messageIndex, S2CConfigSync.class,
                S2CConfigSync::encode, S2CConfigSync::decode, S2CConfigSync::handle );
        registerClientboundMessage( ++messageIndex, S2CConfigData.class,
                S2CConfigData::encode, S2CConfigData::decode, S2CConfigData::handle );
        registerClientboundMessage( ++messageIndex, S2CUpdateCrustModes.class,
                S2CUpdateCrustModes::encode, S2CUpdateCrustModes::decode, S2CUpdateCrustModes::handle );
        registerClientboundMessage( ++messageIndex, S2CDestroyItemOnPointer.class,
                S2CDestroyItemOnPointer::encode, S2CDestroyItemOnPointer::decode, S2CDestroyItemOnPointer::handle );
        registerClientboundMessage( ++messageIndex, S2COpenFeatureGeneratorScreen.class,
                S2COpenFeatureGeneratorScreen::encode, S2COpenFeatureGeneratorScreen::decode, S2COpenFeatureGeneratorScreen::handle );
        
        // Client -> Server
        registerServerboundMessage( ++messageIndex, C2SConfigDataRequest.class,
                C2SConfigDataRequest::encode, C2SConfigDataRequest::decode, C2SConfigDataRequest::handle );
        registerServerboundMessage( ++messageIndex, C2SConfigChangeRequest.class,
                C2SConfigChangeRequest::encode, C2SConfigChangeRequest::decode, C2SConfigChangeRequest::handle );
        registerServerboundMessage( ++messageIndex, C2SFeatureGeneratorData.class,
                C2SFeatureGeneratorData::encode, C2SFeatureGeneratorData::decode, C2SFeatureGeneratorData::handle );
    }
    
    
    // ---- Server -> Client Message Sending ---- //
    
    /** Syncs each relevant config to the player. */
    public static void sendConfigSync( ServerPlayer player ) {
        ConfigManager.getAll().forEach( cfgManager ->
                cfgManager.getConfigs().forEach( file -> {
                    if( file.SPEC.isSynced() ) {
                        sendToClient( new S2CConfigSync( file.SPEC ), player );
                        MinecraftForge.EVENT_BUS.post( new CrustConfigEvent.File.Synced( file, player ) );
                    }
                } ) );
    }
    
    /** Syncs the config to all players. */
    public static void sendConfigSync( CrustConfigSpec spec ) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if( server == null || !spec.isSynced() ) return;
        sendToAll( new S2CConfigSync( spec ) );
        server.getPlayerList().getPlayers().forEach( player ->
                MinecraftForge.EVENT_BUS.post( new CrustConfigEvent.File.Synced( spec.FILE, player ) ) );
    }
    
    /** Sends config data to the player. */
    public static void sendConfigData( CrustConfigSpec spec, ServerPlayer player ) {
        sendToClient( new S2CConfigData( spec ), player );
    }
    
    /** Sends Crust modes data to its owner's client. */
    public static void sendCrustModesUpdate( ServerPlayer player ) {
        sendToClient( new S2CUpdateCrustModes( CrustModesData.of( player ).getSaveTag() ), player );
    }
    
    /** Sends Crust modes data to its owner's client. */
    public static void sendDestroyItemOnPointerUpdate( ServerPlayer player ) {
        sendToClient( S2CDestroyItemOnPointer.INSTANCE, player );
    }
    
    /** Requests opening the Feature Generator GUI on the client for the specified player. */
    public static void openFeatureGeneratorScreen( ServerPlayer player, BlockEntity blockEntity ) {
        sendToClient( new S2COpenFeatureGeneratorScreen( blockEntity.getBlockPos() ), player );
    }
    
    
    // ---- Client -> Server Message Sending ---- //
    
    /** Sends a config data request to the server. */
    public static void sendConfigDataRequest( CrustConfigSpec spec ) {
        CHANNEL.sendToServer( new C2SConfigDataRequest( spec ) );
    }
    
    /** Sends a config change request to the server. */
    public static void sendConfigChangeRequest( CrustConfigSpec spec ) {
        CHANNEL.sendToServer( new C2SConfigChangeRequest( spec ) );
    }
    
    /** Sends a feature generator data update to the server for the given feature generator block entity. */
    public static void sendFeatureGeneratorData( FeatureGeneratorBlockEntity featureGenerator ) {
        Objects.requireNonNull( featureGenerator );
        Objects.requireNonNull( featureGenerator.getLevel() );
        
        // Construct packet
        final CompoundTag tag = new CompoundTag();
        featureGenerator.getData().saveTo( tag );
        C2SFeatureGeneratorData packet = new C2SFeatureGeneratorData(
                tag,
                featureGenerator.getLevel(),
                featureGenerator.getBlockPos()
        );
        CHANNEL.sendToServer( packet );
    }
    
    
    // ---- Internal Methods ---- //
    
    /**
     * Sends the specified message to all connected clients.
     *
     * @param message The message to send to the clients.
     * @param <MSG>   Packet type.
     */
    private static <MSG> void sendToAll( MSG message ) {
        if( ServerLifecycleHooks.getCurrentServer() != null ) {
            CHANNEL.send( PacketDistributor.ALL.noArg(), message );
        }
    }
    
    /**
     * Sends the specified message to all connected clients in the level/dimension.
     *
     * @param message The message to send to the clients.
     * @param level   The level that should receive this message.
     * @param <MSG>   Packet type.
     */
    private static <MSG> void sendToDimension( MSG message, Level level ) {
        CHANNEL.send( PacketDistributor.DIMENSION.with( level::dimension ), message );
    }
    
    /**
     * Sends the specified message to all connected clients in the level/dimension.
     *
     * @param message   The message to send to the clients.
     * @param dimension The dimension that should receive this message.
     * @param <MSG>     Packet type.
     */
    private static <MSG> void sendToDimension( MSG message, ResourceKey<Level> dimension ) {
        CHANNEL.send( PacketDistributor.DIMENSION.with( () -> dimension ), message );
    }
    
    /**
     * Sends the specified message to the client.
     *
     * @param message The message to send to the client.
     * @param player  The player client that should receive this message.
     * @param <MSG>   Packet type.
     */
    private static <MSG> void sendToClient( MSG message, ServerPlayer player ) {
        CHANNEL.sendTo( message, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT );
    }
    
    /** Registers a typical clientbound message. */
    private <MSG> void registerClientboundMessage( int messageIndex, Class<MSG> messageType,
                                                   BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder,
                                                   BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer ) {
        registerMessage( messageIndex, messageType, encoder, decoder, consumer, NetworkDirection.PLAY_TO_CLIENT );
    }
    
    /** Registers a typical serverbound message. */
    private <MSG> void registerServerboundMessage( int messageIndex, Class<MSG> messageType,
                                                   BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder,
                                                   BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer ) {
        registerMessage( messageIndex, messageType, encoder, decoder, consumer, NetworkDirection.PLAY_TO_SERVER );
    }
    
    /** Registers a message with specified direction. */
    private <MSG> void registerMessage( int messageIndex, Class<MSG> messageType,
                                        BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder,
                                        BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer, NetworkDirection direction ) {
        CHANNEL.registerMessage( messageIndex, messageType, encoder, decoder, consumer,
                Optional.of( direction ) );
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