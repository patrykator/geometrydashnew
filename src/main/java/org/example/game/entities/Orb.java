package org.example.game.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Orb {
    private int x;
    private int y;
    private String color;
    private String direction;
    private Integer teleportX;
    private Integer teleportY;
    private int effectDuration;

    public Orb(){

    }

    @JsonCreator
    public Orb(@JsonProperty("x") int x, @JsonProperty("y") int y, @JsonProperty("color") String color,
               @JsonProperty("direction") String direction, @JsonProperty("teleportX") Integer teleportX,
               @JsonProperty("teleportY") Integer teleportY, @JsonProperty("effectDuration") int effectDuration) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.direction = direction;
        this.teleportX = teleportX;
        this.teleportY = teleportY;
        this.effectDuration = effectDuration;
    }

    public Orb(int x, int y, String color) {
        this.x = x;
        this.y = y;
        this.color = color;
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

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Integer getTeleportX() {
        return teleportX;
    }

    public void setTeleportX(Integer teleportX) {
        this.teleportX = teleportX;
    }


    public Integer getTeleportY() {
        return teleportY;
    }
    @JsonSetter("teleportY")
    public void setTeleportY(Integer teleportY) {
        this.teleportY = teleportY;
    }

    public int getEffectDuration() {
        return effectDuration;
    }
    @JsonSetter("effectDuration")
    public void setEffectDuration(int effectDuration) {
        this.effectDuration = effectDuration;
    }

    public void activate(Player player) {
        boolean isShip = player.isShipMode();

        switch (color) {
            case "yellow" -> {
                player.setVelocityY(isShip ? -player.getPlayerSpeed()
                        : player.isGravityReversed() ? 16 : -16);
            }
            case "purple" -> {
                player.setVelocityY(isShip ? -player.getPlayerSpeed() / 2
                        : player.isGravityReversed() ? 10 : -10);
            }
            case "red" -> {
                player.setVelocityY(-20);
                player.setOrbEffectDuration(4);
                System.out.println("Czerwony orb, velocityY: " + player.getVelocityY());
                player.setJumping(true);
            }
            case "blue" -> {
                player.setGravityReversed(!player.isGravityReversed());
                player.setVelocityY(player.isGravityReversed() ? -16 : 16);
            }
            case "green" -> {
                player.setGravityReversed(!player.isGravityReversed());
                player.setVelocityY(player.isGravityReversed() ? 16 : -16);
                if (!isShip) player.setJumping(true);
            }
            case "black" -> {
                if (isShip) {
                    player.setVelocityY(player.isGravityReversed() ? -16 : 16);
                    player.setOrbEffectActive(true);
                } else {
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
            case "teleport" -> {
                if (teleportX != null && teleportY != null) {
                    player.setX(teleportX);
                    player.setY(teleportY);
                }
            }
            default -> throw new IllegalStateException("Nieznany kolor orba: " + color);
        }

        System.out.println("Orb activated: " + color + ", Ship mode: " + isShip);
    }
}