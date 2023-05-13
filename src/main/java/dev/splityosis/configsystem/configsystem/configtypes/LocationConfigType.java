package dev.splityosis.configsystem.configsystem.configtypes;

import dev.splityosis.configsystem.configsystem.ConfigTypeLogic;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Objects;

public class LocationConfigType extends ConfigTypeLogic<Location> {

    @Override
    public Location getFromConfig(ConfigurationSection config, String path) {
        World world = Bukkit.getWorld(Objects.requireNonNull(config.getString(path + ".world")));
        double x = config.getDouble(path + ".x");
        double y = config.getDouble(path + ".y");
        double z = config.getDouble(path + ".z");
        double yaw = config.getDouble(path + ".yaw");
        double pitch = config.getDouble(path + ".pitch");
        return new Location(world, x, y, z, (float) yaw, (float) pitch);
    }

    @Override
    public void setInConfig(Location instance, ConfigurationSection config, String path) {
        config.set(path + ".world", instance.getWorld().getName());
        config.set(path + ".x", instance.getX());
        config.set(path + ".y", instance.getY());
        config.set(path + ".z", instance.getZ());
        config.set(path + ".yaw", instance.getYaw());
        config.set(path + ".pitch", instance.getPitch());
    }
}
