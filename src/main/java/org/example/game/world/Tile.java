package org.example.game.world;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.game.ui.PlayerPanel;
import org.example.game.utilities.GameObject;

import java.awt.*;

public class Tile extends GameObject {
    private final boolean isSolid;

    @JsonCreator
    public Tile(@JsonProperty("x") int x, @JsonProperty("y") int y, @JsonProperty("isSolid") boolean isSolid) {
        super(x, y);
        this.isSolid = isSolid;
    }

    @Override
    public void draw(Graphics2D g2d, PlayerPanel playerPanel) {
        Rectangle tileBounds = new Rectangle(this.getX() * 50, this.getY() * 50, 50, 50);

        if (playerPanel.getTileImage() != null) {
            g2d.drawImage(playerPanel.getTileImage(), tileBounds.x, tileBounds.y, tileBounds.width, tileBounds.height, null);
        } else {
            g2d.setColor(Color.GREEN);
            g2d.fillRect(tileBounds.x, tileBounds.y, tileBounds.width, tileBounds.height);
        }

        if (playerPanel.isShowHitboxes()) {
            g2d.setColor(Color.RED);
            g2d.drawRect(tileBounds.x, tileBounds.y, tileBounds.width, tileBounds.height);
        }
    }

    @JsonProperty("isSolid")
    public boolean isSolid() {
        return isSolid;
    }

    @Override
    public void update() {

    }
}