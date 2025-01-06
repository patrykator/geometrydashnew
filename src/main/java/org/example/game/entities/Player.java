package org.example.game.entities;

import org.example.game.ui.MainWindow;
import org.example.game.ui.PlayerPanel;
import org.example.game.utilities.Drawable;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Player extends Entity implements Drawable {
    private boolean isJumping;
    private double rotationAngle;
    private int orbEffectDuration = 0;
    private boolean gravityReversed = false;
    private boolean teleport = false;
    private boolean spiderOrbActivated = false;
    private double x;
    private double y;
    private double velocityY;
    private double playerSpeed = 5.0;
    private double targetOrbVelocity;
    private boolean spiderOrbJustActivated;
    private boolean robotFlipped = false;
    private boolean blackOrbActive = false;
    private boolean canJumpAfterCollision = true;
    private boolean teleportPad = false;
    private static int staticY;
    private static int staticX;
    private boolean orbEffectActive = false;
    private boolean isShipFlipped;
    private int orbEffectActiveDuration;
    private boolean isPlatformer = false;
    private boolean dead;
    private MainWindow mainWindow;
    private Double checkpointX;
    private Double checkpointY;

    public boolean isDead() {
        return !dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public Player(String name, int x, int y) {
        super(name);
        this.velocityY = 0;
        this.isJumping = false;
        this.rotationAngle = 0;
        this.x = x;
        this.y = y;
    }

    public void setMainWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    public static void setStaticY(int y) {
        Player.staticY = y;
    }

    public Double getCheckpointX() {
        return checkpointX;
    }

    public void setCheckpointX(double checkpointX) {
        this.checkpointX = checkpointX;
    }

    public Double getCheckpointY() {
        return checkpointY;
    }

    public void setCheckpointY(double checkpointY) {
        this.checkpointY = checkpointY;
    }

    public void resetCheckpoint() {
        this.checkpointX = null;
        this.checkpointY = null;
    }

    public static int getStaticY() {
        return staticY;
    }

    public boolean canJumpAfterCollision() {
        return canJumpAfterCollision;
    }
    public void setCanJumpAfterCollision(boolean canJumpAfterCollision) {
        this.canJumpAfterCollision = canJumpAfterCollision;
    }

    private GameMode currentGameMode = GameMode.CUBE;

    public double getTargetOrbVelocity() {
        return targetOrbVelocity;
    }

    public void setTargetOrbVelocity(double targetOrbVelocity) {
        this.targetOrbVelocity = targetOrbVelocity;
    }

    public boolean isSpiderOrbJustActivated() {
        return spiderOrbJustActivated;
    }

    public void setSpiderOrbJustActivated(boolean spiderOrbJustActivated) {
        this.spiderOrbJustActivated = spiderOrbJustActivated;
    }

    public synchronized void setCurrentGameMode(GameMode gameMode) {
        this.currentGameMode = gameMode;
    }

    public synchronized GameMode getCurrentGameMode() {
        return this.currentGameMode;
    }

    public boolean isShipMode() {
        return currentGameMode == GameMode.SHIP;
    }

    public boolean isBallMode() {
        return currentGameMode == GameMode.BALL;
    }

    public boolean isShipFlipped() {
        return isShipFlipped;
    }

    public void setShipFlipped(boolean shipFlipped) {
        this.isShipFlipped = shipFlipped;
    }

    public void decrementOrbEffectActiveDuration() {
        if (orbEffectActiveDuration > 0) {
            orbEffectActiveDuration--;
        }
    }

    public boolean isOrbEffectActive() {
        return orbEffectActive;
    }

    public void setOrbEffectActive(boolean isActive) {
        orbEffectActive = isActive;
    }

    public static double getStaticX() {
        return staticX;
    }

    public static void setStaticX(int staticX) {
        Player.staticX = staticX;
    }

    public boolean isPlatformer() {
        return isPlatformer;
    }

    public void setPlatformer(boolean isPlatformer) {
        this.isPlatformer = isPlatformer;
    }

    public boolean isSpiderOrbActivated() {
        return spiderOrbActivated;
    }

    public void setSpiderOrbActivated(boolean spiderOrbActivated) {
        this.spiderOrbActivated = spiderOrbActivated;
    }

    public boolean isGravityReversed() {
        return gravityReversed;
    }

    public void setGravityReversed(boolean gravityReversed) {
        this.gravityReversed = gravityReversed;
    }

    public int getOrbEffectDuration() {
        return orbEffectDuration;
    }

    public void setOrbEffectDuration(int orbEffectDuration) {
        this.orbEffectDuration = orbEffectDuration;
    }

    public int getOriginalMinJumpSpeed() {
        return -16;
    }

    public void setVelocityY(int velocityY) {
        this.velocityY = velocityY;
    }

    public boolean isJumping() {
        return isJumping;
    }

    public void setJumping(boolean jumping) {
        isJumping = jumping;
    }

    public double getRotationAngle() {
        return rotationAngle;
    }

    public void setRotationAngle(double rotationAngle) {
        this.rotationAngle = rotationAngle;
    }

    public double getPlayerSpeed() {
        return playerSpeed;
    }

    public double getDefaultPlayerSpeed(){
        return 5.0;
    }

    public void setPlayerSpeed(double speed) {
        this.playerSpeed = speed;
    }

    public boolean isTeleport() {
        return teleport;
    }

    public void setTeleport(boolean teleport) {
        this.teleport = teleport;
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

    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public boolean isUfoMode() {
        return currentGameMode == GameMode.UFO;
    }

    public void decrementOrbEffectDuration() {
        if (orbEffectDuration > 0) {
            orbEffectDuration--;
        }
    }

    public boolean isRobotFlipped() {
        return robotFlipped;
    }

    public void setRobotFlipped(boolean b) {
        robotFlipped = b;
    }

    public boolean isBlackOrbActive() {
        return blackOrbActive;
    }

    public void setBlackOrbActive(boolean b) {
        blackOrbActive = b;
    }

    public void setTeleportPad(boolean b) {
        teleportPad = b;
    }

    public boolean isTeleportPad() {
        return teleportPad;
    }
    @Override
    public void draw(Graphics2D g2d, PlayerPanel playerPanel) {
        if (this.isDead()) {
            double x = (int) this.getX();
            double y = (int) this.getY();

            AffineTransform originalTransform = g2d.getTransform();

            g2d.translate(x + 25, y + 25);

            g2d.rotate(Math.toRadians(this.getRotationAngle()));

            g2d.translate(-25, -25);

            if (this.getCurrentGameMode() == GameMode.UFO) {
                if (playerPanel.getUfoImage() != null) {
                    if (this.isGravityReversed()) {
                        g2d.drawImage(playerPanel.getUfoImage(), 0, 50, 50, -50, null);
                    } else {
                        g2d.drawImage(playerPanel.getUfoImage(), 0, 0, 50, 50, null);
                    }
                } else {
                    g2d.setColor(Color.MAGENTA);
                    g2d.fillRect(0, 0, 50, 50);
                }
            } else if (this.getCurrentGameMode() == GameMode.BALL) {
                if (playerPanel.getBallModeImage() != null) {
                    g2d.drawImage(playerPanel.getBallModeImage(), 0, 0, 50, 50, null);
                } else {
                    g2d.setColor(Color.YELLOW);
                    g2d.fillOval(0, 0, 50, 50);
                }
            } else if (this.getCurrentGameMode() == GameMode.SHIP) {
                if (this.isPlatformer() && playerPanel.getShipImagePlatformer() != null) {
                    if (this.isShipFlipped() && this.isGravityReversed()) {
                        g2d.drawImage(playerPanel.getShipImagePlatformer(), 50, 50, -50, -50, null);
                    } else if (this.isShipFlipped()) {
                        g2d.drawImage(playerPanel.getShipImagePlatformer(), 50, 0, -50, 50, null);
                    } else if (this.isGravityReversed()) {
                        g2d.drawImage(playerPanel.getShipImagePlatformer(), 0, 50, 50, -50, null);
                    } else {
                        g2d.drawImage(playerPanel.getShipImagePlatformer(), 0, 0, 50, 50, null);
                    }
                } else if (playerPanel.getShipImage() != null) {
                    if (this.isGravityReversed()) {
                        g2d.drawImage(playerPanel.getShipImage(), 0, 50, 50, -50, null);
                    } else {
                        g2d.drawImage(playerPanel.getShipImage(), 0, 0, 50, 50, null);
                    }
                } else {
                    g2d.setColor(Color.BLUE);
                    g2d.fillRect(0, 0, 50, 50);
                }
            } else if (this.getCurrentGameMode() == GameMode.WAVE) {
                if (playerPanel.getWaveImage() != null) {
                    g2d.drawImage(playerPanel.getWaveImage(), 0, 0, 50, 50, null);
                } else {
                    g2d.setColor(Color.CYAN);
                    g2d.fillRect(0, 0, 50, 50);
                }
            } else if (this.getCurrentGameMode() == GameMode.ROBOT) {

                if (this.isGravityReversed() && this.isRobotFlipped()) {
                    g2d.drawImage(playerPanel.getRobotImage(), 50, 50, -50, -50, null);
                } else if (this.isRobotFlipped()) {
                    g2d.drawImage(playerPanel.getRobotImage(), 50, 0, -50, 50, null);
                } else if (this.isGravityReversed()) {
                    g2d.drawImage(playerPanel.getRobotImage(), 0, 50, 50, -50, null);
                } else if (playerPanel.getRobotImage() != null) {
                    g2d.drawImage(playerPanel.getRobotImage(), 0, 0, 50, 50, null);
                } else {
                    g2d.setColor(Color.PINK);
                    g2d.fillRect(0, 0, 50, 50);
                }
            } else if (this.getCurrentGameMode() == GameMode.SPIDER) {
                if (playerPanel.getSpiderImage() != null) {
                    if (this.isGravityReversed()) {
                        g2d.drawImage(playerPanel.getSpiderImage(), 0, 50, 50, -50, null);
                    } else {
                        g2d.drawImage(playerPanel.getSpiderImage(), 0, 0, 50, 50, null);
                    }
                } else {
                    g2d.setColor(Color.ORANGE);
                    g2d.fillRect(0, 0, 50, 50);
                }
            } else {
                if (playerPanel.getPlayerImage() != null) {
                    if (this.isPlatformer()) {
                        g2d.drawImage(playerPanel.getPlayerImage(), 50, 0, -50, 50, null);
                    } else {
                        g2d.drawImage(playerPanel.getPlayerImage(), 0, 0, 50, 50, null);
                    }
                } else {
                    g2d.setColor(Color.BLUE);
                    g2d.fillRect(0, 0, 50, 50);
                }
            }

            if (playerPanel.isShowHitboxes()) {
                g2d.setColor(Color.RED);
                g2d.drawRect(0, 0, 50, 50);
            }

            g2d.setTransform(originalTransform);

            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 40));
            g2d.drawString("Attempt " + mainWindow.getAttempts(), 100, 700);
        }
    }
}