package org.example.game.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.game.ui.PlayerPanel;
import org.example.game.utilities.GameObject;

import java.awt.*;

public class Portal extends GameObject {
    private final GameMode targetGameMode;

    @JsonCreator
    public Portal(@JsonProperty("x") int x, @JsonProperty("y") int y, @JsonProperty("targetGameMode") GameMode targetGameMode) {
        super(x, y);
        this.targetGameMode = targetGameMode;
    }

    public GameMode getTargetGameMode() {
        return targetGameMode;
    }

    @Override
    public void draw(Graphics2D g2d, PlayerPanel playerPanel) {
        double x = this.getX() * 50;
        double y = this.getY() * 50;

        var portalImages = playerPanel.getPortalImages();
        Image portalImage = portalImages.get(this.getTargetGameMode());
        if (portalImage != null) {
            g2d.drawImage(portalImage, (int) x, (int) y, 50, 100, null);
        } else {
            switch (this.getTargetGameMode()) {
                case CUBE:
                    g2d.setColor(Color.GREEN);
                    break;
                case SHIP:
                    g2d.setColor(Color.BLUE);
                    break;
                case BALL:
                    g2d.setColor(Color.YELLOW);
                    break;
                case UFO:
                    g2d.setColor(Color.MAGENTA);
                    break;
                case WAVE:
                    g2d.setColor(Color.CYAN);
                    break;
                case ROBOT:
                    g2d.setColor(Color.PINK);
                    break;
                case SPIDER:
                    g2d.setColor(Color.ORANGE);
                    break;
                default:
                    g2d.setColor(Color.GRAY);
            }
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