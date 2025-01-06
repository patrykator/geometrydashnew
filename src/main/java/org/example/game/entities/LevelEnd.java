package org.example.game.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.game.ui.PlayerPanel;
import org.example.game.utilities.GameObject;

import java.awt.*;

public class LevelEnd extends GameObject {

    @JsonCreator
    public LevelEnd(@JsonProperty("x") int x, @JsonProperty("y") int y) {
        super(x, y);
    }

    @Override
    public void draw(Graphics2D g2d, PlayerPanel playerPanel) {
        if (playerPanel.getPlayer().isPlatformer()) {
            g2d.setColor(Color.GREEN);
            g2d.fillRect(this.getX() * 50, this.getY() * 50, 50, 50);
        }
    }

    @Override
    public void update() {

    }
}