package org.hedgetech.slashwarp;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import org.hedgetech.slashwarp.commands.CommandRegistry;

/**
 * SlashWarp's Forge Entry Point
 */
@Mod(Constants.MOD_ID)
public class ForgeSlashWarp {
    /**
     * SlashWarp constructor - entry point for Forge Mod Loader
     */
    public ForgeSlashWarp() {
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        // Use Forge to bootstrap the Common mod.
//        Constants.LOG.info("Hello Forge world!");
        CommonClass.init();

        RegisterCommandsEvent.BUS.addListener(ForgeSlashWarp::registerCommandsEventHandler);
        PlayerEvent.PlayerLoggedOutEvent.BUS.addListener(ForgeSlashWarp::registerPlayerLoggedOutEventHandler);
        PlayerEvent.PlayerRespawnEvent.BUS.addListener(ForgeSlashWarp::registerPlayerRespawnEventHandler);
    }

    private static void registerCommandsEventHandler(RegisterCommandsEvent event) {
        CommandRegistry.registerCommands(event.getDispatcher());
    }

    private static void registerPlayerLoggedOutEventHandler(PlayerEvent.PlayerLoggedOutEvent event) {
        Warp.clearPlayerPreviousLocation(event.getEntity().getUUID());
    }

    private static void registerPlayerRespawnEventHandler(PlayerEvent.PlayerRespawnEvent event) {
        if (event.isEndConquered()) return;
        Warp.handlePlayerRespawn(event.getEntity());
    }
}