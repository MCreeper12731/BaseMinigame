package net.skret.minigame.phases;

import net.skret.minigame.managers.PhaseManager;
import net.skret.minigame.models.Team;
import net.skret.minigame.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class RestartingPhase extends Phase {

    private final Team winningTeam;

    public RestartingPhase(PhaseManager phaseManager, Team winningTeam) {
        super(phaseManager);
        this.winningTeam = winningTeam;
    }

    @Override
    public void onEnable() {

        new Countdown().start();

    }

    @Override
    public void onDisable() {

        phaseManager.getPlayerManager().getPlayers().forEach(playerWrapper -> {
            phaseManager.getMessenger().connect(Bukkit.getPlayer(playerWrapper.getId()), "hub");
        });

    }

    @Override
    public Phase getNextPhase() {
        return new WaitingPhase(phaseManager);
    }

    private class Countdown extends BukkitRunnable {

        private int timeLeft = 12;

        @Override
        public void run() {

            if (timeLeft == 0) {
                this.cancel();
                phaseManager.nextPhase();
                return;
            }

            if (timeLeft == 10) {
                Bukkit.broadcastMessage(Color.color("&eArena restarting in 10 seconds"));
            }

            if (timeLeft > 3)
                for (UUID playerId : winningTeam.getPlayers()) {
                    Player player = Bukkit.getPlayer(playerId);
                    if (player == null) return;
                    player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
                }

            timeLeft--;

        }

        public void start() {
            this.runTaskTimer(phaseManager.getPlugin(), 0L, 20L);
        }
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
