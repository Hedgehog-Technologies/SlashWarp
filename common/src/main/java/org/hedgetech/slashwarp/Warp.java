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

/**
 * Class defining Warp Point system
 */
public class Warp {
    private static final HashSet<String> RESERVED_NAMES = new HashSet<>() {
        {
            add("add");
            add("back");
            add("del");
            add("list");
        }
    };

    private static final HashMap<UUID, LocationData> PREVIOUS_LOCATIONS = new HashMap<>();

    /**
     * Default Constructor - does nothing special
     */
    public Warp() { }

    /**
     * Add a Warp Point with the given name at the player's current position
     * @param source Source of the command
     * @param name Name to save the Warp Point as
     * @return 1
     */
    public static int addWarp(CommandSourceStack source, String name) {
        var player = source.getPlayer();

        if (player != null) {
            var server = source.getServer();
            var state = WarpSavedData.ofServer(server);
            var warps = state.getWarps();

            if (warps.containsKey(name)) {
                source.sendSuccess(() -> Component.literal("A warp location with that name already exists."), false);
            } else if (RESERVED_NAMES.contains(name)) {
                source.sendSuccess(() -> Component.literal("Unable to save warp to a reserved name."), false);
            } else {
                var loc = new LocationData(player.level().dimension(), player.position(), player.getYRot(), player.getXRot());

                warps.put(name, loc);
                source.sendSuccess(() -> Component.literal("Successfully added warp location: " + name), false);
            }
        }

        return 1;
    }

    /**
     * Deletes the Warp Point with the specified name
     * @param source Source of the command
     * @param name Name of the Warp Point to delete
     * @return 1
     */
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

    /**
     * List available Warp Points
     * @param source Source of the command
     * @return 1
     */
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

    /**
     * Warp to the Warp Point with the specified name
     * @param source Source of the command
     * @param name Name of the Warp Point
     * @return 1
     */
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

                var world = server.getLevel(loc.getWorld());
                var position = loc.getPosition();

                // If the player has warped less than 2 blocks radius, lets assume they didn't mean to and keep the previous location the same
                if (!player.position().closerThan(position, 2.0)) {
                    setPlayerPreviousLocation(player.getUUID(), new LocationData(player.level().dimension(), player.position(), player.getYRot(), player.getXRot()));
                }

                if (world == null) {
                    source.sendSuccess(() -> Component.literal("Unable to warp from no where."), false);
                    return 1;
                }

                var success = false;

                // Let's assume that the player wants to bring their vehicle / mount with them as they warp
                if (player.isPassenger()) {
                    var vehicle = player.getVehicle();

                    if (vehicle != null) {
                        // Warping the vehicle should automatically bring the player along
                        success = vehicle.teleportTo(world, position.x, position.y + 0.5, position.z, relatives, loc.getYaw(), loc.getPitch(), false);
                        success = success && vehicle.hasPassenger(player);
                    }
                } else {
                    success = player.teleportTo(world, position.x, position.y, position.z, relatives, loc.getYaw(), loc.getPitch(), false);
                }

                var result = success ? "Successfully warped" : "Failed to warp";

                if (name.equals("back")) {
                    source.sendSuccess(() -> Component.literal(result + " back to previous location."), false);
                } else {
                    source.sendSuccess(() -> Component.literal(result + " to: " + name), false);
                }
            } else {
                source.sendSuccess(() -> Component.literal("This warp location doesn't appear to exist."), false);
            }
        }

        return 1;
    }

    /**
     * Get the previous location of the specified player
     * @param playerUuid UUID of the player to get the previous location of
     * @return LocationData | Null of the previous player location
     */
    private static LocationData getPlayerPreviousLocation(UUID playerUuid) {
        return PREVIOUS_LOCATIONS.getOrDefault(playerUuid, null);
    }

    private static void setPlayerPreviousLocation(UUID playerUuid, LocationData location) {
        PREVIOUS_LOCATIONS.put(playerUuid, location);
    }

    /**
     * Remove the saved previous location for the specified player
     * @param playerUuid UUID of the player to clear the previous location for
     */
    public static void clearPlayerPreviousLocation(UUID playerUuid) { PREVIOUS_LOCATIONS.remove(playerUuid); }
}
