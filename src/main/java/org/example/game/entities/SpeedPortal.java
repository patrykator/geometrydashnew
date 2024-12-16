package org.example.game.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SpeedPortal extends Entity {
    private double speedMultiplier;

    @JsonCreator
    public SpeedPortal(@JsonProperty("x") int x, @JsonProperty("y") int y, @JsonProperty("speedMultiplier") double speedMultiplier) {
        super(x, y);
        this.speedMultiplier = speedMultiplier;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public void setSpeedMultiplier(double speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }
}