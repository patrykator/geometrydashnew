package org.example.game.ui;

import org.example.game.engine.GameEngine;
import org.example.game.engine.InputHandler;
import org.example.game.world.LevelData;
import org.example.game.world.World;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MainMenu extends JDialog {
    private final MainWindow mainWindow;
    private final GameEngine gameEngine;
    private JPanel levelSelectionPanel;

    public MainMenu(MainWindow mainWindow, GameEngine gameEngine) {
        super(mainWindow, "Geometry Dash Main Menu", true);
        this.mainWindow = mainWindow;
        this.gameEngine = gameEngine;

        setSize(300, 200);
        setLocationRelativeTo(mainWindow);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(3, 1));

        addButton("Start Game", this::newGame);
        addButton("Edit Level", this::editLevel);
        addButton("Exit", this::exitGame);

        setVisible(false);
    }

    private void addButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(e -> {
            if (text.equals("Exit Level")) {
                System.out.println("Exit Level");
                gameEngine.exitToMainMenu();
            }
            action.run();
            // Nie zamykaj MainMenu po naciśnięciu przycisku!
            // dispose();
        });
        add(button);
    }

    private void chooseLevel() {
        JDialog levelDialog = new JDialog(mainWindow, "Choose Level", true);
        levelDialog.setSize(400, 300);
        levelDialog.setLocationRelativeTo(mainWindow);
        levelDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        levelSelectionPanel = new JPanel();
        levelSelectionPanel.setLayout(new GridLayout(0, 3));
        levelDialog.add(levelSelectionPanel, BorderLayout.CENTER);

        JButton backButton = new JButton("Powrót");
        backButton.addActionListener(e -> {
            levelDialog.dispose();
            setVisible(true); // Pokaż ponownie MainMenu
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(backButton);
        levelDialog.add(bottomPanel, BorderLayout.SOUTH);

        loadLevels(levelSelectionPanel, levelDialog);

        setVisible(false); // Ukryj MainMenu przed pokazaniem okna dialogowego
        levelDialog.setVisible(true);
    }

    private void loadLevels(JPanel panel, JDialog parentDialog) {
        panel.removeAll();

        File levelsDir = new File("src/levels");
        if (levelsDir.exists() && levelsDir.isDirectory()) {
            File[] levelFiles = levelsDir.listFiles((dir, name) -> name.endsWith(".json"));
            if (levelFiles != null) {
                for (File levelFile : levelFiles) {
                    String levelName = levelFile.getName().replace(".json", "");
                    JButton levelButton = new JButton(levelName);
                    levelButton.addActionListener(e -> {
                        startGame(levelFile);
                        parentDialog.dispose();
                    });
                    panel.add(levelButton);
                }
            }
        }

        panel.revalidate();
        panel.repaint();
    }

    private void showEditLevelMenu() {
        setVisible(false); // Ukryj MainMenu
        EditLevelMenu editLevelMenu = new EditLevelMenu(mainWindow, gameEngine);
        mainWindow.setEditLevelMenu(editLevelMenu);
        editLevelMenu.setVisible(true); // Pokaż EditLevelMenu
    }



    private void startGame(File levelFile) {
        mainWindow.showToolSelectionPanel(false);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            LevelData levelData = objectMapper.readValue(levelFile, LevelData.class);
            World world = new World();
            world.loadLevelData(levelData);
            mainWindow.getPlayerPanel().setWorld(world);
            mainWindow.respawnPlayerIfNeeded(mainWindow.getPlayerPanel().getPlayer());
            mainWindow.getPlayerPanel().setVisible(true);
            mainWindow.setVisible(true);
            mainWindow.hideMainMenu();
            mainWindow.startGame(levelFile);
            gameEngine.start();
            mainWindow.getPlayerPanel().removeSaveAsButton();

            // Zresetuj pozycję kamery
            mainWindow.getPlayerPanel().resetCameraPosition();

            JOptionPane.showMessageDialog(this, "Level loaded: " + levelFile.getName());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading level: " + e.getMessage());
        } finally {
            dispose(); // Zamknij MainMenu po załadowaniu poziomu
        }
    }

    private void newGame() {
        mainWindow.showToolSelectionPanel(false);
        chooseLevel();
    }

    private void editLevel() {
        mainWindow.showToolSelectionPanel(true);
        mainWindow.hideMainMenu(); // Ukryj MainMenu
        showEditLevelMenu();
    }


    private void showOptions() {
        JOptionPane.showMessageDialog(this, "Options not yet implemented.");
    }

    private void exitGame() {
        System.exit(0);
    }
}