package org.example.game.engine;

import org.example.game.entities.*;
import org.example.game.ui.MainWindow;
import org.example.game.utilities.Activatable;
import org.example.game.world.Tile;
import org.example.game.world.World;
import org.example.game.ui.PlayerPanel;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class GameEngine implements Runnable {
    private MainWindow mainWindow;
    private final Player player;
    private static boolean running = false;
    private final double gravity = 1;
    private GameMode currentGameMode = GameMode.SHIP;
    private boolean spaceOrUpPressed = false;
    private long robotBoostStartTime = 0;
    private boolean robotBoostActive = false;
    private boolean spiderTeleportPerformed = false;
    private final InputHandler inputHandler;
    private boolean gamePaused = false;
    private final JButton resumeButton;
    private final JButton exitLevelButton;
    private boolean levelEnded = false;
    private long levelEndTime;
    private static final long END_GAME_ANIMATION_DURATION = 1500;
    private double levelEndX = 0;
    private Thread gameThread;



    public GameEngine(MainWindow mainWindow, Player player) {
        this.mainWindow = mainWindow;
        this.player = player;

        resumeButton = new JButton("Return to Level");
        resumeButton.setVisible(false);
        resumeButton.addActionListener(e -> {
            resumeGame();
            mainWindow.getPlayerPanel().setFocusable(true);
            mainWindow.getPlayerPanel().requestFocusInWindow();
        });

        exitLevelButton = new JButton("Exit Level");
        exitLevelButton.setVisible(false);
        exitLevelButton.addActionListener(e -> exitToMainMenu());

        mainWindow.getPlayerPanel().add(resumeButton);
        mainWindow.getPlayerPanel().add(exitLevelButton);

        inputHandler = new InputHandler(player, mainWindow.getPlayerPanel(),  this);
        mainWindow.setInputHandler(inputHandler);
    }

    public void start() {
        if (!running) {
            running = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    public void setMainWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }


    @Override
    public void run() {
        long lastTime = System.nanoTime();
        int targetFps = 75;
        double nsPerTick = 1_000_000_000.0 / targetFps;

        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;

            while (delta >= 1) {
                if (!gamePaused && !levelEnded) {
                    updateGameLogic();
                }
                delta--;
            }

            mainWindow.repaint();
        }
    }

    public void pauseGame() {
        if (!gamePaused) {
            gamePaused = true;
            mainWindow.getPlayerPanel().setDimmed(true);

            inputHandler.setEditingMode(false);

            showPauseButtons();
            mainWindow.repaint();
        }
    }

    public void resumeGame() {
        if (gamePaused) {
            gamePaused = false;
            mainWindow.getPlayerPanel().setDimmed(false);
            hidePauseButtons();
            inputHandler.setPlayer(mainWindow.getPlayerPanel().getPlayer());
            inputHandler.setPlayerPanel(mainWindow.getPlayerPanel());

            inputHandler.setEditingMode(false);

            mainWindow.getPlayerPanel().addKeyListener(inputHandler);
            mainWindow.repaint();
        }
    }

    public void exitToMainMenu() {
        gamePaused = true;
        running = false;

        hidePauseButtons();

        resetGameState();
        mainWindow.getPlayerPanel().setDimmed(false);

        mainWindow.setPlayerPosition(player, mainWindow.getPlayerPanel().getHeight());
        mainWindow.resetAttempts();

        setGamePaused(false);

        mainWindow.setVisible(false);
        mainWindow.showMainMenu();
    }

    public void resetGameState() {
        MainWindow.setPlayerPosition(player, mainWindow.getPlayerPanel().getHeight());
        player.setVelocityY(0);
        player.setJumping(false);
        player.setRotationAngle(0);
        player.setGravityReversed(false);
        player.setSpiderOrbJustActivated(false);
        player.resetCheckpoint();
        setLevelEnded();
        mainWindow.getPlayerPanel().resumeDrawingPlayer();

        player.setOrbEffectDuration(0);
        player.setOrbEffectActive(false);
        player.setShipFlipped(false);
        inputHandler.getPressedKeys().clear();
        setCurrentGameMode(GameMode.CUBE);

        for (Checkpoint checkpoint : mainWindow.getPlayerPanel().getWorld().getCheckpoints()) {
            checkpoint.setActivated(false);
        }
    }

    private void setLevelEnded() {
        levelEnded = false;
        levelEndTime = 0;
        levelEndX = 0;
    }




    public void setGamePaused(boolean gamePaused) {
        this.gamePaused = gamePaused;
    }

    private void showPauseButtons() {
        int buttonWidth = 200;
        int buttonHeight = 50;
        int spaceBetweenButtons = 20;

        int buttonsCenterX = mainWindow.getPlayerPanel().getWidth() / 2 - buttonWidth / 2;

        resumeButton.setBounds(buttonsCenterX, mainWindow.getPlayerPanel().getHeight() / 2 - buttonHeight - spaceBetweenButtons / 2, buttonWidth, buttonHeight);
        resumeButton.setVisible(true);

        exitLevelButton.setBounds(buttonsCenterX, mainWindow.getPlayerPanel().getHeight() / 2 + spaceBetweenButtons / 2, buttonWidth, buttonHeight);
        exitLevelButton.setVisible(true);
    }

    private void hidePauseButtons() {
        resumeButton.setVisible(false);
        exitLevelButton.setVisible(false);
    }

    public InputHandler getInputHandler() {
        return inputHandler;
    }

    private void updateVelocity() {

        if (player.isUfoMode()) {
            updateUfoVelocity();
        } else {
            updateNonUfoVelocity();
        }

        if (player.getOrbEffectDuration() > 0) {
            player.setOrbEffectDuration(player.getOrbEffectDuration() - 1);
        }
    }

    private void updateUfoVelocity() {
        double currentMinJumpSpeed;
        double maxFallSpeed;
        if (player.getOrbEffectDuration() > 0) {

            if (player.isBlackOrbActive()) {
                maxFallSpeed = player.isGravityReversed() ? -18 : 18;
            } else {
                maxFallSpeed = player.isGravityReversed() ? 20 : -20;
            }

            currentMinJumpSpeed = player.getTargetOrbVelocity();

        } else {

            player.setBlackOrbActive(false);
            currentMinJumpSpeed = player.getOriginalMinJumpSpeed();
            maxFallSpeed = 16;

        }
        if (player.getVelocityY() > maxFallSpeed) {
            player.setVelocityY(maxFallSpeed);
        } else if (player.getVelocityY() < currentMinJumpSpeed) {
            player.setVelocityY(currentMinJumpSpeed);
        }
        if (mainWindow.getPlayerPanel().isCollision(player.getX(), player.getY() + player.getVelocityY())) {
            player.setOrbEffectDuration(0);
            player.setOrbEffectActive(false);
        }
    }

    private void updateNonUfoVelocity() {
        double currentMinJumpSpeed;
        double maxFallSpeed;
        currentMinJumpSpeed = player.getOriginalMinJumpSpeed();
        maxFallSpeed = 16;

        double temporaryMaxFallSpeed = maxFallSpeed;

        if (player.getOrbEffectDuration() > 0) {
            temporaryMaxFallSpeed = player.isGravityReversed() ? 20 : -20;
        }

        if (player.getVelocityY() > temporaryMaxFallSpeed) {
            player.setVelocityY(temporaryMaxFallSpeed);
        } else if (player.getVelocityY() < currentMinJumpSpeed) {
            player.setVelocityY(currentMinJumpSpeed);
        }
    }

    private boolean isApproachingTargetVelocity(double currentVelocity, double targetVelocity) {
        if (targetVelocity > 0) {
            return currentVelocity < targetVelocity;
        } else {
            return currentVelocity > targetVelocity;
        }
    }

    public GameMode getCurrentGameMode() {
        return currentGameMode;
    }

    public synchronized void setCurrentGameMode(GameMode gameMode) {
        this.currentGameMode = gameMode;
        this.player.setCurrentGameMode(gameMode);
    }

    private void updateGameLogic() {

        checkGameModeChange();
        handleSpiderOrbInput();
        setPlatformer();



        GameMode currentGameMode = getCurrentGameMode();

        handleFragmentAnimation();
        handlePlayerOutOfBounds();

        checkPadActivation(mainWindow.getPlayerPanel());

        handleTeleportation();
        handleSpikeCollisions();
        handleCheckpointCollisions();

        updatePlayerStaticPosition();

        if (player.isDead()) {
            updateCameraPosition();
            handleNonPlatformerMovement();
            handlePlatformerMovement();
            handlePortalCollisions();
            handleSpeedPortalCollisions();
            handlePlatformerShipAndRobotInput();
            handleGameModeLogic(currentGameMode);
        }

        if (!levelEnded) {
            checkLevelEndConditions();
        }
    }

    private void setPlatformer() {
        boolean isPlatformer = mainWindow.getPlayerPanel().getWorld().isPlatformer();
        player.setPlatformer(isPlatformer);
    }

    private void checkGameModeChange() {
        currentGameMode = player.getCurrentGameMode();
    }

    private void updateGameObjects() {
        for (Tile tile : mainWindow.getPlayerPanel().getWorld().getTiles()) {
            tile.update();
        }
        for (Spike spike : mainWindow.getPlayerPanel().getWorld().getSpikes()) {
            spike.update();
        }
        for (Orb orb : mainWindow.getPlayerPanel().getWorld().getOrbs()) {
            orb.update();
        }
        for (Pad pad : World.getPads()) {
            pad.update();
        }
        for (Portal portal : mainWindow.getPlayerPanel().getWorld().getPortals()) {
            portal.update();
        }
        for (SpeedPortal speedPortal : mainWindow.getPlayerPanel().getWorld().getSpeedPortals()) {
            speedPortal.update();
        }
        for (LevelEnd levelEnd : mainWindow.getPlayerPanel().getWorld().getLevelEnds()) {
            levelEnd.update();
        }
    }

    private void checkLevelEndConditions() {
        if (player.isPlatformer()) {
            checkPlatformerLevelEnd();
        } else {
            checkNormalModeLevelEnd();
        }
    }

    private void checkPlatformerLevelEnd() {
        for (LevelEnd levelEnd : mainWindow.getPlayerPanel().getWorld().getLevelEnds()) {
            double distance = Math.sqrt(
                    Math.pow(player.getX() - levelEnd.getX() * 50, 2) +
                            Math.pow(player.getY() - levelEnd.getY() * 50, 2)
            );

            if (distance <= 50) {
                endLevel();
                return;
            }
        }
    }

    private void checkNormalModeLevelEnd() {
        if (levelEndX == 0) {
            calculateLevelEnd();
        }

        if (player.getX() > levelEndX) {
            endLevel();
        }
    }

    private void calculateLevelEnd() {
        double furthestX = 0;
        World world = mainWindow.getPlayerPanel().getWorld();

        for (Tile tile : world.getTiles()) {
            furthestX = Math.max(furthestX, tile.getX() * 50);
        }
        for (Spike spike : world.getSpikes()) {
            furthestX = Math.max(furthestX, spike.x() * 50);
        }
        for (Orb orb : world.getOrbs()) {
            furthestX = Math.max(furthestX, orb.getX() * 50);
        }
        for (Pad pad : World.getPads()) {
            furthestX = Math.max(furthestX, pad.getX() * 50);
        }
        for (Portal portal : world.getPortals()) {
            furthestX = Math.max(furthestX, portal.getX() * 50);
        }
        for (SpeedPortal speedPortal : world.getSpeedPortals()) {
            furthestX = Math.max(furthestX, speedPortal.getX() * 50);
        }

        levelEndX = furthestX + 200;
    }

    private void endLevel() {
        levelEnded = true;
        levelEndTime = System.currentTimeMillis();

        mainWindow.getPlayerPanel().stopDrawingPlayer();
        running = false;
        SwingUtilities.invokeLater(this::showLevelCompleteDialog);
    }

    public boolean isLevelEnded() {
        return levelEnded;
    }

    public double getLevelEndX() {
        return levelEndX;
    }

    private void showLevelCompleteDialog() {
        Object[] options = {"Play Again", "Exit to Main Menu"};
        int choice = JOptionPane.showOptionDialog(
                mainWindow,
                "Level Complete!",
                "Congratulations!",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == JOptionPane.YES_OPTION) {
            resetLevel();
            running = true;
            gameThread = new Thread(this);
            gameThread.start();
        } else {
            exitToMainMenu();
        }
    }

    private void resetLevel() {
        levelEnded = false;
        levelEndTime = 0;
        levelEndX = 0;
        mainWindow.resetAttempts();
        mainWindow.getPlayerPanel().resetCameraPosition();
        resetGameState();
        mainWindow.getPlayerPanel().resumeDrawingPlayer();
        running = true;

        if (!player.isPlatformer()) {
            player.setPlayerSpeed(player.getDefaultPlayerSpeed());
        }


    }

    private void handleSpiderOrbInput() {
        if (player.isSpiderOrbJustActivated()) {
            mainWindow.getPressedKeys().remove(KeyEvent.VK_SPACE);
            mainWindow.getPressedKeys().remove(KeyEvent.VK_UP);
            player.setSpiderOrbJustActivated(false);
        }
    }

    private void handleFragmentAnimation() {
        if (mainWindow.isAnimatingDeath() && mainWindow.fragmentAnimation != null) {
            mainWindow.fragmentAnimation.update();
        }
    }

    private void handlePlayerOutOfBounds() {
        if (player.getY() > 5000 || player.getY() < -5000) {
            mainWindow.instantDie(player);
        }
    }

    private void handleNonPlatformerMovement() {
        if (!player.isPlatformer()) {
            double newX = player.getX() + player.getPlayerSpeed();
            if (mainWindow.getPlayerPanel().isCollision(newX, player.getY())) {
                mainWindow.die(player);
            } else {
                player.setX(newX);
            }
        }
    }

    private void handlePlatformerMovement() {
        if (player.isPlatformer()) {
            double speed = 5;
            if (mainWindow.getPressedKeys().contains(KeyEvent.VK_A)) {
                double newX = player.getX() - speed;
                if (!mainWindow.getPlayerPanel().isCollision(newX, player.getY())) {
                    player.setX(newX);
                }
            } else if (mainWindow.getPressedKeys().contains(KeyEvent.VK_D)) {
                double newX = player.getX() + speed;
                if (!mainWindow.getPlayerPanel().isCollision(newX, player.getY())) {
                    player.setX(newX);
                }
            }
        }
    }

    private void handlePortalCollisions() {
        for (Portal portal : mainWindow.getPlayerPanel().getWorld().getPortals()) {
            if (isCollisionWithPortal(player.getX(), player.getY(), portal.getX(), portal.getY())) {
                if (!(player.isPlatformer() && portal.getTargetGameMode() == GameMode.WAVE) && portal.getTargetGameMode() != player.getCurrentGameMode()) {
                    setCurrentGameMode(portal.getTargetGameMode());

                    if (portal.getTargetGameMode() == GameMode.ROBOT) {
                        player.setJumping(false);
                    }
                }
            }
        }
    }

    private void handleSpeedPortalCollisions() {
        for (SpeedPortal speedPortal : mainWindow.getPlayerPanel().getWorld().getSpeedPortals()) {
            if (isCollisionWithSpeedPortal(player.getX(), player.getY(), speedPortal.getX(), speedPortal.getY())) {
                if (player.getPlayerSpeed() != player.getDefaultPlayerSpeed() * speedPortal.getSpeedMultiplier()) {
                    player.setPlayerSpeed(player.getDefaultPlayerSpeed() * speedPortal.getSpeedMultiplier());
                }
                break;
            }
        }
    }

    private void handlePlatformerShipAndRobotInput() {
        if (player.isPlatformer() && currentGameMode == GameMode.SHIP) {
            if (mainWindow.getPressedKeys().contains(KeyEvent.VK_A)) {
                player.setShipFlipped(true);
            } else if (mainWindow.getPressedKeys().contains(KeyEvent.VK_D)) {
                player.setShipFlipped(false);
            }
        }

        if (player.isPlatformer() && currentGameMode == GameMode.ROBOT) {
            if (mainWindow.getPressedKeys().contains(KeyEvent.VK_A)) {
                player.setRobotFlipped(true);
            } else if (mainWindow.getPressedKeys().contains(KeyEvent.VK_D)) {
                player.setRobotFlipped(false);
            }
        }

        if (player.isPlatformer() && currentGameMode == GameMode.SPIDER) {
            if (mainWindow.getPressedKeys().contains(KeyEvent.VK_A)) {
                player.setSpiderFlipped(true);
            } else if (mainWindow.getPressedKeys().contains(KeyEvent.VK_D)) {
                player.setSpiderFlipped(false);
            }
        }
    }

    private void handleGameModeLogic(GameMode currentGameMode) {
        if (currentGameMode == GameMode.BALL) {
            handleBallMode();
        } else if (currentGameMode == GameMode.UFO) {
            handleUfoMode();
        } else if (currentGameMode == GameMode.WAVE) {
            handleWaveMode();
        } else if (currentGameMode == GameMode.SHIP) {
            handleShipMode();
        } else if (currentGameMode == GameMode.ROBOT) {
            handleRobotMode();
        } else if (currentGameMode == GameMode.SPIDER) {
            handleSpiderMode();
        } else {
            handleDefaultMode();
        }
    }

    private void handleBallMode() {
        if (player.isPlatformer()) {
            if (mainWindow.getPressedKeys().contains(KeyEvent.VK_A)) {
                player.setRotationAngle((player.getRotationAngle() + (-5)) % 360);
            } else if (mainWindow.getPressedKeys().contains(KeyEvent.VK_D)) {
                player.setRotationAngle((player.getRotationAngle() + (5)) % 360);
            }
        } else {
            player.setRotationAngle((player.getRotationAngle() + (5)) % 360);
        }

        boolean isTouchingGround = mainWindow.getPlayerPanel().isCollisionWithCeilingOrFloor(player.getX(), player.getY(), player.isGravityReversed());

        if ((mainWindow.getPressedKeys().contains(KeyEvent.VK_SPACE) || mainWindow.getPressedKeys().contains(KeyEvent.VK_UP)) && isTouchingGround) {
            player.setGravityReversed(!player.isGravityReversed());
            player.setVelocityY(0);
            player.setJumping(false);

            double ceilingCheckY = player.isGravityReversed() ? player.getY() + 5 : player.getY() + 45;
            if (mainWindow.getPlayerPanel().isCollisionWithCeilingOrFloor(player.getX(), ceilingCheckY, player.isGravityReversed())) {
                player.setY(player.getY() + (player.isGravityReversed() ? -5 : 5));
            }
        } else {
            double newY = player.getY() + player.getVelocityY();
            double appliedGravity = player.isGravityReversed() ? -gravity : gravity;
            player.setVelocityY(player.getVelocityY() + appliedGravity);

            if (!mainWindow.getPlayerPanel().isCollision(player.getX(), newY)) {
                player.setY(newY);
                player.setJumping(true);
            } else {
                player.setVelocityY(0);
                player.setJumping(false);
            }
        }
    }

    private void handleUfoMode() {
        if (player.getOrbEffectDuration() > 0) {
            if (player.getVelocityY() > player.getTargetOrbVelocity()) {
                player.setVelocityY(Math.max(player.getTargetOrbVelocity(), player.getVelocityY() - 1));
            } else if (player.getVelocityY() < player.getTargetOrbVelocity()) {
                player.setVelocityY(Math.min(player.getTargetOrbVelocity(), player.getVelocityY() + 1));
            }
        } else if ((mainWindow.getPressedKeys().contains(KeyEvent.VK_SPACE) || mainWindow.getPressedKeys().contains(KeyEvent.VK_UP)) && !spaceOrUpPressed) {
            double testY = player.getY() + (player.isGravityReversed() ? 1 : -1);
            if (!mainWindow.getPlayerPanel().isCollision(player.getX(), testY)) {
                double ufoJumpStrength = 10;
                player.setVelocityY(player.isGravityReversed() ? ufoJumpStrength : -ufoJumpStrength);
                player.setJumping(true);
                spaceOrUpPressed = true;
            }
        }

        if (!mainWindow.getPressedKeys().contains(KeyEvent.VK_SPACE) && !mainWindow.getPressedKeys().contains(KeyEvent.VK_UP)) {
            spaceOrUpPressed = false;
        }

        updateVelocity();

        double newY = player.getY() + player.getVelocityY();
        double appliedGravity = player.isGravityReversed() ? -gravity : gravity;

        if (player.isJumping() && player.getOrbEffectDuration() <= 0) {
            player.setVelocityY(player.getVelocityY() + appliedGravity);
        }

        if (mainWindow.getPlayerPanel().isCollision(player.getX(), newY)) {
            player.setVelocityY(0);
            player.setJumping(false);
        } else {
            player.setY(newY);
            if (!player.isJumping() && player.getOrbEffectDuration() <= 0) {
                double testY = player.getY() + (player.isGravityReversed() ? -1 : 1);
                if (!mainWindow.getPlayerPanel().isCollision(player.getX(), testY)) {
                    player.setJumping(true);
                    player.setVelocityY(player.isGravityReversed() ? -gravity : gravity);
                } else {
                    player.setJumping(false);
                }
            }
        }
    }

    private void handleWaveMode() {
        int waveSpeed = 5;

        if (mainWindow.getPressedKeys().contains(KeyEvent.VK_SPACE) || mainWindow.getPressedKeys().contains(KeyEvent.VK_UP)) {
            double newY = player.getY() + (player.isGravityReversed() ? waveSpeed : -waveSpeed);
            if (!mainWindow.getPlayerPanel().isCollision(player.getX(), newY)) {
                player.setY(newY);
            }
        } else {
            double newY = player.getY() + (player.isGravityReversed() ? -waveSpeed : waveSpeed);
            if (!mainWindow.getPlayerPanel().isCollision(player.getX(), newY)) {
                player.setY(newY);
            }
        }
    }

    private void handleShipMode() {
        if (player.getOrbEffectDuration() > 0) {
            player.setOrbEffectDuration(player.getOrbEffectDuration() - 1);


            if (player.getVelocityY() > player.getTargetOrbVelocity() ) {
                player.setVelocityY(Math.max(player.getTargetOrbVelocity(), player.getVelocityY() - (1.0)));
            } else if (player.getVelocityY() < player.getTargetOrbVelocity()) {
                player.setVelocityY(Math.min(player.getTargetOrbVelocity(), player.getVelocityY() + (1.0)));
            } else {
                if(player.isGravityReversed()){
                    player.setVelocityY(player.getVelocityY() + (player.isGravityReversed() ? -0.3 : 0.3));
                } else {
                    player.setVelocityY(player.getVelocityY() + (player.isGravityReversed() ? 0.3 : -0.3));
                }
            }

            double newY = player.getY() + player.getVelocityY();
            if (!mainWindow.getPlayerPanel().isCollision(player.getX(), newY)) {
                player.setY(newY);
            } else {
                player.setOrbEffectDuration(0);
                player.setOrbEffectActive(false);
            }

            player.decrementOrbEffectActiveDuration();
            if (player.getOrbEffectDuration() <= 0) {
                player.setOrbEffectActive(false);
            }
        } else if (mainWindow.getPressedKeys().contains(KeyEvent.VK_SPACE) || mainWindow.getPressedKeys().contains(KeyEvent.VK_UP)) {
            if (player.isGravityReversed()) {
                player.setVelocityY(Math.min(7, player.getVelocityY() + 1));
            } else {
                player.setVelocityY(Math.max(-7, player.getVelocityY() - 1));
            }
        } else {
            if (player.isGravityReversed()) {
                player.setVelocityY(Math.max(-7, player.getVelocityY() - 1));
            } else {
                player.setVelocityY(Math.min(7, player.getVelocityY() + 1));
            }
        }
        double newY = player.getY() + player.getVelocityY();
        if (!mainWindow.getPlayerPanel().isCollision(player.getX(), newY)) {
            player.setY(newY);
        }
    }

    private void handleRobotMode() {
        boolean isTouchingGround = mainWindow.getPlayerPanel().isCollisionWithCeilingOrFloor((int) player.getX(), (int) player.getY(), player.isGravityReversed());

        if (mainWindow.getPressedKeys().contains(KeyEvent.VK_SPACE) || mainWindow.getPressedKeys().contains(KeyEvent.VK_UP)) {
            double robotJumpStrength = 8;
            if (!robotBoostActive && isTouchingGround) {
                robotBoostActive = true;
                robotBoostStartTime = System.currentTimeMillis();
                player.setVelocityY(player.isGravityReversed() ? robotJumpStrength : -robotJumpStrength);
                player.setJumping(true);
            } else if (robotBoostActive) {
                long boostDuration = System.currentTimeMillis() - robotBoostStartTime;
                long maxRobotBoostDuration = 300;
                if (boostDuration < maxRobotBoostDuration) {
                    float boostProgress = (float) boostDuration / maxRobotBoostDuration;
                    int currentJumpStrength = (int) (robotJumpStrength * (1 + boostProgress));
                    player.setVelocityY(player.isGravityReversed() ? currentJumpStrength : -currentJumpStrength);
                } else {
                    robotBoostActive = false;
                    player.setVelocityY(player.isGravityReversed() ? player.getVelocityY() - gravity : player.getVelocityY() + gravity);
                }
            }
        } else {
            robotBoostActive = false;
        }

        if (player.isJumping()) {
            double newY = player.getY() + player.getVelocityY();
            double appliedGravity = player.isGravityReversed() ? -gravity : gravity;

            if (!robotBoostActive) {
                player.setVelocityY(player.getVelocityY() + appliedGravity);
            }

            if (mainWindow.getPlayerPanel().isCollision(player.getX(), newY)) {
                player.setJumping(false);
                player.setVelocityY(0);
                robotBoostActive = false;
            } else {
                player.setY(newY);
            }
        } else {
            double testY = player.getY() + (player.isGravityReversed() ? -1 : 1);
            if (!mainWindow.getPlayerPanel().isCollision(player.getX(), testY)) {
                player.setJumping(true);
                player.setVelocityY(player.isGravityReversed() ? -gravity : gravity);
            } else {
                player.setJumping(false);
                player.setVelocityY(0);
            }
        }
    }

    private void handleSpiderMode() {
        boolean isTouchingGround = mainWindow.getPlayerPanel().isCollisionWithCeilingOrFloor(player.getX(), player.getY(), player.isGravityReversed());

        if ((mainWindow.getPressedKeys().contains(KeyEvent.VK_SPACE) || mainWindow.getPressedKeys().contains(KeyEvent.VK_UP)) && isTouchingGround) {
            if (!spiderTeleportPerformed) {
                spiderTeleport(player, mainWindow.getPlayerPanel().getWorld());
                player.setGravityReversed(!player.isGravityReversed());
                spiderTeleportPerformed = true;

                player.setVelocityY(player.isGravityReversed() ? -10 : 10);
            }
        } else {
            spiderTeleportPerformed = false;
        }

        if (player.getOrbEffectDuration() > 0) {
            if (player.getVelocityY() > player.getTargetOrbVelocity()) {
                player.setVelocityY(Math.max(player.getTargetOrbVelocity(), player.getVelocityY() - 0.5));
            } else if (player.getVelocityY() < player.getTargetOrbVelocity()) {
                player.setVelocityY(Math.min(player.getTargetOrbVelocity(), player.getVelocityY() + 0.5));
            }

            if (isApproachingTargetVelocity(player.getVelocityY(), player.getTargetOrbVelocity())) {
                player.decrementOrbEffectDuration();
            }
        } else {
            double appliedGravity = player.isGravityReversed() ? -gravity : gravity;
            player.setVelocityY(player.getVelocityY() + appliedGravity);
        }

        if (player.getOrbEffectDuration() <= 0 || !isApproachingTargetVelocity(player.getVelocityY(), player.getTargetOrbVelocity())) {
            if (player.getVelocityY() > 16) {
                player.setVelocityY(16);
            } else if (player.getVelocityY() < -16) {
                player.setVelocityY(-16);
            }
        }

        double newY = player.getY() + player.getVelocityY();
        if (mainWindow.getPlayerPanel().isCollision(player.getX(), newY)) {
            player.setVelocityY(0);

            if (player.getOrbEffectDuration() > 0) {
                player.setOrbEffectDuration(0);
                player.setOrbEffectActive(false);
            }
        } else {
            player.setY(newY);
        }
    }

    private void handleDefaultMode() {
        if (player.isOrbEffectActive()) {
            double newY = player.getY() + player.getVelocityY();
            if (player.getOrbEffectDuration() > 0)  {
                player.setY(newY);
                player.setOrbEffectDuration(player.getOrbEffectDuration() - 1);
            } else {
                player.setOrbEffectActive(false);
            }
        } else {
            handleJumpAndGravity();
        }
    }

    private void handleJumpAndGravity() {
        if (mainWindow.getPressedKeys().contains(KeyEvent.VK_SPACE) || mainWindow.getPressedKeys().contains(KeyEvent.VK_UP)) {
            if (player.isSpiderOrbActivated() && mainWindow.getInputHandler().isSpiderOrbKeyPressed()) {
                player.setVelocityY(0);
                player.setJumping(false);
            } else if (!player.isJumping() && player.canJumpAfterCollision()) {
                double testY = player.getY() + (player.isGravityReversed() ? 1 : -1);
                if (!mainWindow.getPlayerPanel().isCollision(player.getX(), testY)) {
                    double jumpStrength = 20;
                    player.setVelocityY(player.isGravityReversed() ? jumpStrength : -jumpStrength);
                    player.setJumping(true);
                }
            }
        }

        updateVelocity();

        if (player.isJumping()) {
            handleJumpingState();
        } else {
            handleFallingState();
        }
    }

    private void handleJumpingState() {


        double newY = player.getY() + player.getVelocityY();
        double appliedGravity = player.isGravityReversed() ? -gravity : gravity;
        player.setVelocityY(player.getVelocityY() + appliedGravity);
        double newX = player.getX();


        if (mainWindow.getPlayerPanel().isCollision(player.getX(), newY)) {
            player.setJumping(false);
            player.setCanJumpAfterCollision(false);
            adjustPlayerPositionOnCollision(newY);
            player.setVelocityY(0);
        } else {
            player.setY(newY);
        }
        handleRotation();
    }

    private void adjustPlayerPositionOnCollision(double newY) {
        if (player.isGravityReversed()) {
            if (player.getVelocityY() < 0) {
                while (mainWindow.getPlayerPanel().isCollision(player.getX(), newY)) {
                    newY += 1;
                }
            } else {
                while (mainWindow.getPlayerPanel().isCollision(player.getX(), newY)) {
                    newY -= 1;
                }
            }
        } else {
            if (player.getVelocityY() > 0) {
                while (mainWindow.getPlayerPanel().isCollision(player.getX(), newY)) {
                    newY -= 1;
                }
            } else {
                while (mainWindow.getPlayerPanel().isCollision(player.getX(), newY)) {
                    newY += 1;
                }
            }
        }
        player.setY(newY);
    }

    private void handleRotation() {
        if (mainWindow.getPressedKeys().contains(KeyEvent.VK_A) && player.isPlatformer()) {
            player.setRotationAngle((player.getRotationAngle() - 5 * (player.isGravityReversed() ? -1 : 1)) % 360);
        } else if (mainWindow.getPressedKeys().contains(KeyEvent.VK_D) && player.isPlatformer()) {
            player.setRotationAngle((player.getRotationAngle() + 5 * (player.isGravityReversed() ? -1 : 1)) % 360);
        } else {
            player.setRotationAngle((player.getRotationAngle() + 6 * (player.isGravityReversed() ? -1 : 1)) % 360);
        }
    }

    private void handleFallingState() {
        double testY = player.getY() + (player.isGravityReversed() ? -1 : 1);
        if (!mainWindow.getPlayerPanel().isCollision(player.getX(), testY)) {
            player.setJumping(true);
            player.setVelocityY(player.isGravityReversed() ? -gravity : gravity);
        } else {
            player.setJumping(false);
            player.setVelocityY(0);
            player.setRotationAngle(Math.round(player.getRotationAngle() / 90) * 90);
            player.setCanJumpAfterCollision(true);
        }
    }

    private void handleTeleportation() {
        if (player.isTeleport()) {
            teleportToNearestSurface(player, mainWindow.getPlayerPanel().getWorld());
            player.setTeleport(false);
        }

        if (player.isTeleportPad()) {
            teleportToNearestSurfacePad(player, mainWindow.getPlayerPanel().getWorld());
            player.setTeleportPad(false);
        }
    }

    private void handleSpikeCollisions() {
        for (Spike spike : mainWindow.getPlayerPanel().getWorld().getSpikes()) {
            if (mainWindow.getPlayerPanel().isCollision(player.getX(), player.getY(), spike.x(), spike.y())) {
                mainWindow.die(player);
            }
        }
    }

    private void updatePlayerStaticPosition() {
        Player.setStaticX((int) player.getX());
        Player.setStaticY((int) player.getY());
    }

    private void updateCameraPosition() {
        int panelWidth = mainWindow.getPlayerPanel().getWidth();
        int panelHeight = mainWindow.getPlayerPanel().getHeight();
        int cameraTargetX = (int) (Player.getStaticX() - (double) panelWidth / 2);

        double cameraTargetY = mainWindow.getPlayerPanel().getCameraOffsetY();
        double upperThreshold = 0.1;
        double lowerThreshold = 0.9;
        int titleBarHeight = 35;

        if (Player.getStaticY() < panelHeight * upperThreshold - titleBarHeight - 5) {
            if (Player.getStaticY() < 55) {
                cameraTargetY = Math.min(0, Player.getStaticY() - 40);
            } else {
                cameraTargetY = Math.max(0, Player.getStaticY() - 50);
            }

        } else if (Player.getStaticY() > panelHeight * lowerThreshold) {
            cameraTargetY = (int) (Player.getStaticY() - panelHeight * lowerThreshold);
        }

        cameraTargetY = Math.max(-4435, Math.min(cameraTargetY, 3500));

        double newCameraOffsetX = Math.max(cameraTargetX, -200);
        double newCameraOffsetY = cameraTargetY;

        mainWindow.getPlayerPanel().setCameraOffsetX((int) newCameraOffsetX);
        mainWindow.getPlayerPanel().setCameraOffsetY((int) newCameraOffsetY);
    }

    public boolean isGamePaused() {
        return gamePaused;
    }

    public void checkPadActivation(PlayerPanel playerPanel) {
        for (Pad pad : World.getPads()) {
            if (playerPanel.isCollision(player.getX(), player.getY(), pad.getX(), pad.getY())) {
                if (pad instanceof Activatable) {
                    ((Activatable) pad).activate(player);
                }
            }
        }
    }

    private void handleCheckpointCollisions() {
        for (Checkpoint checkpoint : mainWindow.getPlayerPanel().getWorld().getCheckpoints()) {
            if (isCollisionWithCheckpoint(player.getX(), player.getY(), checkpoint.getX(), checkpoint.getY())) {
                checkpoint.activate(player);
                player.setCheckpointGameMode(player.getCurrentGameMode());
                break;
            }
        }
    }

    private boolean isCollisionWithCheckpoint(double playerX, double playerY, double checkpointX, double checkpointY) {
        double playerWidth = 50;
        double playerHeight = 50;
        double scaledCheckpointX = checkpointX * 50;
        double scaledCheckpointY = checkpointY * 50;

        return playerX < scaledCheckpointX + 50 &&
                playerX + playerWidth > scaledCheckpointX &&
                playerY < scaledCheckpointY + 50 &&
                playerY + playerHeight > scaledCheckpointY;
    }

    void handleTeleportOrbActivation(Player player, Orb orb) {
        if (orb instanceof Activatable) {
            ((Activatable) orb).activate(player);
        }
    }

    private void spiderTeleport(Player player, World world) {
        double playerX = player.getX();
        double playerY = player.getY();
        double nearestY = playerY;
        double minDistance = Integer.MAX_VALUE;

        if (!player.isGravityReversed()) {
            nearestY = getNearestY(world, playerX, playerY, nearestY, minDistance, true);
        } else {
            nearestY = getNearestY(world, playerX, playerY, nearestY, minDistance, false);
        }

        if (nearestY != playerY) {
            player.setY(nearestY);
            player.setVelocityY(player.isGravityReversed() ? -10 : 10);
        } else {
            player.setGravityReversed(!player.isGravityReversed());
        }

        player.setTeleport(false);
        spiderTeleportPerformed = false;
    }

    private double getNearestY(World world, double playerX, double playerY, double nearestY, double minDistance, boolean searchAbove) {
        for (Tile tile : world.getTiles()) {
            if (tile.isSolid() &&
                    tile.getX() * 50 < playerX + 50 &&
                    tile.getX() * 50 + 50 > playerX) {

                double distance;
                if (searchAbove) {
                    if (tile.getY() * 50 < playerY) {
                        distance = playerY - (tile.getY() * 50 + 50);
                    } else {
                        continue;
                    }
                } else {
                    if (tile.getY() * 50 > playerY) {
                        distance = tile.getY() * 50 - playerY;
                    } else {
                        continue;
                    }
                }

                if (distance > 0 && distance < minDistance) {
                    minDistance = distance;
                    nearestY = searchAbove ? tile.getY() * 50 + 50 : tile.getY() * 50 - 50;
                }
            }
        }
        return nearestY;
    }

    public boolean isCollisionWithSpeedPortal(double playerX, double playerY, double portalX, double portalY) {
        double playerWidth = 50;
        double playerHeight = 50;
        double portalWidth = 50;
        double portalHeight = 100;

        double scaledPortalX = portalX * 50;
        double scaledPortalY = portalY * 50;

        return playerX < scaledPortalX + portalWidth &&
                playerX + playerWidth > scaledPortalX &&
                playerY < scaledPortalY + portalHeight &&
                playerY + playerHeight > scaledPortalY;
    }

    public boolean isCollisionWithPortal(double playerX, double playerY, double portalX, double portalY) {
        double playerWidth = 50;
        double playerHeight = 50;
        double portalWidth = 50;
        double portalHeight = 100;

        double scaledPortalX = portalX * 50;
        double scaledPortalY = portalY * 50;

        boolean collision = playerX < scaledPortalX + portalWidth &&
                playerX + playerWidth > scaledPortalX &&
                playerY < scaledPortalY + portalHeight &&
                playerY + playerHeight > scaledPortalY;

        if (collision) {
            player.setRotationAngle(0);
        }

        return collision;
    }


    private void teleportToNearestSurface(Player player, World world) {

        Orb currentOrb = mainWindow.getPlayerPanel().getActivatedOrb(player);
        if (currentOrb == null) {
            return;
        }

        double playerX = player.getX();
        double playerY = player.getY();
        double nearestY = playerY;
        double minDistance = Integer.MAX_VALUE;

        if (currentOrb.getDirection().equals("up")) {
            nearestY = getNearestY(world, playerX, playerY, nearestY, minDistance, true);
        } else if (currentOrb.getDirection().equals("down")) {
            nearestY = getNearestY(world, playerX, playerY, nearestY, minDistance, false);
        }

        if (nearestY != playerY) {
            player.setY(nearestY);
            player.setTeleport(false);
            player.setVelocityY(0);
            player.setJumping(false);
            if ("up".equals(currentOrb.getDirection())) {
                player.setGravityReversed(true);
            } else if ("down".equals(currentOrb.getDirection())) {
                player.setGravityReversed(false);
            }
        }

    }

    private void teleportToNearestSurfacePad(Player player, World world) {

        Pad currentPad = mainWindow.getPlayerPanel().getActivatedPad(player);

        if (currentPad == null) {
            return;
        }


        double playerX = player.getX();
        double playerY = player.getY();
        double nearestY = playerY;
        double minDistance = Integer.MAX_VALUE;


        if (currentPad.getPosition().equals("top")) {
            nearestY = getNearestY(world, playerX, playerY, nearestY, minDistance, true);
        } else if (currentPad.getPosition().equals("bottom")) {
            nearestY = getNearestY(world, playerX, playerY, nearestY, minDistance, false);
        }

        if (nearestY != playerY) {
            player.setY(nearestY);
            player.setTeleport(false);
            player.setVelocityY(0);
            player.setJumping(false);
            if ("top".equals(currentPad.getPosition())) {
                player.setGravityReversed(true);
            } else if ("bottom".equals(currentPad.getPosition())) {
                player.setGravityReversed(false);
            }
        }
    }

    public boolean isRunning() {
        return running;
    }
}