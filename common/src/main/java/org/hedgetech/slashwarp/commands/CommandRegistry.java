package org.hedgetech.slashwarp.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

/**
 * Command Registry for centralizing command registration
 */
public final class CommandRegistry {
    /**
     * Register commands for the SlashWarp mod
     * @param dispatcher Dispatcher for registering commands
     */
    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        WarpCommand.registerCommands(dispatcher);
    }

    private CommandRegistry() { }
}
