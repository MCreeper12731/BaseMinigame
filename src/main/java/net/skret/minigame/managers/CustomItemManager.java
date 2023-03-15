package net.skret.minigame.managers;

import com.github.MCreeper12731.CreeperItems;
import com.github.MCreeper12731.Interaction;
import com.samjakob.spigui.item.ItemBuilder;
import net.skret.minigame.models.Position;
import net.skret.minigame.models.Team;
import net.skret.minigame.util.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomItemManager {

    private final CreeperItems cItemsApi;
    private final GuiManager guiManager;

    public CustomItemManager(CreeperItems cItemsApi, GuiManager guiManager) {
        this.cItemsApi = cItemsApi;
        this.guiManager = guiManager;
    }

    public ItemStack getSpawnSetterItem() {
        return cItemsApi.create(
                new ItemBuilder(Material.BLAZE_ROD)
                        .name(Color.color("&6Set spawn - " + Team.getSelectedTeam().name()))
                        .build(),
                "spawn-setter"
        ).withClickListener(Interaction.RIGHT_CLICK_ANY, event -> {

            Player player = event.getPlayer();
            Location spawnLocation = player.getLocation();
            Team team = Team.getSelectedTeam();
            if (team == null) {
                player.sendMessage(Color.color("&cInvalid Team!"));
                return;
            }
            team.addSpawn(new Position(spawnLocation));
            player.sendMessage(Color.color("&aSpawn successfully set for team " + team.name() + " !"));

        }).withScrollListener(event -> {

            int delta = event.getNewSlot() - event.getPreviousSlot();

            if (delta == 0) return;
            event.setCancelled(true);

            if (delta < 0) {
                Team.decrementSelectedTeam();
            }
            if (delta > 0) {
                Team.incrementSelectedTeam();
            }

            event.getPlayer().getInventory().setItemInMainHand(
                    new ItemBuilder(event.getPlayer().getInventory().getItemInMainHand())
                            .name(Color.color("Set spawn - " + Team.getSelectedTeam().name()))
                            .build()
            );


        }).getItem();
    }

    public ItemStack getTeamSelectionItem() {
        return cItemsApi.create(
                new ItemBuilder(Material.PAPER)
                .name(Color.color("&bSelect Team"))
                .build(), "team-selector"
        ).withClickListener(Interaction.RIGHT_CLICK_ANY, event -> {

            Player player = event.getPlayer();
            player.openInventory(guiManager.getTeamMenu(player).getInventory());

        }).getItem();
    }

    public ItemStack getKitSelectionItem() {
        return cItemsApi.create(
                new ItemBuilder(Material.DIAMOND_SWORD)
                        .name(Color.color("&bSelect Kit"))
                        .build(), "kit-selector"
        ).withClickListener(Interaction.RIGHT_CLICK_ANY, event -> {

            Player player = event.getPlayer();
            player.openInventory(guiManager.getKitMenu(player).getInventory());

        }).getItem();
    }
}
