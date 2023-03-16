package net.skret.minigame;


import com.github.MCreeper12731.CreeperItems;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandAPIConfig;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import net.skret.minigame.managers.*;
import net.skret.minigame.map.GameMap;
import net.skret.minigame.map.LocalGameMap;
import net.skret.minigame.messenger.Messenger;
import net.skret.minigame.models.kits.Kit;
import net.skret.minigame.util.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class Main extends JavaPlugin {

    private Messenger messenger;
    private CreeperItems api;
    private GameMap map;

    private ConfigManager configManager;
    private SpawnManager spawnManager;
    private PlayerManager playerManager;
    private PhaseManager phaseManager;
    private GuiManager guiManager;
    private CustomItemManager customItemManager;


    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIConfig());
        registerCommands();
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable(this);

        getDataFolder().mkdirs();

        File gameMapsFolder = new File(getDataFolder().getAbsoluteFile(), "gameMaps");
        if (!gameMapsFolder.exists()) gameMapsFolder.getAbsoluteFile().mkdirs();

        map = new LocalGameMap(gameMapsFolder, "TestWorld", true);

        messenger = new Messenger(this);
        api = new CreeperItems(this, "minigame");
        configManager = new ConfigManager(this);
        spawnManager = new SpawnManager(this);
        playerManager = new PlayerManager(this);
        customItemManager = new CustomItemManager(api, guiManager);
        phaseManager = new PhaseManager(this, playerManager, configManager, customItemManager, map.getWorld(), messenger);
        guiManager = new GuiManager(this, playerManager);

        spawnManager.loadSpawns();

    }

    private void registerCommands() {

        new CommandAPICommand("item")
                .withArguments(
                        new StringArgument("item name").replaceSuggestions(ArgumentSuggestions.strings(
                                        "spawnsetter", "teamselector", "kitselector"
                                )))
                .executes((sender, args) -> {
                    if (!(sender instanceof Player player)) return;

                    String item = (String) args[0];

                    if (item.equalsIgnoreCase("spawnsetter")) {
                        player.getInventory().addItem(customItemManager.getSpawnSetterItem());
                        return;
                    }
                    if (item.equalsIgnoreCase("teamselector")) {
                        player.getInventory().addItem(customItemManager.getTeamSelectionItem());
                        return;
                    }
                    if (item.equalsIgnoreCase("kitselector")) {
                        player.getInventory().addItem(customItemManager.getKitSelectionItem());
                        return;
                    }

                })
                .register();

        new CommandAPICommand("spawns")
                .withArguments(
                        new StringArgument("").replaceSuggestions(ArgumentSuggestions.strings(
                                "save", "load"
                        )))
                .executes((sender, args) -> {
                    if (!(sender instanceof Player player)) return;

                    if (((String)args[0]).equalsIgnoreCase("save")) {
                        spawnManager.saveSpawns();
                        player.sendMessage(Color.color("&aSaved spawns!"));
                        return;
                    }
                    if (((String)args[0]).equalsIgnoreCase("load")) {
                        spawnManager.loadSpawns();
                        player.sendMessage(Color.color("&aLoaded spawns!"));
                    }
                })
                .register();

        new CommandAPICommand("game")
                .withArguments(
                        new StringArgument("").replaceSuggestions(ArgumentSuggestions.strings(
                                        "start", "stop"
                                )))
                .executes((sender, args) -> {
                    if (((String)args[0]).equalsIgnoreCase("start")) {
                        phaseManager.forceStart(sender);
                        return;
                    }
                    if (((String)args[0]).equalsIgnoreCase("stop")) {
                        phaseManager.forceStart(sender);
                        return;
                    }
                })
                .register();

        new CommandAPICommand("map")
                .withArguments(
                        new StringArgument("").replaceSuggestions(ArgumentSuggestions.strings(
                                "reset", "warp"
                        )))
                .executes((sender, args) -> {

                    String arg = (String) args[0];

                    if (arg.equalsIgnoreCase("reset")) {
                        map.restoreFromSource();
                        sender.sendMessage(Color.color("&aMap restored!"));
                        return;
                    }
                    if (arg.equalsIgnoreCase("warp")) {
                        if (!(sender instanceof Player player)) {
                            sender.sendMessage(Color.color("&cOnly players can run this command!"));
                            return;
                        }
                        player.teleport(new Location(map.getWorld(), 0, 100, 0));
                    }

                })
                .register();

    }

}
