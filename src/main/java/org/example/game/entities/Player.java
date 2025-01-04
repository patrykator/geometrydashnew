package org.example.game.entities;

import java.awt.*;

public class Player extends Entity {
    private boolean isJumping;
    private double rotationAngle;
    private int orbEffectDuration = 0;
    private int originalMinJumpSpeed = -16;
    private boolean gravityReversed = false;
    private boolean teleport = false;
    private boolean teleportActivated = false;
    private boolean jumpKeyReleased = true;
    private boolean spiderOrbActivated = false;
    private long jumpStartTime = 0;
    private final long maxJumpHoldTime = 200;
    private double x;
    private double y;
    private double velocityY;
    private double playerSpeed = 5.0; // Zmień na double
    private final double defaultPlayerSpeed = 5.0;
    private double redOrbVelocity = 0;
    private double targetOrbVelocity;
    private double orbVelocityChangeRate = 0.0;
    private boolean spiderOrbJustActivated;
    private boolean inputBlockedAfterSpiderOrb;
    private boolean robotFlipped = false;
    private boolean blackOrbActive = false;
    private boolean canJumpAfterCollision = true;
    private boolean teleportationFailed = false;
    private boolean teleportPad = false;
    private static int staticY;

    public static void setStaticY(int y) {
        Player.staticY = y;
    }

    public static int getStaticY() {
        return staticY;
    }

    public void resetState() {
        setVelocityY(0);
        setJumping(false);
        setRotationAngle(0);
        resetJumpTime();
        setTeleport(false);
        setTeleportActivated(false);
        setJumpKeyReleased(true);
        setSpiderOrbActivated(false);
        setSpiderOrbJustActivated(false);
        setOrbEffectActive(false);
        setOrbEffectDuration(0);
        setOrbEffectActiveDuration(0);
        setBlackOrbActive(false);
        setCanJumpAfterCollision(true);
        setTeleportationFailed(false);
        setInputBlockedAfterSpiderOrb(false);
        setPlatformer(false);
        setShipFlipped(false);
        setRobotFlipped(false);
        setTeleportPad(false);
    }

    private void setOrbEffectActiveDuration(int i) {
        orbEffectActiveDuration = i;
    }


    public boolean isTeleportationFailed() {
        return teleportationFailed;
    }

    public void setTeleportationFailed(boolean teleportationFailed) {
        this.teleportationFailed = teleportationFailed;
    }

    public boolean canJumpAfterCollision() {
        return canJumpAfterCollision;
    }
    public void setCanJumpAfterCollision(boolean canJumpAfterCollision) {
        this.canJumpAfterCollision = canJumpAfterCollision;
    }
    public boolean isInputBlockedAfterSpiderOrb() {
        return inputBlockedAfterSpiderOrb;
    }

    public void setInputBlockedAfterSpiderOrb(boolean inputBlockedAfterSpiderOrb) {
        this.inputBlockedAfterSpiderOrb = inputBlockedAfterSpiderOrb;
    }


    private static int staticX;// Dodaj zmienną isPlatformer
    private boolean orbEffectActive = false;
    private boolean isShipFlipped;
    private int orbEffectActiveDuration;

    private boolean isPlatformer = false;

    private Image ballModeImage;

    private GameMode currentGameMode = GameMode.CUBE;

    public double getTargetOrbVelocity() {
        return targetOrbVelocity;
    }

    public void setTargetOrbVelocity(double targetOrbVelocity) {
        this.targetOrbVelocity = targetOrbVelocity;
    }

    public double getOrbVelocityChangeRate() {
        return orbVelocityChangeRate;
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

    public double getRedOrbVelocity() {
        return redOrbVelocity;
    }

    public void setRedOrbVelocity(double redOrbVelocity) {
        this.redOrbVelocity = redOrbVelocity;
    }


    public synchronized GameMode getCurrentGameMode() { // metoda do pobierania trybu gracza
        return this.currentGameMode;
    }

    public boolean isCubeMode() {
        return currentGameMode == GameMode.CUBE;
    }

    public boolean isShipMode() {
        return currentGameMode == GameMode.SHIP;
    }

    public boolean isBallMode() {
        return currentGameMode == GameMode.BALL;
    }



    public Image getBallModeImage() {
        return ballModeImage;
    }

    public void setBallModeImage(Image ballModeImage) {
        this.ballModeImage = ballModeImage;
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

    // Gettery i settery dla isPlatformer
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


    public Player(int id, String name, String description, String image, String type, int level, int health, int x, int y) {
        super(id, name, description, image, type, level, health);
        this.velocityY = 0;
        this.isJumping = false;
        this.rotationAngle = 0;
        this.jumpStartTime = 0;
        this.x = x;
        this.y = y;
    }

    public void resetJumpTime() {
        jumpStartTime = 0;
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
        return originalMinJumpSpeed;
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
        return defaultPlayerSpeed;
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

    public boolean isTeleportActivated() {
        return teleportActivated;
    }

    public void setTeleportActivated(boolean teleportActivated) {
        this.teleportActivated = teleportActivated;
    }

    public boolean isJumpKeyReleased() {
        return jumpKeyReleased;
    }

    public void setJumpKeyReleased(boolean jumpKeyReleased) {
        this.jumpKeyReleased = jumpKeyReleased;
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

    public boolean isteleportPad() {
        return teleportPad;
    }

}