package org.hedgetech.slashwarp.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.hedgetech.slashwarp.Warp;
import org.hedgetech.slashwarp.providers.WarpLocationSuggestionProvider;

/**
 * Warp Command Registration
 */
public final class WarpCommand {
    private static final WarpLocationSuggestionProvider WARP_LOCATIONS = new WarpLocationSuggestionProvider();

    /**
     * Registers the warp command set with the game
     * @param dispatcher Command Dispatcher used to register the Warp command
     */
    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("warp")
                // /warp add <name>
                .then(Commands.literal("add")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(context ->
                                        Warp.addWarp(context.getSource(), StringArgumentType.getString(context, "name"))
                                )
                        )
                )
                // /warp back
                .then(Commands.literal("back")
                        .executes(context ->
                                Warp.warpTo(context.getSource(), "back")
                        )
                )
                // /warp del <name>
                .then(Commands.literal("del")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .suggests(WARP_LOCATIONS)
                                .executes(context ->
                                        Warp.delWarp(context.getSource(), StringArgumentType.getString(context, "name"))
                                )
                        )
                )
                // /warp list
                .then(Commands.literal("list")
                        .executes(context ->
                                Warp.listWarps(context.getSource())
                        )
                )
                // /warp <name>
                .then(Commands.argument("loc", StringArgumentType.word())
                        .suggests(WARP_LOCATIONS)
                        .executes(context ->
                                Warp.warpTo(context.getSource(), StringArgumentType.getString(context, "loc"))
                        )
                )
        );
    }

    private WarpCommand() {
        throw new UnsupportedOperationException("There is no need to instantiate this class.");
    }
}
