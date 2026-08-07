package fathertoast.crust.common.network.work;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.event.config.CrustConfigEvent;
import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.crust.api.util.OnClient;
import fathertoast.crust.client.screen.CrustConfigFetchScreen;
import fathertoast.crust.client.screen.FeatureGeneratorScreen;
import fathertoast.crust.common.block.entity.FeatureGeneratorBlockEntity;
import fathertoast.crust.common.core.Crust;
import fathertoast.crust.common.mode.CrustModesData;
import fathertoast.crust.common.network.message.clientbound.S2CConfigData;
import fathertoast.crust.common.network.message.clientbound.S2CConfigSync;
import fathertoast.crust.common.network.message.clientbound.S2COpenFeatureGeneratorScreen;
import fathertoast.crust.common.network.message.clientbound.S2CUpdateCrustModes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
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
        
        // If this happens, something is very strange
        if( level == null ) return;
        
        BlockEntity blockEntity = level.getExistingBlockEntity( message.POS );
        
        if( blockEntity instanceof FeatureGeneratorBlockEntity featureGenerator ) {
            Minecraft.getInstance().setScreen( new FeatureGeneratorScreen( featureGenerator ) );
        }
        else {
            Crust.LOG.warn( "Received {} packet from server, but there is no Feature Generator block entity at pos {}!",
                    S2COpenFeatureGeneratorScreen.class.getSimpleName(), message.POS );
        }
    }
}