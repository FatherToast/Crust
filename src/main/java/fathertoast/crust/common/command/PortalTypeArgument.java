package fathertoast.crust.common.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fathertoast.crust.api.lib.CrustObjects;
import fathertoast.crust.api.portal.PortalBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public class PortalTypeArgument implements ArgumentType<PortalBuilder> {
    
    public static final SimpleCommandExceptionType INVALID_PORTAL_TYPE = new SimpleCommandExceptionType( Component.translatable( "commands.crustportal.portaltype.failure" ) );
    
    
    public static PortalTypeArgument portalType() {
        return new PortalTypeArgument();
    }
    
    public static PortalBuilder getPortalType( CommandContext<CommandSourceStack> context, String name ) {
        return context.getArgument( name, PortalBuilder.class );
    }
    
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions( CommandContext<S> context, SuggestionsBuilder suggestions ) {
        for( PortalBuilder builder : CrustObjects.PORTAL_REGISTRY.get().getValues() ) {
            //noinspection ConstantConditions
            suggestions.suggest( CrustObjects.PORTAL_REGISTRY.get().getKey( builder ).toString() );
        }
        return suggestions.buildFuture();
    }
    
    @Override
    public PortalBuilder parse( StringReader reader ) throws CommandSyntaxException {
        final ResourceLocation id = ResourceLocation.read( reader );
        final PortalBuilder portalBuilder = CrustObjects.PORTAL_REGISTRY.get().getValue( id );
        
        if( portalBuilder == null )
            throw INVALID_PORTAL_TYPE.create();
        
        return portalBuilder;
    }
}