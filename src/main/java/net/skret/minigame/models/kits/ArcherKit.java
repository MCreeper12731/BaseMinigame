package net.skret.minigame.models.kits;

import net.skret.minigame.Main;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class ArcherKit extends Kit {

    public ArcherKit(Main plugin, UUID playerId) {
        super(plugin, playerId);
    }

    @Override
    public KitType getKitType() {
        return KitType.ARCHER;
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[4];
    }

    @Override
    public List<ItemStack> getItems() {
        return null;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }
}
