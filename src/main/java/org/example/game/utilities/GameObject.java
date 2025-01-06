package org.example.game.utilities;

import org.example.game.ui.PlayerPanel;

import java.awt.*;

public abstract class GameObject implements Drawable {
    protected int x;
    protected int y;

    public GameObject(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public abstract void update();

    public abstract void draw(Graphics2D g2d, PlayerPanel playerPanel);

}