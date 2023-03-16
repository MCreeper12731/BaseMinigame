package net.skret.minigame.phases;

import net.skret.minigame.managers.PhaseManager;
import net.skret.minigame.models.PlayerWrapper;
import net.skret.minigame.models.Team;
import net.skret.minigame.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class WaitingPhase extends Phase {

    private Countdown countdown;

    public WaitingPhase(PhaseManager phaseManager) {
        super(phaseManager);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public Phase getNextPhase() {
        return new StartingPhase(phaseManager);
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (phaseManager.getPlayerManager().getPlayers().size() >= phaseManager.getConfigManager().getConfig().getInt("max-players"))
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, Color.color("&cGame Full!"));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        if (event.getPlayer().isOp()) return;
        event.setJoinMessage("");

        countdown = new Countdown();
        List<Double> spawnPoint = phaseManager.getConfigManager().getConfig().getDoubleList("spawn-point");
        Location mainSpawnPoint = new Location(phaseManager.getWorld(), spawnPoint.get(0), spawnPoint.get(1), spawnPoint.get(2));

        Player player = event.getPlayer();
        phaseManager.getPlayerManager().addPlayer(player.getUniqueId(), Team.values()[(int)(Math.random() * Team.values().length)]);

        player.teleport(mainSpawnPoint);
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getInventory().setItem(0, phaseManager.getCustomItemManager().getTeamSelectionItem());
        player.getInventory().setItem(1, phaseManager.getCustomItemManager().getKitSelectionItem());

        Bukkit.broadcastMessage(Color.color(String.format(
                "&e%s joined the game (%d/%d)",
                player.getDisplayName(),
                phaseManager.getPlayerManager().getAlivePlayers().size(),
                phaseManager.getConfigManager().getConfig().getInt("max-players")
        )));

        checkStartRequirements();

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        if (player.isOp()) return;
        event.setQuitMessage("");

        phaseManager.getPlayerManager().removePlayer(player.getUniqueId());
        Bukkit.broadcastMessage(Color.color(String.format(
                "&e%s left the game (%d/%d)",
                player.getDisplayName(),
                phaseManager.getPlayerManager().getPlayers().size(),
                phaseManager.getConfigManager().getConfig().getInt("max-players")
        )));

    }

    private void checkStartRequirements() {

        int numberOfPlayers = phaseManager.getPlayerManager().getPlayers().size();
        int maxPlayers = phaseManager.getConfigManager().getConfig().getInt("max-players");
        double playersPercent = 1.0 * numberOfPlayers / maxPlayers;
        boolean taskRunning = true;
        try {
            countdown.isCancelled();
        } catch (IllegalStateException exception) {
            taskRunning = false;
        }

        if (playersPercent < 0.5 && taskRunning) {
            countdown.cancel();
            countdown.timeLeft = 60;
            return;
        }
        if (playersPercent > 0.5 && !taskRunning) countdown.start();
        if (playersPercent > 0.8 && countdown.timeLeft > 15) countdown.timeLeft = 15;
        if (playersPercent == 1.0 && countdown.timeLeft > 10) countdown.timeLeft = 10;

    }

    private class Countdown extends BukkitRunnable {

        private int timeLeft = 60;

        @Override
        public void run() {

            if (timeLeft == 0) {
                this.cancel();
                phaseManager.nextPhase();
                return;
            }

            if (timeLeft == 60 || timeLeft == 30 || timeLeft == 15 || (timeLeft <= 10 && timeLeft >= 2)) {
                Bukkit.broadcastMessage(Color.color("&eGame starting in " + timeLeft + " seconds!"));
            } else if (timeLeft == 1) {
                Bukkit.broadcastMessage(Color.color("&eGame starting in " + timeLeft + " second!"));
            }

            timeLeft--;

        }

        public void start() {
            this.runTaskTimer(phaseManager.getPlugin(), 0L, 20L);
        }
    }


    //Standard login and grief listeners

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().isOp()) return;
        event.setCancelled(true);
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
