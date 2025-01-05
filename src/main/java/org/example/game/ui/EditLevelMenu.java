package org.example.game.ui;

import org.example.game.world.LevelData;
import org.example.game.world.World;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

public class EditLevelMenu extends JFrame {
    private final MainWindow mainWindow;

    public EditLevelMenu(MainWindow mainWindow) {
        super("Edit Level");
        this.mainWindow = mainWindow;
        mainWindow.setEditLevelMenu(this);
        mainWindow.getToolSelectionPanel().setSaveButtonVisibility(true);

        setupWindow();
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);
        JPanel buttonsPanel = createButtonsPanel();
        mainPanel.add(buttonsPanel, BorderLayout.CENTER);
        JPanel backButtonPanel = createBackButtonPanel();
        mainPanel.add(backButtonPanel, BorderLayout.SOUTH);
        addKeyListenerToWindow();

        setVisible(true);
    }

    private void setupWindow() {
        setSize(400, 300);
        setLocationRelativeTo(mainWindow);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setLayout(new BorderLayout());
        setBackground(new Color(0, 0, 0, 0));
        setFocusable(true);
        requestFocus();
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
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        return mainPanel;
    }

    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        buttonsPanel.setOpaque(false);

        addButton("Create Level", this::createLevel, buttonsPanel);
        addButton("Edit Existing Level", this::chooseLevelToEdit, buttonsPanel);

        return buttonsPanel;
    }

    private JPanel createBackButtonPanel() {
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backButtonPanel.setOpaque(false);
        backButtonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        JButton backButton = createStyledButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            mainWindow.showMainMenu();
        });
        backButtonPanel.add(backButton);
        return backButtonPanel;
    }

    private void addKeyListenerToWindow() {
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                handleKeyPressed(evt);
            }
        });
    }

    private void handleKeyPressed(KeyEvent evt) {
        if (mainWindow.getInputHandler().isEditingMode()) {
            switch (evt.getKeyCode()) {
                case KeyEvent.VK_1:
                    mainWindow.getPlayerPanel().setSelectedTool(1);
                    break;
                case KeyEvent.VK_2:
                    mainWindow.getPlayerPanel().setSelectedTool(2);
                    break;
                case KeyEvent.VK_3:
                    mainWindow.getPlayerPanel().setSelectedTool(3);
                    break;
                case KeyEvent.VK_4:
                    mainWindow.getPlayerPanel().setSelectedTool(4);
                    break;
            }
        }
    }

    private void addButton(String text, Runnable action, JPanel panel) {
        JButton button = createButton(text);
        button.addActionListener(e -> action.run());
        panel.add(button);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(60, 60, 60));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        addHoverEffect(button);
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
        addHoverEffect(button);
        return button;
    }

    private void chooseLevelToEdit() {
        JDialog levelDialog = createLevelDialog();
        JPanel mainPanel = createLevelDialogMainPanel();
        levelDialog.setContentPane(mainPanel);

        JPanel levelSelectionPanel = createLevelSelectionPanel();
        JScrollPane scrollPane = createScrollPane(levelSelectionPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = createLevelDialogBottomPanel(levelDialog);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        loadLevels(levelSelectionPanel, levelDialog);

        setVisible(false);
        levelDialog.setVisible(true);
    }

    private JDialog createLevelDialog() {
        JDialog levelDialog = new JDialog(mainWindow, "Choose Level to Edit", true);
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

    private JPanel createLevelDialogBottomPanel(JDialog levelDialog) {
        JButton backButton = createStyledButton("Back");
        backButton.addActionListener(e -> {
            levelDialog.dispose();
            setVisible(true);
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        bottomPanel.add(backButton);
        return bottomPanel;
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
            editExistingLevel(levelFile);
            parentDialog.dispose();
        });
        panel.add(levelButton);
    }

    private void createLevel() {
        World world = new World();
        world.setPlatformer(false);

        mainWindow.getPlayerPanel().setWorld(world);
        mainWindow.respawnPlayerIfNeeded(mainWindow.getPlayerPanel().getPlayer());
        mainWindow.getPlayerPanel().setVisible(true);
        mainWindow.setVisible(true);
        mainWindow.getInputHandler().setEditingMode(true);

        addToolSelectionPanel();

        JOptionPane.showMessageDialog(this, "New empty level created. Click 'Save As...' to save it to a file.");
        dispose();
    }
    private void addToolSelectionPanel() {
        mainWindow.addToolSelectionPanel();
    }

    private void editExistingLevel(File levelFile) {
        mainWindow.setCurrentLevelPath(levelFile.getAbsolutePath());
        addToolSelectionPanel();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            LevelData levelData = objectMapper.readValue(levelFile, LevelData.class);
            World world = new World();
            world.loadLevelData(levelData);

            mainWindow.getPlayerPanel().setPlatformer(world.isPlatformer());
            mainWindow.getPlayerPanel().setWorld(world);
            mainWindow.respawnPlayerIfNeeded(mainWindow.getPlayerPanel().getPlayer());
            mainWindow.getPlayerPanel().setVisible(true);
            mainWindow.setVisible(true);
            mainWindow.getInputHandler().setEditingMode(true);

            addToolSelectionPanel();
            mainWindow.getToolSelectionPanel().setPlatformerCheckboxState(world.isPlatformer());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading level from file: " + e.getMessage());
        } finally {
            dispose();
        }
    }
}