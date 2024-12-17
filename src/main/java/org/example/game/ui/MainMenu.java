package org.example.game.ui;

import org.example.game.engine.GameEngine;
import org.example.game.engine.InputHandler;
import org.example.game.world.LevelData;
import org.example.game.world.World;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
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

        // Ustawienia okna
        setSize(400, 300);
        setLocationRelativeTo(mainWindow);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setUndecorated(true); // Usuń ramkę okna
        setLayout(new BorderLayout());

        // Stylowanie okna
        setBackground(new Color(0, 0, 0, 0)); // Przezroczyste tło

        // Panel główny
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                int width = getWidth();
                int height = getHeight();

                g2d.setColor(new Color(30, 30, 30)); // Ciemne tło
                g2d.fillRect(0, 0, width, height);

                g2d.dispose();
            }
        };
        mainPanel.setLayout(new GridLayout(3, 1, 0, 20)); // Zwiększony odstęp między przyciskami
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        add(mainPanel, BorderLayout.CENTER);

        // Dodaj przyciski
        addButton("Start Game", this::newGame, mainPanel);
        addButton("Edit Level", this::editLevel, mainPanel);
        addButton("Exit", this::exitGame, mainPanel);

        setVisible(false);
    }

    private void addButton(String text, Runnable action, JPanel panel) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.BOLD, 18)); // Nowoczesna czcionka
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(60, 60, 60)); // Ciemne tło
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.addActionListener(e -> {
            action.run();
            // Nie zamykaj MainMenu po naciśnięciu przycisku!
            // dispose();
        });

        // Efekt hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(90, 90, 90)); // Jaśniejsze tło po najechaniu
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(60, 60, 60)); // Powrót do oryginalnego tła
            }
        });

        panel.add(button);
    }

    private void chooseLevel() {
        JDialog levelDialog = new JDialog(mainWindow, "Choose Level", true);
        levelDialog.setSize(600, 450);
        levelDialog.setLocationRelativeTo(mainWindow);
        levelDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        levelDialog.setUndecorated(true);
        levelDialog.setBackground(new Color(0, 0, 0, 0));

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int width = getWidth();
                int height = getHeight();
                g2d.setColor(new Color(30, 30, 30));
                g2d.fillRect(0, 0, width, height);
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        levelDialog.setContentPane(mainPanel);

        levelSelectionPanel = new JPanel();
        levelSelectionPanel.setLayout(new GridLayout(0, 3, 10, 10));
        levelSelectionPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(levelSelectionPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JButton backButton = createStyledButton("Back");
        backButton.addActionListener(e -> {
            levelDialog.dispose();
            setVisible(true);
        });

        // Panel dla przycisku "Back" z większym marginesem
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backButtonPanel.setOpaque(false);
        backButtonPanel.setBorder(new EmptyBorder(20, 0, 0, 0)); // Dodano margines górny
        backButtonPanel.add(backButton);

        mainPanel.add(backButtonPanel, BorderLayout.SOUTH);

        loadLevels(levelSelectionPanel, levelDialog);

        setVisible(false);
        levelDialog.setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
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
                    String levelName = levelFile.getName().replace(".json", "");
                    JButton levelButton = createStyledButton(levelName);
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

    public void showEditLevelMenu() {
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

//            JOptionPane.showMessageDialog(this, "Level loaded: " + levelFile.getName());
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