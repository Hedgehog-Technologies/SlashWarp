package org.hedgetech.slashwarp.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

public class CommandRegistry {
    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        WarpCommand.registerCommands(dispatcher);
    }
}
