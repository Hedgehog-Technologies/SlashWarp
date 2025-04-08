package org.hedgetech.slashwarp.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.text.DecimalFormat;

/**
 * LocationData used for Warp Points
 */
public class LocationData {
    /**
     * Codec defining LocationData
     */
    public static final Codec<LocationData> CODEC;

    private ResourceKey<Level> world;
    private Vec3 position;
    private float yaw;
    private float pitch;

    /**
     * Default Constructor - nothing special
     */
    public LocationData() { }

    /**
     * New LocationData from passed in data
     * @param world ResourceKey for the Level to stored
     * @param position Position of the Warp Point
     * @param yaw Yaw (y-rot) of the Warp Point
     * @param pitch Pitch (x-rot) of the Warp Point
     */
    public LocationData(ResourceKey<Level> world, Vec3 position, float yaw, float pitch) {
        this.world = world;
        this.position = position;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
     * New LocationData from existing data
     * @param world Level ResourceLocation String
     * @param position Position Vec3
     * @param yaw Yaw (y-rot)
     * @param pitch Pitch (x-rot)
     */
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

    /**
     * Get the stored Level ResourceKey value
     * @return World value
     */
    public ResourceKey<Level> getWorld() { return world; }

    /**
     * Get the stored Level ResourceLocation as a String
     * @return ResourceLocation as a String
     */
    public String getWorldLocationString() { return world.location().toString(); }

    /**
     * Get the stored position (vec3) value
     * @return Position value
     */
    public Vec3 getPosition() { return position; }

    /**
     * Get the stored Yaw (y-rot) value
     * @return Yaw value
     */
    public float getYaw() { return yaw; }

    /**
     * Get the stored Pitch (x-rot) value
     * @return Pitch value
     */
    public float getPitch() { return pitch; }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("world").forGetter(LocationData::getWorldLocationString),
            Vec3.CODEC.fieldOf("position").forGetter(LocationData::getPosition),
            Codec.FLOAT.fieldOf("yaw").forGetter(LocationData::getYaw),
            Codec.FLOAT.fieldOf("pitch").forGetter(LocationData::getPitch)
        ).apply(instance, LocationData::new));
    }
}
