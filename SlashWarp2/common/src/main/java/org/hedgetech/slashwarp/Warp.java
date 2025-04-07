package org.hedgetech.slashwarp;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Relative;
import org.hedgetech.slashwarp.data.LocationData;
import org.hedgetech.slashwarp.saveddata.WarpSavedData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Warp {
    private static final HashSet<String> reservedNames = new HashSet<>() {
        {
            add("add");
            add("back");
            add("del");
            add("list");
        }
    };
    private static final HashMap<UUID, LocationData> previousLocations = new HashMap<>();

    public static int addWarp(CommandSourceStack source, String name) {
        var player = source.getPlayer();

        if (player != null) {
            var server = source.getServer();
            var state = WarpSavedData.ofServer(server);
            var warps = state.getWarps();

            if (warps.containsKey(name)) {
                source.sendSuccess(() -> Component.literal("A warp location with that name already exists."), false);
            } else if (reservedNames.contains(name)) {
                source.sendSuccess(() -> Component.literal("Unable to save warp to a reserved name."), false);
            } else {
                var loc = new LocationData() {
                    {
                        world = player.level().dimension();
                        position = player.position();
                        yaw = player.getYRot();
                        pitch = player.getXRot();
                    }
                };

                warps.put(name, loc);
                source.sendSuccess(() -> Component.literal("Successfully added warp location: " + name), false);
            }
        }

        return 1;
    }

    public static int delWarp(CommandSourceStack source, String name) {
        var player = source.getPlayer();

        if (player != null) {
            var server = source.getServer();
            var state = WarpSavedData.ofServer(server);
            var warps = state.getWarps();

            if (warps.containsKey(name)) {
                warps.remove(name);
                source.sendSuccess(() -> Component.literal("Successfully removed warp locations: " + name), false);
            } else {
                source.sendSuccess(() -> Component.literal("A warp location with that names does not exist."), false);
            }
        }

        return 1;
    }

    public static int listWarps(CommandSourceStack source) {
        var player = source.getPlayer();

        if (player != null) {
            var server = source.getServer();
            var state = WarpSavedData.ofServer(server);
            var warpList = new StringBuilder();

            state.getWarps().forEach((name, loc) -> {
                var str = "\n" + name + ": " + loc.toString();
                warpList.append(str);
            });

            source.sendSuccess(() -> Component.literal(warpList.toString()), false);
        }

        return 1;
    }

    public static int warpTo(CommandSourceStack source, String name) {
        var player = source.getPlayer();

        if (player != null) {
            var server = source.getServer();
            var state = WarpSavedData.ofServer(server);
            var warps = state.getWarps();
            var previousLocation = getPlayerPreviousLocation(player.getUUID());

            if (warps.containsKey(name) || (name.equals("back") && previousLocation != null)) {
                LocationData loc;
                Set<Relative> relatives = Set.of();

                if (name.equals("back")) {
                    loc = previousLocation;
                } else {
                    loc = warps.get(name);
                }

                var world = server.getLevel(loc.world);

                setPlayerPreviousLocation(player.getUUID(), new LocationData() {
                    {
                        world = player.level().dimension();
                        position = player.position();
                        yaw = player.getYRot();
                        pitch = player.getXRot();
                    }
                });

                assert world != null;
                player.teleportTo(world, loc.position.x, loc.position.y, loc.position.z, relatives, loc.yaw, loc.pitch, false);

                if (name.equals("back")) {
                    source.sendSuccess(() -> Component.literal("Successfully warped back to previous location."), false);
                } else {
                    source.sendSuccess(() -> Component.literal("Successfully warped to: " + name), false);
                }
            } else {
                source.sendSuccess(() -> Component.literal("This warp location doesn't appear to exist."), false);
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

    public static void clearPlayerPreviousLocation(UUID playerUuid) { previousLocations.remove(playerUuid); }
}
