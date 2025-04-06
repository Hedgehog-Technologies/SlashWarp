package org.HedgeTech.slashwarp;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.HedgeTech.slashwarp.data.LocationData;
import org.HedgeTech.slashwarp.providers.WarpLocationSuggestionProvider;
import org.HedgeTech.slashwarp.states.StateSaverAndLoader;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class Warp {
    private static final WarpLocationSuggestionProvider WARP_LOCS = new WarpLocationSuggestionProvider();
    private static final HashMap<UUID, LocationData> previousLocations = new HashMap<>();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("warp")
                .then(CommandManager.literal("add")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .executes(context ->
                                        addWarp(context.getSource(), StringArgumentType.getString(context, "name"))
                                )
                        )
                )
                .then(CommandManager.literal("back")
                        .executes(context ->
                                warpTo(context.getSource(), "back")
                        )
                )
                .then(CommandManager.literal("del")
                        .then(CommandManager.argument("name", StringArgumentType.word())
                                .suggests(WARP_LOCS)
                                .executes(context ->
                                        delWarp(context.getSource(), StringArgumentType.getString(context, "name"))
                                )
                        )
                )
                .then(CommandManager.literal("list")
                        .executes(context ->
                                listWarps(context.getSource())
                        )
                )
                .then(CommandManager.argument("loc", StringArgumentType.word())
                        .suggests(WARP_LOCS)
                        .executes(context ->
                                warpTo(context.getSource(), StringArgumentType.getString(context, "loc"))
                        )
                )
        );
    }

    private static int addWarp(ServerCommandSource source, String name) {
        var player = source.getPlayer();

        if (player != null) {
            var server = source.getServer();
            var state = StateSaverAndLoader.ofServer(server);
            var warps = state.getWarps();

            if (warps.containsKey(name)) {
                source.sendFeedback(() -> Text.literal("A warp location with that name already exists."), false);
            } else {
                var loc = new LocationData() {
                    {
                        world = player.getWorld().getRegistryKey();
                        position = player.getPos();
                        yaw = player.getYaw();
                        pitch = player.getPitch();
                    }
                };


                warps.put(name, loc);
                source.sendFeedback(() -> Text.literal("Successfully added warp location: " + name), false);
            }
        }

        return 1;
    }

    private static int delWarp(ServerCommandSource source, String name) {
        var player = source.getPlayer();

        if (player != null) {
            var server = source.getServer();
            var state = StateSaverAndLoader.ofServer(server);
            var warps = state.getWarps();

            if (warps.containsKey(name)) {
                warps.remove(name);
                source.sendFeedback(() -> Text.literal("Successfully removed warp location: " + name), false);
            } else {
                source.sendFeedback(() -> Text.literal("A warp location with that name does not exist."), false);
            }
        }

        return 1;
    }

    private static int listWarps(ServerCommandSource source) {
        var player = source.getPlayer();

        if (player != null) {
            var server = source.getServer();
            var state = StateSaverAndLoader.ofServer(server);
            var feedback = new StringBuilder();

            state.getWarps().forEach((name, loc) -> {
                var str = "\n" + name + ": " + loc.toString();
                feedback.append(str);
            });

            source.sendFeedback(() -> Text.literal(feedback.toString()), false);
        }

        return 1;
    }

    private static int warpTo(ServerCommandSource source, String name) {
        var player = source.getPlayer();

        if (player != null) {
            var server = source.getServer();
            var state = StateSaverAndLoader.ofServer(server);
            var warps = state.getWarps();
            var previousLocation = getPlayerPreviousLocation(player.getUuid());

            if (warps.containsKey(name) || (name.equals("back") && previousLocation != null)) {
                LocationData loc;
                Set<PositionFlag> flags = Set.of();

                if (name.equals("back")) {
                    loc = previousLocation;
                } else {
                    loc = warps.get(name);
                }

                var world = server.getWorld(loc.world);

                setPlayerPreviousLocation(player.getUuid(), new LocationData() {
                        {
                            position = player.getPos();
                            world = player.getWorld().getRegistryKey();
                            yaw = player.getYaw();
                            pitch = player.getPitch();
                        }
                    }
                );

                player.teleport(world, loc.position.x, loc.position.y, loc.position.z, flags, loc.yaw, loc.pitch, true);
                source.sendFeedback(() -> Text.literal("Successfully warped to: " + name), false);
            } else {
                source.sendFeedback(() -> Text.literal("This warp location doesn't appear to exist."), false);
            }
        }

        return 1;
    }

    private static LocationData getPlayerPreviousLocation(UUID playerUuid) {
        return previousLocations.getOrDefault(playerUuid, null);
    }

    private static void setPlayerPreviousLocation(UUID playerUuid, LocationData location) {
        previousLocations.put(playerUuid, location);
    }

    public static void clearPlayerPreviousLocation(UUID playerUuid) {
        previousLocations.remove(playerUuid);
    }
}
