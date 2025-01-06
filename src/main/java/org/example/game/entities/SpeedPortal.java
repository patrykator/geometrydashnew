package org.example.game.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.game.ui.PlayerPanel;
import org.example.game.utilities.GameObject;

import java.awt.*;

public class SpeedPortal extends GameObject {
    private final double speedMultiplier;

    @JsonCreator
    public SpeedPortal(@JsonProperty("x") int x, @JsonProperty("y") int y, @JsonProperty("speedMultiplier") double speedMultiplier) {
        super(x, y);
        this.speedMultiplier = speedMultiplier;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    @Override
    public void draw(Graphics2D g2d, PlayerPanel playerPanel) {
        double x = this.getX() * 50;
        double y = this.getY() * 50;

        double speedMultiplier = this.getSpeedMultiplier();

        Image portalImage = null;
        var portalImages = playerPanel.getSpeedPortalImages();
        if (speedMultiplier == 0.807) {
            portalImage = portalImages.get(GameMode.SLOW);
        } else if (speedMultiplier == 1.0) {
            portalImage = portalImages.get(GameMode.NORMAL);
        } else if (speedMultiplier == 1.243) {
            portalImage = portalImages.get(GameMode.FAST);
        } else if (speedMultiplier == 1.502) {
            portalImage = portalImages.get(GameMode.VERY_FAST);
        } else if (speedMultiplier == 1.849) {
            portalImage = portalImages.get(GameMode.EXTREMELY_FAST);
        }

        if (portalImage != null) {
            g2d.drawImage(portalImage, (int) x, (int) y, 50, 100, null);
        } else {
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect((int) x, (int) y, 50, 100);
        }

        if (playerPanel.isShowHitboxes()) {
            g2d.setColor(Color.RED);
            g2d.drawRect((int) x, (int) y, 50, 100);
        }
    }

    @Override
    public void update() {

    }
}