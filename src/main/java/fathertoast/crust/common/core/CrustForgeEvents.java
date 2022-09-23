package fathertoast.crust.common.core;

import fathertoast.crust.common.command.impl.CrustPortalCommand;
import fathertoast.crust.common.command.impl.CrustRecoverCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber( modid = Crust.MOD_ID )
public class CrustForgeEvents {
    
    /** Called each time commands are loaded. */
    @SubscribeEvent
    static void registerCommands( RegisterCommandsEvent event ) {
        CrustRecoverCommand.register( event.getDispatcher() );
        CrustPortalCommand.register( event.getDispatcher() );
    }
}