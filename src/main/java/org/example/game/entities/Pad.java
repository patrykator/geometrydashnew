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
    private long lastActivatedTime; // Dodajemy zmienną przechowującą czas ostatniej aktywacji
    private static final long COOLDOWN_TIME = 200; // Czas cooldownu w milisekundach (0.1 sekundy)

    public Pad() {
        this.lastActivatedTime = 0;
    }

    @JsonCreator
    public Pad(@JsonProperty("x") int x, @JsonProperty("y") int y, @JsonProperty("color") String color,
               @JsonProperty("position") String position, @JsonProperty("direction") String direction) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.position = position;
        this.direction = direction; // Teraz direction jest uwzględniane przy tworzeniu pada
        this.lastActivatedTime = 0;
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
        // Sprawdzamy, czy minął czas cooldownu
        if (System.currentTimeMillis() - lastActivatedTime < COOLDOWN_TIME) {
            return;
        }

        boolean isShip = player.isShipMode();
        boolean isUfo = player.isUfoMode();

        switch (color) {
            case "yellow":
            case "purple":
            case "red":
                handleYellowPurpleRedPads(player, isShip, isUfo);
                break;
            case "blue":
                handleBluePad(player, isShip);
                // Aktualizujemy czas ostatniej aktywacji tylko dla niebieskiego pada
                lastActivatedTime = System.currentTimeMillis();
                break;
            case "spider":
                // Dla spider pada, aktywacja zależy od kierunku
                handleSpiderPad(player);
                break;
            default:
                throw new IllegalStateException("Nieznany kolor pada: " + color);
        }
    }

    private void handleYellowPurpleRedPads(Player player, boolean isShip, boolean isUfo) {
        double velocityY;

        switch (color) {
            case "yellow":
                velocityY = isShip ? 13 : isUfo ? 15 : 16;
                break;
            case "purple":
                velocityY = isShip ? 10 : isUfo ? 12 : 10;
                break;
            case "red":
                velocityY = isShip ? 16 : isUfo ? 18 : 20;
                break;
            default:
                throw new IllegalStateException("Nieznany kolor pada: " + color);
        }

        if (direction.equals("down")) {
            velocityY = -velocityY;
        }

        player.setVelocityY(player.isGravityReversed() ? velocityY : -velocityY);

        if (isShip || isUfo) {
            player.setOrbEffectActive(true);
            player.setOrbEffectDuration(isShip ? (color.equals("red") ? 15 : 10) : (color.equals("red") ? 4 : 0));
        } else {
            player.setJumping(true);
        }
    }

    private void handleBluePad(Player player, boolean isShip) {
        player.setGravityReversed(!player.isGravityReversed());
        player.setVelocityY(player.isGravityReversed() ? - (isShip ? 7 : 16) : (isShip ? 7 : 16));
    }

    private void handleSpiderPad(Player player) {
        if ("up".equals(direction)) {
            player.setGravityReversed(true);
        } else if ("down".equals(direction)) {
            player.setGravityReversed(false);
        }
        player.setTeleportPad(true);
        player.setSpiderOrbActivated(true);
    }
}