package org.example.game.entities;

public class Entity {
    private final String name;
    protected double x;
    protected double y;


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