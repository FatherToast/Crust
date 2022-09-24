package fathertoast.crust.common.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.common.command.CommandUtil;
import fathertoast.crust.common.core.Crust;
import fathertoast.crust.common.mode.CrustModes;
import fathertoast.crust.common.mode.CrustModesData;
import fathertoast.crust.common.mode.type.CrustMode;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;

import javax.annotation.Nullable;
import java.util.Collection;

public class CrustModeCommand {
    
    /** Command builder. */
    public static void register( CommandDispatcher<CommandSource> dispatcher ) {
        // crustmode [<player>]
        LiteralArgumentBuilder<CommandSource> argBuilder = CommandUtil.literal( Crust.MOD_ID + "mode" )
                .executes( ( context ) -> runQuery( context.getSource(), CommandUtil.player( context ) ) )
                
                .then( CommandUtil.argumentPlayer( "player" )
                        .executes( ( context ) -> runQuery( context.getSource(),
                                CommandUtil.player( context, "player" ) ) ) );
        
        // crustmode <mode> (disable|<value>) [<players>]
        for( CrustMode<?> mode : CrustModes.registry().values() ) {
            argBuilder.then( CommandUtil.literal( mode.ID )
                    .requires( ( source ) -> source.hasPermission( mode.OP_LEVEL ) )
                    
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
    private static int runQuery( CommandSource source, ServerPlayerEntity player ) {
        CrustModesData playerModes = new CrustModesData( player );
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
    private static int runSet( CommandContext<CommandSource> context, CrustMode<?> mode, @Nullable String valueArg,
                               Collection<ServerPlayerEntity> players ) {
        for( ServerPlayerEntity player : players ) {
            mode.onCommand( context, valueArg, player );
        }
        
        String event = "mode." + (valueArg == null ? "disable" : "enable");
        if( players.size() == 1 ) {
            CommandUtil.sendSuccess( context.getSource(), event + ".single",
                    mode, valueArg, players.iterator().next().getDisplayName() );
        }
        else {
            CommandUtil.sendSuccess( context.getSource(), event + ".multiple", mode, valueArg, players.size() );
        }
        return players.size(); // return the number of players affected
    }
}