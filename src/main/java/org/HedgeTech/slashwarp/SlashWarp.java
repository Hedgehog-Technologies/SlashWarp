package org.HedgeTech.slashwarp;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlashWarp implements ModInitializer {
    public static final String MOD_ID = "HedgeTech.SlashWarp";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                CommandRegistry.registerCommands(dispatcher)
        );

        ServerPlayConnectionEvents.DISCONNECT.register((serverPlayNetworkHandler, minecraftServer) ->
            Warp.clearPlayerPreviousLocation(serverPlayNetworkHandler.getPlayer().getUuid())
        );
    }
}
