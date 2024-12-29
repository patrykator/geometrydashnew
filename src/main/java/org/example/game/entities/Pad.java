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
        boolean isShip = player.isShipMode();

        switch (color) {
            case "yellow" -> {
                if (isShip) {
                    player.setVelocityY(player.isGravityReversed() ? 13 : -13);
                    player.setOrbEffectActive(true);
                    player.setOrbEffectDuration(10);
                } else {
                    player.setVelocityY(player.isGravityReversed() ? 16 : -16);
                    player.setJumping(true);
                }
            }
            case "purple" -> {
                if (isShip) {
                    player.setVelocityY(player.isGravityReversed() ? 10 : -10);
                    player.setOrbEffectActive(true);
                    player.setOrbEffectDuration(3);
                } else {
                    player.setVelocityY(player.isGravityReversed() ? 10 : -10);
                    player.setJumping(true);
                }
            }
            case "red" -> {
                if (isShip) {
                    // Zmiana: Nadaj prędkość w górę, nie teleportuj
                    player.setVelocityY(player.isGravityReversed() ? 16 : -16);
                    player.setRedOrbVelocity(player.getVelocityY());
                    player.setOrbEffectDuration(15);
                    player.setOrbEffectActive(true);
                    System.out.println("Czerwony orb, velocityY: " + player.getVelocityY());
                    player.setJumping(true);
                } else {
                    player.setVelocityY(player.isGravityReversed() ? 20 : -20);
                    player.setOrbEffectDuration(4);
                    System.out.println("Czerwony orb, velocityY: " + player.getVelocityY());
                    player.setJumping(true);
                }
            }
            case "blue" -> {
                if (isShip) {
                    player.setGravityReversed(!player.isGravityReversed());
                    player.setVelocityY(player.isGravityReversed() ? -7 : 7);
                } else {
                    player.setGravityReversed(!player.isGravityReversed());
                    player.setVelocityY(player.isGravityReversed() ? -16 : 16);
                }
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