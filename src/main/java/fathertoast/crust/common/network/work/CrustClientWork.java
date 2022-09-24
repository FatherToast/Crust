package fathertoast.crust.common.network.work;

import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.crust.common.core.Crust;
import fathertoast.crust.common.mode.CrustModesData;
import fathertoast.crust.common.network.message.S2CUpdateCrustModes;
import fathertoast.crust.common.util.annotations.OnClient;
import net.minecraft.client.Minecraft;

@OnClient
public class CrustClientWork {
    
    public static void handleCrustModesUpdate( S2CUpdateCrustModes message ) {
        Minecraft mc = Minecraft.getInstance();
        if( mc.player != null ) {
            NBTHelper.getPlayerData( mc.player, Crust.MOD_ID )
                    .put( CrustModesData.TAG_NAME, message.CRUST_MODES_TAG );
        }
    }
}