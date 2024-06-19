package fathertoast.crust.common.network.work;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.crust.common.mode.CrustModesData;
import fathertoast.crust.common.network.message.S2CDestroyItemOnPointer;
import fathertoast.crust.common.network.message.S2CUpdateCrustModes;
import fathertoast.crust.common.util.annotations.OnClient;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

@OnClient
public class CrustClientWork {
    
    public static void handleDestroyItemOnPointer( @SuppressWarnings( "unused" ) S2CDestroyItemOnPointer message ) {
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
}