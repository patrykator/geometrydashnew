package org.example.game.utilities;

import org.example.game.ui.PlayerPanel;

import java.awt.Graphics2D;

public interface Drawable {
    void draw(Graphics2D g2d, PlayerPanel playerPanel);
}