package org.example.game.ui;

import org.example.game.engine.GameEngine;
import org.example.game.world.LevelData;
import org.example.game.world.World;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MainMenu extends JDialog {
    private final MainWindow mainWindow;
    private final GameEngine gameEngine;

    public MainMenu(MainWindow mainWindow, GameEngine gameEngine) {
        super(mainWindow, "Geometry Dash Main Menu", true);
        this.mainWindow = mainWindow;
        this.gameEngine = gameEngine;

        setupDialog();
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);
        addButtons(mainPanel);

        setVisible(false);
    }

    private void setupDialog() {
        setSize(400, 300);
        setLocationRelativeTo(mainWindow);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setLayout(new BorderLayout());
        setBackground(new Color(0, 0, 0, 0));
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(30, 30, 30));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        mainPanel.setLayout(new GridLayout(3, 1, 0, 20));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        return mainPanel;
    }

    private void addButtons(JPanel mainPanel) {
        addButton("Start Game", this::newGame, mainPanel);
        addButton("Edit Level", this::editLevel, mainPanel);
        addButton("Exit", this::exitGame, mainPanel);
    }

    private void addButton(String text, Runnable action, JPanel panel) {
        JButton button = createStyledButton(text);
        button.addActionListener(e -> action.run());
        addHoverEffect(button);
        panel.add(button);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(60, 60, 60));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        return button;
    }

    private void addHoverEffect(JButton button) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(90, 90, 90));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(60, 60, 60));
            }
        });
    }

    private void chooseLevel() {
        JDialog levelDialog = createLevelDialog();
        JPanel mainPanel = createLevelDialogMainPanel();
        levelDialog.setContentPane(mainPanel);

        JPanel levelSelectionPanel = createLevelSelectionPanel();
        JScrollPane scrollPane = createScrollPane(levelSelectionPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel backButtonPanel = createBackButtonPanel(levelDialog);
        mainPanel.add(backButtonPanel, BorderLayout.SOUTH);

        loadLevels(levelSelectionPanel, levelDialog);

        setVisible(false);
        levelDialog.setVisible(true);
    }

    private JDialog createLevelDialog() {
        JDialog levelDialog = new JDialog(mainWindow, "Choose Level", true);
        levelDialog.setSize(600, 450);
        levelDialog.setLocationRelativeTo(mainWindow);
        levelDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        levelDialog.setUndecorated(true);
        levelDialog.setBackground(new Color(0, 0, 0, 0));
        return levelDialog;
    }

    private JPanel createLevelDialogMainPanel() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(30, 30, 30));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return mainPanel;
    }

    private JPanel createLevelSelectionPanel() {
        JPanel levelSelectionPanel = new JPanel();
        levelSelectionPanel.setLayout(new GridLayout(0, 3, 10, 10));
        levelSelectionPanel.setOpaque(false);
        return levelSelectionPanel;
    }

    private JScrollPane createScrollPane(JPanel panel) {
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    private JPanel createBackButtonPanel(JDialog levelDialog) {
        JButton backButton = createStyledBackButton();
        backButton.addActionListener(e -> {
            levelDialog.dispose();
            setVisible(true);
        });

        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backButtonPanel.setOpaque(false);
        backButtonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        backButtonPanel.add(backButton);
        return backButtonPanel;
    }
    private JButton createStyledBackButton() {
        JButton button = new JButton("Back");
        button.setFont(new Font("Roboto", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(60, 60, 60));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                new EmptyBorder(8, 16, 8, 16)
        ));
        button.setFocusPainted(false);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(90, 90, 90));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(60, 60, 60));
            }
        });
        return button;
    }

    private void loadLevels(JPanel panel, JDialog parentDialog) {
        panel.removeAll();
        File levelsDir = new File("src/levels");
        if (levelsDir.exists() && levelsDir.isDirectory()) {
            File[] levelFiles = levelsDir.listFiles((dir, name) -> name.endsWith(".json"));
            if (levelFiles != null) {
                for (File levelFile : levelFiles) {
                    addLevelButton(panel, levelFile, parentDialog);
                }
            }
        }
        panel.revalidate();
        panel.repaint();
    }

    private void addLevelButton(JPanel panel, File levelFile, JDialog parentDialog) {
        String levelName = levelFile.getName().replace(".json", "");
        JButton levelButton = createStyledButton(levelName);
        levelButton.addActionListener(e -> {
            startGame(levelFile);
            parentDialog.dispose();
        });
        panel.add(levelButton);
    }

    public void showEditLevelMenu() {
        setVisible(false);
        EditLevelMenu editLevelMenu = new EditLevelMenu(mainWindow);
        mainWindow.setEditLevelMenu(editLevelMenu);
        editLevelMenu.setVisible(true);
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
            gameEngine.start();
            mainWindow.getPlayerPanel().removeSaveAsButton();
            mainWindow.getPlayerPanel().resetCameraPosition();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading level: " + e.getMessage());
        } finally {
            dispose();
        }
    }

    private void newGame() {
        mainWindow.showToolSelectionPanel(false);
        chooseLevel();
    }

    private void editLevel() {
        mainWindow.showToolSelectionPanel(true);
        mainWindow.hideMainMenu();
        showEditLevelMenu();
    }

    private void exitGame() {
        System.exit(0);
    }
}