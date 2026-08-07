package fathertoast.crust.common.command;

import com.mojang.brigadier.CommandDispatcher;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.lib.CrustCmdHelper;
import fathertoast.crust.common.network.CrustPacketHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class CrustCleanCommand {
    
    public static final String NAME = ICrustApi.MOD_ID + "clean";
    
    /** Command builder. */
    public static void register( CommandDispatcher<CommandSourceStack> dispatcher ) {
        // crustclean pointer [<player>]
        dispatcher.register( CrustCmdHelper.literal( NAME )
                .then( CrustCmdHelper.literal( "pointer" )
                        .executes( ( context ) -> runPointer( context.getSource(), CrustCmdHelper.player( context ) ) )
                        .then( CrustCmdHelper.argumentPlayer( "player" )
                                .executes( ( context ) -> runPointer( context.getSource(), CrustCmdHelper.player( context, "player" ) ) ) ) )
        ); // crustclean [<players>] goes here when implemented
    }
    
    /** Command implementation. */
    private static int runPointer( CommandSourceStack source, ServerPlayer player ) {
        if( player.containerMenu.getCarried().isEmpty() ) {
            CrustCmdHelper.sendFailure( source, NAME, "pointer", player.getDisplayName() );
            return 0;
        }
        
        player.containerMenu.setCarried( ItemStack.EMPTY );
        CrustPacketHandler.sendDestroyItemOnPointerUpdate( player );
        CrustCmdHelper.sendSuccess( source, NAME, "pointer", player.getDisplayName() );
        return 1;
    }
}