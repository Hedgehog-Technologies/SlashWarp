package org.hedgetech.slashwarp.providers;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import org.hedgetech.slashwarp.saveddata.WarpSavedData;

import java.util.concurrent.CompletableFuture;

/**
 * Class to build and define the suggestions for the warp command set
 */
public class WarpLocationSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        var server = context.getSource().getServer();
        var state = WarpSavedData.ofServer(server);
        var warpNames = state.getWarps().keySet();

        if (warpNames.isEmpty()) {
            return Suggestions.empty();
        }

        state.getWarps().keySet().forEach(builder::suggest);

        return builder.buildFuture();
    }

    /**
     * Default constructor - does nothing special
     */
    public WarpLocationSuggestionProvider() { }
}
