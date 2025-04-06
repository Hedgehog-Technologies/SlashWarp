package org.HedgeTech.slashwarp.states;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;
import org.HedgeTech.slashwarp.data.LocationData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.HedgeTech.slashwarp.SlashWarp.MOD_ID;

public class StateSaverAndLoader extends PersistentState {
    public static final Codec<Map<String, LocationData>> warpCodec = Codec.unboundedMap(Codec.STRING, LocationData.CODEC);
    public static final Codec<StateSaverAndLoader> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            warpCodec.fieldOf("warps").forGetter(StateSaverAndLoader::getWarps)
    ).apply(instance, StateSaverAndLoader::new));

    private final HashMap<String, LocationData> warps;

    public StateSaverAndLoader() {
        warps = new HashMap<>();
    }

    public StateSaverAndLoader(Map<String, LocationData> warps) {
        this.warps = new HashMap<>();
        System.out.println("Map" + warps);
        System.out.println("HashMap" + this.warps);
        this.warps.putAll(warps);
    }

    public Map<String, LocationData> getWarps() {
        return warps;
    }

    private static final PersistentStateType<StateSaverAndLoader> TYPE = new PersistentStateType<>(MOD_ID, StateSaverAndLoader::new, CODEC, null);

    public static StateSaverAndLoader ofServer(MinecraftServer server) {
        try {
            var state = Objects.requireNonNull(server.getWorld(World.OVERWORLD))
                    .getPersistentStateManager()
                    .getOrCreate(TYPE);

            state.markDirty();
            return state;
        } catch (Exception e) {
            var worldStateManager = Objects.requireNonNull(server.getWorld(World.OVERWORLD))
                    .getPersistentStateManager();

            worldStateManager.set(TYPE, new StateSaverAndLoader());

            var state = worldStateManager.getOrCreate(TYPE);
            state.markDirty();

            return state;
        }
    }
}
