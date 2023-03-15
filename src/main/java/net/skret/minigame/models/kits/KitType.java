package net.skret.minigame.models.kits;

import com.samjakob.spigui.item.ItemBuilder;
import lombok.Getter;
import net.skret.minigame.util.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum KitType {

    //Note to self:
    //Process to add new kits: enum -> class -> add to KitManager

    SWORDSMAN("Swordsman",
            new ItemBuilder(Material.STONE_SWORD)
                    .name(Color.color("&bSwordsman"))
                    .lore(Color.color("&7Sample kit text"))
                    .build()),
    ARCHER("Archer",
            new ItemBuilder(Material.BOW)
                    .name(Color.color("&bArcher"))
                    .lore(Color.color("&7Sample kit text"))
                    .build());

    private final String name;
    private final ItemStack icon;

    KitType(String name, ItemStack icon) {
        this.name = name;
        this.icon = icon;
    }
}
