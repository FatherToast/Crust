package fathertoast.crust.common.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.common.command.CommandUtil;
import fathertoast.crust.common.network.CrustPacketHandler;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class CrustCleanCommand {
    
    /** Command builder. */
    public static void register( CommandDispatcher<CommandSourceStack> dispatcher ) {
        // crustclean pointer [<player>]
        dispatcher.register( CommandUtil.literal( ICrustApi.MOD_ID + "clean" )
                .then( CommandUtil.literal( "pointer" )
                        .executes( ( context ) -> runPointer( context.getSource(), CommandUtil.player( context ) ) )
                        .then( CommandUtil.argumentPlayer( "player" )
                                .executes( ( context ) -> runPointer( context.getSource(), CommandUtil.player( context, "player" ) ) ) ) )
        ); // crustclean [<players>] goes here when implemented
    }
    
    /** Command implementation. */
    private static int runPointer( CommandSourceStack source, ServerPlayer player ) {
        if( player.inventoryMenu.getCarried().isEmpty() ) {
            CommandUtil.sendFailure( source, "clean.pointer", player.getDisplayName() );
            return 0;
        }
        
        player.inventoryMenu.setCarried( ItemStack.EMPTY );
        CrustPacketHandler.sendDestroyItemOnPointerUpdate( player );
        CommandUtil.sendSuccess( source, "clean.pointer", player.getDisplayName() );
        return 1;
    }
}