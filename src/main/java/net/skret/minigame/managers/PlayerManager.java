package net.skret.minigame.managers;

import com.google.common.collect.ImmutableSet;
import net.skret.minigame.Main;
import net.skret.minigame.models.PlayerWrapper;
import net.skret.minigame.models.Team;
import net.skret.minigame.models.kits.Kit;
import net.skret.minigame.models.kits.KitType;

import java.util.*;

public class PlayerManager {

    private final KitManager kitManager;
    private final Map<UUID, PlayerWrapper> players = new HashMap<>();

    public PlayerManager(Main plugin) {
        this.kitManager = new KitManager(plugin, this);
    }

    public void addPlayer(UUID id, Team team) {
        PlayerWrapper player = new PlayerWrapper(id, team);
        players.put(id, player);
        player.getTeam().addPlayer(id);
    }

    public void removePlayer(UUID id) {
        players.get(id).getTeam().removePlayer(id);
        players.remove(id);
    }

    public PlayerWrapper getPlayer(UUID id) {
        return players.get(id);
    }

    public Set<PlayerWrapper> getPlayers(Team team) {
        Set<PlayerWrapper> playersFromTeam = new HashSet<>();
        team.getPlayers().forEach(playerId -> playersFromTeam.add(players.get(playerId)));
        return playersFromTeam;
    }

    public Set<PlayerWrapper> getPlayers() {
        return ImmutableSet.copyOf(players.values());
    }

    //TEAM MANIPULATION LOGIC

    public Team getTeam(UUID id) {
        if (players.get(id) == null) return null;
        return players.get(id).getTeam();
    }

    public void setTeam(UUID id, Team team) {
        Team prevTeam = getTeam(id);
        if (prevTeam != null) prevTeam.removePlayer(id);
        players.get(id).setTeam(team);
        team.addPlayer(id);
    }

    //ALIVE CHECKS

    public void setAlive(UUID id, boolean alive) {
        players.get(id).setAlive(alive);
    }

    public boolean isAlive(UUID id) {
        return getPlayer(id).isAlive();
    }

    public Set<PlayerWrapper> getAlivePlayers(Team team) {
        Set<PlayerWrapper> players = getPlayers(team);
        players.removeIf(playerWrapper -> !playerWrapper.isAlive());
        return players;
    }

    public Set<PlayerWrapper> getAlivePlayers() {
        Set<PlayerWrapper> players = new HashSet<>();
        this.players.forEach((id, player) -> {
            if (player.isAlive()) players.add(player);
        });
        return players;
    }

    //KIT MANIPULATION LOGIC

    public void setKit(UUID id, KitType kitType) {
        getPlayer(id).setSelectedKit(kitManager.getKit(id, kitType));
    }

    public Kit getKit(UUID id) {
        return getPlayer(id).getSelectedKit();
    }

    public void giveItems() {
        kitManager.giveItems();
    }

    public void enableKits() {
        kitManager.enableListeners();
    }

    public void disableKits() {
        kitManager.disableListeners();
    }
}
