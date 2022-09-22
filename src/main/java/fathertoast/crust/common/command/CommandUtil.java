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

public class CommandUtil {
    
    public static void sendSuccess( CommandSource source, String event, Object... args ) {
        source.sendSuccess( new TranslationTextComponent( "commands." + Crust.MOD_ID + event +
                ".success", args ), true );
    }
    
    public static LiteralArgumentBuilder<CommandSource> literal( String arg ) {
        return Commands.literal( arg );
    }
    
    public static <T> RequiredArgumentBuilder<CommandSource, T> argument( String arg, ArgumentType<T> t ) {
        return Commands.argument( arg, t );
    }
    
    public static boolean isOP( CommandSource source ) { return source.hasPermission( 2 ); }
    
    public static Collection<? extends Entity> targetSelf( CommandContext<CommandSource> context ) throws CommandSyntaxException {
        return ImmutableList.of( context.getSource().getEntityOrException() );
    }
    
    public static Collection<? extends Entity> targets( CommandContext<CommandSource> context, String arg ) throws CommandSyntaxException {
        return EntityArgument.getEntities( context, arg );
    }
}