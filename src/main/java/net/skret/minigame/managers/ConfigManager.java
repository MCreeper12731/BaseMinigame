package net.skret.minigame.managers;

import lombok.Getter;
import net.skret.minigame.Main;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class ConfigManager {

    private final Main plugin;

    @Getter
    private FileConfiguration config;

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
        this.plugin.saveDefaultConfig();
        this.config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder().getAbsoluteFile() + "/config.yml"));
    }

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder().getAbsoluteFile() + "/config.yml"));
    }

    public Location getLocation(World world, String path) {
        List<Double> coordsList = config.getDoubleList(path);
        return new Location(world, coordsList.get(0), coordsList.get(1), coordsList.get(2));
    }

}
