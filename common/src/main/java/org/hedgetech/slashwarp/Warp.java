package org.hedgetech.slashwarp;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.hedgetech.slashwarp.data.LocationData;
import org.hedgetech.slashwarp.saveddata.WarpSavedData;

import java.util.*;

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
            add("top");
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

    private static BlockPos findSafeTop(Level world, BlockPos playerPos, int maxRadius, int verticalScan) {
        var start = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, playerPos);

        // Try the starting column first
        var safe = scanDownwards(world, start, verticalScan);
        if (safe != null) return safe;

        // Spiral outwards
        for (int r = 1; r <= maxRadius; r++) {
            for (BlockPos candidate : spiralAround(playerPos, r)) {
                var top = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, candidate);
                var found = scanDownwards(world, top, verticalScan);
                if (found != null) return found;
            }
        }

        return null; // no safe spot found
    }

    private static List<BlockPos> spiralAround(BlockPos center, int radius) {
        var positions = new ArrayList<BlockPos>();
        var cx = center.getX();
        var cz = center.getZ();

        for (int r = 1; r <= radius; r++) {
            for (int dx = -r; dx <= r; dx++) {
                positions.add(new BlockPos(cx + dx, center.getY(), cz - r));
                positions.add(new BlockPos(cx + dx, center.getY(), cz + r));
            }
            for (int dz = -r + 1; dz <= r - 1; dz++) {
                positions.add(new BlockPos(cx - r, center.getY(), cz + dz));
                positions.add(new BlockPos(cx + r, center.getY(), cz + dz));
            }
        }

        return positions;
    }

    private static BlockPos scanDownwards(Level world, BlockPos top, int depth) {
        var cursor = top.mutable();

        for (int dy = 0; dy < depth; dy++) {
            if (isSafeSpot(world, cursor)) {
                return cursor.immutable();
            }
            cursor.move(Direction.DOWN);
        }

        return null;
    }

    private static boolean isSafeSpot(Level world, BlockPos pos) {
        var feet = pos.above();
        var head = feet.above();

        var floor = world.getBlockState(pos);
        var feetState = world.getBlockState(feet);
        var headState = world.getBlockState(head);

        // Floor must be solid and safe
        if (floor.getCollisionShape(world, pos, CollisionContext.empty()).isEmpty()) return false;

        if (floor.is(Blocks.CACTUS)
                || floor.is(Blocks.MAGMA_BLOCK)
                || floor.is(Blocks.CAMPFIRE)
                || floor.is(Blocks.SOUL_CAMPFIRE)
                || floor.is(Blocks.FIRE)
                || floor.is(Blocks.LAVA)
                || floor.is(Blocks.POWDER_SNOW)) return false;

        // Feet can be air or water
        if (feetState.getFluidState().is(FluidTags.LAVA)) return false;
        if (feetState.is(Blocks.POWDER_SNOW)) return false;

        var feetOk = feetState.isAir() || feetState.getFluidState().is(FluidTags.WATER);
        if (!feetOk) return false;

        // Head must be air
        return headState.isAir() && headState.getCollisionShape(world, head, CollisionContext.empty()).isEmpty();
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

            if (warps.containsKey(name)
                || (name.equals("back") && previousLocation != null)
                || name.equals("top")
            ) {
                LocationData loc;
                Set<Relative> relatives = Set.of();

                if (name.equals("back")) {
                    loc = previousLocation;
                } else if (name.equals("top")) {
                    var safePos = findSafeTop(player.level(), player.blockPosition(), 8, 8);
                    if (safePos == null) {
                        source.sendSuccess(() -> Component.literal("Failed to find a safe position at the top."), false);
                        return 1;
                    }

                    loc = new LocationData(player.level().dimension(), safePos.above().getCenter(), player.getYRot(), player.getXRot());
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

                var pets = world.getEntities(EntityTypeTest.forClass(TamableAnimal.class), animal -> animal.isTame() && animal.isOwnedBy(player) && !animal.isOrderedToSit());
                var success = false;

                // Let's assume that the player wants to bring their vehicle / mount with them as they warp
                if (player.isPassenger()) {
                    var vehicle = player.getVehicle();

                    if (vehicle != null) {
                        // Warping the vehicle should automatically bring the player (and any other passengers) along
                        success = vehicle.teleportTo(world, position.x, position.y + 0.5, position.z, relatives, loc.getYaw(), loc.getPitch(), false);
                        success = success && vehicle.hasPassenger(player);
                    }
                } else {
                    success = player.teleportTo(world, position.x, position.y, position.z, relatives, loc.getYaw(), loc.getPitch(), false);
                }

                // Bring pets along that aren't currently told to sit and stay
                // May fail if a safe place wasn't found to teleport to
                if (!pets.isEmpty()) {
                    pets.forEach(TamableAnimal::tryToTeleportToOwner);
                }

                var result = success ? "Successfully warped" : "Failed to warp";

                if (name.equals("back")) {
                    source.sendSuccess(() -> Component.literal(result + " back to previous location."), false);
                } else if (name.equals("top")) {
                    source.sendSuccess(() -> Component.literal(result + " to the top."), false);
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

    public static void handlePlayerRespawn(Player player) {
        var playerDeathLocation = player.getLastDeathLocation();
        if (playerDeathLocation.isEmpty()) return;

        var deathLocation = new LocationData(playerDeathLocation.get());
        setPlayerPreviousLocation(player.getUUID(), deathLocation);
    }

    /**
     * Remove the saved previous location for the specified player
     * @param playerUuid UUID of the player to clear the previous location for
     */
    public static void clearPlayerPreviousLocation(UUID playerUuid) { PREVIOUS_LOCATIONS.remove(playerUuid); }
}
