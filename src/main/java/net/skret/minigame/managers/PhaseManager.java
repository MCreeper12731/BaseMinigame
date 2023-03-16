package net.skret.minigame.managers;

import lombok.Getter;
import net.skret.minigame.Main;
import net.skret.minigame.messenger.Messenger;
import net.skret.minigame.phases.ActivePhase;
import net.skret.minigame.phases.Phase;
import net.skret.minigame.phases.WaitingPhase;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

public class PhaseManager {

    @Getter
    private final Main plugin;
    @Getter
    private final PlayerManager playerManager;
    @Getter
    private final ConfigManager configManager;
    @Getter
    private final CustomItemManager customItemManager;
    @Getter
    private final World world;
    @Getter
    private final Messenger messenger;
    private Phase currentPhase;

    public PhaseManager(Main plugin,
                        PlayerManager playerManager,
                        ConfigManager configManager,
                        CustomItemManager customItemManager,
                        World world,
                        Messenger messenger) {
        this.plugin = plugin;
        this.playerManager = playerManager;
        this.configManager = configManager;
        this.customItemManager = customItemManager;
        this.world = world;
        this.messenger = messenger;
        setPhase(new WaitingPhase(this));
    }

    public Class<? extends Phase> getPhase() {
        return currentPhase.getClass();
    }

    public boolean isInPhase(Class<? extends Phase> phaseClass) {
        return getPhase().equals(phaseClass);
    }

    public void nextPhase() {
        setPhase(currentPhase.getNextPhase());
    }

    private void setPhase(Phase phase) {
        if (currentPhase != null) {
            currentPhase.onDisable();
            HandlerList.unregisterAll(currentPhase);
        }

        currentPhase = phase;
        Bukkit.getPluginManager().registerEvents(currentPhase, plugin);
        currentPhase.onEnable();
    }

    public void forceStart(CommandSender sender) {
        if (!isInPhase(WaitingPhase.class)) {
            sender.sendMessage("Game cannot be force started at the moment!");
            return;
        }
        nextPhase();
    }

    public void forceStop(CommandSender sender) {
        if (!isInPhase(ActivePhase.class)) {
            sender.sendMessage("Game cannot be force stopped at the moment!");
            return;
        }
        nextPhase();
    }

}
