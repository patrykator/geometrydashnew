package org.example.game.ui;

import org.example.Main;
import org.example.game.engine.GameEngine;
import org.example.game.engine.InputHandler;
import org.example.game.world.LevelData;
import org.example.game.world.World;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;

public class EditLevelMenu extends JFrame {
    private final MainWindow mainWindow;
    private final GameEngine gameEngine;
    private JPanel levelSelectionPanel;
    private MainMenu mainmenu;

    public EditLevelMenu(MainWindow mainWindow, GameEngine gameEngine) {
        super("Edit Level");
        this.mainWindow = mainWindow;
        this.gameEngine = gameEngine;
        mainWindow.setEditLevelMenu(this);
        mainWindow.getToolSelectionPanel().setSaveButtonVisibility(true);

        // Ustawienia okna
        setSize(400, 300);
        setLocationRelativeTo(mainWindow);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setLayout(new BorderLayout());
        setBackground(new Color(0, 0, 0, 0));

        // Panel główny
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                int width = getWidth();
                int height = getHeight();
                g2d.setColor(new Color(30, 30, 30));
                g2d.fillRect(0, 0, width, height);
                g2d.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout()); // Zmieniono na BorderLayout
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        add(mainPanel, BorderLayout.CENTER);

        // Panel z przyciskami Create i Edit
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        buttonsPanel.setOpaque(false); // Ustaw przezroczystość

        addButton("Create Level", this::createLevel, buttonsPanel);
        addButton("Edit Existing Level", this::chooseLevelToEdit, buttonsPanel);

        // Dodanie panelu z przyciskami Create i Edit do głównego panelu
        mainPanel.add(buttonsPanel, BorderLayout.CENTER);

        // Przycisk powrotu w osobnym panelu, aby był wyśrodkowany
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backButtonPanel.setOpaque(false); // Ustaw przezroczystość, aby dziedziczył tło
        backButtonPanel.setBorder(new EmptyBorder(20, 0, 0, 0)); // Dodano margines
        JButton backButton = createStyledButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            mainWindow.showMainMenu();
        });
        backButtonPanel.add(backButton);

        // Dodanie panelu z przyciskiem "Back" na dole głównego panelu
        mainPanel.add(backButtonPanel, BorderLayout.SOUTH);

        // KeyListener
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

        setFocusable(true);
        requestFocus();
        setVisible(true);
    }

    private void addButton(String text, Runnable action, JPanel panel) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(60, 60, 60));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.addActionListener(e -> action.run());

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(90, 90, 90));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(60, 60, 60));
            }
        });

        panel.add(button);
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

    private void chooseLevelToEdit() {
        JDialog levelDialog = new JDialog(mainWindow, "Choose Level to Edit", true);
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

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(20, 0, 0, 0)); // Dodano margines
        bottomPanel.add(backButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        loadLevels(levelSelectionPanel, levelDialog);

        setVisible(false);
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
                    JButton levelButton = createStyledButton(levelName);
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
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading level from file: " + e.getMessage());
        } finally {
            dispose();
        }
    }
}