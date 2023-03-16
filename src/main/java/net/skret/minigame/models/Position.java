package net.skret.minigame.models;

import lombok.Data;
import net.skret.minigame.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.World;

@Data
public class Position {

    private double x;
    private double y;
    private double z;
    private double yaw;
    private double pitch;

    public Position() {
        this(0, 0, 0, 0, 0);
    }

    public Position(double x, double y, double z) {
        this(x, y, z, 0, 0);
    }

    public Position(double x, double y, double z, double yaw, double pitch) {
        this.x = MathUtil.round(x, 0.5);
        this.y = MathUtil.round(y, 0.5);
        this.z = MathUtil.round(z, 0.5);
        this.yaw = MathUtil.round(yaw, 45);
        this.pitch = MathUtil.round(pitch, 45);
    }

    public Position(Location location) {
        this(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public Location getLocation(World world) {
        return new Location(world, x, y, z, (float)yaw, (float)pitch);
    }

}
