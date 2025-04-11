package org.hedgetech.slashwarp.saveddata;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import org.hedgetech.slashwarp.Constants;
import org.hedgetech.slashwarp.data.LocationData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to keep Warp Points persistent
 */
public class WarpSavedData extends SavedData {
    private final HashMap<String, LocationData> warps;

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
        var warpsTag = new CompoundTag();

        warps.forEach((name, loc) -> {
            var warpTag = new CompoundTag();

            var worldIdStr = loc.getWorld().location();
            warpTag.putString("world", worldIdStr.toString());

            warpTag.putDouble("x", loc.getPosition().x);
            warpTag.putDouble("y", loc.getPosition().y);
            warpTag.putDouble("z", loc.getPosition().z);

            warpTag.putFloat("yaw", loc.getYaw());
            warpTag.putFloat("pitch", loc.getPitch());

            warpsTag.put(name, warpTag);
        });

        compoundTag.put("warps", warpsTag);

        return compoundTag;
    }

    /**
     * Default constructor - Creates an empty name-location data mapping
     */
    public WarpSavedData() { warps = new HashMap<>(); }

    /**
     * Creates a name-location data mapping with the passed in data
     * @param tag Data storage
     * @param provider Unused provider
     * @return A new Warp Saved Data name-location data mapping with any existing warp points
     */
    public static WarpSavedData createFromTag(CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        var savedData = new WarpSavedData();
        var warpsTag = tag.getCompound("warps");

        warpsTag.getAllKeys().forEach(key -> {
            var warpTag = warpsTag.getCompound(key);

            var worldIdStr = warpTag.getString("world");
            var x = warpTag.getDouble("x");
            var y = warpTag.getDouble("y");
            var z = warpTag.getDouble("z");
            var yaw = warpTag.getFloat("yaw");
            var pitch = warpTag.getFloat("pitch");

            savedData.warps.put(key, new LocationData(worldIdStr, new Vec3(x, y, z), yaw, pitch));
        });

        return savedData;
    }

    /**
     * Returns the name-location data mapping
     * @return A MAp of names to warp point location data
     */
    public Map<String, LocationData> getWarps() { return warps; }

    private static final Factory<WarpSavedData> WARP_SAVED_DATA_FACTORY = new Factory<>(
            WarpSavedData::new,
            WarpSavedData::createFromTag,
            null
    );

    /**
     * Gets the persistent data store for the passed in server
     * @param server Server to get saved data from
     * @return Save data containing any existing Warp Points
     */
    public static WarpSavedData ofServer(MinecraftServer server) {
        var overworldDataStore = server.overworld().getDataStorage();

        try {
            var savedData = overworldDataStore.computeIfAbsent(WARP_SAVED_DATA_FACTORY, Constants.MOD_ID);
            savedData.setDirty();
            return savedData;
        } catch (Exception e) {
            overworldDataStore.set(Constants.MOD_ID, new WarpSavedData());
            var savedData = overworldDataStore.computeIfAbsent(WARP_SAVED_DATA_FACTORY, Constants.MOD_ID);
            savedData.setDirty();
            return savedData;
        }
    }
}
