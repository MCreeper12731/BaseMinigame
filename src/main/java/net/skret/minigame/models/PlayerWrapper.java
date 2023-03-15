package net.skret.minigame.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.skret.minigame.models.kits.Kit;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PlayerWrapper {

    private final UUID id;
    private Team team;
    private Kit selectedKit;
    private boolean isAlive = true;

    public PlayerWrapper(UUID id, Team team) {
        this.id = id;
        this.team = team;
    }
}
