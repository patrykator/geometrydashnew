package org.example.game.ui;

import org.example.game.entities.GameMode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.Objects;

public class ToolSelectionPanel extends JPanel {
    private final MainWindow mainWindow;
    private final JComboBox<String> toolSelectionComboBox;
    private JComboBox<String> portalSelectionComboBox;
    private JComboBox<String> speedSelectionComboBox;
    private JComboBox<String> orbSelectionComboBox;
    private JComboBox<String> padSelectionComboBox;
    private final JLabel toolLabel;
    private JButton saveButton;
    private final GridBagConstraints gbc;
    private final int defaultComboBoxWidth = 150;
    private final int SECOND_COMBOBOX_Y = 2;
    private final int orbDirectionComboBoxY;
    private JCheckBox hitboxCheckBox;
    private JCheckBox platformerCheckBox;
    private JCheckBox fullscreenCheckBox;
    private JComboBox<String> orbDirectionComboBox;
    private JComboBox<String> padPositionComboBox;
    private static final int LEVEL_END_TOOL_INDEX = 12;
    private JComboBox<String> spikePositionComboBox;
    private final String[] spikePositions = {"top", "bottom"};

    private final String[] padPositions = {"top", "bottom"};
    private final String[] portalNames = {"Select", "Cube", "Ship", "Ball", "Ufo", "Wave", "Robot", "Spider"};
    private final String[] speedNames = {"Select", "Slow", "Normal", "Fast", "Very Fast", "Extremely Fast"};
    private final String[] orbColors = {"Select", "Yellow", "Purple", "Red", "Blue", "Green", "Black", "Spider", "Teleport"};
    private final String[] padColors = {"Select", "Yellow", "Purple", "Red", "Blue", "Spider"};
    private final String[] orbDirections = {"up", "down"};

    public ToolSelectionPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(40, 40, 40));
        setOpaque(false);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);


        orbDirectionComboBoxY = 4;

        JLabel toolPanelLabel = createLabel("Tool:");
        add(toolPanelLabel, gbc);
        String[] toolNames = {"None", "Tile", "Spike", "Orb", "Pad", "Portals", "Speed", "Delete", "Checkpoint" , "Level End"};
        toolSelectionComboBox = createStyledComboBox(toolNames);
        toolSelectionComboBox.addItemListener(this::handleToolSelection);
        toolSelectionComboBox.setPreferredSize(new Dimension(defaultComboBoxWidth, toolSelectionComboBox.getPreferredSize().height));
        gbc.gridy++;
        add(toolSelectionComboBox, gbc);

        initializeComboBoxes();

        gbc.gridy = SECOND_COMBOBOX_Y;
        add(new JLabel(" "), gbc);

        toolLabel = createLabel("Tool: None");
        toolLabel.setForeground(Color.WHITE);
        gbc.gridy = 5;
        add(toolLabel, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        buttonPanel.setOpaque(false);
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        createButtons(buttonPanel);

        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backButtonPanel.setOpaque(false);
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 0, 0);
        add(backButtonPanel, gbc);

        createBackButton(backButtonPanel);
        createCheckBoxes();

        setVisible(true);
    }

    private void initializeComboBoxes() {
        portalSelectionComboBox = createStyledComboBox(portalNames);
        portalSelectionComboBox.addItemListener(this::handlePortalSelection);
        portalSelectionComboBox.setPreferredSize(new Dimension(defaultComboBoxWidth, portalSelectionComboBox.getPreferredSize().height));

        speedSelectionComboBox = createStyledComboBox(speedNames);
        speedSelectionComboBox.addItemListener(this::handleSpeedSelection);
        speedSelectionComboBox.setPreferredSize(new Dimension(defaultComboBoxWidth, speedSelectionComboBox.getPreferredSize().height));

        orbSelectionComboBox = createStyledComboBox(orbColors);
        orbSelectionComboBox.addItemListener(this::handleOrbSelection);
        orbSelectionComboBox.setPreferredSize(new Dimension(defaultComboBoxWidth, orbSelectionComboBox.getPreferredSize().height));

        orbDirectionComboBox = createStyledComboBox(orbDirections);
        orbDirectionComboBox.addItemListener(this::handleOrbDirectionSelection);
        orbDirectionComboBox.setPreferredSize(new Dimension(defaultComboBoxWidth, orbDirectionComboBox.getPreferredSize().height));

        padSelectionComboBox = createStyledComboBox(padColors);
        padSelectionComboBox.addItemListener(this::handlePadSelection);
        padSelectionComboBox.setPreferredSize(new Dimension(defaultComboBoxWidth, padSelectionComboBox.getPreferredSize().height));

        spikePositionComboBox = createStyledComboBox(spikePositions);
        spikePositionComboBox.addItemListener(this::handleSpikePositionSelection);
        spikePositionComboBox.setPreferredSize(new Dimension(defaultComboBoxWidth, spikePositionComboBox.getPreferredSize().height));

        JButton setTargetButton = createStyledButton("Set Teleport Target");
        setTargetButton.addActionListener(e -> {
            if (Objects.equals(orbSelectionComboBox.getSelectedItem(), "Teleport")) {
                JOptionPane.showMessageDialog(mainWindow, "Click on the screen to set the teleport target.");
            }
        });
    }

    private void createButtons(JPanel buttonPanel) {
        JButton saveAsButton = createStyledButton("Save As...");
        saveAsButton.addActionListener(e -> mainWindow.showSaveAsDialog());
        buttonPanel.add(saveAsButton);

        saveButton = createStyledButton("Save");
        saveButton.addActionListener(e -> {
            if (mainWindow.getCurrentLevelPath() != null) {
                mainWindow.saveLevelToJson(mainWindow.getPlayerPanel().getWorld(), mainWindow.getCurrentLevelPath());
            } else {
                JOptionPane.showMessageDialog(mainWindow, "No file selected to overwrite.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(saveButton);
    }

    private void createBackButton(JPanel backButtonPanel) {
        JButton backButton = createStyledButton("Back");
        backButton.addActionListener(e -> {
            if (mainWindow.isFullScreen()) {
                mainWindow.toggleFullScreen();
                fullscreenCheckBox.setSelected(mainWindow.isFullScreen());
            }
            mainWindow.getInputHandler().setEditingMode(false);
            mainWindow.removeToolSelectionPanel();
            mainWindow.hideEditLevelMenu();
            mainWindow.setVisible(false);
            mainWindow.showMainMenu();
        });
        backButtonPanel.add(backButton);
    }

    private void createCheckBoxes() {
        hitboxCheckBox = new JCheckBox("Show Hitboxes");
        hitboxCheckBox.setSelected(false);
        hitboxCheckBox.setFocusPainted(false);
        hitboxCheckBox.setFont(new Font("Arial", Font.BOLD, 14));
        hitboxCheckBox.setForeground(Color.WHITE);
        hitboxCheckBox.setBackground(new Color(60, 60, 60));
        hitboxCheckBox.addActionListener(e -> mainWindow.getPlayerPanel().setShowHitboxes(hitboxCheckBox.isSelected()));
        gbc.gridy++;
        add(hitboxCheckBox, gbc);

        platformerCheckBox = new JCheckBox("Platformer");
        platformerCheckBox.setSelected(mainWindow.getPlayerPanel().isPlatformer());
        platformerCheckBox.setFocusPainted(false);
        platformerCheckBox.setFont(new Font("Arial", Font.BOLD, 14));
        platformerCheckBox.setForeground(Color.WHITE);
        platformerCheckBox.setBackground(new Color(60, 60, 60));
        platformerCheckBox.addActionListener(e -> {
            mainWindow.getPlayerPanel().setPlatformer(platformerCheckBox.isSelected());
            mainWindow.getPlayerPanel().repaint();
        });
        gbc.gridy++;
        add(platformerCheckBox, gbc);

        fullscreenCheckBox = new JCheckBox("Fullscreen");
        fullscreenCheckBox.setSelected(mainWindow.isFullScreen());
        fullscreenCheckBox.setFocusPainted(false);
        fullscreenCheckBox.setFont(new Font("Arial", Font.BOLD, 14));
        fullscreenCheckBox.setForeground(Color.WHITE);
        fullscreenCheckBox.setBackground(new Color(60, 60, 60));
        fullscreenCheckBox.addActionListener(e -> mainWindow.toggleFullScreen());
        gbc.gridy++;
        add(fullscreenCheckBox, gbc);
    }

    private void handleOrbDirectionSelection(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            String selectedOrbDirection = (String) orbDirectionComboBox.getSelectedItem();

                mainWindow.getPlayerPanel().setSelectedOrbDirection(selectedOrbDirection);

        }
    }

    private void handleToolSelectionChange() {
        String selectedToolName = (String) toolSelectionComboBox.getSelectedItem();

        removeOptionalComboBoxes();

        switch (selectedToolName) {
            case "Tile":
                mainWindow.getPlayerPanel().setSelectedTool(1);
                break;
            case "Spike":
                mainWindow.getPlayerPanel().setSelectedTool(2);
                addSpikePositionComboBox();
                break;
            case "Orb":
                mainWindow.getPlayerPanel().setSelectedTool(3);
                addOrbSelectionComboBox();
                break;
            case "Pad":
                mainWindow.getPlayerPanel().setSelectedTool(4);
                addPadSelectionComboBox();
                addPadPositionComboBox();
                break;
            case "Checkpoint":
                mainWindow.getPlayerPanel().setSelectedTool(5);
                break;
            case "Portals":
                mainWindow.getPlayerPanel().setSelectedTool(9);
                addPortalSelectionComboBox();
                break;
            case "Speed":
                mainWindow.getPlayerPanel().setSelectedTool(10);
                addSpeedSelectionComboBox();
                break;
            case "Delete":
                mainWindow.getPlayerPanel().setSelectedTool(11);
                break;
            case "Level End":
                mainWindow.getPlayerPanel().setSelectedTool(LEVEL_END_TOOL_INDEX);
                break;
            case null:
                break;
            default:
                mainWindow.getPlayerPanel().setSelectedTool(0);
                break;
        }

        updateToolLabel();
        revalidate();
        repaint();
    }

    private void addSpikePositionComboBox() {
        if (spikePositionComboBox == null) {
            spikePositionComboBox = createStyledComboBox(spikePositions);
            spikePositionComboBox.addItemListener(this::handleSpikePositionSelection);
            spikePositionComboBox.setPreferredSize(new Dimension(defaultComboBoxWidth, spikePositionComboBox.getPreferredSize().height));
        }
        if (isComponentOnPanel(spikePositionComboBox)) {
            gbc.gridy = 3;
            add(spikePositionComboBox, gbc);
            revalidate();
            repaint();
        }
    }

    private void removeSpikePositionComboBox() {
        removeComboBoxSafely(spikePositionComboBox);
    }

    private void handleSpikePositionSelection(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            String selectedSpikePosition = (String) spikePositionComboBox.getSelectedItem();
            mainWindow.getPlayerPanel().setSelectedSpikePosition(selectedSpikePosition);
        }
    }

    private void removeOptionalComboBoxes() {
        removePortalSelectionComboBox();
        removeSpeedSelectionComboBox();
        removeOrbSelectionComboBox();
        removeOrbDirectionComboBox();
        removePadSelectionComboBox();
        removePadDirectionComboBox();
        removeSpikePositionComboBox();
    }

    private void addPadPositionComboBox() {
        if (padPositionComboBox == null) {
            padPositionComboBox = createStyledComboBox(padPositions);
            padPositionComboBox.addItemListener(this::handlePadPositionSelection);
            padPositionComboBox.setPreferredSize(new Dimension(defaultComboBoxWidth, padPositionComboBox.getPreferredSize().height));
        }
        if (isComponentOnPanel(padPositionComboBox)) {
            gbc.gridy = 3;
            add(padPositionComboBox, gbc);
            revalidate();
            repaint();
        }
    }

    private void removePadDirectionComboBox() {
        removeComboBoxSafely(padPositionComboBox);
    }

    private void handlePadPositionSelection(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            String selectedPadPosition = (String) padPositionComboBox.getSelectedItem();
            mainWindow.getPlayerPanel().setSelectedPadPosition(selectedPadPosition);
        }
    }

    private void removePortalSelectionComboBox() {
        removeComboBoxSafely(portalSelectionComboBox);
    }

    private void removeSpeedSelectionComboBox() {
        removeComboBoxSafely(speedSelectionComboBox);
    }

    private void removePadSelectionComboBox() {
        removeComboBoxSafely(padSelectionComboBox);
    }

    private void removeComboBoxSafely(JComboBox<?> comboBox) {
        for (int i = 0; i < getComponentCount(); i++) {
            if (getComponent(i) == comboBox) {
                remove(comboBox);
                revalidate();
                repaint();
                return;
            }
        }
    }

    public void setPlatformerCheckboxState(boolean state) {
        platformerCheckBox.setSelected(state);
    }

    private void addOrbSelectionComboBox() {
        if (isComponentOnPanel(orbSelectionComboBox)) {
            gbc.gridy = SECOND_COMBOBOX_Y;
            add(orbSelectionComboBox, gbc);
            revalidate();
            repaint();
        }
    }

    private void addPadSelectionComboBox() {
        if (isComponentOnPanel(padSelectionComboBox)) {
            gbc.gridy = SECOND_COMBOBOX_Y;
            add(padSelectionComboBox, gbc);
            revalidate();
            repaint();
        }
    }

    private void addPortalSelectionComboBox() {
        if (isComponentOnPanel(portalSelectionComboBox)) {
            gbc.gridy = SECOND_COMBOBOX_Y;
            add(portalSelectionComboBox, gbc);
            revalidate();
            repaint();
        }
    }

    private void addSpeedSelectionComboBox() {
        if (isComponentOnPanel(speedSelectionComboBox)) {
            gbc.gridy = SECOND_COMBOBOX_Y;
            add(speedSelectionComboBox, gbc);
            revalidate();
            repaint();
        }
    }

    private void addOrbDirectionComboBox() {
        if (isComponentOnPanel(orbDirectionComboBox)) {
            gbc.gridy = orbDirectionComboBoxY;
            add(orbDirectionComboBox, gbc);
            revalidate();
            repaint();
        }
    }

    private void removeOrbSelectionComboBox() {
        removeComboBoxSafely(orbSelectionComboBox);
    }

    private void removeOrbDirectionComboBox() {
        removeComboBoxSafely(orbDirectionComboBox);
    }

    private boolean isComponentOnPanel(Component component) {
        for (Component comp : getComponents()) {
            if (comp == component) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        int width = getWidth();
        int height = getHeight();
        int arc = 20;
        RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(0, 0, width - 1, height - 1, arc, arc);

        g2d.setColor(new Color(40, 40, 40));
        g2d.fill(roundedRectangle);

        g2d.setColor(new Color(60, 60, 60));
        g2d.setStroke(new BasicStroke(1));
        g2d.draw(roundedRectangle);

        g2d.dispose();
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        comboBox.setBackground(new Color(60, 60, 60));
        comboBox.setForeground(Color.WHITE);
        comboBox.setFocusable(false);
        comboBox.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        return comboBox;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(60, 60, 60));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return button;
    }

    public void setSaveButtonVisibility(boolean visible) {
        saveButton.setVisible(visible);
    }

    private void handleToolSelection(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            handleToolSelectionChange();
            if (Objects.equals(toolSelectionComboBox.getSelectedItem(), "Orb")) {
                addOrbSelectionComboBox();
                if (orbSelectionComboBox.getSelectedItem() != null && orbSelectionComboBox.getSelectedItem().equals("Spider")) {
                    addOrbDirectionComboBox();
                }
            } else {
                removeOrbSelectionComboBox();
                removeOrbDirectionComboBox();
            }
        }
    }

    private void handlePortalSelection(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            handlePortalSelectionChange();
        }
    }

    private void handleSpeedSelection(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            handleSpeedSelectionChange();
        }
    }

    private void handleOrbSelection(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            String selectedOrbColor = (String) orbSelectionComboBox.getSelectedItem();
            if (!"Select".equals(selectedOrbColor)) {
                assert selectedOrbColor != null;
                mainWindow.getPlayerPanel().setSelectedOrbColor(selectedOrbColor.toLowerCase());
                if ("Teleport".equals(selectedOrbColor)) {
                    addOrbDirectionComboBox();
                } else if ("Spider".equals(selectedOrbColor)) {
                    addOrbDirectionComboBox();
                } else {
                    removeOrbDirectionComboBox();
                }
            } else {
                removeOrbDirectionComboBox();
            }
        }
    }

    private void handlePadSelection(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            handlePadSelectionChange();
        }
    }

    private void handlePortalSelectionChange() {
        String selectedPortal = (String) portalSelectionComboBox.getSelectedItem();
        if (!"Select".equals(selectedPortal)) {
            assert selectedPortal != null;
            mainWindow.getPlayerPanel().setPortalGameMode(GameMode.valueOf(selectedPortal.toUpperCase()));
        }
    }

    private void handleSpeedSelectionChange() {
        String selectedSpeed = (String) speedSelectionComboBox.getSelectedItem();
        if (!"Select".equals(selectedSpeed)) {
            switch (selectedSpeed) {
                case "Slow":
                    mainWindow.getPlayerPanel().setPortalSpeedMultiplier(0.807);
                    break;
                case "Normal":
                    mainWindow.getPlayerPanel().setPortalSpeedMultiplier(1.0);
                    break;
                case "Fast":
                    mainWindow.getPlayerPanel().setPortalSpeedMultiplier(1.243);
                    break;
                case "Very Fast":
                    mainWindow.getPlayerPanel().setPortalSpeedMultiplier(1.502);
                    break;
                case "Extremely Fast":
                    mainWindow.getPlayerPanel().setPortalSpeedMultiplier(1.849);
                    break;
                case null:
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + selectedSpeed);
            }
        }
    }

    private void handlePadSelectionChange() {
        String selectedPadColor = (String) padSelectionComboBox.getSelectedItem();
        if (!"Select".equals(selectedPadColor)) {
            assert selectedPadColor != null;
            mainWindow.getPlayerPanel().setSelectedPadColor(selectedPadColor.toLowerCase());
        }
    }

    private void updateToolLabel() {
        String toolName = mainWindow.getPlayerPanel().getToolName();
        toolLabel.setText("Tool: " + toolName);
    }
}