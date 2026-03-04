package fathertoast.crust.common.network.work;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.crust.common.mode.CrustModesData;
import fathertoast.crust.common.network.CrustPacketHandler;
import fathertoast.crust.common.network.message.C2SPacketAccepted;
import fathertoast.crust.common.network.message.S2CSendConfigData;
import fathertoast.crust.common.network.message.S2CUpdateCrustModes;
import fathertoast.crust.common.util.annotations.OnClient;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

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
    
    public static void handleReceivedConfigData( S2CSendConfigData message, NetworkEvent.Context context ) {
        CrustConfigSync.processConfigSync( message, context );
        context.setPacketHandled( true );
        CrustPacketHandler.CHANNEL.reply( new C2SPacketAccepted(), context );
    }
    
    private CrustClientWork() { }
}