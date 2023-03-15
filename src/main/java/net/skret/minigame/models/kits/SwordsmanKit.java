package net.skret.minigame.models.kits;

import net.skret.minigame.Main;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class SwordsmanKit extends Kit {

    public SwordsmanKit(Main plugin, UUID playerId) {
        super(plugin, playerId);
    }

    @Override
    public KitType getKitType() {
        return null;
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[0];
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
