package org.example.game.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Spike {
    private int x;
    private int y;
    private Image spikeImage;

    @JsonCreator
    public Spike(@JsonProperty("x") int x, @JsonProperty("y") int y) {
        this.x = x;
        this.y = y;
    }
    public Image getSpikeImage() {
        return spikeImage;
    }

    public int getX() {
        return x;
    }


    public int getY() {
        return y;
    }


    public boolean isCollision(int playerX, int playerY) {

        return this.x == playerX && this.y == playerY;
    }
}