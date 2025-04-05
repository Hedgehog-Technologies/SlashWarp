package org.HedgeTech.slashwarp.data;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.text.DecimalFormat;

public class LocationData {
    public RegistryKey<World> world;
    public Vec3d position;
    public float yaw;
    public float pitch;

    public String toString() {
        var numFormat = new DecimalFormat("0.00");
        return "[" + world.getValue() + "] (" + numFormat.format(position.x) + ", " + numFormat.format(position.y) + ", " + numFormat.format(position.z) + ")";
    }
}
