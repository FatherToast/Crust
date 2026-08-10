package fathertoast.crust.common.network.work;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.event.config.CrustConfigEvent;
import fathertoast.crust.api.lib.CrustCmdHelper;
import fathertoast.crust.common.block.entity.FeatureGeneratorBlockEntity;
import fathertoast.crust.common.config.CrustConfig;
import fathertoast.crust.common.core.Crust;
import fathertoast.crust.common.network.CrustPacketHandler;
import fathertoast.crust.common.network.message.serverbound.C2SConfigChangeRequest;
import fathertoast.crust.common.network.message.serverbound.C2SConfigDataRequest;
import fathertoast.crust.common.network.message.serverbound.C2SFeatureGeneratorData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;


public final class CrustServerWork {
    
    /** Sent by clients that would like to view one of the server's local config files in the config editor screen. */
    public static void handleConfigDataRequest( C2SConfigDataRequest message, @Nullable ServerPlayer sender ) {
        if( sender != null ) {
            var event = new CrustConfigEvent.File.ReadAccessRequest( message.spec().FILE, sender );
            MinecraftForge.EVENT_BUS.post( event );
            Event.Result result = event.getResult();
            if( result == Event.Result.ALLOW || result == Event.Result.DEFAULT &&
                    CrustCmdHelper.hasPermissions( sender, CrustConfig.UTILITIES.CONFIGS.viewConfigsOpLevel.get() ) ) {
                CrustPacketHandler.sendConfigData( message.spec(), sender );
            }
        }
    }
    
    /** Sent by clients that would like to apply changes to one of the server's local config files. */
    public static void handleConfigChangeRequest( C2SConfigChangeRequest message, @Nullable ServerPlayer sender ) {
        if( sender == null ) return;
        var event = new CrustConfigEvent.File.WriteAccessRequest( message.spec().FILE, sender );
        MinecraftForge.EVENT_BUS.post( event );
        Event.Result result = event.getResult();
        if( result == Event.Result.ALLOW || result == Event.Result.DEFAULT &&
                CrustCmdHelper.hasPermissions( sender, CrustCmdHelper.PERMISSION_SERVER_OP ) ) {
            // Accept the config change
            message.apply();
        }
        else {
            // Illegal operation; player would need to have the config re-synced, but instead we kick them
            ConfigUtil.LOG.warn( "Player attempted to edit server's configs without permission: name={}, uuid={}",
                    sender.getGameProfile().getName(), sender.getStringUUID() );
            sender.connection.disconnect( Component.translatable(
                    "multiplayer.disconnect.crust.config_edit_not_allowed" ) );
        }
    }
    
    public static void handleFeatureGeneratorData( C2SFeatureGeneratorData message ) {
        ServerLevel level = ServerLifecycleHooks.getCurrentServer().getLevel( message.POS.dimension() );
        
        // Check if level exists
        if( level == null ) {
            Crust.LOG.warn( "Received {} packet with invalid position data! Dimension '{}' does not exist on the server!",
                    C2SFeatureGeneratorData.class.getSimpleName(), message.POS.dimension().location() );
            return;
        }
        final BlockPos pos = message.POS.pos();
        
        // Make sure we are in a loaded chunk
        if( !level.isLoaded( pos ) ) return;
        
        final BlockEntity blockEntity = level.getExistingBlockEntity( pos );
        
        if( blockEntity instanceof FeatureGeneratorBlockEntity featureGenerator ) {
            FeatureGeneratorBlockEntity.FeatureData data = FeatureGeneratorBlockEntity.FeatureData.newEmpty();
            data.loadFrom( message.DATA_TAG );
            featureGenerator.setData( data );
        }
        // No feature generator block entity at position, log warning
        else {
            Crust.LOG.warn( "Received {} packet for position {}, but no feature generator block entity was found!",
                    C2SFeatureGeneratorData.class.getSimpleName(), message.POS.toString() );
        }
    }
}