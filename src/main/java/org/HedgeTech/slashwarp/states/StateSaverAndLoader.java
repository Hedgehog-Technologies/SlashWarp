package org.HedgeTech.slashwarp.states;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import org.HedgeTech.slashwarp.data.LocationData;

import java.util.HashMap;

import static org.HedgeTech.slashwarp.SlashWarp.MOD_ID;

public class StateSaverAndLoader extends PersistentState {
    public HashMap<String, LocationData> warps = new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        var warpsNbt = new NbtCompound();

        warps.forEach((name, loc) -> {
            var warpNbt = new NbtCompound();

            var worldIdStr = loc.world.getValue();
            warpNbt.putString("world", worldIdStr.toString());

            warpNbt.putDouble("x", loc.position.x);
            warpNbt.putDouble("y", loc.position.y);
            warpNbt.putDouble("z", loc.position.z);

            warpNbt.putFloat("yaw", loc.yaw);
            warpNbt.putFloat("pitch", loc.pitch);

            warpsNbt.put(name, warpNbt);
        });

        nbt.put("warps", warpsNbt);

        return nbt;
    }

    public static StateSaverAndLoader createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        var state = new StateSaverAndLoader();
        var warpsNbt = tag.getCompound("warps");

        warpsNbt.getKeys().forEach(key -> {
            var warpNbt = warpsNbt.getCompound(key);

            var x = warpNbt.getDouble("x");
            var y = warpNbt.getDouble("y");
            var z = warpNbt.getDouble("z");
            var worldIdStr = warpNbt.getString("world");
            var worldId = Identifier.of(worldIdStr);

            state.warps.put(key, new LocationData() {
                {
                    world = RegistryKey.of(RegistryKeys.WORLD, worldId);
                    position = new Vec3d(x, y, z);
                    yaw = warpNbt.getFloat("yaw");
                    pitch = warpNbt.getFloat("pitch");
                }
            });
        });

        return state;
    }

    public static StateSaverAndLoader createNew() {
        var state = new StateSaverAndLoader();
        state.warps = new HashMap<>();
        return state;
    }

    private static final Type<StateSaverAndLoader> type = new Type<>(
            StateSaverAndLoader::createNew,
            StateSaverAndLoader::createFromNbt,
            null
    );

    public static StateSaverAndLoader getServerState(MinecraftServer server) {
        var serverWorld = server.getWorld(World.OVERWORLD);
        assert serverWorld != null;

        var state = serverWorld.getPersistentStateManager().getOrCreate(type, MOD_ID);
        state.markDirty();

        return state;
    }
}
