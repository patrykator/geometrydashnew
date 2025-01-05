package org.example.game.entities;

public class Entity {
    private String name;
    protected double x;
    protected double y;


    public Entity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Entity(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }


    public double getX() {
        return x;
    }

    public void setX(double x) {

        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}