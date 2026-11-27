package fathertoast.crust.common.network.work;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.event.config.CrustConfigEvent;
import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.crust.api.util.OnClient;
import fathertoast.crust.client.ClientRegister;
import fathertoast.crust.client.renderer.NavigationPathRenderer;
import fathertoast.crust.client.screen.CrustConfigFetchScreen;
import fathertoast.crust.client.screen.FeatureGeneratorScreen;
import fathertoast.crust.common.block.entity.FeatureGeneratorBlockEntity;
import fathertoast.crust.common.core.Crust;
import fathertoast.crust.common.mode.CrustModesData;
import fathertoast.crust.common.network.CrustPacketHandler;
import fathertoast.crust.common.network.message.clientbound.*;
import fathertoast.crust.common.network.message.serverbound.C2SSimpleRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

@OnClient
public final class CrustClientWork {
    
    /** Received upon login or synced file reload to sync relevant fields from server. */
    public static void handleConfigSync( S2CConfigSync message ) {
        Minecraft mc = Minecraft.getInstance();
        if( mc.player != null ) {
            message.apply();
            MinecraftForge.EVENT_BUS.post( new CrustConfigEvent.File.Synced( message.spec().FILE, mc.player ) );
        }
    }
    
    /** Received as a response to our data request to populate the config editor screen with the server's local values. */
    public static void handleConfigData( S2CConfigData message ) {
        Minecraft mc = Minecraft.getInstance();
        if( mc.player != null && mc.screen instanceof CrustConfigFetchScreen screen &&
                screen.SPEC.equals( message.spec() ) ) {
            message.apply();
            screen.receivedData();
        }
    }
    
    public static void handleDestroyItemOnPointer() {
        Minecraft mc = Minecraft.getInstance();
        if( mc.player != null ) mc.player.inventoryMenu.setCarried( ItemStack.EMPTY );
    }
    
    public static void handleCrustModesUpdate( S2CUpdateCrustModes message ) {
        Minecraft mc = Minecraft.getInstance();
        if( mc.player != null ) {
            NBTHelper.getPlayerData( mc.player, ICrustApi.MOD_ID )
                    .put( CrustModesData.TAG_NAME, message.CRUST_MODES_TAG );
        }
    }
    
    public static void handleOpenFeatureGenScreen( S2COpenFeatureGeneratorScreen message ) {
        ClientLevel level = Minecraft.getInstance().level;
        if( level == null ) return;
        
        if( level.getExistingBlockEntity( message.POS ) instanceof FeatureGeneratorBlockEntity featureGenerator ) {
            Minecraft.getInstance().setScreen( new FeatureGeneratorScreen( featureGenerator ) );
        }
        else {
            Crust.LOG.warn( "Received {} packet from server, but there is no Feature Generator block entity at pos {}!",
                    S2COpenFeatureGeneratorScreen.class.getSimpleName(), message.POS );
        }
    }
    
    public static void handleDebugPath( S2CSendPathfindingData message ) {
        ClientLevel level = Minecraft.getInstance().level;
        if( level == null ) return;
        
        if( level.getEntity( message.entityId() ) == null ) {
            Crust.LOG.warn( "Received {} packet from server with entity ID {}, but no entity with said ID exists in the world!",
                    S2CSendPathfindingData.class.getSimpleName(), message.entityId() );
        }
        else {
            NavigationPathRenderer.putEntry( message.entityId(), message.path() );
        }
    }
    
    /** Sent by clients that request a simple task to be executed by the server. */
    public static void handleSimpleRequest( S2CSimpleRequest message ) {
        S2CSimpleRequest.Type type = message.type();
        
        switch( type ) {
            case NULL -> Crust.LOG.warn( "Received {} packet from server with NULL request. This is likely an error!",
                    S2CSimpleRequest.class.getSimpleName() );
            case DESTROY_ITEM_ON_CURSOR -> {
                Minecraft mc = Minecraft.getInstance();
                if( mc.player != null ) mc.player.inventoryMenu.setCarried( ItemStack.EMPTY );
            }
            case TOGGLE_PATHFINDER_DATA_REQUEST -> {
                boolean enabled = ClientRegister.RENDER_SETTINGS.renderPth.get();
                CrustPacketHandler.sendSimpleRequest( enabled ? C2SSimpleRequest.Type.RECEIVE_PATHFINDER_DATA : C2SSimpleRequest.Type.UNSUBSCRIBE_PATHFINDER_DATA );
            }
        }
    }
}