package net.skret.minigame.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.skret.minigame.Main;
import net.skret.minigame.models.Position;
import net.skret.minigame.models.Team;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SpawnManager {

    private final Main plugin;

    public SpawnManager(Main plugin) {
        this.plugin = plugin;
    }

    public void saveSpawns() {
        try {
            File file = new File(
                    plugin.getDataFolder().getAbsoluteFile() +
                            "/spawns.json"
            );
            file.getParentFile().mkdir();
            file.createNewFile();
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
            Writer writer = new FileWriter(file, false);
            List<Set<Position>> positions = new ArrayList<>();

            for (Team team : Team.values()) {
                positions.add(team.getSpawns());
            }

            gson.toJson(positions, writer);

            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadSpawns() {
        try {
            File file = new File(
                    plugin.getDataFolder().getAbsoluteFile() +
                            "/spawns.json"
            );
            if (!file.exists()) return;
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
            Reader reader = new FileReader(file);
            Type listType = new TypeToken<List<List<Position>>>(){}.getType();
            List<List<Position>> spawns = gson.fromJson(reader, listType);
            for (int i = 0; i < spawns.size(); i++) {
                Team team = Team.values()[i];
                team.addSpawns(spawns.get(i));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
