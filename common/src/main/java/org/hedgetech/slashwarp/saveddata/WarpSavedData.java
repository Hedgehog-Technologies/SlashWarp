package org.hedgetech.slashwarp.saveddata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.hedgetech.slashwarp.data.LocationData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.hedgetech.slashwarp.Constants.MOD_ID;

/**
 * Class to keep Warp Points persistent
 */
public class WarpSavedData extends SavedData {
    public static final Identifier WARP_SAVED_DATA_ID = Identifier.fromNamespaceAndPath(MOD_ID, "warp_saved_data");
    /**
     * Codec to be used to define the name-location data mapping
     */
    public static final Codec<Map<String, LocationData>> WARP_CODEC = Codec.unboundedMap(Codec.STRING, LocationData.CODEC);
    /**
     * Codec defining the WarpSavedData class
     */
    public static final Codec<WarpSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        WARP_CODEC.fieldOf("warps").forGetter(WarpSavedData::getWarps)
    ).apply(instance, WarpSavedData::new));

    private final HashMap<String, LocationData> warps;

    /**
     * Default constructor - Creates an empty name-location data mapping
     */
    public WarpSavedData() { warps = new HashMap<>(); }

    /**
     * Creates a name-location data mapping with the passed in data
     * @param warps Existing name-location data mapping to build stored Warp Points from
     */
    public WarpSavedData(Map<String, LocationData> warps) {
        this.warps = new HashMap<>();
        this.warps.putAll(warps);
    }

    /**
     * Returns the name-location data mapping
     * @return A Map of names to warp point location data
     */
    public Map<String, LocationData> getWarps() { return warps; }

    private static final SavedDataType<WarpSavedData> TYPE = new SavedDataType<>(WARP_SAVED_DATA_ID, WarpSavedData::new, CODEC, DataFixTypes.LEVEL);

    /**
     * Gets the persistent data store for the passed in server
     * @param server Server to get save data from
     * @return Save data containing any existing Warp Points
     */
    public static WarpSavedData ofServer(MinecraftServer server) {
        try {
            var state = Objects.requireNonNull(server.overworld())
                    .getDataStorage()
                    .computeIfAbsent(TYPE);

            state.setDirty();
            return state;
        } catch (Exception e) {
            var dataStore = Objects.requireNonNull(server.overworld()).getDataStorage();
            dataStore.set(TYPE, new WarpSavedData());

            var state = dataStore.computeIfAbsent(TYPE);
            state.setDirty();

            return state;
        }
    }
}
