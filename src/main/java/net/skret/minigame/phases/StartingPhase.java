package net.skret.minigame.phases;

import net.skret.minigame.managers.PhaseManager;
import net.skret.minigame.models.Position;
import net.skret.minigame.models.Team;
import net.skret.minigame.util.Color;
import net.skret.minigame.util.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.UUID;

public class StartingPhase extends Phase {

    public StartingPhase(PhaseManager phaseManager) {
        super(phaseManager);
    }

    @Override
    public void onEnable() {

        for (Team team : Team.values()) {
            Iterator<Position> positionIterator = team.getSpawns().iterator();
            for (UUID playerId : team.getPlayers()) {

                Player player = Bukkit.getPlayer(playerId);
                Location location = positionIterator.next().getLocation(player.getWorld());
                player.teleport(location);
                player.getInventory().remove(Material.PAPER);

            }
        }

        new Countdown().start();
    }

    @Override
    public void onDisable() {

        phaseManager.getPlayerManager().getPlayers().forEach(playerWrapper -> {
            Player player = Bukkit.getPlayer(playerWrapper.getId());
            if (player == null) {
                phaseManager.getPlayerManager().removePlayer(playerWrapper.getId());
                return;
            }
            player.getInventory().clear();
            InventoryUtil.closeInventory(player, phaseManager.getPlugin());
        });

    }

    @Override
    public Phase getNextPhase() {
        return new ActivePhase(phaseManager);
    }

    private class Countdown extends BukkitRunnable {

        private int timeLeft = 10;

        @Override
        public void run() {

            if (timeLeft < 5) {
                phaseManager.getPlayerManager().getPlayers().forEach(playerWrapper -> {
                    Player player = Bukkit.getPlayer(playerWrapper.getId());
                    if (player == null) {
                        phaseManager.getPlayerManager().removePlayer(playerWrapper.getId());
                        return;
                    }
                    if (timeLeft == 0)
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 2, 2);
                    else
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 2, 2);
                });
            }

            if (timeLeft <= 0) {
                this.cancel();
                phaseManager.nextPhase();
                return;
            }

            if (timeLeft <= 10 && timeLeft >= 2) {
                Bukkit.broadcastMessage(Color.color("&eGame starting in " + timeLeft + " seconds!"));
            } else {
                Bukkit.broadcastMessage(Color.color("&eGame starting in " + timeLeft + " second!"));
            }

            timeLeft--;

        }

        public void start() {
            this.runTaskTimer(phaseManager.getPlugin(), 0L, 20L);
        }
    }

    //Standard login/disconnect and grief listeners

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
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager().isOp()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
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
