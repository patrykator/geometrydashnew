package org.example.game.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Portal extends Entity {
    private GameMode targetGameMode;

    @JsonCreator
    public Portal(@JsonProperty("x") int x, @JsonProperty("y") int y, @JsonProperty("targetGameMode") GameMode targetGameMode) {
        super(x, y);
        this.targetGameMode = targetGameMode;
    }

    public GameMode getTargetGameMode() {
        return targetGameMode;
    }

    public void setTargetGameMode(GameMode targetGameMode) {
        this.targetGameMode = targetGameMode;
    }
}