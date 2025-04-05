package org.HedgeTech.slashwarp;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

public class CommandRegistry {
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        Warp.register(dispatcher);
    }
}
