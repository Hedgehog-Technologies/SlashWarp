package org.hedgetech.slashwarp;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.hedgetech.slashwarp.commands.CommandRegistry;

/**
 * SlashWarp's NeoForge Entry Point
 */
@Mod(Constants.MOD_ID)
public class NeoForgeSlashWarp {
    /**
     * SlashWarp constructor - entry point for NeoForge Mod Loader
     * @param eventBus - NeoForge EventBus
     */
    public NeoForgeSlashWarp(IEventBus eventBus) {

        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.

        // Use NeoForge to bootstrap the Common mod.
//        Constants.LOG.info("Hello NeoForge world!");
        CommonClass.init();

        NeoForge.EVENT_BUS.addListener(NeoForgeSlashWarp::onCommandRegister);
        NeoForge.EVENT_BUS.addListener(NeoForgeSlashWarp::onPlayerLogout);
    }

    private static void onCommandRegister(RegisterCommandsEvent event) {
        CommandRegistry.registerCommands(event.getDispatcher());
    }

    private static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        Warp.clearPlayerPreviousLocation(event.getEntity().getUUID());
    }
}