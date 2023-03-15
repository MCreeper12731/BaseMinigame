package net.skret.minigame.util;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryUtil {

    public static void closeInventory(Player player, Plugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                player.closeInventory();
            }
        }.runTaskLater(plugin, 1L);
    }

}
