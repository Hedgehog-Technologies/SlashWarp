package org.HedgeTech.slashwarp.providers;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import org.HedgeTech.slashwarp.states.StateSaverAndLoader;

import java.util.concurrent.CompletableFuture;

public class WarpLocationSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        var server = context.getSource().getServer();
        var state = StateSaverAndLoader.ofServer(server);
        var warpNames = state.getWarps().keySet();

        if (warpNames.isEmpty()) {
            return Suggestions.empty();
        }

        state.getWarps().keySet().forEach(builder::suggest);

        return builder.buildFuture();
    }
}
