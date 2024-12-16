package org.example.game.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Pad {
    private int x;
    private int y;
    private String color;
    private String direction;
    private String position;


    public Pad() {
    }

    @JsonCreator
    public Pad(@JsonProperty("x") int x, @JsonProperty("y") int y, @JsonProperty("color") String color,
               @JsonProperty("position") String position, @JsonProperty("direction") String direction) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.position = position;
        this.direction = direction;
    }

    public Pad(int x, int y, String color, String position) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.position = position;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getColor() {
        return color;
    }

    public String getDirection() {
        return direction;
    }

    public String getPosition() {
        return position;
    }

    @JsonSetter("direction")
    public void setDirection(String direction) {
        this.direction = direction;
    }

    @JsonSetter("position")
    public void setPosition(String position) {
        this.position = position;
    }


    public void activate(Player player) {
        switch (color) {
            case "yellow" -> {
                player.setVelocityY(player.isGravityReversed() ? 16 : -16);
                player.setJumping(true);
            }
            case "purple" -> {
                player.setVelocityY(player.isGravityReversed() ? 10 : -10);
                player.setJumping(true);
            }
            case "red" -> {
                player.setOrbEffectDuration(4);
                player.setVelocityY(player.isGravityReversed() ? 20 : -20);
                player.setJumping(true);
            }
            case "blue" -> {
                player.setGravityReversed(!player.isGravityReversed());
                player.setVelocityY(player.isGravityReversed() ? -16 : 16);
            }
            case "spider" -> {
                if ("up".equals(direction)) {
                    player.setGravityReversed(true);
                } else if ("down".equals(direction)) {
                    player.setGravityReversed(false);
                }
                player.setTeleport(true);
                player.setSpiderOrbActivated(true);
            }
            default -> throw new IllegalStateException("Nieznany kolor pada: " + color);
        }
    }
}