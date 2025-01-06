package org.example.game.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.example.game.ui.PlayerPanel;
import org.example.game.utilities.Activatable;
import org.example.game.utilities.GameObject;

import java.awt.*;

public class Pad extends GameObject implements Activatable {
    private final String color;
    private String direction;
    private String position;
    private long lastActivatedTime;
    private static final long COOLDOWN_TIME = 200;

    @JsonCreator
    public Pad(@JsonProperty("x") int x, @JsonProperty("y") int y, @JsonProperty("color") String color,
               @JsonProperty("position") String position, @JsonProperty("direction") String direction) {
        super(x, y);
        this.color = color;
        this.position = position;
        this.direction = direction;
        this.lastActivatedTime = 0;
    }

    @Override
    public void draw(Graphics2D g2d, PlayerPanel playerPanel) {
        int x = this.getX() * 50;
        int y = this.getY() * 50;
        String padKey = this.getColor().toLowerCase();

        var padImages = playerPanel.getPadImages();

        Image padImage = padImages.get(padKey);
        if (padImage != null) {
            if ("bottom".equals(this.getPosition())) {
                g2d.drawImage(padImage, x, y + 20, 50, -20, null);
            } else {
                g2d.drawImage(padImage, x, y + 30, 50, 20, null);
            }
        } else {
            g2d.setColor(Color.MAGENTA);
            g2d.fillRect(x + 12, y + 14, 36, 36);
        }

        if (playerPanel.isShowHitboxes()) {
            g2d.setColor(Color.RED);
            g2d.drawRect(x, y, 50, 50);
        }
    }

    public String getColor() {
        return color;
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

    @Override
    public void activate(Player player) {
        if (isCooldownActive()) {
            return;
        }

        switch (color) {
            case "yellow":
            case "purple":
            case "red":
                handleYellowPurpleRedPads(player);
                break;
            case "blue":
                handleBluePad(player);
                break;
            case "spider":
                handleSpiderPad(player);
                break;
            default:
                throw new IllegalStateException("Nieznany kolor pada: " + color);
        }
    }

    private boolean isCooldownActive() {
        return System.currentTimeMillis() - lastActivatedTime < COOLDOWN_TIME;
    }

    private void handleYellowPurpleRedPads(Player player) {
        double velocityY = calculateVelocity(player);

        if (player.isShipMode() || player.isUfoMode()) {
            player.setOrbEffectActive(true);
            player.setOrbEffectDuration(calculateEffectDuration(player));
        } else {
            if (color.equals("red")) {
                velocityY = 20;
                player.setOrbEffectDuration(4);
            }
            player.setJumping(true);
        }

        player.setVelocityY(player.isGravityReversed() ? velocityY : -velocityY);
        lastActivatedTime = System.currentTimeMillis();
    }

    private void handleSpiderPad(Player player) {
        player.setGravityReversed(!player.isGravityReversed());
        player.setSpiderOrbActivated(true);
        player.setTeleportPad(true);
        lastActivatedTime = System.currentTimeMillis();
    }

    private double calculateVelocity(Player player) {
        boolean isShip = player.isShipMode();
        boolean isUfo = player.isUfoMode();

        double velocityY = switch (color) {
            case "yellow" -> isShip ? 13 : isUfo ? 15 : 16;
            case "purple" -> isShip ? 10 : isUfo ? 12 : 10;
            case "red" -> isShip ? 16 : isUfo ? 18 : 20;
            default -> throw new IllegalStateException("Nieznany kolor pada: " + color);
        };

        return direction.equals("down") ? -velocityY : velocityY;
    }

    private int calculateEffectDuration(Player player) {
        if (player.isShipMode()) {
            return color.equals("red") ? 15 : 10;
        } else if (player.isUfoMode()) {
            return color.equals("red") ? 4 : 0;
        } else {
            return 0;
        }
    }

    private void handleBluePad(Player player) {
        player.setGravityReversed(!player.isGravityReversed());
        player.setVelocityY(player.isGravityReversed() ? -(player.isShipMode() ? 7 : 16) : (player.isShipMode() ? 7 : 16));
        lastActivatedTime = System.currentTimeMillis();
    }


    @Override
    public void update() {

    }
}