package org.example.game.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.game.ui.PlayerPanel;
import org.example.game.utilities.GameObject;

import java.awt.*;

public class Spike extends GameObject {
    private final String position;

    @JsonCreator
    public Spike(@JsonProperty("x") int x, @JsonProperty("y") int y, @JsonProperty("position") String position) {
        super(x, y);
        this.position = position;
    }

    public int x() {
        return super.getX();
    }

    public int y() {
        return super.getY();
    }

    public String getPosition() {
        return position;
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Graphics2D g2d, PlayerPanel playerPanel) {
        int x = this.x * 50;
        int y = this.y * 50;

        Image spikeImage = playerPanel.getSpikeImage();

        if (spikeImage != null) {
            if ("bottom".equals(this.getPosition())) {
                g2d.drawImage(spikeImage, x, y + 50, 50, -50, null);
            } else {
                g2d.drawImage(spikeImage, x, y, 50, 50, null);
            }
        } else {
            g2d.setColor(Color.RED);
            g2d.fillRect(x, y, 50, 50);
        }

        if (playerPanel.isShowHitboxes()) {
            g2d.setColor(Color.GREEN);
            if ("bottom".equals(this.getPosition())) {
                g2d.drawPolygon(new int[]{x, x + 25, x + 50}, new int[]{y, y + 50, y}, 3);
            } else {
                g2d.drawPolygon(new int[]{x, x + 25, x + 50}, new int[]{y + 50, y, y + 50}, 3);
            }
            g2d.setColor(Color.RED);
            g2d.drawRect(x + 19, y + 15, 12, 20);
        }
    }
}