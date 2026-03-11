package fathertoast.crust.common.network.work;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.crust.api.util.OnClient;
import fathertoast.crust.client.screen.FeatureGeneratorScreen;
import fathertoast.crust.common.block.entity.FeatureGeneratorBlockEntity;
import fathertoast.crust.common.core.Crust;
import fathertoast.crust.common.mode.CrustModesData;
import fathertoast.crust.common.network.message.S2COpenFeatureGeneratorScreen;
import fathertoast.crust.common.network.message.S2CUpdateCrustModes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

@OnClient
public final class CrustClientWork {
    
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