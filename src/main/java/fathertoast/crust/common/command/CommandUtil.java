package fathertoast.crust.common.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fathertoast.crust.common.core.Crust;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;
import java.util.Locale;

public class CommandUtil {
    
    // ---- Command Feedback ---- //
    
    /** Provides feedback on successful command results. */
    public static void sendSuccess( CommandSource source, String event, Object... args ) {
        source.sendSuccess( new TranslationTextComponent( "commands." + Crust.MOD_ID + event +
                ".success", args ), true );
    }
    
    /** Provides feedback on command failure. */
    public static void sendFailure( CommandSource source, String event, Object... args ) {
        source.sendFailure( new TranslationTextComponent( "commands." + Crust.MOD_ID + event +
                ".failure", args ) );
    }
    
    
    // ---- Command Nodes ---- //
    
    /** A command 'literal' representing the given enum value. This is an exact, case-sensitive keyword. */
    public static LiteralArgumentBuilder<CommandSource> literal( Enum<?> arg ) {
        return literal( arg.name().toLowerCase( Locale.ROOT ) );
    }
    
    /** A command 'literal'. This is an exact, case-sensitive keyword. */
    public static LiteralArgumentBuilder<CommandSource> literal( String arg ) { return Commands.literal( arg ); }
    
    /** A command 'argument'. This has a particular formula for input defined by the argument type. */
    public static <T> RequiredArgumentBuilder<CommandSource, T> argument( String arg, ArgumentType<T> t ) {
        return Commands.argument( arg, t );
    }
    
    
    // ---- Requirements ---- //
    
    /** @return True if the source is allowed to cheat (op level 2+). */
    public static boolean canCheat( CommandSource source ) { return source.hasPermission( 2 ); }
    
    ///** @return True if the source is a moderator (op level 3+). */
    //public static boolean isModerator( CommandSource source ) { return source.hasPermission( 3 ); }
    
    ///** @return True if the source is a server operator (op level 4). */
    //public static boolean isServerOp( CommandSource source ) { return source.hasPermission( 4 ); }
    
    
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
}