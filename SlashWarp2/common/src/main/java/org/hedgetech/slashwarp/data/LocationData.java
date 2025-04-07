package org.hedgetech.slashwarp.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.text.DecimalFormat;

public class LocationData {
    public static final Codec<LocationData> CODEC;

    public ResourceKey<Level> world;
    public Vec3 position;
    public float yaw;
    public float pitch;

    public LocationData() { }

    public LocationData(String world, Vec3 position, float yaw, float pitch) {
        this.world = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(world));
        this.position = position;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public String toString() {
        var numFormat = new DecimalFormat("0.00");
        return "[" + world.location() + "] (" + numFormat.format(position.x) + ", " + numFormat.format(position.y) + ", " + numFormat.format(position.z) + ")";
    }

    public String getWorldString() { return world.location().toString(); }

    public Vec3 getPosition() { return position; }

    public float getYaw() { return yaw; }

    public float getPitch() { return pitch; }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("world").forGetter(LocationData::getWorldString),
            Vec3.CODEC.fieldOf("position").forGetter(LocationData::getPosition),
            Codec.FLOAT.fieldOf("yaw").forGetter(LocationData::getYaw),
            Codec.FLOAT.fieldOf("pitch").forGetter(LocationData::getPitch)
        ).apply(instance, LocationData::new));
    }
}
