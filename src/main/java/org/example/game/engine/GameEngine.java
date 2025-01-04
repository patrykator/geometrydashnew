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
    private Orb currentOrb;
    private double previousPlayerY;

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

    public void resetGameState() {
        MainWindow.setPlayerPosition(player, mainWindow.getPlayerPanel().getHeight());
        player.setVelocityY(0);
        player.setJumping(false);
        player.setRotationAngle(0); // Zresetuj kąt obrotu
        player.setGravityReversed(false);
        player.setSpiderOrbJustActivated(false);


        player.setOrbEffectDuration(0);
        player.setOrbEffectActive(false);
        player.setShipFlipped(false);
        setCurrentGameMode(GameMode.CUBE);
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
        double currentMinJumpSpeed;
        double maxFallSpeed;

        if (player.isUfoMode()) {
            if (player.getOrbEffectDuration() > 0) {

                if (player.isBlackOrbActive()) {
                    maxFallSpeed = player.isGravityReversed() ? -18 : 18;
                } else {
                    maxFallSpeed = player.isGravityReversed() ? 20 : -20;
                }

                // Użyj docelowej prędkości jako ograniczeń podczas trwania efektu orba

                currentMinJumpSpeed = player.getTargetOrbVelocity();

                // Ogranicz prędkość zawsze, gdy efekt orba jest aktywny
                if (player.getVelocityY() > maxFallSpeed) {
                    player.setVelocityY(maxFallSpeed);
                } else if (player.getVelocityY() < currentMinJumpSpeed) {
                    player.setVelocityY(currentMinJumpSpeed);
                }
            } else {

                player.setBlackOrbActive(false);
                // Poza efektem orba, użyj standardowych wartości dla UFO
                currentMinJumpSpeed = player.getOriginalMinJumpSpeed();
                maxFallSpeed = 16;

                // Ogranicz prędkość
                if (player.getVelocityY() > maxFallSpeed) {
                    player.setVelocityY(maxFallSpeed);
                } else if (player.getVelocityY() < currentMinJumpSpeed) {
                    player.setVelocityY(currentMinJumpSpeed);
                }
            }
            if (mainWindow.getPlayerPanel().isCollision(player.getX(), player.getY() + player.getVelocityY())) {
                player.setOrbEffectDuration(0);
                player.setOrbEffectActive(false);
            }
        } else {
            currentMinJumpSpeed = player.getOriginalMinJumpSpeed();
            maxFallSpeed = 16;

            // Logika dla trybów innych niż UFO - bez zmian
            double temporaryMaxFallSpeed = maxFallSpeed; // Wprowadź zmienną tymczasową

            if (player.getOrbEffectDuration() > 0) {
                temporaryMaxFallSpeed = player.isGravityReversed() ? 20 : -20;
                System.out.println("temporaryMaxFallSpeed" + temporaryMaxFallSpeed);
            }

            // Ogranicz prędkość używając temporaryMaxFallSpeed
            if (player.getVelocityY() > temporaryMaxFallSpeed) {
                player.setVelocityY(temporaryMaxFallSpeed);
            } else if (player.getVelocityY() < currentMinJumpSpeed) {
                player.setVelocityY(currentMinJumpSpeed);
            }
        }

        if (player.getOrbEffectDuration() > 0) {
            player.setOrbEffectDuration(player.getOrbEffectDuration() - 1);
        }

        // Wyłącz ograniczenie prędkości, jeśli orb jest aktywny w trybie SHIP
        if (player.isOrbEffectActive() && (player.getCurrentGameMode() == GameMode.SHIP)) {
            return;
        }
    }

    // Pomocnicza funkcja do sprawdzania, czy zbliżamy się do docelowej prędkości
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
        this.player.setCurrentGameMode(gameMode); // Ustaw tryb gry w Player
    }

    // In src/main/java/org/example/game/engine/GameEngine.java
    private void updateGameLogic() {
        previousPlayerY = player.getY();








        if (player.isSpiderOrbJustActivated()) {
            mainWindow.getPressedKeys().remove(KeyEvent.VK_SPACE);
            mainWindow.getPressedKeys().remove(KeyEvent.VK_UP);
            player.setSpiderOrbJustActivated(false); // Zresetuj flagę
            player.setInputBlockedAfterSpiderOrb(true); // Aktywuj flagę blokady inputu
        }

//        System.out.println("Velocity: " + player.getVelocityY());


        boolean isPlatformer = mainWindow.getPlayerPanel().getWorld().isPlatformer();
        player.setPlatformer(isPlatformer);


        GameMode currentGameMode = getCurrentGameMode();


        if (mainWindow.isAnimatingDeath() && mainWindow.fragmentAnimation != null) {
            mainWindow.fragmentAnimation.update();
            return;
        }

        if (player.getY() > 5000 || player.getY() < -5000) {
            mainWindow.instantDie(player); // Zmieniamy die() na instantDie()
            return;
        }



        // Tryb platformer jest niezależny od trybu gry

        if (!player.isPlatformer()) {
            double newX = player.getX() + player.getPlayerSpeed();
            // Sprawdzenie kolizji z kafelkami w trybie non-platformer i śmierć
            if (mainWindow.getPlayerPanel().isCollision( newX,  player.getY())) {
                mainWindow.die(player, mainWindow);
                return; // Zatrzymaj dalsze wykonywanie updateGameLogic() po śmierci
            } else {
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

                if (player.isPlatformer() && portal.getTargetGameMode() == GameMode.WAVE) {
                    continue;
                } else if (portal.getTargetGameMode() == player.getCurrentGameMode()) {
                    continue;
                } else {
                    setCurrentGameMode(portal.getTargetGameMode());
                }


                if (portal.getTargetGameMode() == GameMode.ROBOT) {
                    player.setJumping(false);
                }

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



//        mainWindow.respawnPlayerIfNeeded(player);

        // Tryb platformer i statek - obsługa odwracania
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

        if (currentGameMode == GameMode.BALL) {
            if (player.isPlatformer()) {
                // Platformer i Ball - obracanie kontrolowane klawiszami
                if (mainWindow.getPressedKeys().contains(KeyEvent.VK_A)) {
                    player.setRotationAngle((player.getRotationAngle() + (-5)) % 360);

                } else if (mainWindow.getPressedKeys().contains(KeyEvent.VK_D)) {
                    player.setRotationAngle((player.getRotationAngle() + (5)) % 360);
                }
            } else {
                // Ball (poza platformerem) - automatyczny obrótaaaa
                player.setRotationAngle((player.getRotationAngle() + (5)) % 360);
                System.out.println("Gravity" + player.isGravityReversed() + "rotation" + player.getRotationAngle());
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
        }   else if (currentGameMode == GameMode.UFO) {
            // Logika dla trybu UFO
            if (player.getOrbEffectDuration() > 0) {
                // Jeśli efekt orba jest aktywny, stopniowo zbliżaj prędkość do docelowej
                if (player.getVelocityY() > player.getTargetOrbVelocity()) {
                    player.setVelocityY(Math.max(player.getTargetOrbVelocity(), player.getVelocityY() - 1));
                } else if (player.getVelocityY() < player.getTargetOrbVelocity()) {
                    player.setVelocityY(Math.min(player.getTargetOrbVelocity(), player.getVelocityY() + 1));
                }
                // Nie pozwól na dodatkowy skok, gdy efekt orba jest aktywny
            } else if ((mainWindow.getPressedKeys().contains(KeyEvent.VK_SPACE) || mainWindow.getPressedKeys().contains(KeyEvent.VK_UP)) && !spaceOrUpPressed) {
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

            // Zastosuj grawitację tylko, gdy gracz skacze i efekt orba nie jest aktywny
            if (player.isJumping() && player.getOrbEffectDuration() <= 0) {
                player.setVelocityY(player.getVelocityY() + appliedGravity);
            }

            if (mainWindow.getPlayerPanel().isCollision(player.getX(), newY)) {
                // Kolizja w trybie UFO - zatrzymaj ruch w pionie i zresetuj flagę isJumping()
                player.setVelocityY(0);
                player.setJumping(false);
            } else {
                player.setY(newY);
                // Jeśli nie ma kolizji, sprawdź czy gracz zaczyna spadać
                if (!player.isJumping() && player.getOrbEffectDuration() <= 0) {
                    double testY = player.getY() + (player.isGravityReversed() ? -1 : 1);
                    if (!mainWindow.getPlayerPanel().isCollision(player.getX(), testY)) {
                        player.setJumping(true); // Zacznij spadanie jeśli nie ma kolizji pod spodem
                        player.setVelocityY(player.isGravityReversed() ? -gravity : gravity);
                    } else {
                        //player.setVelocityY(0);
                        player.setJumping(false); // Dodano: zresetuj isJumping(), jeśli dotyka podłogi
                    }
                }
            }
        }else if (currentGameMode == GameMode.WAVE) {
            // Logika dla trybu WAVE
            int waveSpeed = 5;

            if (mainWindow.getPressedKeys().contains(KeyEvent.VK_SPACE) || mainWindow.getPressedKeys().contains(KeyEvent.VK_UP)) {
                // Ruch w górę lub w dół w zależności od grawitacji
                double newY = player.getY() + (player.isGravityReversed() ? waveSpeed : -waveSpeed);
                if (!mainWindow.getPlayerPanel().isCollision(player.getX(), newY)) {
                    player.setY(newY);
                }
            } else {
                // Ruch w dół lub w górę w zależności od grawitacji
                double newY = player.getY() + (player.isGravityReversed() ? -waveSpeed : waveSpeed);
                if (!mainWindow.getPlayerPanel().isCollision(player.getX(), newY)) {
                    player.setY(newY);
                }
            }
        } else if (currentGameMode == GameMode.SHIP) {
            // Logika dla trybu SHIP
            if (player.getOrbEffectDuration() > 0) {
                player.setOrbEffectDuration(player.getOrbEffectDuration() - 1);

                // Stopniowo zbliżaj prędkość do docelowej
                if (player.getVelocityY() > player.getTargetOrbVelocity()) {
                    player.setVelocityY(Math.max(player.getTargetOrbVelocity(), player.getVelocityY() - (player.isGravityReversed() ? 1.0 : 2.0)));
                } else if (player.getVelocityY() < player.getTargetOrbVelocity()) {
                    player.setVelocityY(Math.min(player.getTargetOrbVelocity(), player.getVelocityY() + (player.isGravityReversed() ? 2.0 : 0.5)));
                }

                // Zastosuj nową prędkość
                double newY = player.getY() + player.getVelocityY();
                if (!mainWindow.getPlayerPanel().isCollision(player.getX(), newY)) {
                    player.setY(newY);
                } else {
                    // Jeśli jest kolizja, wyłącz efekt orba
                    player.setOrbEffectDuration(0);
                    player.setOrbEffectActive(false);
                }

                player.decrementOrbEffectActiveDuration();
                if (player.getOrbEffectDuration() <= 0) {
                    player.setOrbEffectActive(false);
                }
            }  else if (mainWindow.getPressedKeys().contains(KeyEvent.VK_SPACE) || mainWindow.getPressedKeys().contains(KeyEvent.VK_UP)) {
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
                boolean isTouchingGround = mainWindow.getPlayerPanel().isCollisionWithCeilingOrFloor(player.getX(), player.getY(), player.isGravityReversed());

                if ((mainWindow.getPressedKeys().contains(KeyEvent.VK_SPACE) || mainWindow.getPressedKeys().contains(KeyEvent.VK_UP)) && isTouchingGround) {
                    if (!spiderTeleportPerformed) {
                        // Teleportacja
                        spiderteleport(player, mainWindow.getPlayerPanel().getWorld());
                        player.setGravityReversed(!player.isGravityReversed());
                        spiderTeleportPerformed = true;

                        // Ustaw prędkość po teleportacji, aby kontynuować ruch
                        player.setVelocityY(player.isGravityReversed() ? -10 : 10); // Dostosuj prędkość do grawitacji
                    }
                } else {
                    // Zresetuj flagę, gdy klawisz jest zwolniony lub nie dotyka podłogi/sufitu
                    spiderTeleportPerformed = false;
                }

                // Zastosuj efekt orba, jeśli jest aktywny
                if (player.getOrbEffectDuration() > 0) {
                    if (player.getVelocityY() > player.getTargetOrbVelocity()) {
                        player.setVelocityY(Math.max(player.getTargetOrbVelocity(), player.getVelocityY() - 0.5));
                    } else if (player.getVelocityY() < player.getTargetOrbVelocity()) {
                        player.setVelocityY(Math.min(player.getTargetOrbVelocity(), player.getVelocityY() + 0.5));
                    }

                    // Zmniejsz licznik duration, ale tylko jeśli zbliżamy się do targetVelocity
                    if (isApproachingTargetVelocity(player.getVelocityY(), player.getTargetOrbVelocity())) {
                        player.decrementOrbEffectDuration();
                    }
                } else {
                    // Standardowa grawitacja, gdy efekt orba nie jest aktywny
                    double appliedGravity = player.isGravityReversed() ? -gravity : gravity;
                    player.setVelocityY(player.getVelocityY() + appliedGravity);
                }

                // Ogranicz prędkość, ale tylko jeśli nie zbliżamy się do docelowej
                if (player.getOrbEffectDuration() <= 0 || !isApproachingTargetVelocity(player.getVelocityY(), player.getTargetOrbVelocity())) {
                    if (player.getVelocityY() > 16) {
                        player.setVelocityY(16);
                    } else if (player.getVelocityY() < -16) {
                        player.setVelocityY(-16);
                    }
                }

                // Obsługa kolizji
                double newY = player.getY() + player.getVelocityY();
                if (mainWindow.getPlayerPanel().isCollision(player.getX(), newY)) {
                    player.setVelocityY(0); // Zatrzymaj prędkość przy kolizji

                    // Jeśli kolizja z sufitem/podłogą, zresetuj efekt orba
                    if (player.getOrbEffectDuration() > 0) {
                        player.setOrbEffectDuration(0);
                        player.setOrbEffectActive(false);
                    }
                } else {
                    player.setY(newY);
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
                    } else if (!player.isJumping() && player.canJumpAfterCollision()) { // Dodano warunek canJumpAfterCollision()
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

                    if (mainWindow.getPlayerPanel().isCollision(player.getX(), newY))
                    {
                        player.setJumping(false);
                        player.setCanJumpAfterCollision(false);

                        // Uproszczona korekta pozycji, bez pętli while
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
                        player.setVelocityY(0); // To miejsce sprawiało problem
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
                        player.setCanJumpAfterCollision(true); // Ustaw flagę na true, gdy gracz dotyka ziemi
                    }
                }
            }
        }

        checkPadActivation(mainWindow.getPlayerPanel());

        if (player.isTeleport()) {
            teleportToNearestSurface(player, mainWindow.getPlayerPanel().getWorld());
            player.setTeleport(false);
        }

        if (player.isteleportPad()) {
            teleportToNearestSurfacePad(player, mainWindow.getPlayerPanel().getWorld());
            player.setTeleportPad(false);
        }

        for (Spike spike : mainWindow.getPlayerPanel().getWorld().getSpikes()) {
            if (mainWindow.getPlayerPanel().isCollision(player.getX(), player.getY(), spike.getX(), spike.getY())) {
                mainWindow.die(player, mainWindow);
            }
        }


        Player.setStaticX((int) player.getX());
        Player.setStaticY((int) player.getY());

        int panelWidth = mainWindow.getPlayerPanel().getWidth();
        int panelHeight = mainWindow.getPlayerPanel().getHeight();
        int cameraTargetX = (int) (Player.getStaticX() - (double) panelWidth / 2);

// Modyfikacja dla cameraOffsetY
        double cameraTargetY = mainWindow.getPlayerPanel().getCameraOffsetY(); // Inicjalizacja aktualną pozycją kamery
        double upperThreshold = 0.1; // 10% od góry
        double lowerThreshold = 0.9; // 10% od dołu
        int titleBarHeight = 35; // Wysokość paska tytułowego

        if (Player.getStaticY() < panelHeight * upperThreshold - titleBarHeight - 5) {
            // Gracz w górnych 10% (plus wysokość paska), kamera podąża za graczem do góry
            if (Player.getStaticY() < 55) {
                System.out.println("Gracz poza górnym limitem");
                cameraTargetY = Math.min(0, Player.getStaticY() - 40);
                System.out.println("cameraTargetY: " + cameraTargetY);
            } else {
                cameraTargetY = Math.max(0, Player.getStaticY() - 50); // Poprawiony wzór
            }

        } else if (Player.getStaticY() > panelHeight * lowerThreshold) {
            // Gracz w dolnych 10%, kamera podąża za graczem
            cameraTargetY = (int) (Player.getStaticY() - panelHeight * lowerThreshold);
        } else {
            // Gracz w środkowych 80%, kamera pozostaje na tej samej wysokości
            // Nie zmieniamy cameraTargetY
        }

        cameraTargetY = Math.max(-4435, Math.min(cameraTargetY, 3500));
        System.out.println(player.getY());

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
            nearestY = getNearestY(world, playerX, playerY, nearestY, minDistance, true);
        } else {
            // Normalna grawitacja - szukaj najbliższej powierzchni POD graczem
            nearestY = getNearestY(world, playerX, playerY, nearestY, minDistance, false);
        }

        // Sprawdź, czy nearestY zostało zmienione (czyli czy znaleziono powierzchnię)
        if (nearestY != playerY) {
            player.setY(nearestY);
            // Zmień grawitację tylko, jeśli teleportacja się udała
            player.setVelocityY(player.isGravityReversed() ? -10 : 10); // Dostosuj prędkość do grawitacji
        } else {
            player.setGravityReversed(!player.isGravityReversed());
        }

        player.setTeleport(false);
        player.setTeleportActivated(true);
        spiderTeleportPerformed = false; // Resetuj flagę, niezależnie od tego, czy teleportacja się udała, czy nie
    }

    private double getNearestY(World world, double playerX, double playerY, double nearestY, double minDistance, boolean searchAbove) {
        for (Tile tile : world.getTiles()) {
            if (tile.isSolid() &&
                    tile.getX() * 50 < playerX + 50 &&
                    tile.getX() * 50 + 50 > playerX) {

                double distance;
                if (searchAbove) {
                    // Szukaj NAD graczem
                    if (tile.getY() * 50 < playerY) {
                        distance = playerY - (tile.getY() * 50 + 50);
                    } else {
                        continue; // Płytka nie jest nad graczem
                    }
                } else {
                    // Szukaj POD graczem
                    if (tile.getY() * 50 > playerY) {
                        distance = tile.getY() * 50 - playerY;
                    } else {
                        continue; // Płytka nie jest pod graczem
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

    private String getOrbColorActivated(Player player) {
        for (Orb orb : mainWindow.getPlayerPanel().getWorld().getOrbs()) {
            if (Math.abs(player.getX() - orb.getX() * 50) < 50 && Math.abs(player.getY() - orb.getY() * 50) < 50) {
                return orb.getColor();
            }
        }
        return null; // Żaden orb nie jest aktywowany
    }

    private void teleportToNearestSurface(Player player, World world) {

        // Pobierz aktywowany orb
        currentOrb = mainWindow.getPlayerPanel().getActivatedOrb(player);

//         Sprawdź, czy orb został znaleziony
        if (currentOrb == null) {
            System.out.println("Brak aktywowanego orba");
            return;
        }

        double playerX = player.getX();
        double playerY = player.getY();
        double nearestY = playerY;
        double minDistance = Integer.MAX_VALUE;
        boolean surfaceFound = false;

        if (currentOrb.getDirection().equals("up")) {
            nearestY = getNearestY(world, playerX, playerY, nearestY, minDistance, true);
        } else if (currentOrb.getDirection().equals("down")) {
            nearestY = getNearestY(world, playerX, playerY, nearestY, minDistance, false);
        }

        if (nearestY != playerY) {
            player.setY(nearestY);
            player.setTeleport(false);
            player.setTeleportActivated(true);
            player.setVelocityY(0);
            player.setJumping(false);
            if ("up".equals(currentOrb.getDirection())) {
                   player.setGravityReversed(true);
              } else if ("down".equals(currentOrb.getDirection())) {
                player.setGravityReversed(false);
              }
        }
        // Zmien grawitacje tylko jesli surfaceFound jest true i jest to orb pajaka

        if (!surfaceFound) {
            System.out.println("Brak powierzchni do teleportacji");
        }
    }


    private void teleportToNearestSurfacePad(Player player, World world) {

        // Pobierz aktywowany pad
        Pad currentPad = mainWindow.getPlayerPanel().getActivatedPad(player);

        // Sprawdź, czy pad został znaleziony
        if (currentPad == null) {
            System.out.println("Brak aktywowanego pada");
            return;
        }

        double playerX = player.getX();
        double playerY = player.getY();
        double nearestY = playerY;
        double minDistance = Integer.MAX_VALUE;

        if (currentPad.getDirection().equals("top")) {
            nearestY = getNearestY(world, playerX, playerY, nearestY, minDistance, true);
        } else if (currentPad.getDirection().equals("bottom")) {
            nearestY = getNearestY(world, playerX, playerY, nearestY, minDistance, false);
        }

        if (nearestY != playerY) {
            player.setY(nearestY);
            player.setTeleport(false);
            player.setTeleportActivated(true);
            player.setVelocityY(0);
            player.setJumping(false);

            System.out.println(currentPad.getDirection());
            if ("top".equals(currentPad.getDirection())) {
                System.out.println("Grawitacja odwrócona");
                player.setGravityReversed(true);
            } else if ("bottom".equals(currentPad.getDirection())) {
                System.out.println("Grawitacja normalna");
                player.setGravityReversed(false);
            }
        }

    }

    public boolean isRunning() {
        return running;
    }
}