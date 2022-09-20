package fathertoast.crust.common.command.impl;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import fathertoast.crust.common.core.Crust;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;

public class CrustRecoverCommand {
    
    public static void register( CommandDispatcher<CommandSource> dispatcher ) {
        dispatcher.register( Commands.literal( Crust.MOD_ID + "recover" )
                .requires( ( source ) -> source.hasPermission( 2 ) )
                .executes( ( source ) -> recover( source.getSource(), ImmutableList.of( source.getSource().getEntityOrException() ) ) )
                .then(
                        Commands.argument( "targets", EntityArgument.entities() )
                                .executes( ( source ) -> recover( source.getSource(), EntityArgument.getEntities( source, "targets" ) ) )
                )
        );
    }
    
    private static int recover( CommandSource source, Collection<? extends Entity> targets ) {
        for( Entity target : targets ) {
            target.kill();
        }
        
        if( targets.size() == 1 ) {
            source.sendSuccess( new TranslationTextComponent( "commands.kill.success.single", targets.iterator().next().getDisplayName() ), true );
        }
        else {
            source.sendSuccess( new TranslationTextComponent( "commands.kill.success.multiple", targets.size() ), true );
        }
        
        return targets.size();
    }
}