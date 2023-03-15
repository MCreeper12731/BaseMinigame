package net.skret.minigame.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CusItem {

    public static ItemStack create(Material material, String name) {
        return create(material, 1, name, null , false);
    }

    public static ItemStack create(Material material, String name, List<String> lore) {
        return create(material, 1, name, lore, false);
    }

    public static ItemStack create(Material material, int count, String name, List<String> lore) {
        return create(material, count, name, lore, false);
    }

    public static ItemStack create(Material material, int count, String name, List<String> lore, boolean enchanted) {
        ItemStack item = new ItemStack(material, count);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore != null) meta.setLore(lore);
        if (enchanted) meta.addEnchant(Enchantment.LUCK, 1, true);
        item.setItemMeta(meta);
        return item;
    }

}
