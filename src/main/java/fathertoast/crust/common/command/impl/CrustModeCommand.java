package fathertoast.crust.common.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.common.command.CommandUtil;
import fathertoast.crust.common.mode.CrustModes;
import fathertoast.crust.common.mode.CrustModesData;
import fathertoast.crust.common.mode.type.CrustMode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.Collection;

public class CrustModeCommand {
    
    /** Command builder. */
    public static void register( CommandDispatcher<CommandSourceStack> dispatcher ) {
        // crustmode [<player>]
        LiteralArgumentBuilder<CommandSourceStack> argBuilder = CommandUtil.literal( ICrustApi.MOD_ID + "mode" )
                .executes( ( context ) -> runQuery( context.getSource(), CommandUtil.player( context ) ) )
                
                .then( CommandUtil.argumentPlayer( "player" )
                        .executes( ( context ) -> runQuery( context.getSource(),
                                CommandUtil.player( context, "player" ) ) ) );
        
        // crustmode <mode> (disable|<value>) [<players>]
        for( CrustMode<?> mode : CrustModes.registry().values() ) {
            argBuilder.then( CommandUtil.literal( mode.ID )
                    .requires( ( source ) -> source.hasPermission( mode.OP_LEVEL.get() ) )
                    
                    .then( CommandUtil.literal( "disable" )
                            .executes( ( context ) -> runSet( context, mode, null,
                                    CommandUtil.players( context ) ) )
                            .then( CommandUtil.argumentPlayers( "players" )
                                    .executes( ( context ) -> runSet( context, mode, null,
                                            CommandUtil.players( context, "players" ) ) ) ) )
                    
                    .then( mode.commandArgument( "value" )
                            .executes( ( context ) -> runSet( context, mode, "value",
                                    CommandUtil.players( context ) ) )
                            .then( CommandUtil.argumentPlayers( "players" )
                                    .executes( ( context ) -> runSet( context, mode, "value",
                                            CommandUtil.players( context, "players" ) ) ) ) )
            );
        }
        
        dispatcher.register( argBuilder );
    }
    
    /** Command implementation. */
    private static int runQuery( CommandSourceStack source, ServerPlayer player ) {
        CrustModesData playerModes = CrustModesData.of( player );
        StringBuilder output = new StringBuilder( "[ " );
        int modes = 0;
        for( CrustMode<?> mode : CrustModes.registry().values() ) {
            if( playerModes.enabled( mode ) ) {
                modes++;
                if( modes > 1 ) output.append( ", " );
                output.append( mode.ID ).append( '=' ).append( TomlHelper.toLiteral( playerModes.get( mode ) ) );
            }
        }
        output.append( " ]" );
        CommandUtil.sendSuccess( source, "mode.query", player.getDisplayName(), output.toString() );
        return modes; // return the number of enabled modes
    }
    
    /** Command implementation. */
    private static int runSet( CommandContext<CommandSourceStack> context, CrustMode<?> mode, @Nullable String valueArg,
                               Collection<ServerPlayer> players ) {
        for( ServerPlayer player : players ) {
            mode.onCommand( context, valueArg, player );
        }
        
        String event = "mode." + (valueArg == null ? "disable" : "enable");
        if( players.size() == 1 ) {
            CommandUtil.sendSuccess( context.getSource(), event + ".single",
                    mode.ID, players.iterator().next().getDisplayName() );
        }
        else {
            CommandUtil.sendSuccess( context.getSource(), event + ".multiple", mode.ID, players.size() );
        }
        return players.size(); // return the number of players affected
    }
}