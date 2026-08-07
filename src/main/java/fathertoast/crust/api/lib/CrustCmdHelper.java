package fathertoast.crust.api.lib;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Locale;

/**
 * Provides static helper methods to make building commands easier and more readable.
 * <p>
 * For examples of this being used, see Crust's {@link fathertoast.crust.common.command command package}.
 */
@SuppressWarnings( "unused" )
public final class CrustCmdHelper {
    
    // ---- Permissions ---- //
    
    public static final byte PERMISSION_NONE = 0;
    public static final byte PERMISSION_TRUSTED = 1;
    public static final byte PERMISSION_CHEAT = 2;
    public static final byte PERMISSION_MODERATE = 3;
    public static final byte PERMISSION_SERVER_OP = 4;
    
    /** @return True if the source is allowed to cheat (op level 2+). */
    public static boolean canCheat( CommandSourceStack source ) { return source.hasPermission( PERMISSION_CHEAT ); }
    
    /** @return True if the source is a moderator (op level 3+). */
    public static boolean isModerator( CommandSourceStack source ) { return source.hasPermission( PERMISSION_MODERATE ); }
    
    /** @return True if the source is a server operator (op level 4). */
    public static boolean isServerOp( CommandSourceStack source ) { return source.hasPermission( PERMISSION_SERVER_OP ); }
    
    /**
     * @return True if the player has appropriate permissions, or they are the owner on an integrated server.
     * Always returns true when out of the game (title screen, etc.).
     * <p>
     * Note that this works differently from baseline permissions in that if the player is hosting an integrated
     * server (doesn't need to be opened to LAN), they are considered to have all permissions.
     */
    public static boolean hasPermissions( @Nullable Player player, int level ) {
        if( player == null || player.hasPermissions( level ) ) return true;
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server != null && server.isSingleplayerOwner( player.getGameProfile() );
    }
    
    
    // ---- Command Feedback ---- //
    
    /** Provides feedback on successful command results. */
    public static void sendSuccess( CommandSourceStack source, String cmd, String event, Object... args ) {
        source.sendSuccess( () -> Component.translatable( "commands." + cmd + "." + event +
                ".success", args ), true );
    }
    
    /** Provides feedback on command failure. */
    public static void sendFailure( CommandSourceStack source, String cmd, String event, Object... args ) {
        source.sendFailure( Component.translatable( "commands." + cmd + "." + event +
                ".failure", args ) );
    }
    
    /** @return The enum converted to the standard string used by commands. */
    public static String toString( Enum<?> e ) { return e.name().toLowerCase( Locale.ROOT ); }
    
    
    // ---- Command Nodes ---- //
    
    /** A command 'literal' representing the given enum value. This is an exact, case-sensitive keyword. */
    public static LiteralArgumentBuilder<CommandSourceStack> literal( Enum<?> arg ) {
        return literal( toString( arg ) );
    }
    
    /** A command 'literal'. This is an exact, case-sensitive keyword. */
    public static LiteralArgumentBuilder<CommandSourceStack> literal( String arg ) { return Commands.literal( arg ); }
    
    /** A command 'argument'. This has a particular formula for input defined by the argument type. */
    public static <T> RequiredArgumentBuilder<CommandSourceStack, T> argument( String arg, ArgumentType<T> t ) {
        return Commands.argument( arg, t );
    }
    
    /** A command 'argument' that accepts a single-entity selector. */
    public static RequiredArgumentBuilder<CommandSourceStack, EntitySelector> argumentTarget( String arg ) {
        return argument( arg, EntityArgument.entity() );
    }
    
    /** A command 'argument' that accepts a multiple-entity selector. */
    public static RequiredArgumentBuilder<CommandSourceStack, EntitySelector> argumentTargets( String arg ) {
        return argument( arg, EntityArgument.entities() );
    }
    
    /** A command 'argument' that accepts a single-player entity selector. */
    public static RequiredArgumentBuilder<CommandSourceStack, EntitySelector> argumentPlayer( String arg ) {
        return argument( arg, EntityArgument.player() );
    }
    
    /** A command 'argument' that accepts a multiple-player entity selector. */
    public static RequiredArgumentBuilder<CommandSourceStack, EntitySelector> argumentPlayers( String arg ) {
        return argument( arg, EntityArgument.players() );
    }
    
    
    // ---- Argument Parsers ---- //
    
    /** @return A single entity target (the player sending the command). */
    public static Entity target( CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
        return context.getSource().getEntityOrException();
    }
    
    /** @return A single entity target determined by the entity selector argument. */
    public static Entity target( CommandContext<CommandSourceStack> context, String arg ) throws CommandSyntaxException {
        return EntityArgument.getEntity( context, arg );
    }
    
    /** @return A collection of entity targets (only containing the player sending the command). */
    public static Collection<? extends Entity> targets( CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
        return ImmutableList.of( target( context ) );
    }
    
    /** @return A collection of entity targets determined by the entity selector argument. */
    public static Collection<? extends Entity> targets( CommandContext<CommandSourceStack> context, String arg ) throws CommandSyntaxException {
        return EntityArgument.getEntities( context, arg );
    }
    
    /** @return A single entity target (the player sending the command). */
    public static ServerPlayer player( CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
        return context.getSource().getPlayerOrException();
    }
    
    /** @return A single entity target determined by the entity selector argument. */
    public static ServerPlayer player( CommandContext<CommandSourceStack> context, String arg ) throws CommandSyntaxException {
        return EntityArgument.getPlayer( context, arg );
    }
    
    /** @return A collection of entity targets (only containing the player sending the command). */
    public static Collection<ServerPlayer> players( CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
        return ImmutableList.of( player( context ) );
    }
    
    /** @return A collection of entity targets determined by the entity selector argument. */
    public static Collection<ServerPlayer> players( CommandContext<CommandSourceStack> context, String arg ) throws CommandSyntaxException {
        return EntityArgument.getPlayers( context, arg );
    }
    
    
    // Utility class
    private CrustCmdHelper() {}
}