package net.skret.minigame.managers;

import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.SpiGUI;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import net.skret.minigame.Main;
import net.skret.minigame.models.Team;
import net.skret.minigame.models.kits.KitType;
import net.skret.minigame.util.Color;
import net.skret.minigame.util.InventoryUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public class GuiManager {

    private final Main plugin;
    private final SpiGUI spiGUI;
    private final PlayerManager playerManager;

    public GuiManager(
            Main plugin,
            PlayerManager playerManager) {
        this.plugin = plugin;
        this.spiGUI = new SpiGUI(plugin);
        this.playerManager = playerManager;
    }

    private List<SGButton> getTeamInfo(Player player) {

        List<SGButton> returnList = new ArrayList<>();

        for (Team team : Team.values()) {
            SGButton teamButton = new SGButton(
                    new ItemBuilder(Material.valueOf(team.name() + "_WOOL"))
                            .lore(Color.color("&7Players: " + team.getPlayers().size()))
                            .build()
            ).withListener((InventoryClickEvent event) -> {
                Player whoClicked = (Player) event.getWhoClicked();
                if (playerManager.getTeam(whoClicked.getUniqueId()) == team) {
                    whoClicked.sendMessage(Color.color("&cYou are already on team " + team));
                    return;
                }
                if (!isSelectionValid(team)) {
                    whoClicked.sendMessage(Color.color("&cThis team has too many players! Fill others first"));
                    return;
                }
                playerManager.setTeam(player.getUniqueId(), team);
                whoClicked.sendMessage(Color.color("&eJoined " + team + " &eteam!"));

                spiGUI.findOpenWithTag("team-selection").forEach(openMenu -> {
                    List<SGButton> teamInfo = getTeamInfo(player);
                    for (int i = 0; i < teamInfo.size(); i++) {
                        openMenu.getGUI().setButton(i, teamInfo.get(i));
                    }
                });

                InventoryUtil.closeInventory(player, plugin);
            });
            returnList.add(teamButton);
        }

        return returnList;
    }

    public SGMenu getTeamMenu(Player player) {

        SGMenu teamMenu = spiGUI.create("Team Selection Menu", 5, "team-selection");

        teamMenu.addButtons(getTeamInfo(player).toArray(new SGButton[0]));

        return teamMenu;
    }

    private boolean isSelectionValid(Team teamAttemptingToJoin) {

        for (Team team : Team.values()) {
            if (team.getPlayers().size() + 1 < teamAttemptingToJoin.getPlayers().size()) return false;
        }
        return true;
    }

    public SGMenu getKitMenu(Player player) {

        SGMenu kitMenu = spiGUI.create("Kit Selection Menu", 5);

        for (KitType kit : KitType.values()) {

            SGButton kitButton = new SGButton(
                    kit.getIcon()
            ).withListener((InventoryClickEvent event) -> {
                Player whoClicked = (Player) event.getWhoClicked();
                if (playerManager.getKit(whoClicked.getUniqueId()).getKitType() == kit) {
                    whoClicked.sendMessage(Color.color("&cYou already have kit &b" + kit.getName() + " &cselected!"));
                    InventoryUtil.closeInventory(player, plugin);
                    return;
                }
                playerManager.setKit(whoClicked.getUniqueId(), kit);
                whoClicked.sendMessage(Color.color("&eSelected kit &b" + kit.getName()));
                InventoryUtil.closeInventory(player, plugin);
            });
            kitMenu.addButton(kitButton);

        }

        return kitMenu;

    }

}
