package org.example.game.ui;

import org.example.game.engine.GameEngine;
import org.example.game.world.LevelData;
import org.example.game.world.World;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

public class EditLevelMenu extends JFrame {
    private final MainWindow mainWindow;
    private final GameEngine gameEngine;
    private JPanel levelSelectionPanel;

    public EditLevelMenu(MainWindow mainWindow, GameEngine gameEngine) {
        super("Edit Level");
        this.mainWindow = mainWindow;
        this.gameEngine = gameEngine;
        mainWindow.setEditLevelMenu(this);
        mainWindow.getToolSelectionPanel().setSaveButtonVisibility(true);

        setSize(300, 200);
        setLocationRelativeTo(mainWindow);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
        buttonPanel.add(new JButton("Create Level"));
        buttonPanel.add(new JButton("Edit Existing Level"));

        setFocusable(true);
        requestFocus();

        for (Component comp : buttonPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                if (button.getText().equals("Create Level")) {
                    button.addActionListener(e -> createLevel());
                } else if (button.getText().equals("Edit Existing Level")) {
                    button.addActionListener(e -> chooseLevelToEdit());
                }
            }
        }

        add(buttonPanel, BorderLayout.NORTH);

        JButton backButton = new JButton("Powrót");
        backButton.addActionListener(e -> {
            dispose();
            mainWindow.showMainMenu();
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Dodaj KeyListener po ustawieniu wszystkich komponentów i przed setVisible(true)
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (mainWindow.getInputHandler().isEditingMode()) {
                    if (evt.getKeyCode() == KeyEvent.VK_1) {
                        mainWindow.getPlayerPanel().setSelectedTool(1);
                    } else if (evt.getKeyCode() == KeyEvent.VK_2) {
                        mainWindow.getPlayerPanel().setSelectedTool(2);
                    } else if (evt.getKeyCode() == KeyEvent.VK_3) {
                        mainWindow.getPlayerPanel().setSelectedTool(3);
                    } else if (evt.getKeyCode() == KeyEvent.VK_4) {
                        mainWindow.getPlayerPanel().setSelectedTool(4);
                    }
                }
            }
        });

        // Ustaw focus na EditLevelMenu, aby KeyListener działał
        setFocusable(true);
        requestFocus();

        setVisible(true);
    }

    private void chooseLevelToEdit() {
        JDialog levelDialog = new JDialog(mainWindow, "Choose Level to Edit", true);
        levelDialog.setSize(400, 300);
        levelDialog.setLocationRelativeTo(mainWindow);
        levelDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        levelSelectionPanel = new JPanel();
        levelSelectionPanel.setLayout(new GridLayout(0, 3));
        levelDialog.add(levelSelectionPanel, BorderLayout.CENTER);

        JButton backButton = new JButton("Powrót");
        backButton.addActionListener(e -> {
            levelDialog.dispose();
            setVisible(true); // Pokaż ponownie EditLevelMenu
            mainWindow.showMainMenu(); // Dodane: Pokaż MainMenu
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(backButton);
        levelDialog.add(bottomPanel, BorderLayout.SOUTH);

        String currentLevelPath = mainWindow.getCurrentLevelPath();
        mainWindow.setCurrentLevelPath(currentLevelPath);

        loadLevels(levelSelectionPanel, levelDialog);

        setVisible(false); // Ukryj EditLevelMenu
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
                        editExistingLevel(levelFile);
                        parentDialog.dispose();
                    });
                    panel.add(levelButton);
                }
            }
        }

        panel.revalidate();
        panel.repaint();
    }

    private void createLevel() {
        World world = new World();
        mainWindow.getPlayerPanel().setWorld(world);
        mainWindow.respawnPlayerIfNeeded(mainWindow.getPlayerPanel().getPlayer());
        mainWindow.getPlayerPanel().setVisible(true);
        mainWindow.setVisible(true);
        mainWindow.getInputHandler().setEditingMode(true);
        mainWindow.addToolSelectionPanel();
        mainWindow.addToolSelectionPanel(); // Poprawione wywołanie - bez argumentu editLevelMenu


        JOptionPane.showMessageDialog(this, "New empty level created. Click 'Save As...' to save it to a file.");
        dispose();
    }

    private void editExistingLevel(File levelFile) {
        mainWindow.setCurrentLevelPath(levelFile.getAbsolutePath());
        mainWindow.addToolSelectionPanel(this);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            LevelData levelData = objectMapper.readValue(levelFile, LevelData.class);
            World world = new World();
            world.loadLevelData(levelData);
            mainWindow.getPlayerPanel().setWorld(world);
            mainWindow.respawnPlayerIfNeeded(mainWindow.getPlayerPanel().getPlayer());
            mainWindow.getPlayerPanel().setVisible(true);
            mainWindow.setVisible(true);
            mainWindow.getInputHandler().setEditingMode(true);

            mainWindow.addToolSelectionPanel(); // Dodaj ToolSelectionPanel

            mainWindow.startGame(levelFile);

            JOptionPane.showMessageDialog(this, "Level loaded from " + levelFile.getName() + " for editing.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading level from file: " + e.getMessage());
        } finally {
            dispose();
        }
    }
}