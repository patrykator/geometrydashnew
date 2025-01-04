package org.example.game.ui;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.game.animations.FragmentAnimation;
import org.example.game.engine.GameEngine;
import org.example.game.engine.InputHandler;
import org.example.game.entities.*;
import org.example.game.world.LevelData;
import org.example.game.world.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class MainWindow extends JFrame {
    private PlayerPanel playerPanel;
    private InputHandler inputHandler;
    private Timer timer;
    private boolean isFullScreen = false;
    private final int GRAVITY = 1;
    private final int JUMP_STRENGTH = 16;
    private int attempts = 1;
    private MainMenu mainMenu;
    private GameEngine gameEngine;
    private ToolSelectionPanel toolSelectionPanel;
    private String currentLevelPath;
    private JSplitPane splitPane;
    private EditLevelMenu editLevelMenu;
    public FragmentAnimation fragmentAnimation;
    private boolean isAnimatingDeath = false;
    private long animationDelay = 750;
    private GraphicsDevice graphicsDevice;
    private DisplayMode originalDisplayMode;






    public MainWindow(Player player, World world) {
        setTitle("Main Window");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);




        playerPanel = new PlayerPanel(player, world, this);
        playerPanel.setVisible(false); // Początkowo ukryty

        add(playerPanel);
        System.out.println("MainWindow - PlayerPanel hashcode: " + playerPanel.hashCode());




        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    inputHandler.setEditingMode(!inputHandler.isEditingMode());
                }
            }
        });


        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, playerPanel, null);
        splitPane.setResizeWeight(1.0);
        splitPane.setDividerSize(0);
        splitPane.setEnabled(false); // splitPane ma być nieaktywny, gdy nie ma ToolSelectionPanel

        toolSelectionPanel = new ToolSelectionPanel(this);
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(toolSelectionPanel);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, playerPanel, rightPanel); // Inicjalizacja splitPane
        splitPane.setResizeWeight(1.0);
        splitPane.setDividerSize(0);
        add(splitPane, BorderLayout.CENTER);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        graphicsDevice = ge.getDefaultScreenDevice();
        originalDisplayMode = graphicsDevice.getDisplayMode();
    }

    public void toggleFullScreen() {
        MainWindow mainWindow = this;
        if (isFullScreen) {
            // Wyjdź z trybu pełnoekranowego
            dispose(); // Zwolnij zasoby okna
            setUndecorated(false);

            // Przywróć oryginalny tryb wyświetlania, jeśli to możliwe
            if (graphicsDevice.isDisplayChangeSupported()) {
                graphicsDevice.setDisplayMode(originalDisplayMode);
            }



            // Ustaw rozmiar okna na oryginalny rozmiar
            setSize(1600, 900 + 35); // Ustaw domyślny rozmiar okna

            setVisible(true);
            setLocationRelativeTo(null);
            isFullScreen = false;
        } else {
            dispose(); // Zwolnij zasoby okna
            setUndecorated(true);

            // Ustaw tryb pełnoekranowy, jeśli to możliwe
            if (graphicsDevice.isFullScreenSupported()) {
                // Użyj Exclusive Full-Screen Mode
                graphicsDevice.setFullScreenWindow(this);
            } else {
                // Jeśli Exclusive Full-Screen Mode nie jest wspierany,
                // ustaw okno na cały ekran
                setExtendedState(JFrame.MAXIMIZED_BOTH);
            }

            setVisible(true);
            isFullScreen = true;
        }
    }

    public boolean isFullScreen() {
        return isFullScreen;
    }

    public void startGame(File levelFile) {
        System.out.println("MainWindow.startGame() called");
    }

    public void hideEditLevelMenu() {
        if (editLevelMenu != null) {
            editLevelMenu.setVisible(false);
        }
    }


    public void setInputHandler(InputHandler inputHandler) {
        System.out.println("MainWindow.setInputHandler() called, new InputHandler: " + inputHandler);
        // Usuń istniejący InputHandler, jeśli istnieje
        for (var kl : getKeyListeners()) {
            if (kl instanceof InputHandler) {
                removeKeyListener(kl);
            }
        }

        this.inputHandler = inputHandler;
        addKeyListener(inputHandler);
        inputHandler.setEditingMode(false);
    }


    public void removeToolSelectionPanel() {
        if (splitPane != null) {
            splitPane.remove(splitPane.getRightComponent()); // Zmiana: usuń prawy komponent
        }
        toolSelectionPanel.setVisible(false);
        //Nie trzeba usuwać toolSelectionPanel, bo jest usuwane przez splitPane
        //remove(toolSelectionPanel);
        revalidate();
        repaint();
    }

    public void setEditLevelMenu(EditLevelMenu editLevelMenu) {
        this.editLevelMenu = editLevelMenu;
    }

    public void addToolSelectionPanel() {
        if (toolSelectionPanel == null) {
            toolSelectionPanel = new ToolSelectionPanel(this);
        }

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(toolSelectionPanel);

        if (splitPane == null) {
            splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, playerPanel, rightPanel);
            splitPane.setResizeWeight(1.0);
            splitPane.setDividerSize(0);
            add(splitPane, BorderLayout.CENTER);
        } else {
            splitPane.setRightComponent(rightPanel);
        }

        splitPane.setEnabled(true);

        playerPanel.setInputHandler(inputHandler);
        playerPanel.addMouseListenersToPlayerPanel(inputHandler);
        playerPanel.requestFocusInWindow();

        revalidate();
        repaint();
    }

    public void addToolSelectionPanel(EditLevelMenu editLevelMenu) {
        if (toolSelectionPanel == null) {
            toolSelectionPanel = new ToolSelectionPanel(this);
        }

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(toolSelectionPanel);

        if (splitPane == null) {
            splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, playerPanel, rightPanel);
            splitPane.setResizeWeight(1.0);
            splitPane.setDividerSize(0);
            add(splitPane, BorderLayout.CENTER);
        } else {
            splitPane.setRightComponent(rightPanel);
        }

        splitPane.setEnabled(true);

        playerPanel.setInputHandler(inputHandler);
        playerPanel.addMouseListenersToPlayerPanel(inputHandler);
        playerPanel.requestFocusInWindow();

        revalidate();
        repaint();
    }



    public void showMainMenu() {
        if (mainMenu == null) {
            mainMenu = new MainMenu(this, gameEngine);
        }
        mainMenu.setVisible(true);
    }


    public void hideMainMenu(){
        if (mainMenu != null) {
            getToolSelectionPanel().setSaveButtonVisibility(false);
            mainMenu.setVisible(false);
        }
    }


    public void setGameEngine(GameEngine gameEngine){
        this.gameEngine = gameEngine;
        if (inputHandler == null && gameEngine != null) {
            inputHandler = gameEngine.getInputHandler();
            addKeyListener(inputHandler);
        }
    }

    public GameEngine getGameEngine(){
        return gameEngine;
    }


    public void respawnPlayerIfNeeded(Player player) {
        if (player.getY() > getHeight()) {
            die(player, this);
        }
    }

    public Set<Integer> getPressedKeys() {
        return inputHandler.getPressedKeys();
    }

    public PlayerPanel getPlayerPanel() {
        return playerPanel;
    }

    public String getCurrentLevelPath() {
        return currentLevelPath;
    }

    public void setCurrentLevelPath(String currentLevelPath) {
        this.currentLevelPath = currentLevelPath;
    }

    public ToolSelectionPanel getToolSelectionPanel() {
        return toolSelectionPanel;
    }

    public void updateGameLogic() {
        playerPanel.repaint();
    }

    public static Player createPlayer() {
        return new Player(1, "Player", "Opis gracza", "sciezka/do/obrazu.jpg", "typ", 1, 100, 0, 100);
    }

    public int getAttempts() {
        return attempts;
    }

    public void incrementAttempts() {
        attempts++;
    }

//    public static World createWorld() {
//        World world = new World();
//        for (int i = -4; i < 400; i++) {
//            world.addTile(new Tile(i, 17, true));
//        }
//
//        for (int i = -4; i < 400; i++) {
//            world.addTile(new Tile(i, 0, true));
//        }
//
//        for (int i = 0; i <= 17; i++) {
//            world.addTile(new Tile(-4, i, true));
//        }
//
//        world.addTile(new Tile(3, 14, true));
//        world.addTile(new Tile(3, 3, true));
//
//
//
//
////                world.addSpike(new Spike(5, 16, "src/main/java/org/example/game/img/spike.png"));
////                world.addSpike(new Spike(6, 16, "src/main/java/org/example/game/img/spike.png"));
//
//        // dol
//        for (int i = 10; i <= 30; i += 2) {
//            world.addOrb(new Orb(i, 14, "red"));
//        }
//        for (int i = 40; i <= 60; i += 2) {
//            world.addOrb(new Orb(i, 14, "yellow"));
//        }
//        for (int i = 70; i <= 90; i += 2) {
//            world.addOrb(new Orb(i, 14, "blue"));
//        }
//        for (int i = 100; i <= 120; i += 2) {
//            world.addOrb(new Orb(i, 14, "purple"));
//        }
//        for (int i = 130; i <= 150; i += 2) {
//            world.addOrb(new Orb(i, 14, "green"));
//        }
//        for (int i = 160; i <= 180; i += 2) {
//            world.addOrb(new Orb(i, 14, "black"));
//        }
//        for (int i = 190; i <= 210; i += 2) {
//            world.addOrb(new Orb(i, 14, "spider", "up"));
//        }
//
//        // gora
//        for (int i = 10; i <= 30; i += 2) {
//            world.addOrb(new Orb(i, 3, "red"));
//        }
//        for (int i = 40; i <= 60; i += 2) {
//            world.addOrb(new Orb(i, 3, "yellow"));
//        }
//        for (int i = 70; i <= 90; i += 2) {
//            world.addOrb(new Orb(i, 3, "blue"));
//        }
//        for (int i = 100; i <= 120; i += 2) {
//            world.addOrb(new Orb(i, 3, "purple"));
//        }
//        for (int i = 130; i <= 150; i += 2) {
//            world.addOrb(new Orb(i, 2, "green"));
//        }
//        for (int i = 160; i <= 180; i += 2) {
//            world.addOrb(new Orb(i, 3, "black"));
//        }
//        for (int i = 190; i <= 210; i += 2) {
//            world.addOrb(new Orb(i, 3, "spider", "down"));
//        }
//
//        world.addOrb(new Orb(220, 14, "teleport", 10, 600));
//
//        world.addTile(new Tile(17, 8, true));
//
//        world.addTile(new Tile(16, 8, true));
//        world.addTile(new Tile(15, 8, true));
//        world.addTile(new Tile(14, 8, true));
//        world.addTile(new Tile(13, 8, true));
//        world.addTile(new Tile(12, 8, true));
//
    ////                world.addPad(new Pad(5, 16, "yellow", "bottom"));
    ////                world.addPad(new Pad(7, 16, "purple","bottom"));
    ////                world.addPad(new Pad(9, 16, "red","bottom"));
    ////                world.addPad(new Pad(11, 16, "blue","bottom"));
    ////                world.addPad(new Pad(13, 16, "spider","bottom", "up"));
    ////                world.addPad(new Pad(15, 16, "spider","bottom", "down"));
//
//        world.addPad(new Pad(5, 3, "yellow","top"));
//        world.addPad(new Pad(7, 3, "purple","top"));
//        world.addPad(new Pad(9, 3, "red","top"));
//        world.addPad(new Pad(11, 3, "blue","top"));
//        world.addPad(new Pad(13, 3, "spider","top", "up"));
//        world.addPad(new Pad(15, 3, "spider","top", "down"));
//
//
//
//        return world;
//    }

    // W klasie MainWindow
    public void instantDie(Player player) {
        player.setVelocityY(0);
        player.setX(100);
        player.setY(playerPanel.getHeight() - 100);
        player.setRotationAngle(0);
        player.setGravityReversed(false);
        playerPanel.setCameraOffsetY(0);
        playerPanel.setCameraOffsetX(-200);
        gameEngine.resetGameState();
        incrementAttempts();
        isAnimatingDeath = false;
        player.setCurrentGameMode(GameMode.CUBE);
        repaint();
        player.setOrbEffectDuration(0);
        player.setOrbEffectActive(false);

        playerPanel.resetCameraPosition();
    }


    public void showToolSelectionPanel(boolean show) {
        toolSelectionPanel.setVisible(show);
    }

    public void saveLevelToJson(World world, String filePath) {
        LevelData levelData = world.toLevelData();

        ObjectMapper objectMapper = new ObjectMapper();

        try (FileWriter writer = new FileWriter(filePath)) {
            objectMapper.writeValue(writer, levelData);
            System.out.println("File saved to: " + new File(filePath).getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving level: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showSaveAsDialog() {
        String fileName = JOptionPane.showInputDialog(this, "Enter new file name for saving:", "Save As", JOptionPane.QUESTION_MESSAGE);
        if (fileName != null && !fileName.isEmpty()) {
            if (!fileName.endsWith(".json")) {
                fileName += ".json";
            }

            fileName = "src\\levels\\" + fileName;

            boolean isPlatformer = playerPanel.getWorld().isPlatformer();
            playerPanel.getWorld().setPlatformer(isPlatformer);

            saveLevelToJson(playerPanel.getWorld(), fileName);
            JOptionPane.showMessageDialog(this, "Level saved to " + fileName);
        } else {
            JOptionPane.showMessageDialog(this, "File name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void configureMainWindow(MainWindow mainWindow, int gameWidth, int gameHeight) {
        mainWindow.setVisible(true);

        Insets insets = mainWindow.getInsets();
        int frameWidth = insets.left + insets.right;
        int frameHeight = insets.top + insets.bottom;

        mainWindow.setSize(new Dimension(gameWidth + frameWidth, gameHeight + frameHeight));
        mainWindow.setLocationRelativeTo(null);
    }

    public void setPlayerPanel(PlayerPanel newPlayerPanel) {
        this.playerPanel = newPlayerPanel;
    }


    public static void setPlayerPosition(Player player, int gameHeight) {
        player.setX(100);
        player.setY(gameHeight - 100);
    }

    public void die(Player player, MainWindow mainWindow) {
        if (!isAnimatingDeath) {

            isAnimatingDeath = true;
            // Rozpocznij animację rozpadu
            BufferedImage playerImage = null;

            // Pobierz odpowiedni obrazek w zależności od trybu gry
            if (player.getCurrentGameMode() == GameMode.CUBE) {
                playerImage = imageToBufferedImage(mainWindow.getPlayerPanel().getPlayerImage());
            } else if (player.getCurrentGameMode() == GameMode.SHIP) {
                playerImage = imageToBufferedImage(mainWindow.getPlayerPanel().getShipImage());
            } else if (player.getCurrentGameMode() == GameMode.BALL) {
                playerImage = imageToBufferedImage(mainWindow.getPlayerPanel().getBallModeImage());
            } else if (player.getCurrentGameMode() == GameMode.UFO) {
                playerImage = imageToBufferedImage(mainWindow.getPlayerPanel().getUfoImage());
            } else if (player.getCurrentGameMode() == GameMode.WAVE) {
                playerImage = imageToBufferedImage(mainWindow.getPlayerPanel().getWaveImage());
            } else if (player.getCurrentGameMode() == GameMode.ROBOT) {
                playerImage = imageToBufferedImage(mainWindow.getPlayerPanel().getRobotImage());
            } else if (player.getCurrentGameMode() == GameMode.SPIDER) {
                playerImage = imageToBufferedImage(mainWindow.getPlayerPanel().getSpiderImage());
            }

            if (playerImage != null) {
                // Zwiększ initial speed w FragmentAnimation
                fragmentAnimation = new FragmentAnimation(player.getX(), player.getY(), playerImage, 8, 8, 4, 0.2); // Zmieniono z 1 na 3
            }

            // Uruchom wątek, który opóźni respawn
            new Thread(() -> {
                while (fragmentAnimation != null && !fragmentAnimation.isAnimationFinished()) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                try {
                    Thread.sleep(animationDelay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    playerPanel.setCameraOffsetY(0);
                    playerPanel.setCameraOffsetX(-200);
                    gameEngine.resetGameState();
                    incrementAttempts();
                    isAnimatingDeath = false;
                    player.setCurrentGameMode(GameMode.CUBE);
                    repaint();
                });
            }).start();
        }
    }

    private BufferedImage imageToBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        BufferedImage bufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bufferedImage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bufferedImage;
    }

    public boolean isAnimatingDeath() {
        return isAnimatingDeath;
    }

    public InputHandler getInputHandler() {
        return inputHandler;
    }
}
