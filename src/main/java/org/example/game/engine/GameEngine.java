package org.example.game.engine;

import org.example.game.entities.*;
import org.example.game.ui.MainWindow;
import org.example.game.world.Tile;
import org.example.game.world.World;
import org.example.game.ui.PlayerPanel;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

public class GameEngine implements Runnable {
    private MainWindow mainWindow;
    private final Player player;
    private static boolean running = false;
    private final int targetFps = 75;
    private final double gravity = 1;
    private final double jumpStrength = 20;
    private final double ufoJumpStrength = 10;
    private final double robotJumpStrength = 8; // Zmniejszona siła skoku robota
    private GameMode currentGameMode = GameMode.SHIP;
    private boolean spaceOrUpPressed = false;
    private long robotBoostStartTime = 0;
    private final long maxRobotBoostDuration = 300;
    private boolean robotBoostActive = false;
    private boolean spiderTeleportPerformed = false;
    private boolean speedPortalActivated = false;
    private double lastSpeedMultiplier = 1.0;
    private InputHandler inputHandler;

    private boolean gamePaused = false;
    private String pauseReason = "";
    private JButton resumeButton;
    private JButton exitLevelButton;

    private boolean escPressed = false;

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
        exitLevelButton.addActionListener(e -> {
            exitToMainMenu();
        });

        mainWindow.getPlayerPanel().add(resumeButton);
        mainWindow.getPlayerPanel().add(exitLevelButton);

        inputHandler = new InputHandler(player, mainWindow.getPlayerPanel(), 16, this);
        mainWindow.setInputHandler(inputHandler); // Ustaw InputHandler w MainWindow
    }



    public void start() {
        running = true;
        Thread gameThread = new Thread(this);
        gameThread.start();
    }

    public void setMainWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerTick = 1_000_000_000.0 / targetFps;

        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;

            while (delta >= 1) {
                updateGameLogic();
                delta--;
            }

            mainWindow.repaint();
        }
    }

    public void pauseGame(String reason) {
        if (!gamePaused) {
            gamePaused = true;
            running = false;
            mainWindow.getPlayerPanel().setDimmed(true);

            // Zablokuj edycję po wstrzymaniu gry
            inputHandler.setEditingMode(false);

            showPauseButtons();
            mainWindow.repaint();
        }
    }

    public void pauseGamewave(String reason) {
        if (!gamePaused) {
            gamePaused = true;
            pauseReason = reason;
            running = false;
            // Przyciemnij ekran
            mainWindow.getPlayerPanel().setDimmed(true);


            // Wyświetl komunikat, jeśli nie jest pusty
            if (!reason.isEmpty()) {
                JOptionPane.showMessageDialog(mainWindow, reason, "Gra zatrzymana", JOptionPane.WARNING_MESSAGE);
            }

            mainWindow.repaint();
        }
    }

    public void resumeGame() {
        if (gamePaused) {
            gamePaused = false;
            running = true;
            mainWindow.getPlayerPanel().setDimmed(false);
            hidePauseButtons();
            inputHandler.setPlayer(mainWindow.getPlayerPanel().getPlayer());
            inputHandler.setPlayerPanel(mainWindow.getPlayerPanel());

            // Upewnij się, że tryb edycji jest wyłączony po wznowieniu gry
            inputHandler.setEditingMode(false);

            Thread gameThread = new Thread(this);
            gameThread.start();
            mainWindow.getPlayerPanel().addKeyListener(inputHandler);
            mainWindow.repaint();
        }
    }

    public void exitToMainMenu() {
        gamePaused = true;
        // Zatrzymaj silnik gry
        running = false;

        // Ukryj i usuń przyciski pauzy
        hidePauseButtons();

        // Resetuj stan gry
        resetGameState();
        mainWindow.getPlayerPanel().setDimmed(false);

        setgamepaused(false);

        mainWindow.setVisible(false);
        mainWindow.showMainMenu();
    }

    private void resetGameState() {
        MainWindow.setPlayerPosition(player, mainWindow.getPlayerPanel().getHeight());
        player.setVelocityY(0);
        player.setJumping(false);
        player.setRotationAngle(0); // Zresetuj kąt obrotu
        player.setGravityReversed(false);
    }

    public void setgamepaused(boolean gamePaused) {
        this.gamePaused = gamePaused;
    }


    private void showPauseButtons() {
        int buttonWidth = 200;
        int buttonHeight = 50;
        int spaceBetweenButtons = 20;


        // Oblicz pozycję X dla wyśrodkowania przycisków
        int buttonsCenterX = mainWindow.getPlayerPanel().getWidth() / 2 - buttonWidth / 2;

        // Ustaw pozycję i rozmiar dla resumeButton
        resumeButton.setBounds(buttonsCenterX, mainWindow.getPlayerPanel().getHeight() / 2 - buttonHeight - spaceBetweenButtons / 2, buttonWidth, buttonHeight);
        resumeButton.setVisible(true);

        // Ustaw pozycję i rozmiar dla exitLevelButton
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
        double currentMinJumpSpeed; // Zmienione na double
        double maxFallSpeed; // Zmienione na double

        if (player.getOrbEffectDuration() > 0) {
            currentMinJumpSpeed = -20;
            maxFallSpeed = player.isGravityReversed() ? 20 : -20;
            player.setOrbEffectDuration(player.getOrbEffectDuration() - 1);
        } else {
            currentMinJumpSpeed = player.getOriginalMinJumpSpeed();
            maxFallSpeed = 16;
        }

        if (player.getVelocityY() > maxFallSpeed) {
            player.setVelocityY(maxFallSpeed);
        } else if (player.getVelocityY() < currentMinJumpSpeed) {
            player.setVelocityY(currentMinJumpSpeed);
        }
    }



    public GameMode getCurrentGameMode() {
        return currentGameMode;
    }

    public synchronized void setCurrentGameMode(GameMode gameMode) {
        this.currentGameMode = gameMode;
        this.player.setCurrentGameMode(gameMode); // Ustaw tryb gry w Player
    }

    // In src/main/java/org/example/game/engine/GameEngine.java
    private void updateGameLogic() {


        if (player.isPlatformer() && getCurrentGameMode() == GameMode.WAVE) {
            pauseGamewave("Tryb platformera i Wave nie mogą być aktywne jednocześnie!");
            return; // Zatrzymaj dalsze wykonywanie updateGameLogic()
        }

        GameMode currentGameMode = getCurrentGameMode();

        int panelWidth = mainWindow.getPlayerPanel().getWidth();
        double playerX = player.getX();
        double playerY = player.getY();
        int cameraOffsetX = mainWindow.getPlayerPanel().getCameraOffsetX();

        // Tryb platformer jest niezależny od trybu gry

        if (!player.isPlatformer()) {
            double newX = player.getX() + player.getPlayerSpeed();
            if (!mainWindow.getPlayerPanel().isCollision( newX,  player.getY())) {
                player.setX(newX);
            }
        }

        if (player.isPlatformer()) {
            if (mainWindow.getPressedKeys().contains(KeyEvent.VK_A)) {
                // Dostosuj prędkość w trybie platformera
                double newX = player.getX() - (player.isJumping() ? 6 : 5);
                // Dostosuj warunek kolizji
                if (!mainWindow.getPlayerPanel().isCollision(newX, player.getY())) {
                    player.setX(newX);
                }
            }

            if (mainWindow.getPressedKeys().contains(KeyEvent.VK_D)) {
                // Dostosuj prędkość w trybie platformera
                double newX = player.getX() + (player.isJumping() ? 6 : 5);
                // Dostosuj warunek kolizji
                if (!mainWindow.getPlayerPanel().isCollision( newX,  player.getY())) {
                    player.setX(newX);
                }
            }
        }

        for (Portal portal : mainWindow.getPlayerPanel().getWorld().getPortals()) {

            if (isCollisionWithPortal(player.getX(),player.getY(), portal.getX(),  portal.getY())) {

                setCurrentGameMode(portal.getTargetGameMode());
            }
        }

        // Zresetuj flagę, jeśli gracz nie dotyka żadnego portalu
        boolean isTouchingAnySpeedPortal = false;
        speedPortalActivated = false; // Załóżmy, że gracz nie dotyka portalu
        for (SpeedPortal speedPortal : mainWindow.getPlayerPanel().getWorld().getSpeedPortals()) {
            if (isCollisionWithSpeedPortal(player.getX(), player.getY(), speedPortal.getX(), speedPortal.getY())) {
                // Sprawdź, czy prędkość już została zmieniona przez ten portal
                if (player.getPlayerSpeed() != player.getDefaultPlayerSpeed() * speedPortal.getSpeedMultiplier()) {
                    player.setPlayerSpeed(player.getDefaultPlayerSpeed() * speedPortal.getSpeedMultiplier());
                }
                speedPortalActivated = true; // Gracz dotyka portalu
                lastSpeedMultiplier = speedPortal.getSpeedMultiplier(); // Zapisz ostatnio użyty mnożnik
                break; // Wyjście z pętli, gdy znaleziono kolizję z portalem
            }
        }



        mainWindow.respawnPlayerIfNeeded(player);

        // Tryb platformer i statek - obsługa odwracania
        if (player.isPlatformer() && currentGameMode == GameMode.SHIP) {
            if (mainWindow.getPressedKeys().contains(KeyEvent.VK_A)) {
                player.setShipFlipped(true);
            } else if (mainWindow.getPressedKeys().contains(KeyEvent.VK_D)) {
                player.setShipFlipped(false);
            }
        }

        if (currentGameMode == GameMode.BALL) {
            if (player.isPlatformer()) {
                // Platformer i Ball - obracanie kontrolowane klawiszami
                if (mainWindow.getPressedKeys().contains(KeyEvent.VK_A)) {
                    player.setRotationAngle((player.getRotationAngle() - 5) % 360);
                } else if (mainWindow.getPressedKeys().contains(KeyEvent.VK_D)) {
                    player.setRotationAngle((player.getRotationAngle() + 5) % 360);
                }
            } else {
                // Ball (poza platformerem) - automatyczny obrót
                player.setRotationAngle((player.getRotationAngle() + 3) % 360);
            }

            // Reszta logiki dla trybu Ball (obsługa grawitacji, kolizji itp.)
            boolean isTouchingGround = mainWindow.getPlayerPanel().isCollisionWithCeilingOrFloor(player.getX(), player.getY(), player.isGravityReversed());
            boolean gameStarted = false; // Flaga, czy gra się rozpoczęła

            if ((mainWindow.getPressedKeys().contains(KeyEvent.VK_SPACE) || mainWindow.getPressedKeys().contains(KeyEvent.VK_UP)) && isTouchingGround) {
                player.setGravityReversed(!player.isGravityReversed());
                player.setVelocityY(0); // Reset velocity when gravity is reversed
                player.setJumping(false); // Pozwól na natychmiastową zmianę grawitacji w kolejnej klatce
                gameStarted = true;

                // Sprawdź, czy kula jest blisko sufitu po zmianie grawitacji
                double ceilingCheckY = player.isGravityReversed() ? player.getY() + 5 : player.getY() + 45; // 5 pikseli poniżej górnej krawędzi lub 5 pikseli powyżej dolnej
                if (mainWindow.getPlayerPanel().isCollisionWithCeilingOrFloor(player.getX(), ceilingCheckY, player.isGravityReversed())) {
                    player.setY(player.getY() + (player.isGravityReversed() ? -5 : 5)); // Przesuń o 5 pikseli
                }
            } else {
                // Apply normal gravity and velocity
                double newY = player.getY() + player.getVelocityY();
                double appliedGravity = player.isGravityReversed() ? -gravity : gravity;
                player.setVelocityY(player.getVelocityY() + appliedGravity);

                // Użyj isCollision na początku gry, isCollisionWithCeilingOrFloor w trakcie gry
                if (!mainWindow.getPlayerPanel().isCollision(player.getX(), newY)) {
                    player.setY(newY);
                    player.setJumping(true);
                } else {
                    player.setVelocityY(0);
                    player.setJumping(false);
                }
            }
        } else if (currentGameMode == GameMode.UFO) {
            // Logika dla trybu UFO
            if ((mainWindow.getPressedKeys().contains(KeyEvent.VK_SPACE) || mainWindow.getPressedKeys().contains(KeyEvent.VK_UP)) && !spaceOrUpPressed) {
                // Skok w trybie UFO (można skakać tylko po pojedynczym naciśnięciu klawisza)
                double testY = player.getY() + (player.isGravityReversed() ? 1 : -1);
                if (!mainWindow.getPlayerPanel().isCollision(player.getX(), testY)) {
                    player.setVelocityY(player.isGravityReversed() ? ufoJumpStrength : -ufoJumpStrength);
                    player.setJumping(true);
                    spaceOrUpPressed = true; // Zapamiętaj, że klawisz skoku został wciśnięty
                }
            }

            // Zwolnij flagę spaceOrUpPressed, gdy klawisz zostanie puszczony
            if (!mainWindow.getPressedKeys().contains(KeyEvent.VK_SPACE) && !mainWindow.getPressedKeys().contains(KeyEvent.VK_UP)) {
                spaceOrUpPressed = false;
            }

            updateVelocity();

            double newY = player.getY() + player.getVelocityY();
            double appliedGravity = player.isGravityReversed() ? -gravity : gravity;

            if (player.isJumping()) {
                player.setVelocityY(player.getVelocityY() + appliedGravity);
            }

            if (mainWindow.getPlayerPanel().isCollision(player.getX(), newY)) {
                // Kolizja w trybie UFO - zatrzymaj ruch w pionie
                player.setVelocityY(0);
                player.setJumping(false); // Zatrzymaj skakanie po kolizji
            } else {
                player.setY(newY);
                // Jeśli nie ma kolizji, sprawdź czy gracz zaczyna spadać
                if (!player.isJumping()) {
                    double testY = player.getY() + (player.isGravityReversed() ? -1 : 1);
                    if (!mainWindow.getPlayerPanel().isCollision(player.getX(), testY)) {
                        player.setJumping(true); // Zacznij spadanie jeśli nie ma kolizji pod spodem
                        player.setVelocityY(player.isGravityReversed() ? -gravity : gravity);
                    } else {
                        player.setVelocityY(0);
                        player.setJumping(false);
                    }
                }
            }
            // Brak obracania w trybie UFO
        } else if (currentGameMode == GameMode.WAVE) {
            // Logika dla trybu WAVE
            int waveSpeed = 5; // Prędkość poruszania się góra/dół w trybie Wave

            if (mainWindow.getPressedKeys().contains(KeyEvent.VK_SPACE) || mainWindow.getPressedKeys().contains(KeyEvent.VK_UP)) {
                // Ruch w górę
                double newY = player.getY() - waveSpeed;
                if (!mainWindow.getPlayerPanel().isCollision(player.getX(), newY)) {
                    player.setY(newY);
                }
            } else {
                // Ruch w dół
                double newY = player.getY() + waveSpeed;
                if (!mainWindow.getPlayerPanel().isCollision(player.getX(), newY)) {
                    player.setY(newY);
                }
            }
        } else if (currentGameMode == GameMode.SHIP) {
            // Logika dla trybu SHIP
            if (player.getOrbEffectDuration() > 0) {
                double newY = player.getY() + player.getVelocityY();
                if (mainWindow.getPlayerPanel().isCollision(player.getX(), newY)) {
                    player.setOrbEffectDuration(0);
                } else {
                    player.setY(newY);
                    if (player.getVelocityY() < 7) {
                        player.setVelocityY(player.getVelocityY() + 1);
                    } else {
                        if (mainWindow.getPressedKeys().contains(KeyEvent.VK_SPACE) || mainWindow.getPressedKeys().contains(KeyEvent.VK_UP)) {
                            if (player.isGravityReversed()) {
                                player.setVelocityY(Math.min(7, player.getVelocityY() + 1));
                                player.decrementOrbEffectActiveDuration();
                            } else {
                                player.setVelocityY(Math.max(-7, player.getVelocityY() - 1));
                                player.decrementOrbEffectActiveDuration();
                            }
                        }
                    }
                }
                player.decrementOrbEffectActiveDuration();
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
        } else {
            if (currentGameMode == GameMode.ROBOT) {
                boolean isTouchingGround = mainWindow.getPlayerPanel().isCollisionWithCeilingOrFloor((int)player.getX(), (int)player.getY(), player.isGravityReversed());

                if (mainWindow.getPressedKeys().contains(KeyEvent.VK_SPACE) || mainWindow.getPressedKeys().contains(KeyEvent.VK_UP)) {
                    if (!robotBoostActive && isTouchingGround) {
                        // Rozpocznij skok tylko jeśli dotyka podłoża
                        robotBoostActive = true;
                        robotBoostStartTime = System.currentTimeMillis();
                        player.setVelocityY(player.isGravityReversed() ? robotJumpStrength : -robotJumpStrength);
                        player.setJumping(true);
                    } else if (robotBoostActive) {
                        // Kontynuuj skok, jeśli czas boostu nie minął
                        long boostDuration = System.currentTimeMillis() - robotBoostStartTime;
                        if (boostDuration < maxRobotBoostDuration) {
                            float boostProgress = (float) boostDuration / maxRobotBoostDuration;
                            int currentJumpStrength = (int) (robotJumpStrength * (1 + boostProgress));
                            player.setVelocityY(player.isGravityReversed() ? currentJumpStrength : -currentJumpStrength);
                        } else {
                            // Zakończ boost, jeśli czas minął
                            robotBoostActive = false;
                            // Zacznij zmniejszać prędkość, symulując działanie grawitacji
                            player.setVelocityY(player.isGravityReversed() ? player.getVelocityY()- gravity : player.getVelocityY() + gravity);
                        }
                    }
                } else {
                    // Zakończ skok, gdy klawisz jest zwolniony
                    robotBoostActive = false;
                }

                if (player.isJumping()) {
                    double newY = player.getY() + player.getVelocityY();
                    double appliedGravity = player.isGravityReversed() ? -gravity : gravity;

                    // Stopniowo zmniejszaj prędkość, jeśli boost nie jest aktywny, ale nie zeruj od razu
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
                    // Sprawdź, czy gracz zaczyna spadać
                    double testY = player.getY() + (player.isGravityReversed() ? -1 : 1);
                    if (!mainWindow.getPlayerPanel().isCollision(player.getX(), testY)) {
                        player.setJumping(true);
                        player.setVelocityY(player.isGravityReversed() ? -gravity : gravity);
                    } else {
                        player.setJumping(false);
                        player.setVelocityY(0);
                    }
                }
            } else if (currentGameMode == GameMode.SPIDER) {
                // Logika dla trybu SPIDER
                boolean isTouchingGround = mainWindow.getPlayerPanel().isCollisionWithCeilingOrFloor((int)player.getX(), (int)player.getY(), player.isGravityReversed());

                if ((mainWindow.getPressedKeys().contains(KeyEvent.VK_SPACE) || mainWindow.getPressedKeys().contains(KeyEvent.VK_UP)) && isTouchingGround) {
                    if (!spiderTeleportPerformed) { // Sprawdź, czy teleportacja nie została już wykonana
                        spiderteleport(player, mainWindow.getPlayerPanel().getWorld());
                        player.setGravityReversed(!player.isGravityReversed());
                        spiderTeleportPerformed = true; // Ustaw flagę na true, aby zablokować kolejne teleportacje
                    }
                } else {
                    // Zresetuj flagę, gdy klawisz jest zwolniony
                    spiderTeleportPerformed = false;
                }

                // Obsługa grawitacji (opadanie, jeśli nie jest na ziemi)
                if (!isTouchingGround) {
                    double newY = player.getY() + (player.isGravityReversed() ? -5 : 5);
                    if (!mainWindow.getPlayerPanel().isCollision(player.getX(), newY)) {
                        player.setY(newY);
                    }
                }
            }

            // Inne tryby gry
            else if (player.isOrbEffectActive()) {
                double newY = player.getY() + player.getVelocityY();
                if (!mainWindow.getPlayerPanel().isCollision(player.getX(),newY)) {
                    player.setY(newY);
                } else {
                    player.setOrbEffectActive(false);
                }
            }  else {
                if (mainWindow.getPressedKeys().contains(KeyEvent.VK_SPACE) || mainWindow.getPressedKeys().contains(KeyEvent.VK_UP)) {
                    if (player.isSpiderOrbActivated() && mainWindow.getInputHandler().isSpiderOrbKeyPressed()) {
                        player.setVelocityY(0);
                        player.setJumping(false);
                    } else if (!player.isJumping()) {
                        double testY = player.getY() + (player.isGravityReversed() ? 1 : -1);
                        if (!mainWindow.getPlayerPanel().isCollision(player.getX(),testY)) {
                            player.setVelocityY(player.isGravityReversed() ? jumpStrength : -jumpStrength);
                            player.setJumping(true);
                        }
                    }
                }

                updateVelocity();

                if (player.isJumping()) {
                    double newY = player.getY() + player.getVelocityY();
                    double appliedGravity = player.isGravityReversed() ? -gravity : gravity;
                    player.setVelocityY(player.getVelocityY() + appliedGravity);
//
                    if (mainWindow.getPlayerPanel().isCollision(player.getX(), newY))
                    {
                        player.setJumping(false);
//
//                        // Uproszczona korekta pozycji, bez pętli while
                        if (player.isGravityReversed()) {
                            if (player.getVelocityY() < 0) {
                                while (mainWindow.getPlayerPanel().isCollision(player.getX(), newY))
                                {
                                    newY += 2;
                                }
                            } else {
                                while (mainWindow.getPlayerPanel().isCollision(player.getX(), newY))
                                {
                                    newY -= 2;
                                }
                            }
                        } else {
                            if (player.getVelocityY() > 0) {
                                while (mainWindow.getPlayerPanel().isCollision(player.getX(), newY))
                                {
                                    newY -= 2;
                                }
                            } else {
                                while (mainWindow.getPlayerPanel().isCollision(player.getX(), newY))
                                {
                                    newY += 2;
                                }
                            }
                        }

                        player.setY(newY);
                        player.setVelocityY(0);
                    } else {
                        player.setY(newY);
                    }
                    if (mainWindow.getPressedKeys().contains(KeyEvent.VK_A)) {
                        player.setRotationAngle((player.getRotationAngle() - 5 * (player.isGravityReversed() ? -1 : 1)) % 360);
                    } else if (mainWindow.getPressedKeys().contains(KeyEvent.VK_D)) {
                        player.setRotationAngle((player.getRotationAngle() + 5 * (player.isGravityReversed() ? -1 : 1)) % 360);
                    } else {
                        player.setRotationAngle((player.getRotationAngle() + 3 * (player.isGravityReversed() ? -1 : 1)) % 360);
                    }
                } else {
                    double testY = player.getY() + (player.isGravityReversed() ? -1 : 1);
                    if (!mainWindow.getPlayerPanel().isCollision(player.getX(), testY)) {
                        player.setJumping(true);
                        player.setVelocityY(player.isGravityReversed() ? -gravity : gravity);
                    } else {
                        player.setJumping(false);
                        player.setVelocityY(0);
                        player.setRotationAngle(Math.round(player.getRotationAngle() / 90) * 90);
                    }
                }
            }
        }

        checkPadActivation(mainWindow.getPlayerPanel());

        if (player.isTeleport()) {
            teleportToNearestSurface(player, mainWindow.getPlayerPanel().getWorld());
            player.setTeleport(false);
        }

        for (Spike spike : mainWindow.getPlayerPanel().getWorld().getSpikes()) {
            if (mainWindow.getPlayerPanel().isCollision(player.getX(), player.getY(), spike.getX(), spike.getY())) {
                mainWindow.die(player, mainWindow);
            }
        }

        Player.setStaticX((int)player.getX());

        int cameraTargetX = (int) (Player.getStaticX() - (double) panelWidth / 2);
        double newCameraOffsetX = Math.max(cameraTargetX, -200);
        mainWindow.getPlayerPanel().setCameraOffsetX((int) newCameraOffsetX);



    }

    public boolean isGamePaused() {
        return gamePaused;
    }



    public void checkPadActivation(PlayerPanel playerPanel) {
        for (Pad pad : World.getPads()) {
            if (playerPanel.isCollision(player.getX(), player.getY(), pad.getX(), pad.getY())) {
                System.out.println("Aktywacja pada!");
                pad.activate(player);
            }
        }
    }

    private void spiderteleport(Player player, World world) {
        double playerX = player.getX();
        double playerY = player.getY();
        double nearestY = playerY;
        double minDistance = Integer.MAX_VALUE;

        if (!player.isGravityReversed()) {
            // Grawitacja odwrócona - szukaj najbliższej powierzchni NAD graczem
            nearestY = getNearestY(world, playerX, playerY, nearestY, minDistance);
        } else {
            // Normalna grawitacja - szukaj najbliższej powierzchni POD graczem
            for (Tile tile : world.getTiles()) {
                if (tile.isSolid() &&
                        tile.getX() * 50 < playerX + 50 &&
                        tile.getX() * 50 + 50 > playerX &&
                        tile.getY() * 50 > playerY) { // Zmieniony warunek - szukamy płytek POD graczem

                    double distance = tile.getY() * 50 - playerY;
                    if (distance > 0 && distance < minDistance) { // Sprawdzamy, czy dystans jest dodatni
                        System.out.println("Znaleziono powierzchnię pod graczem: " + tile.getX() + ", " + tile.getY());
                        minDistance = distance;
                        nearestY = tile.getY() * 50 - 50;
                    }
                }
            }
        }

        player.setY(nearestY);
        player.setTeleport(false);
        player.setTeleportActivated(true);
    }

    private double getNearestY(World world, double playerX, double playerY, double nearestY, double minDistance) {
        for (Tile tile : world.getTiles()) {
            if (tile.isSolid() &&
                    tile.getX() * 50 < playerX + 50 &&
                    tile.getX() * 50 + 50 > playerX &&
                    tile.getY() * 50 < playerY) { // Zmieniony warunek - szukamy płytek NAD graczem

                double distance = playerY - (tile.getY() * 50 + 50);
                if (distance > 0 && distance < minDistance) { // Sprawdzamy, czy dystans jest dodatni
                    System.out.println("Znaleziono powierzchnię nad graczem: " + tile.getX() + ", " + tile.getY());
                    minDistance = distance;
                    nearestY = tile.getY() * 50 + 50;
                }
            }
        }
        return nearestY;
    }

    public boolean isCollisionWithSpeedPortal(double playerX, double playerY, double portalX, double portalY) {
        double playerWidth = 50;
        double playerHeight = 50;
        double portalWidth = 50;
        double portalHeight = 50;

        // Przeskaluj współrzędne portalu do pikseli
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

        // Przeskaluj współrzędne portalu do pikseli
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
        double playerX = player.getX();
        double playerY = player.getY();
        double nearestY = playerY;
        double minDistance = Integer.MAX_VALUE;

        if (player.isGravityReversed()) {
            // Grawitacja odwrócona szukaj najbliższej powierzchni NAD graczem
            nearestY = getNearestY(world, playerX, playerY, nearestY, minDistance);
        } else {
            // Normalna grawitacja szukaj najbliższej powierzchni POD graczem
            for (Tile tile : world.getTiles()) {
                if (tile.isSolid() &&
                        tile.getX() * 50 < playerX + 50 &&
                        tile.getX() * 50 + 50 > playerX &&
                        tile.getY() * 50 > playerY) { // Zmieniony warunek szukamy płytek POD graczem

                    double distance = tile.getY() * 50 - playerY;
                    if (distance > 0 && distance < minDistance) { // Sprawdzamy, czy dystans jest dodatni
                        System.out.println("Znaleziono powierzchnię pod graczem: " + tile.getX() + ", " + tile.getY());
                        minDistance = distance;
                        nearestY = tile.getY() * 50 - 50;
                    }
                }
            }
        }

        player.setY(nearestY);
        player.setTeleport(false);
        player.setTeleportActivated(true);
    }

    public boolean isRunning() {
        return running;
    }
}