package fathertoast.crust.common.command.impl;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fathertoast.crust.api.portal.PortalBuilder;
import fathertoast.crust.common.portal.CrustPortals;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public class PortalTypeArgument implements ArgumentType<PortalBuilder> {
    
    public static final SimpleCommandExceptionType INVALID_PORTAL_TYPE = new SimpleCommandExceptionType( Component.translatable( "crust.argument.portal_type.notfound" ) );
    
    
    public static PortalTypeArgument portalType() {
        return new PortalTypeArgument();
    }
    
    public static PortalBuilder getPortalType( CommandContext<CommandSourceStack> context, String name ) {
        return context.getArgument( name, PortalBuilder.class );
    }
    
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions( CommandContext<S> context, SuggestionsBuilder suggestions ) {
        StringReader reader = new StringReader( suggestions.getInput() );
        reader.setCursor( suggestions.getStart() );
        
        for( PortalBuilder builder : CrustPortals.PORTAL_REGISTRY.get().getValues() ) {
            //noinspection ConstantConditions
            suggestions.suggest( CrustPortals.PORTAL_REGISTRY.get().getKey( builder ).toString() );
        }
        return suggestions.buildFuture();
    }
    
    @Override
    public PortalBuilder parse( StringReader reader ) throws CommandSyntaxException {
        ResourceLocation resourceLocation = ResourceLocation.read( reader );
        PortalBuilder portalBuilder = CrustPortals.PORTAL_REGISTRY.get().getValue( resourceLocation );
        
        if( portalBuilder == null )
            throw INVALID_PORTAL_TYPE.create();
        
        return portalBuilder;
    }
}