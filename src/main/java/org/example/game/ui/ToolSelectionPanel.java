package org.example.game.ui;

import org.example.game.entities.GameMode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.geom.RoundRectangle2D;

public class ToolSelectionPanel extends JPanel {
    private final MainWindow mainWindow;
    private JComboBox<String> toolSelectionComboBox;
    private JComboBox<String> portalSelectionComboBox;
    private JComboBox<String> speedSelectionComboBox;
    private JComboBox<String> orbSelectionComboBox;
    private JComboBox<String> padSelectionComboBox;
    private JLabel toolLabel;
    private JLabel toolPanelLabel;
    private JButton saveButton;
    private JButton saveAsButton;
    private JButton backButton;
    private GridBagConstraints gbc;
    private int toolSelectionComboBoxY;
    private int toolLabelY;
    private boolean isComboBoxAdded = false;
    private int defaultComboBoxWidth = 150;
    private int toolLabelDescY;
    // Dodaj stałą pozycję dla drugiego JComboBox
    private final int SECOND_COMBOBOX_Y = 2;
    private JCheckBox hitboxCheckBox;

    private final String[] toolNames = {"None", "Tile", "Spike", "Orb", "Pad", "Portals", "Speed"};
    private final String[] portalNames = {"Select", "Cube", "Ship", "Ball", "Ufo", "Wave", "Robot", "Spider"};
    private final String[] speedNames = {"Select", "Slow", "Normal", "Fast", "Very Fast", "Extremely Fast"};
    private final String[] orbColors = {"Select", "Yellow", "Purple", "Red", "Blue", "Green", "Black", "Spider"};
    private final String[] padColors = {"Select", "Yellow", "Purple", "Red", "Blue", "Spider"};

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

        // Dodanie etykiety "Tool:"
        toolPanelLabel = createLabel("Tool:");
        add(toolPanelLabel, gbc);
        toolLabelY = gbc.gridy;

        // Dodanie toolSelectionComboBox
        toolSelectionComboBox = createStyledComboBox(toolNames);
        toolSelectionComboBox.addItemListener(this::handleToolSelection);
        toolSelectionComboBox.setPreferredSize(new Dimension(defaultComboBoxWidth, toolSelectionComboBox.getPreferredSize().height));
        gbc.gridy++;
        add(toolSelectionComboBox, gbc);
        toolSelectionComboBoxY = gbc.gridy;

        // Inicjalizacja pozostałych JComboBox, ale nie dodawanie ich od razu
        portalSelectionComboBox = createStyledComboBox(portalNames);
        portalSelectionComboBox.addItemListener(this::handlePortalSelection);
        portalSelectionComboBox.setPreferredSize(new Dimension(defaultComboBoxWidth, portalSelectionComboBox.getPreferredSize().height));

        speedSelectionComboBox = createStyledComboBox(speedNames);
        speedSelectionComboBox.addItemListener(this::handleSpeedSelection);
        speedSelectionComboBox.setPreferredSize(new Dimension(defaultComboBoxWidth, speedSelectionComboBox.getPreferredSize().height));

        orbSelectionComboBox = createStyledComboBox(orbColors);
        orbSelectionComboBox.addItemListener(this::handleOrbSelection);
        orbSelectionComboBox.setPreferredSize(new Dimension(defaultComboBoxWidth, orbSelectionComboBox.getPreferredSize().height));

        padSelectionComboBox = createStyledComboBox(padColors);
        padSelectionComboBox.addItemListener(this::handlePadSelection);
        padSelectionComboBox.setPreferredSize(new Dimension(defaultComboBoxWidth, padSelectionComboBox.getPreferredSize().height));

        // Dodanie pustego JLabel na pozycji drugiego JComboBox, aby zarezerwować miejsce
        gbc.gridy = SECOND_COMBOBOX_Y;
        add(new JLabel(" "), gbc);

        // Dodanie etykiety toolLabel (opisującej wybrane narzędzie)
        toolLabel = createLabel("Tool: None");
        toolLabel.setForeground(Color.WHITE);
        gbc.gridy = 5; // Zmienione z 5
        add(toolLabel, gbc);
        toolLabelDescY = gbc.gridy;

        // Dodanie panelu z przyciskami
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        buttonPanel.setOpaque(false);
        gbc.gridy = 6; // Zmienione z 6
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        // Dodanie przycisku "Save As..."
        saveAsButton = createStyledButton("Save As...");
        saveAsButton.addActionListener(e -> mainWindow.showSaveAsDialog());
        buttonPanel.add(saveAsButton);

        // Dodanie przycisku "Save"
        saveButton = createStyledButton("Save");
        saveButton.addActionListener(e -> {
            if (mainWindow.getCurrentLevelPath() != null) {
                mainWindow.saveLevelToJson(mainWindow.getPlayerPanel().getWorld(), mainWindow.getCurrentLevelPath());
            } else {
                JOptionPane.showMessageDialog(mainWindow, "No file selected to overwrite.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(saveButton);

        // Dodanie panelu z przyciskiem "Back"
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backButtonPanel.setOpaque(false);
        gbc.gridy = 7; // Zmienione z 7
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 0, 0);
        add(backButtonPanel, gbc);

        // Dodanie przycisku "Back"
        backButton = createStyledButton("Back");
        backButton.addActionListener(e -> {
            mainWindow.getInputHandler().setEditingMode(false);
            mainWindow.removeToolSelectionPanel();
            mainWindow.hideEditLevelMenu();
            mainWindow.setVisible(false);
            mainWindow.showMainMenu();
        });
        backButtonPanel.add(backButton);

        hitboxCheckBox = new JCheckBox("Show Hitboxes");
        hitboxCheckBox.setSelected(false); // Domyślnie hitboxy są wyłączone
        hitboxCheckBox.setFocusPainted(false);
        hitboxCheckBox.setFont(new Font("Arial", Font.BOLD, 14));
        hitboxCheckBox.setForeground(Color.WHITE);
        hitboxCheckBox.setBackground(new Color(60, 60, 60));
        hitboxCheckBox.addActionListener(e -> mainWindow.getPlayerPanel().setShowHitboxes(hitboxCheckBox.isSelected()));
        gbc.gridy++;
        add(hitboxCheckBox, gbc);

        setVisible(true);
    }

    private void handleToolSelectionChange() {
        String selectedToolName = (String) toolSelectionComboBox.getSelectedItem();

        // Usuń poprzednio dodany JComboBox, jeśli istnieje
        removeComboBox();

        // Dodaj odpowiedni JComboBox, jeśli jest potrzebny
        switch (selectedToolName) {
            case "Tile":
                mainWindow.getPlayerPanel().setSelectedTool(1);
                break;
            case "Spike":
                mainWindow.getPlayerPanel().setSelectedTool(2);
                break;
            case "Orb":
                mainWindow.getPlayerPanel().setSelectedTool(3);
                addComboBox(orbSelectionComboBox);
                break;
            case "Pad":
                mainWindow.getPlayerPanel().setSelectedTool(4);
                addComboBox(padSelectionComboBox);
                break;
            case "Portals":
                mainWindow.getPlayerPanel().setSelectedTool(9);
                addComboBox(portalSelectionComboBox);
                break;
            case "Speed":
                mainWindow.getPlayerPanel().setSelectedTool(10);
                addComboBox(speedSelectionComboBox);
                break;
            default:
                mainWindow.getPlayerPanel().setSelectedTool(0);
                break;
        }

        // Aktualizuj label i przerysuj panel
        updateToolLabel();
        revalidate();
        repaint();
    }

    private void removeComboBox() {
        if (isComboBoxAdded) {
            for (int i = getComponentCount() - 1; i >= 0; i--) {
                Component comp = getComponent(i);
                if (comp instanceof JComboBox && comp != toolSelectionComboBox) {
                    remove(i);
                    isComboBoxAdded = false;

                    // Dodaj pusty JLabel, aby utrzymać układ
                    gbc.gridy = SECOND_COMBOBOX_Y;
                    add(new JLabel(" "), gbc);

                    revalidate();
                    repaint();
                    break;
                }
            }
        }
    }

    private void addComboBox(JComboBox<String> comboBox) {
        if (!isComboBoxAdded) {
            // Usuń pusty JLabel
            for (int i = 0; i < getComponentCount(); i++) {
                Component comp = getComponent(i);
                if (comp instanceof JLabel && ((JLabel) comp).getText().equals(" ")) {
                    remove(comp);
                    break;
                }
            }

            // Dodaj nowy JComboBox na zarezerwowanej pozycji
            gbc.gridy = SECOND_COMBOBOX_Y;
            add(comboBox, gbc);
            isComboBoxAdded = true;

            revalidate();
            repaint();
        }
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
            handleOrbSelectionChange();
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
            }
        }
    }

    private void handleOrbSelectionChange() {
        String selectedOrbColor = (String) orbSelectionComboBox.getSelectedItem();
        if (!"Select".equals(selectedOrbColor)) {
            mainWindow.getPlayerPanel().setSelectedOrbColor(selectedOrbColor.toLowerCase());
        }
    }

    private void handlePadSelectionChange() {
        String selectedPadColor = (String) padSelectionComboBox.getSelectedItem();
        if (!"Select".equals(selectedPadColor)) {
            mainWindow.getPlayerPanel().setSelectedPadColor(selectedPadColor.toLowerCase());
        }
    }

    private void updateToolLabel() {
        String toolName = mainWindow.getPlayerPanel().getToolName();
        toolLabel.setText("Tool: " + toolName);
    }
}