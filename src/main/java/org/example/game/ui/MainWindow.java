package org.example.game.ui;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainWindow extends JFrame {
    private PlayerPanel playerPanel;
    private InputHandler inputHandler;
    private boolean isFullScreen = false;
    private int attempts = 1;
    private MainMenu mainMenu;
    private GameEngine gameEngine;
    private ToolSelectionPanel toolSelectionPanel;
    private String currentLevelPath;
    private JSplitPane splitPane;
    private EditLevelMenu editLevelMenu;
    public  FragmentAnimation fragmentAnimation;
    private boolean isAnimatingDeath = false;
    private GraphicsDevice graphicsDevice;
    private DisplayMode originalDisplayMode;
    private static final Logger logger = LoggerFactory.getLogger(MainWindow.class);

    public MainWindow(Player player, World world) {
        initializeWindow();
        initializePlayerPanel(player, world);
        initializeInputHandler();
        initializeToolSelectionPanel();
        initializeSplitPane();
        initializeGraphicsDevice();
    }

    private void initializeWindow() {
        setTitle("Main Window");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initializePlayerPanel(Player player, World world) {
        playerPanel = new PlayerPanel(player, world, this);
        playerPanel.setVisible(false);
        add(playerPanel);
        System.out.println("MainWindow - PlayerPanel hashcode: " + playerPanel.hashCode());
    }

    private void initializeInputHandler() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e) && inputHandler != null) {
                    inputHandler.setEditingMode(!inputHandler.isEditingMode());
                }
            }
        });
    }


    private void initializeToolSelectionPanel() {
        toolSelectionPanel = new ToolSelectionPanel(this);
    }

    private void initializeSplitPane() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(toolSelectionPanel);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, playerPanel, rightPanel);
        splitPane.setResizeWeight(1.0);
        splitPane.setDividerSize(0);
        splitPane.setEnabled(false);
        add(splitPane, BorderLayout.CENTER);
    }

    private void initializeGraphicsDevice() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        graphicsDevice = ge.getDefaultScreenDevice();
        originalDisplayMode = graphicsDevice.getDisplayMode();
    }

    public void toggleFullScreen() {
        if (isFullScreen) {
            exitFullScreenMode();
        } else {
            enterFullScreenMode();
        }
    }

    private void exitFullScreenMode() {
        dispose();
        setUndecorated(false);

        if (graphicsDevice.isDisplayChangeSupported()) {
            graphicsDevice.setDisplayMode(originalDisplayMode);
        }

        setSize(1600, 900 + 35);
        setVisible(true);
        setLocationRelativeTo(null);
        isFullScreen = false;
    }

    private void enterFullScreenMode() {
        dispose();
        setUndecorated(true);

        if (graphicsDevice.isFullScreenSupported()) {
            graphicsDevice.setFullScreenWindow(this);
        } else {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        setVisible(true);
        isFullScreen = true;
    }

    public boolean isFullScreen() {
        return isFullScreen;
    }

    public void hideEditLevelMenu() {
        if (editLevelMenu != null) {
            editLevelMenu.setVisible(false);
        }
    }

    public void setInputHandler(InputHandler inputHandler) {
        System.out.println("MainWindow.setInputHandler() called, new InputHandler: " + inputHandler);
        removeExistingInputHandler();

        this.inputHandler = inputHandler;
        addKeyListener(inputHandler);
    }


    private void removeExistingInputHandler() {
        for (var kl : getKeyListeners()) {
            if (kl instanceof InputHandler) {
                removeKeyListener(kl);
            }
        }
    }

    public void removeToolSelectionPanel() {
        if (splitPane != null) {
            splitPane.remove(splitPane.getRightComponent());
        }
        toolSelectionPanel.setVisible(false);
        revalidate();
        repaint();
    }

    public void setEditLevelMenu(EditLevelMenu editLevelMenu) {
        this.editLevelMenu = editLevelMenu;
    }

    public void addToolSelectionPanel() {
        setupToolSelectionPanel();
        configureSplitPaneForToolPanel();
        playerPanel.requestFocusInWindow();
        revalidate();
        repaint();
    }

    private void setupToolSelectionPanel() {
        if (toolSelectionPanel == null) {
            toolSelectionPanel = new ToolSelectionPanel(this);
        }
        toolSelectionPanel.setVisible(true);
    }

    private void configureSplitPaneForToolPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(toolSelectionPanel);

        if (splitPane == null) {
            splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, playerPanel, rightPanel);
            add(splitPane, BorderLayout.CENTER);
        } else {
            splitPane.setRightComponent(rightPanel);
        }
        splitPane.setResizeWeight(1.0);
        splitPane.setDividerSize(0);
        splitPane.setEnabled(true);
    }


    public void showMainMenu() {
        if (mainMenu == null) {
            mainMenu = new MainMenu(this, gameEngine);
        }
        mainMenu.setVisible(true);
    }

    public void hideMainMenu() {
        if (mainMenu != null) {
            getToolSelectionPanel().setSaveButtonVisibility(false);
            mainMenu.setVisible(false);
        }
    }

    public void setGameEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        if (inputHandler == null && gameEngine != null) {
            inputHandler = gameEngine.getInputHandler();
            addKeyListener(inputHandler);
        }
    }

    public GameEngine getGameEngine() {
        return gameEngine;
    }

    public void respawnPlayerIfNeeded(Player player) {
        if (player.getY() > getHeight()) {
            die(player);
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


    public static Player createPlayer() {
        return new Player("Player", 0, 100);
    }

    public int getAttempts() {
        return attempts;
    }

    public void incrementAttempts() {
        attempts++;
    }

    public void instantDie(Player player) {
        resetPlayerState(player);
        incrementAttempts();
        isAnimatingDeath = false;
        repaint();
    }

    private void resetPlayerState(Player player) {
        player.setVelocityY(0);
        player.setX(100);
        player.setY(playerPanel.getHeight() - 100);
        player.setRotationAngle(0);
        player.setGravityReversed(false);
        playerPanel.resetCameraPosition();
        gameEngine.resetGameState();
        player.setCurrentGameMode(GameMode.CUBE);
        player.setOrbEffectDuration(0);
        player.setOrbEffectActive(false);
    }

    public void showToolSelectionPanel(boolean show) {
        toolSelectionPanel.setVisible(show);
    }

    public void saveLevelToJson(World world, String filePath) {
        LevelData levelData = world.toLevelData();
        ObjectMapper objectMapper = new ObjectMapper();

        try (FileWriter writer = new FileWriter(filePath)) {
            objectMapper.writeValue(writer, levelData);
            logger.info("File saved to: {}", new File(filePath).getAbsolutePath());
        } catch (IOException e) {
            logger.error("Error saving level to file: {}", filePath, e);
            JOptionPane.showMessageDialog(this, "Error saving level: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showSaveAsDialog() {
        String fileName = JOptionPane.showInputDialog(this, "Enter new file name for saving:", "Save As", JOptionPane.QUESTION_MESSAGE);
        if (fileName != null && !fileName.isEmpty()) {
            fileName = formatFileName(fileName);
            saveLevel(fileName);
        } else {
            JOptionPane.showMessageDialog(this, "File name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formatFileName(String fileName) {
        if (!fileName.endsWith(".json")) {
            fileName += ".json";
        }
        return "src\\levels\\" + fileName;
    }

    private void saveLevel(String fileName) {
        boolean isPlatformer = playerPanel.getWorld().isPlatformer();
        playerPanel.getWorld().setPlatformer(isPlatformer);
        saveLevelToJson(playerPanel.getWorld(), fileName);
        JOptionPane.showMessageDialog(this, "Level saved to " + fileName);
    }

    public static void configureMainWindow(MainWindow mainWindow, int gameWidth, int gameHeight) {
        mainWindow.setVisible(true);
        Insets insets = mainWindow.getInsets();
        int frameWidth = insets.left + insets.right;
        int frameHeight = insets.top + insets.bottom;
        mainWindow.setSize(new Dimension(gameWidth + frameWidth, gameHeight + frameHeight));
        mainWindow.setLocationRelativeTo(null);
    }

    public static void setPlayerPosition(Player player, int gameHeight) {
        player.setX(100);
        player.setY(gameHeight - 100);
    }

    public void die(Player player) {
        if (!isAnimatingDeath) {
            isAnimatingDeath = true;
            BufferedImage playerImage = getPlayerImage(player);
            initializeFragmentAnimation(player, playerImage);
            initiateDeathAnimationSequence(player);
        }
    }

    private BufferedImage getPlayerImage(Player player) {
        Image image = switch (player.getCurrentGameMode()) {
            case CUBE -> getPlayerPanel().getPlayerImage();
            case SHIP -> getPlayerPanel().getShipImage();
            case BALL -> getPlayerPanel().getBallModeImage();
            case UFO -> getPlayerPanel().getUfoImage();
            case WAVE -> getPlayerPanel().getWaveImage();
            case ROBOT -> getPlayerPanel().getRobotImage();
            case SPIDER -> getPlayerPanel().getSpiderImage();
            default -> null;
        };
        return imageToBufferedImage(image);
    }

    private void initializeFragmentAnimation(Player player, BufferedImage playerImage) {
        if (playerImage != null) {
            fragmentAnimation = new FragmentAnimation(player.getX(), player.getY(), playerImage, 8, 8, 4, 0.2);
        }
    }

    private void initiateDeathAnimationSequence(Player player) {
        new Thread(() -> {
            waitForAnimationToFinish();
            delayBeforeRespawn();
            SwingUtilities.invokeLater(() -> resetGameAfterDeath(player));
        }).start();
    }

    private void waitForAnimationToFinish() {
        FragmentAnimation animation = fragmentAnimation;
        while (animation != null && !animation.isAnimationFinished()) {
            try {
                synchronized (animation) {
                    animation.wait(10);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void delayBeforeRespawn() {
        try {
            long animationDelay = 750;
            Thread.sleep(animationDelay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void resetGameAfterDeath(Player player) {
        playerPanel.resetCameraPosition();
        gameEngine.resetGameState();
        incrementAttempts();
        isAnimatingDeath = false;
        player.setCurrentGameMode(GameMode.CUBE);
        repaint();
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