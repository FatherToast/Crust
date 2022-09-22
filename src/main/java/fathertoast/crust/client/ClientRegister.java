package fathertoast.crust.client;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import fathertoast.crust.api.config.client.ClientConfigUtil;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.common.core.Crust;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ISuggestionProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = Crust.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class ClientRegister {
    
    public static final ExtraInvButtonsCrustConfigFile EXTRA_INV_BUTTONS =
            new ExtraInvButtonsCrustConfigFile( ConfigManager.getRequired( Crust.MOD_ID ), "extra_inv_buttons" );
    
    /** Called after common setup to perform client-side-only setup. */
    @SubscribeEvent
    static void onClientSetup( FMLClientSetupEvent event ) {
        // Perform first-time loading of the client-only config
        EXTRA_INV_BUTTONS.SPEC.initialize();
        
        // Tell Forge to open our config folder when our mod's "Config" button is clicked in the Mods screen
        ClientConfigUtil.registerConfigButtonAsOpenFolder();
    }
}