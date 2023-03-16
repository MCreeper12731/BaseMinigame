package net.skret.minigame.map;

import org.bukkit.World;

//Amazing map reset system by Jordan Osterberg https://www.youtube.com/watch?v=sVs5OC3RQEM
public interface GameMap {

    void load();
    void unload();
    void restoreFromSource();

    boolean isLoaded();
    World getWorld();

}
