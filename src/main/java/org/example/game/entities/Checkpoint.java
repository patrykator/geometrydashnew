package org.example.game.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.game.ui.PlayerPanel;
import org.example.game.utilities.Activatable;
import org.example.game.utilities.GameObject;

import java.awt.*;

public class Checkpoint extends GameObject implements Activatable {
    private boolean activated;

    @JsonCreator
    public Checkpoint(@JsonProperty("x") int x, @JsonProperty("y") int y) {
        super(x, y);
        this.activated = false;
    }


    @Override
    public void draw(Graphics2D g2d, PlayerPanel playerPanel) {
        int x = this.getX() * 50;
        int y = this.getY() * 50;

        Image activatedImage = playerPanel.getCheckpointImage();
        Image unactivatedImage = playerPanel.getActivatedCheckpointImage();

        Image imageToDraw = isActivated() ? unactivatedImage : activatedImage;

        if (imageToDraw != null) {
            if (isActivated()) {
                g2d.drawImage(imageToDraw, x, y, 50, 50, null);
            } else {
                g2d.drawImage(imageToDraw, x + 10, y, 30, 50, null);
            }
        } else {
            g2d.setColor(activated ? Color.GREEN : Color.GRAY);
            g2d.fillRect(x, y, 50, 50);
        }

        if (playerPanel.isShowHitboxes()) {
            g2d.setColor(Color.RED);
            g2d.drawRect(x, y, 50, 50);
        }
    }

    @Override
    public void update() {

    }

    @Override
    public void activate(Player player) {
        if (!activated) {
            this.activated = true;
            player.setCheckpointX(this.getX());
            player.setCheckpointY(this.getY());
        }
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
}