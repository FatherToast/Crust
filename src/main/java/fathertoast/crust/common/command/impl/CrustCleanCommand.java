package fathertoast.crust.common.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import fathertoast.crust.common.command.CommandUtil;
import fathertoast.crust.common.core.Crust;
import fathertoast.crust.common.network.CrustPacketHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;

public class CrustCleanCommand {
    
    /** Command builder. */
    public static void register( CommandDispatcher<CommandSource> dispatcher ) {
        // crustclean pointer [<player>]
        dispatcher.register( CommandUtil.literal( Crust.MOD_ID + "clean" )
                .then( CommandUtil.literal( "pointer" )
                        .executes( ( context ) -> runPointer( context.getSource(), CommandUtil.player( context ) ) )
                        .then( CommandUtil.argumentPlayer( "player" )
                                .executes( ( context ) -> runPointer( context.getSource(), CommandUtil.player( context, "player" ) ) ) ) )
        ); // crustclean [<players>] goes here when implemented
    }
    
    /** Command implementation. */
    private static int runPointer( CommandSource source, ServerPlayerEntity player ) {
        if( player.inventory.getCarried().isEmpty() ) {
            CommandUtil.sendFailure( source, "clean.pointer", player.getDisplayName() );
            return 0;
        }
        
        player.inventory.setCarried( ItemStack.EMPTY );
        CrustPacketHandler.sendDestroyItemOnPointerUpdate( player );
        CommandUtil.sendSuccess( source, "clean.pointer", player.getDisplayName() );
        return 1;
    }
}