package fathertoast.crust.common.command.impl;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fathertoast.crust.api.impl.PortalBuilderRegistry;
import fathertoast.crust.api.portal.IPortalBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.concurrent.CompletableFuture;

public class PortalTypeArgument implements ArgumentType<IPortalBuilder> {

    public static final SimpleCommandExceptionType INVALID_PORTAL_TYPE = new SimpleCommandExceptionType(new TranslationTextComponent("crust.argument.portal_type.notfound"));


    public static PortalTypeArgument portalType() {
        return new PortalTypeArgument();
    }

    public static IPortalBuilder getPortalType(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, IPortalBuilder.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder suggestions) {
        StringReader reader = new StringReader(suggestions.getInput());
        reader.setCursor(suggestions.getStart());

        for (IPortalBuilder builder : PortalBuilderRegistry.getAllBuilders()) {
            suggestions.suggest(builder.getId().toString());
        }
        return suggestions.buildFuture();
    }

    @Override
    public IPortalBuilder parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation resourceLocation = ResourceLocation.read(reader);
        IPortalBuilder portalBuilder = PortalBuilderRegistry.getBuilder(resourceLocation);

        if (portalBuilder == null)
            throw INVALID_PORTAL_TYPE.create();

        return portalBuilder;
    }
}
