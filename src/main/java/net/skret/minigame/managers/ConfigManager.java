package net.skret.minigame.managers;

import lombok.Getter;
import net.skret.minigame.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

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

}
