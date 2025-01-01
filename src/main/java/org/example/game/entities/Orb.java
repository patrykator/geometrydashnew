package org.example.game.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.example.game.engine.GameEngine;

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

    public Orb(int x, int y, String color, String direction) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.direction = direction;
        this.teleportX = null;
        this.teleportY = null;
        this.effectDuration = 0;
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
        boolean isUfo = player.isUfoMode();
        boolean isBall = player.isBallMode();

        switch (color) {
            case "yellow" -> {
                if (isShip) {
                    // Zmiana: Nadaj prędkość w górę, nie teleportuj
                    player.setVelocityY(player.isGravityReversed() ? 13 : -13);
                    player.setOrbEffectActive(true);
                    player.setOrbEffectDuration(10);
                } else if (isUfo) {
                    player.setVelocityY(player.isGravityReversed() ? 15 : -15);
                    player.setOrbEffectActive(true);
                    player.setOrbEffectDuration(6);
                } else {
                    player.setVelocityY(player.isGravityReversed() ? 16 : -16);
                }
            }
            case "purple" -> {
                if (isShip) {
                    // Zmiana: Nadaj prędkość w górę, nie teleportuj
                    player.setVelocityY(player.isGravityReversed() ? 10 : -10);
                    player.setOrbEffectActive(true);
                    player.setOrbEffectDuration(10);
                } else if (isUfo) {
                    player.setVelocityY(player.isGravityReversed() ? 12 : -12);
                    player.setOrbEffectActive(true);
                    player.setOrbEffectDuration(3);
                } else {
                    player.setVelocityY(player.isGravityReversed() ? 10 : -10);
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
                } else if (isUfo) {
                    player.setVelocityY(player.isGravityReversed() ? 18 : -18);
                    player.setRedOrbVelocity(player.getVelocityY());
                    player.setOrbEffectDuration(9);
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
            case "green" -> {
                if (isShip) {
                    player.setVelocityY(player.isGravityReversed() ? 10 : -10);
                    player.setOrbEffectActive(true);
                    player.setOrbEffectDuration(4);
                    player.setGravityReversed(!player.isGravityReversed());
                } else if (isUfo) {
                    player.setVelocityY(player.isGravityReversed() ? 12 : -12);
                    player.setOrbEffectActive(true);
                    player.setOrbEffectDuration(4);
                    player.setGravityReversed(!player.isGravityReversed());
                } else {
                    player.setJumping(true);
                    player.setGravityReversed(!player.isGravityReversed());
                }
                player.setVelocityY(player.isGravityReversed() ? 16 : -16);
            }
            case "black" -> {
                if (isShip) {
                    player.setOrbEffectDuration(100);
                    player.setTargetOrbVelocity(player.isGravityReversed() ? -16 : 16);
                    player.setOrbEffectActive(true);
                    player.setVelocityY(player.isGravityReversed() ? -16 : 16);
                } else if (isBall) {
                    player.setVelocityY(player.isGravityReversed() ? 16 : -16);
                    player.setGravityReversed(!player.isGravityReversed());
                } else if (isUfo) {
                    // Zmiana dla UFO: Ustaw docelową prędkość, a nie sztywną wartość
                    player.setOrbEffectDuration(100);
                    player.setOrbEffectActive(true);
                    player.setTargetOrbVelocity(player.isGravityReversed() ? -18 : 18);

                    player.setVelocityY(player.isGravityReversed() ? -18 : 18);
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
                player.setSpiderOrbJustActivated(true); // Ustaw flagę w Player
                player.setJumping(false); // To może być zbędne, jeśli flaga działa
                // poprawnie
                player.setInputBlockedAfterSpiderOrb(true);
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