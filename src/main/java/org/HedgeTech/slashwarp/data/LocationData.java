package org.HedgeTech.slashwarp.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.text.DecimalFormat;

public class LocationData {
    public static final Codec<LocationData> CODEC;
    public RegistryKey<World> world;
    public Vec3d position;
    public float yaw;
    public float pitch;

    public LocationData() { }

    public LocationData(String world, Vec3d position, float yaw, float pitch) {
        this.world = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(world));
        this.position = position;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public String toString() {
        var numFormat = new DecimalFormat("0.00");
        return "[" + world.getValue() + "] (" + numFormat.format(position.x) + ", " + numFormat.format(position.y) + ", " + numFormat.format(position.z) + ")";
    }

    public String getWorldString() {
        return world.getValue().toString();
    }

    public Vec3d getPosition() {
        return position;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("world").forGetter(LocationData::getWorldString),
            Vec3d.CODEC.fieldOf("position").forGetter(LocationData::getPosition),
            Codec.FLOAT.fieldOf("yaw").forGetter(LocationData::getYaw),
            Codec.FLOAT.fieldOf("pitch").forGetter(LocationData::getPitch)
        ).apply(instance, LocationData::new));
    }
}
