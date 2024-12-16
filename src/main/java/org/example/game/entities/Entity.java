package org.example.game.entities;

public class Entity {
    private int id;
    private String name;
    private String description;
    private String image;
    private String type;
    private int level;
    private int health;
    protected double x; // Zmień na double
    protected double y; // Zmień na double


    public Entity(int id, String name, String description, String image, String type, int level, int health, int x, int y) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.type = type;
        this.level = level;
        this.health = health;
        this.x = x;
        this.y = y;
    }

    public Entity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Entity(int id, String name, String description, String image, String type, int level, int health) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.type = type;
        this.level = level;
        this.health = health;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
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

    public void display() {
        System.out.println("Entity ID: " + id);
        System.out.println("Entity Name: " + name);
        System.out.println("Entity Description: " + description);
        System.out.println("Entity Image: " + image);
        System.out.println("Entity Type: " + type);
        System.out.println("Entity Level: " + level);
    }

    public void attack() {
        System.out.println("Entity is attacking");
    }

    public void move() {
        System.out.println("Entity is moving");
    }

    public void die() {
        System.out.println("Entity is dead");
        health = 0;
    }
}