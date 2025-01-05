package org.example.game.entities;

public class Player extends Entity {
    private boolean isJumping;
    private double rotationAngle;
    private int orbEffectDuration = 0;
    private boolean gravityReversed = false;
    private boolean teleport = false;
    private boolean spiderOrbActivated = false;
    private double x;
    private double y;
    private double velocityY;
    private double playerSpeed = 5.0; // Zmień na double
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

    public Player(String name, int x, int y) {
        super(name);
        this.velocityY = 0;
        this.isJumping = false;
        this.rotationAngle = 0;
        this.x = x;
        this.y = y;
    }

    public static void setStaticY(int y) {
        Player.staticY = y;
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

    public synchronized GameMode getCurrentGameMode() { // metoda do pobierania trybu gracza
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
}