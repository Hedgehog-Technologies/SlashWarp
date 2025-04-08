package org.hedgetech.slashwarp;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import org.hedgetech.slashwarp.commands.CommandRegistry;

/**
 * SlashWarp's Forge Entry Point
 */
@Mod(Constants.MOD_ID)
public class SlashWarp {
    /**
     * SlashWarp constructor - entry point for Forge Mod Loader
     */
    public SlashWarp() {
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        // Use Forge to bootstrap the Common mod.
        Constants.LOG.info("Hello Forge world!");
        CommonClass.init();

        MinecraftForge.EVENT_BUS.addListener(this::registerCommandsEventHandler);
        MinecraftForge.EVENT_BUS.addListener(this::registerPlayerLoggedOutEventHandler);
    }

    private void registerCommandsEventHandler(RegisterCommandsEvent event) {
        CommandRegistry.registerCommands(event.getDispatcher());
    }

    private void registerPlayerLoggedOutEventHandler(PlayerEvent.PlayerLoggedOutEvent event) {
        Warp.clearPlayerPreviousLocation(event.getEntity().getUUID());
    }
}