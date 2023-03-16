package net.skret.minigame.phases;

import com.samjakob.spigui.item.ItemBuilder;
import net.skret.minigame.managers.PhaseManager;
import net.skret.minigame.models.Team;
import net.skret.minigame.util.Color;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ActivePhase extends Phase {

    private Team winningTeam;

    public ActivePhase(PhaseManager phaseManager) {
        super(phaseManager);
    }

    @Override
    public void onEnable() {
        checkPlayerState();
    }

    @Override
    public void onDisable() {

    }

    @Override
    public Phase getNextPhase() {
        return new RestartingPhase(phaseManager, winningTeam);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player damageTaker)) return;

        Player damageDealer;

        //Get damage dealer from different damage sources
        if (event.getDamager() instanceof Arrow arrow) {
            if (!(arrow.getShooter() instanceof Player player)) return;
            damageDealer = player;
        } else if (event.getDamager() instanceof Player) {
            damageDealer = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Firework firework) {
            if (!(firework.getShooter() instanceof Player player)) return;
            damageDealer = player;
        } else return;

        //Check if damage dealer and damage taker are on the same team
        if (phaseManager.getPlayerManager().getPlayer(damageDealer.getUniqueId()).getTeam().getPlayers().contains(damageTaker.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        //Check if hit would kill the player
        if (damageTaker.getHealth() - event.getDamage() <= 0) {
            damageTaker.setHealth(20.0);
            Bukkit.broadcastMessage(Color.color("&e" + damageDealer.getDisplayName() + " killed " + damageTaker.getDisplayName()));
            List<Double> spawnPoint = phaseManager.getConfigManager().getConfig().getDoubleList("spawn-point");
            Location location = new Location(damageTaker.getWorld(), spawnPoint.get(0), spawnPoint.get(1), spawnPoint.get(2));
            damageTaker.sendTitle(Color.color("&cYou are dead!"), Color.color("&7You are now spectating"), 0, 60, 10);
            damageTaker.teleport(location);
            damageTaker.playSound(damageTaker.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);
            damageTaker.setGameMode(GameMode.SPECTATOR);
            damageTaker.getInventory().clear();

            phaseManager.getPlayerManager().setAlive(damageTaker.getUniqueId(), false);
            checkPlayerState();
            event.setCancelled(true);
        }

    }

    private void checkPlayerState() {

        List<Team> teamsWithAlivePlayers = new ArrayList<>();

        for (Team team : Team.values()) {

            if (phaseManager.getPlayerManager().getAlivePlayers(team).size() > 0) teamsWithAlivePlayers.add(team);
            if (teamsWithAlivePlayers.size() > 1) return;

        }

        this.winningTeam = teamsWithAlivePlayers.get(0);
        Bukkit.broadcastMessage(Color.color("&e" + teamsWithAlivePlayers.get(0).toString() + " &eteam won the game!"));
        for (UUID playerId : teamsWithAlivePlayers.get(0).getPlayers()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player == null) continue;
            Bukkit.broadcastMessage(Color.color("&6" + player.getName() + " survived with " + (int)player.getHealth()/2 + " health"));
        }
        phaseManager.nextPhase();

    }

    //Standard login and grief listeners

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Color.color("&cYou cannot join the game right now!"));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (player.isOp()) return;
        event.setQuitMessage("");

        phaseManager.getPlayerManager().removePlayer(player.getUniqueId());
        Bukkit.broadcastMessage(Color.color("&e" + player.getName() + " left the game"));
        checkPlayerState();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().isOp()) return;
        if (event.getBlockReplacedState().getBlock().getType() == Material.LAVA) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().isOp()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDropItem(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) event.setCancelled(true);
    }
}
