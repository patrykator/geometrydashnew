package org.example.game.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.example.game.ui.PlayerPanel;
import org.example.game.utilities.Activatable;
import org.example.game.utilities.GameObject;

import java.awt.*;

public class Orb extends GameObject implements Activatable {
    private final String color;
    private String direction;
    private Integer teleportX;
    private Integer teleportY;

    @JsonCreator
    public Orb(@JsonProperty("x") int x, @JsonProperty("y") int y, @JsonProperty("color") String color,
               @JsonProperty("direction") String direction, @JsonProperty("teleportX") Integer teleportX,
               @JsonProperty("teleportY") Integer teleportY) {
        super(x, y);
        this.color = color;
        this.direction = direction;
        this.teleportX = teleportX;
        this.teleportY = teleportY;

    }

    public Orb(int x, int y, String color) {
        super(x, y);
        this.color = color;
    }

    public Orb(int x, int y, String color, String direction) {
        super(x, y);
        this.color = color;
        this.direction = direction;
        this.teleportX = null;
        this.teleportY = null;
    }

    public String getColor() {
        return color;
    }

    public String getDirection() {
        return direction;
    }

    @JsonSetter("teleportY")
    public void setTeleportY(Integer teleportY) {
        this.teleportY = teleportY;
    }

    @Override
    public void activate(Player player) {
        switch (color) {
            case "yellow":
                handleYellowOrb(player);
                break;
            case "purple":
                handlePurpleOrb(player);
                break;
            case "red":
                handleRedOrb(player);
                break;
            case "blue":
                handleBlueOrb(player);
                break;
            case "green":
                handleGreenOrb(player);
                break;
            case "black":
                handleBlackOrb(player);
                break;
            case "spider":
                handleSpiderOrb(player);
                break;
            case "teleport":
                handleTeleportOrb(player);
                break;
            default:
                throw new IllegalStateException("Nieznany kolor orba: " + color);
        }
    }

    private void handleYellowOrb(Player player) {
        if (player.isShipMode()) {
            player.setVelocityY(player.isGravityReversed() ? 13 : -13);
            player.setOrbEffectActive(true);
            player.setOrbEffectDuration(10);
        } else if (player.isUfoMode()) {
            player.setVelocityY(player.isGravityReversed() ? 15 : -15);
            player.setOrbEffectActive(true);
            player.setOrbEffectDuration(6);
        } else {
            player.setVelocityY(player.isGravityReversed() ? 16 : -16);
        }
    }

    private void handlePurpleOrb(Player player) {
        if (player.isShipMode()) {
            player.setVelocityY(player.isGravityReversed() ? 10 : -10);
            player.setOrbEffectActive(true);
            player.setOrbEffectDuration(10);
        } else if (player.isUfoMode()) {
            player.setVelocityY(player.isGravityReversed() ? 12 : -12);
            player.setOrbEffectActive(true);
            player.setOrbEffectDuration(3);
        } else {
            player.setVelocityY(player.isGravityReversed() ? 10 : -10);
        }
    }

    private void handleRedOrb(Player player) {
        if (player.isShipMode()) {
            player.setVelocityY(player.isGravityReversed() ? 16 : -16);
            player.setOrbEffectDuration(15);
        } else if (player.isUfoMode()) {
            player.setVelocityY(player.isGravityReversed() ? 18 : -18);
            player.setOrbEffectDuration(9);
        } else {
            player.setVelocityY(player.isGravityReversed() ? 20 : -20);
            player.setOrbEffectDuration(4);
        }
        player.setOrbEffectActive(true);
        player.setJumping(true);
    }

    private void handleBlueOrb(Player player) {
        if (player.isShipMode()) {
            player.setGravityReversed(!player.isGravityReversed());
            player.setVelocityY(player.isGravityReversed() ? -7 : 7);
        } else {
            player.setGravityReversed(!player.isGravityReversed());
            player.setVelocityY(player.isGravityReversed() ? -16 : 16);
        }
    }

    private void handleGreenOrb(Player player) {
        if (player.isShipMode()) {
            player.setVelocityY(player.isGravityReversed() ? 10 : -10);
            player.setOrbEffectActive(true);
            player.setOrbEffectDuration(4);
        } else if (player.isUfoMode()) {
            player.setVelocityY(player.isGravityReversed() ? 12 : -12);
            player.setOrbEffectActive(true);
            player.setOrbEffectDuration(4);
        } else {
            player.setJumping(true);
        }
        player.setGravityReversed(!player.isGravityReversed());
        player.setVelocityY(player.isGravityReversed() ? 16 : -16);
    }

    private void handleBlackOrb(Player player) {
        if (player.isShipMode()) {
            player.setOrbEffectDuration(100);
            player.setTargetOrbVelocity(player.isGravityReversed() ? -16 : 16);
            player.setVelocityY(player.isGravityReversed() ? -16 : 16);
        } else if (player.isBallMode()) {
            player.setVelocityY(player.isGravityReversed() ? 16 : -16);
            player.setGravityReversed(!player.isGravityReversed());
        } else if (player.isUfoMode()) {
            player.setOrbEffectDuration(100);
            player.setBlackOrbActive(true);
            player.setTargetOrbVelocity(player.isGravityReversed() ? -18 : 18);
            player.setVelocityY(player.isGravityReversed() ? -18 : 18);
        } else {
            player.setVelocityY(player.isGravityReversed() ? -16 : 16);
        }
        player.setOrbEffectActive(true);
    }

    private void handleSpiderOrb(Player player) {
        player.setTeleport(true);
        player.setSpiderOrbActivated(true);
        player.setSpiderOrbJustActivated(true);
    }

    private void handleTeleportOrb(Player player) {
        player.setX(teleportX);
        player.setY(teleportY);
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

    @Override
    public void draw(Graphics2D g2d, PlayerPanel playerPanel) {
        int x = this.getX() * 50;
        int y = this.getY() * 50;
        String orbKey = this.getColor().toLowerCase();
        if ("spider".equals(this.getColor())) orbKey += "_" + this.getDirection();

        var orbImages = playerPanel.getOrbImages();

        Image orbImage = orbImages.get(orbKey);
        if (orbImage != null) {
            g2d.drawImage(orbImage, x + 7, y + 7, 36, 36, null);
        } else {
            g2d.setColor(Color.YELLOW);
            g2d.fillOval(x + 12, y + 12, 36, 36);
        }

        if (playerPanel.isShowHitboxes()) {
            g2d.setColor(Color.RED);
            g2d.drawRect(x, y, 50, 50);
        }
    }

    @Override
    public void update() {

    }
}