package org.hedgetech.slashwarp;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.hedgetech.slashwarp.commands.CommandRegistry;

/**
 * Fabric Server Entry Point for SlashWarp Mod
 */
public class SlashWarp implements ModInitializer {
    
    @Override
    public void onInitialize() {
        
        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common mod.
        Constants.LOG.info("Hello Fabric world!");
        CommonClass.init();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                CommandRegistry.registerCommands(dispatcher)
        );

        ServerPlayConnectionEvents.DISCONNECT.register((serverPlayNetworkHandler, minecraftServer) ->
                Warp.clearPlayerPreviousLocation(serverPlayNetworkHandler.getPlayer().getUUID())
        );
    }

    /**
     * Default Constructor
     */
    public SlashWarp() { }
}
