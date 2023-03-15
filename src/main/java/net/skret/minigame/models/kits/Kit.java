package net.skret.minigame.models.kits;

import net.skret.minigame.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public abstract class Kit implements Listener {

    protected final Main plugin;
    protected final UUID playerId;

    public Kit(Main plugin, UUID playerId) {
        this.plugin = plugin;
        this.playerId = playerId;
    }

    public abstract KitType getKitType();

    public abstract ItemStack[] getArmor();

    public abstract List<ItemStack> getItems();

    public abstract void onStart();

    public abstract void onStop();

    public final void enable() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        onStart();
    }

    public final void disable() {
        HandlerList.unregisterAll(this);
        onStop();
    }

}
