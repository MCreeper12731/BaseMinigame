package net.skret.minigame.managers;

import net.skret.minigame.Main;
import net.skret.minigame.models.PlayerWrapper;
import net.skret.minigame.models.kits.ArcherKit;
import net.skret.minigame.models.kits.Kit;
import net.skret.minigame.models.kits.KitType;
import net.skret.minigame.models.kits.SwordsmanKit;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.UUID;

public class KitManager {

    private final Main plugin;
    private final PlayerManager playerManager;

    public KitManager(Main plugin, PlayerManager playerManager) {
        this.plugin = plugin;
        this.playerManager = playerManager;
    }

    public Kit getKit(UUID playerId, KitType kitType) {
        return switch(kitType) {
            case SWORDSMAN -> new SwordsmanKit(plugin, playerId);
            case ARCHER -> new ArcherKit(plugin, playerId);
        };
    }

    public void giveItems() {

        for (PlayerWrapper playerWrapper : playerManager.getPlayers()) {

            Player player = Bukkit.getPlayer(playerWrapper.getId());
            player.getInventory().addItem(playerWrapper.getSelectedKit().getItems().toArray(new ItemStack[0]));

            ItemStack[] armor = playerWrapper.getSelectedKit().getArmor();
            LeatherArmorMeta meta = (LeatherArmorMeta) armor[3].getItemMeta();
            int[] color = playerWrapper.getTeam().getRgbValue();
            meta.setColor(Color.fromRGB(color[0], color[1], color[2]));
            armor[3].setItemMeta(meta);

            player.getInventory().setArmorContents(armor);
        }

    }

    public void enableListeners() {
        for (PlayerWrapper playerWrapper : playerManager.getPlayers()) {
            playerWrapper.getSelectedKit().enable();
        }
    }

    public void disableListeners() {
        for (PlayerWrapper playerWrapper : playerManager.getPlayers()) {
            playerWrapper.getSelectedKit().disable();
        }
    }
}
