package fathertoast.crust.common.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.common.command.impl.CrustCleanCommand;
import fathertoast.crust.common.command.impl.CrustModeCommand;
import fathertoast.crust.common.command.impl.CrustPortalCommand;
import fathertoast.crust.common.command.impl.CrustRecoverCommand;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;
import java.util.Locale;

@Mod.EventBusSubscriber( modid = ICrustApi.MOD_ID )
public class CommandUtil {
    
    /** Called each time commands are loaded. */
    @SubscribeEvent
    static void registerCommands( RegisterCommandsEvent event ) {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
        CrustCleanCommand.register( dispatcher );
        CrustModeCommand.register( dispatcher );
        CrustPortalCommand.register( dispatcher );
        CrustRecoverCommand.register( dispatcher );
    }
    
    
    // ---- Command Feedback ---- //
    
    /** Provides feedback on successful command results. */
    public static void sendSuccess( CommandSource source, String event, Object... args ) {
        source.sendSuccess( new TranslationTextComponent( "commands." + ICrustApi.MOD_ID + event +
                ".success", args ), true );
    }
    
    /** Provides feedback on command failure. */
    public static void sendFailure( CommandSource source, String event, Object... args ) {
        source.sendFailure( new TranslationTextComponent( "commands." + ICrustApi.MOD_ID + event +
                ".failure", args ) );
    }
    
    /** @return The enum converted to the standard string used by commands. */
    public static String toString( Enum<?> e ) { return e.name().toLowerCase( Locale.ROOT ); }
    
    
    // ---- Command Nodes ---- //
    
    /** A command 'literal' representing the given enum value. This is an exact, case-sensitive keyword. */
    public static LiteralArgumentBuilder<CommandSource> literal( Enum<?> arg ) {
        return literal( toString( arg ) );
    }
    
    /** A command 'literal'. This is an exact, case-sensitive keyword. */
    public static LiteralArgumentBuilder<CommandSource> literal( String arg ) { return Commands.literal( arg ); }
    
    /** A command 'argument'. This has a particular formula for input defined by the argument type. */
    public static <T> RequiredArgumentBuilder<CommandSource, T> argument( String arg, ArgumentType<T> t ) {
        return Commands.argument( arg, t );
    }
    
    /** A command 'argument' that accepts a single-entity selector. */
    public static RequiredArgumentBuilder<CommandSource, EntitySelector> argumentTarget( String arg ) {
        return argument( arg, EntityArgument.entity() );
    }
    
    /** A command 'argument' that accepts a multiple-entity selector. */
    public static RequiredArgumentBuilder<CommandSource, EntitySelector> argumentTargets( String arg ) {
        return argument( arg, EntityArgument.entities() );
    }
    
    /** A command 'argument' that accepts a single-player entity selector. */
    public static RequiredArgumentBuilder<CommandSource, EntitySelector> argumentPlayer( String arg ) {
        return argument( arg, EntityArgument.player() );
    }
    
    /** A command 'argument' that accepts a multiple-player entity selector. */
    public static RequiredArgumentBuilder<CommandSource, EntitySelector> argumentPlayers( String arg ) {
        return argument( arg, EntityArgument.players() );
    }
    
    
    // ---- Requirements ---- //
    
    public static final byte PERMISSION_NONE = 0;
    public static final byte PERMISSION_TRUSTED = 1;
    public static final byte PERMISSION_CHEAT = 2;
    public static final byte PERMISSION_MODERATE = 3;
    public static final byte PERMISSION_SERVER_OP = 4;
    
    /** @return True if the source is allowed to cheat (op level 2+). */
    public static boolean canCheat( CommandSource source ) { return source.hasPermission( PERMISSION_CHEAT ); }
    
    ///** @return True if the source is a moderator (op level 3+). */
    //public static boolean isModerator( CommandSource source ) { return source.hasPermission( PERMISSION_MODERATOR ); }
    
    ///** @return True if the source is a server operator (op level 4). */
    //public static boolean isServerOp( CommandSource source ) { return source.hasPermission( PERMISSION_SERVER ); }
    
    
    // ---- Argument Parsers ---- //
    
    /** @return A single entity target (the player sending the command). */
    public static Entity target( CommandContext<CommandSource> context ) throws CommandSyntaxException {
        return context.getSource().getEntityOrException();
    }
    
    /** @return A single entity target determined by the entity selector argument. */
    public static Entity target( CommandContext<CommandSource> context, String arg ) throws CommandSyntaxException {
        return EntityArgument.getEntity( context, arg );
    }
    
    /** @return A collection of entity targets (only containing the player sending the command). */
    public static Collection<? extends Entity> targets( CommandContext<CommandSource> context ) throws CommandSyntaxException {
        return ImmutableList.of( target( context ) );
    }
    
    /** @return A collection of entity targets determined by the entity selector argument. */
    public static Collection<? extends Entity> targets( CommandContext<CommandSource> context, String arg ) throws CommandSyntaxException {
        return EntityArgument.getEntities( context, arg );
    }
    
    /** @return A single entity target (the player sending the command). */
    public static ServerPlayerEntity player( CommandContext<CommandSource> context ) throws CommandSyntaxException {
        return context.getSource().getPlayerOrException();
    }
    
    /** @return A single entity target determined by the entity selector argument. */
    public static ServerPlayerEntity player( CommandContext<CommandSource> context, String arg ) throws CommandSyntaxException {
        return EntityArgument.getPlayer( context, arg );
    }
    
    /** @return A collection of entity targets (only containing the player sending the command). */
    public static Collection<ServerPlayerEntity> players( CommandContext<CommandSource> context ) throws CommandSyntaxException {
        return ImmutableList.of( player( context ) );
    }
    
    /** @return A collection of entity targets determined by the entity selector argument. */
    public static Collection<ServerPlayerEntity> players( CommandContext<CommandSource> context, String arg ) throws CommandSyntaxException {
        return EntityArgument.getPlayers( context, arg );
    }
}