package org.example.game.ui;

import org.example.game.entities.*;
import org.example.game.utilities.ImageLoader;
import org.example.game.world.Tile;
import org.example.game.world.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;

public class PlayerPanel extends JPanel {
    private final Player player;
    private World world;
    private final MainWindow mainWindow;
    private Image playerImage;
    private Image shipImage;
    private Image shipImagePlatformer;
    private Image ufoImage;
    private int cameraOffsetX, cameraOffsetY;
    private final Map<String, Image> orbImages = new HashMap<>();
    private final Map<String, Image> padImages = new HashMap<>();
    private Image tileImage;
    private Image ballModeImage;
    private Image waveImage;
    private Image robotImage;
    private Image spiderImage;
    private int selectedTool = 1;
    private Point dragStartPoint;
    private static final int INITIAL_WIDTH = 800;
    private static final int INITIAL_HEIGHT = 600;
    private Image spikeImage;
    private int mouseX, mouseY;
    private GameMode portalGameMode = null;
    private final Map<GameMode, Image> portalImages = new HashMap<>();
    private double portalSpeedMultiplier = 1.0;
    private boolean isDimmed = false;
    private String selectedOrbColor = "yellow";
    private String selectedPadColor = "yellow";
    private boolean showHitboxes = false;
    private boolean isPlatformer;
    private String selectedOrbDirection = "up";
    private String selectedPadPosition = "bottom";
    private final int MIN_BUILD_Y = -4450;
    private final int MAX_BUILD_Y = 4450;
    private final int MIN_BUILD_X = -200;

    public PlayerPanel(Player player, World world, MainWindow mainWindow) {
        this.player = player;
        this.world = world;
        this.mainWindow = mainWindow;
        setDoubleBuffered(true);
        loadImages();
        this.setPreferredSize(new Dimension(INITIAL_WIDTH, INITIAL_HEIGHT));
        setupMouseListeners();
        setupMouseMotionListeners();
    }

    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseReleased();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClicked(e);
            }
        });
    }

    private void setupMouseMotionListeners() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMoved(e);
            }
        });
    }

    private void handleMouseDragged(MouseEvent e) {
        if (mainWindow.getInputHandler().isEditingMode()) {
            if (dragStartPoint != null) {
                int dx = dragStartPoint.x - e.getX();
                int dy = dragStartPoint.y - e.getY();

                cameraOffsetX += dx;
                cameraOffsetY += dy;

                dragStartPoint = e.getPoint();
                repaint();
            }
        }
    }

    private void handleMouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    private void handleMousePressed(MouseEvent e) {
        if (mainWindow.getInputHandler().isEditingMode()) {
            dragStartPoint = e.getPoint();
            mouseX = e.getX();
            mouseY = e.getY();
        }
    }

    private void handleMouseReleased() {
        if (mainWindow.getInputHandler().isEditingMode()) {
            dragStartPoint = null;
        }
    }

    private void handleMouseClicked(MouseEvent e) {
        if (mainWindow.getGameEngine().isRunning() && !mainWindow.getInputHandler().isEditingMode()) {
            return;
        }

        int gridX = (e.getX() + cameraOffsetX) / 50;
        int gridY = (e.getY() + cameraOffsetY) / 50;

        if (mainWindow.getInputHandler().isEditingMode()) {
            switch (selectedTool) {
                case 1:
                    placeTile(gridX, gridY);
                    break;
                case 2:
                    placeSpike(gridX, gridY);
                    break;
                case 3:
                    placeOrb(gridX, gridY);
                    break;
                case 4:
                    placePad(gridX, gridY, selectedOrbDirection);
                    break;
                case 9:
                    placePortal(gridX, gridY, portalGameMode);
                    break;
                case 10:
                    placeSpeedPortal(gridX, gridY, portalSpeedMultiplier);
                    break;
                case 11:
                    deleteObject(gridX, gridY);
                    break;
            }
        }
    }

    private void deleteObject(int gridX, int gridY) {
        if (isWithinBuildLimits(gridX * 50, gridY * 50)) {
            return;
        }

        System.out.println("Deleting object at gridX: " + gridX + ", gridY: " + gridY);
        Tile tileToRemove = null;
        for (Tile tile : world.getTiles()) {
            if (tile.getX() == gridX && tile.getY() == gridY) {
                tileToRemove = tile;
                break;
            }
        }
        if (tileToRemove != null) {
            world.getTiles().remove(tileToRemove);
        }

        Spike spikeToRemove = null;
        for (Spike spike : world.getSpikes()) {
            if (spike.x() == gridX && spike.y() == gridY) {
                spikeToRemove = spike;
                break;
            }
        }
        if (spikeToRemove != null) {
            world.getSpikes().remove(spikeToRemove);
        }

        Orb orbToRemove = null;
        for (Orb orb : world.getOrbs()) {
            if (orb.getX() == gridX && orb.getY() == gridY) {
                orbToRemove = orb;
                break;
            }
        }
        if (orbToRemove != null) {
            world.getOrbs().remove(orbToRemove);
        }

        Pad padToRemove = null;
        for (Pad pad : World.getPads()) {
            if (pad.getX() == gridX && pad.getY() == gridY) {
                padToRemove = pad;
                break;
            }
        }
        if (padToRemove != null) {
            World.getPads().remove(padToRemove);
        }

        Portal portalToRemove = null;
        for (Portal portal : world.getPortals()) {
            if (portal.getX() == gridX && portal.getY() == gridY) {
                portalToRemove = portal;
                break;
            }
        }
        if (portalToRemove != null) {
            world.getPortals().remove(portalToRemove);
        }

        SpeedPortal speedPortalToRemove = null;
        for (SpeedPortal speedPortal : world.getSpeedPortals()) {
            if (speedPortal.getX() == gridX && speedPortal.getY() == gridY) {
                speedPortalToRemove = speedPortal;
                break;
            }
        }
        if (speedPortalToRemove != null) {
            world.getSpeedPortals().remove(speedPortalToRemove);
        }

        repaint();
    }

    public void setDimmed(boolean dimmed) {
        isDimmed = dimmed;
        repaint();
    }

    public boolean isPlatformer() {
        return isPlatformer;
    }

    public void setPlatformer(boolean platformer) {
        this.isPlatformer = platformer;
        if (world != null) {
            world.setPlatformer(platformer);
        }
    }

    public void setSelectedOrbDirection(String selectedOrbDirection) {
        this.selectedOrbDirection = selectedOrbDirection;
    }




    public void setPortalGameMode(GameMode gameMode) {
        this.portalGameMode = gameMode;
    }

    public void resetCameraPosition() {
        cameraOffsetX = 0;
        cameraOffsetY = 0;
        repaint();
    }

    public void removeSaveAsButton() {
        for (Component comp : getComponents()) {
            if (comp instanceof JButton && ((JButton) comp).getText().equals("Save As...")) {
                remove(comp);
                revalidate();
                repaint();
                return;
            }
        }
    }

    public void setWorld(World world) {
        this.world = world;
        this.isPlatformer = world.isPlatformer();
        repaint();
    }

    public void setSelectedTool(int selectedTool) {
        this.selectedTool = selectedTool;
        if (selectedTool != 9) {
            setPortalGameMode(null);
        }
        repaint();
    }

    private void loadImages() {

        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/Ball.png", img -> {
            ballModeImage = img;
            repaint();
        });

        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/spike.png", img -> {
            spikeImage = img;
            repaint();
        });

        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/geometry-dash-icon.jpg", img -> {
            playerImage = img;
            repaint();
        });

        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/Ship_Icon.png", img -> {
            shipImage = img;
            repaint();
        });

        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/Platformer_Ship.png", img -> {
            shipImagePlatformer = img;
            repaint();
        });

        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/Ufo_Icon.png", img -> {
            ufoImage = img;
            repaint();
        });

        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/Wave_Icon.png", img -> {
            waveImage = img;
            repaint();
        });

        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/Robot_Icon.png", img -> {
            robotImage = img;
            repaint();
        });

        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/Spider_Icon.png", img -> {
            spiderImage = img;
            repaint();
        });

        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/Tile.png", img -> {
            tileImage = img;
            repaint();
        });

        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/portal_cube.png", img -> portalImages.put(GameMode.CUBE, img));
        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/portal_ship.png", img -> portalImages.put(GameMode.SHIP, img));
        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/portal_ball.png", img -> portalImages.put(GameMode.BALL, img));
        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/portal_ufo.png", img -> portalImages.put(GameMode.UFO, img));
        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/portal_wave.png", img -> portalImages.put(GameMode.WAVE, img));
        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/portal_robot.png", img -> portalImages.put(GameMode.ROBOT, img));
        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/portal_spider.png", img -> portalImages.put(GameMode.SPIDER, img));

        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/portal_slow.png", img -> portalImages.put(GameMode.SLOW, img));
        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/portal_normal.png", img -> portalImages.put(GameMode.NORMAL, img));
        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/portal_fast.png", img -> portalImages.put(GameMode.FAST, img));
        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/portal_very_fast.png", img -> portalImages.put(GameMode.VERY_FAST, img));
        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/portal_extremely_fast.png", img -> portalImages.put(GameMode.EXTREMELY_FAST, img));

        loadEntityImages("Orb", orbImages, "Yellow", "Purple", "Red", "Blue", "Green", "Black", "Spider_Up", "Spider_Down", "Teleport");
        loadEntityImages("Pad", padImages, "Yellow", "Purple", "Red", "Blue", "Spider");
    }



    public Image getPlayerImage() {
        return playerImage;
    }

    public Image getShipImage() {
        return shipImage;
    }

    public Image getBallModeImage() {
        return ballModeImage;
    }

    public Image getUfoImage() {
        return ufoImage;
    }

    public Image getWaveImage() {
        return waveImage;
    }

    public Image getRobotImage() {
        return robotImage;
    }

    public Image getSpiderImage() {
        return spiderImage;
    }

    private void loadEntityImages(String type, Map<String, Image> imageMap, String... keys) {
        for (String key : keys) {
            String path = "src/main/java/org/example/game/img/" + key + "_" + type + ".png";
            ImageLoader.loadImageAsync(path, img -> {
                imageMap.put(key.toLowerCase(), img);
                repaint();
            });
        }
    }

    private int adjustGridX(int gridX, int mouseX, int cameraOffsetX) {
        int adjustedX = mouseX + cameraOffsetX;
        if (adjustedX < 0) {
            return gridX - 1;
        } else {
            return gridX;
        }
    }

    private int adjustGridY(int gridY, int mouseY, int cameraOffsetY) {
        int adjustedY = mouseY + cameraOffsetY;
        if (adjustedY < 0) {
            return gridY - 1;
        } else {
            return gridY;
        }
    }

    private boolean isWithinBuildLimits(int gridX, int gridY) {
        return gridX * 50 < MIN_BUILD_X + 50 || gridY * 50 < MIN_BUILD_Y + 50 || gridY * 50 > MAX_BUILD_Y - 50;
    }

    private void placeTile(int gridX, int gridY) {
        if (mainWindow.getGameEngine().isGamePaused()) {
            return;
        }

        if (isWithinBuildLimits(gridX, gridY)) {
            return;
        }

        int mouseX = this.mouseX;
        int adjustedGridX = adjustGridX(gridX, mouseX, cameraOffsetX);

        int mouseY = this.mouseY;
        int adjustedGridY = adjustGridY(gridY, mouseY, cameraOffsetY);

        Tile existingTile = null;
        for (Tile tile : world.getTiles()) {
            if (tile.getX() == adjustedGridX && tile.getY() == adjustedGridY) {
                existingTile = tile;
                break;
            }
        }

        if (existingTile != null) {
            world.getTiles().remove(existingTile);
        } else {
            world.addTile(new Tile(adjustedGridX, adjustedGridY, true));
        }

        repaint();
    }

    private void placeSpike(int gridX, int gridY) {
        if (mainWindow.getGameEngine().isGamePaused()) {
            return;
        }

        if (isWithinBuildLimits(gridX, gridY)) {
            return;
        }

        int mouseX = this.mouseX;
        int adjustedGridX = adjustGridX(gridX, mouseX, cameraOffsetX);

        int mouseY = this.mouseY;
        int adjustedGridY = adjustGridY(gridY, mouseY, cameraOffsetY);

        if (selectedTool == 2) {
            for (Spike spike : world.getSpikes()) {
                if (spike.x() == adjustedGridX && spike.y() == adjustedGridY) {
                    world.getSpikes().remove(spike);
                    repaint();
                    return;
                }
            }
            world.addSpike(new Spike(adjustedGridX, adjustedGridY));
            repaint();
        }
    }

    private void placeOrb(int gridX, int gridY) {
        if (mainWindow.getGameEngine().isGamePaused()) {
            return;
        }

        if (isWithinBuildLimits(gridX, gridY)) {
            return;
        }

        int mouseX = this.mouseX;
        int adjustedGridX = adjustGridX(gridX, mouseX, cameraOffsetX);
        int mouseY = this.mouseY;
        int adjustedGridY = adjustGridY(gridY, mouseY, cameraOffsetY);

        for (Orb orb : world.getOrbs()) {
            if (orb.getX() == adjustedGridX && orb.getY() == adjustedGridY) {
                world.getOrbs().remove(orb);
                repaint();
                return;
            }
        }
        if ("spider".equals(selectedOrbColor)) {
            world.addOrb(new Orb(adjustedGridX, gridY, selectedOrbColor, selectedOrbDirection));
        } else {
            world.addOrb(new Orb(adjustedGridX, adjustedGridY, selectedOrbColor));
        }
        repaint();
    }

    private void placePad(int gridX, int gridY, String direction) {
        if (mainWindow.getGameEngine().isGamePaused()) {
            return;
        }

        if (isWithinBuildLimits(gridX, gridY)) {
            return;
        }

        int mouseX = this.mouseX;
        int adjustedGridX = adjustGridX(gridX, mouseX, cameraOffsetX);
        int mouseY = this.mouseY;
        int adjustedGridY = adjustGridY(gridY, mouseY, cameraOffsetY);

        for (Pad pad : World.getPads()) {
            if (pad.getX() == adjustedGridX && pad.getY() == adjustedGridY) {
                World.getPads().remove(pad);
                repaint();
                return;
            }
        }
        world.addPad(new Pad(adjustedGridX, adjustedGridY, selectedPadColor, selectedPadPosition, direction));
        repaint();
    }

    private void placePortal(int gridX, int gridY, GameMode gameMode) {
        if (mainWindow.getGameEngine().isGamePaused()) {
            return;
        }

        if (isWithinBuildLimits(gridX, gridY)) {
            return;
        }

        int mouseX = this.mouseX;
        int adjustedGridX = adjustGridX(gridX, mouseX, cameraOffsetX);
        int mouseY = this.mouseY;
        int adjustedGridY = adjustGridY(gridY, mouseY, cameraOffsetY);

        if (gameMode == null) {
            System.err.println("Error: portalGameMode is null!");
            return;
        }
        for (Portal portal : world.getPortals()) {
            if (portal.getX() == adjustedGridX && portal.getY() == adjustedGridY) {
                world.getPortals().remove(portal);
                repaint();
                return;
            }
        }
        world.addPortal(new Portal(adjustedGridX, adjustedGridY, gameMode));
        repaint();
    }

    private void placeSpeedPortal(int gridX, int gridY, double speedMultiplier) {
        System.out.println("placeSpeedPortal() called");
        if (mainWindow.getGameEngine().isGamePaused()) {
            return;
        }

        if (isWithinBuildLimits(gridX, gridY)) {
            return;
        }

        int mouseX = this.mouseX;
        int adjustedGridX = adjustGridX(gridX, mouseX, cameraOffsetX);
        int mouseY = this.mouseY;
        int adjustedGridY = adjustGridY(gridY, mouseY, cameraOffsetY);

        for (SpeedPortal portal : world.getSpeedPortals()) {
            if (portal.getX() == adjustedGridX && portal.getY() == adjustedGridY) {
                world.getSpeedPortals().remove(portal);
                repaint();
                return;
            }
        }
        world.addSpeedPortal(new SpeedPortal(adjustedGridX, adjustedGridY, speedMultiplier));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();


        g2d.setColor(new Color(173, 216, 230));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.translate(-cameraOffsetX, -cameraOffsetY);

        if (mainWindow.getInputHandler().isEditingMode()) {
            drawGrid(g2d);
            drawBuildLimitLines(g2d);
        }

        if (!mainWindow.isAnimatingDeath()) {
            drawPlayer(g2d);
        }

        drawWorld(g2d);

        if (mainWindow.isAnimatingDeath() && mainWindow.fragmentAnimation != null) {
            mainWindow.fragmentAnimation.draw(g2d);
        }

        g2d.dispose();

        if (isDimmed) {
            g2d = (Graphics2D) g.create();
            g2d.setColor(new Color(0, 0, 0, 128));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.dispose();
        }
    }

    public String getToolName() {
        return switch (selectedTool) {
            case 0 -> "None";
            case 1 -> "Tile";
            case 2 -> "Spike";
            case 3 -> "Orb";
            case 4 -> "Pad";
            case 9 -> "Portal";
            case 10 -> "Speed Portal";
            case 11 -> "Delete";
            default -> "Unknown";
        };
    }

    private void drawBuildLimitLines(Graphics2D g2d) {
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(2));

        int lineX = MIN_BUILD_X;
        int maxY = Math.max(getHeight(), MAX_BUILD_Y );

        g2d.drawLine(lineX, MIN_BUILD_Y, lineX, maxY);

        g2d.drawLine(lineX, MIN_BUILD_Y, getWidth() + cameraOffsetX, MIN_BUILD_Y);
        g2d.drawLine(lineX, MAX_BUILD_Y, getWidth() + cameraOffsetX, MAX_BUILD_Y);

        System.out.println("lineX: " + lineX + ", maxY: " + maxY);
    }

    private void drawPlayer(Graphics2D g2d) {
        if (player == null) return;

        double x = (int) player.getX();
        double y = (int) player.getY();

        AffineTransform originalTransform = g2d.getTransform();

        g2d.translate(x + 25, y + 25);

        g2d.rotate(Math.toRadians(player.getRotationAngle()));

        g2d.translate(-25, -25);

        if (player.getCurrentGameMode() == GameMode.UFO) {
            if (ufoImage != null) {
                if (player.isGravityReversed()) {
                    g2d.drawImage(ufoImage, 0, 50, 50, -50, null);
                } else {
                    g2d.drawImage(ufoImage, 0, 0, 50, 50, null);
                }
            } else {
                g2d.setColor(Color.MAGENTA);
                g2d.fillRect(0, 0, 50, 50);
            }
        } else if (player.getCurrentGameMode() == GameMode.BALL) {
            if (ballModeImage != null) {
                g2d.drawImage(ballModeImage, 0, 0, 50, 50, null);
            } else {
                g2d.setColor(Color.YELLOW);
                g2d.fillOval(0, 0, 50, 50);
            }
        } else if (player.getCurrentGameMode() == GameMode.SHIP) {
            if (player.isPlatformer() && shipImagePlatformer != null) {
                if (player.isShipFlipped() && player.isGravityReversed()) {
                    g2d.drawImage(shipImagePlatformer, 50, 50, -50, -50, null);
                } else if (player.isShipFlipped()) {
                    g2d.drawImage(shipImagePlatformer, 50, 0, -50, 50, null);
                } else if (player.isGravityReversed()) {
                    g2d.drawImage(shipImagePlatformer, 0, 50, 50, -50, null);
                } else {
                    g2d.drawImage(shipImagePlatformer, 0, 0, 50, 50, null);
                }
            } else if (shipImage != null) {
                if (player.isGravityReversed()) {
                    g2d.drawImage(shipImage, 0, 50, 50, -50, null);
                } else {
                    g2d.drawImage(shipImage, 0, 0, 50, 50, null);
                }
            } else {
                g2d.setColor(Color.BLUE);
                g2d.fillRect(0, 0, 50, 50);
            }
        } else if (player.getCurrentGameMode() == GameMode.WAVE) {
            if (waveImage != null) {
                g2d.drawImage(waveImage, 0, 0, 50, 50, null);
            } else {
                g2d.setColor(Color.CYAN);
                g2d.fillRect(0, 0, 50, 50);
            }
        } else if (player.getCurrentGameMode() == GameMode.ROBOT) {

            if (player.isGravityReversed() && player.isRobotFlipped()) {
                g2d.drawImage(robotImage, 50, 50, -50, -50, null);
            } else if (player.isRobotFlipped()) {
                g2d.drawImage(robotImage, 50, 0, -50, 50, null);
            } else if (player.isGravityReversed()) {
                g2d.drawImage(robotImage, 0, 50, 50, -50, null);
            } else if (robotImage != null) {
                g2d.drawImage(robotImage, 0, 0, 50, 50, null);
            } else {
                g2d.setColor(Color.PINK);
                g2d.fillRect(0, 0, 50, 50);
            }
        } else if (player.getCurrentGameMode() == GameMode.SPIDER) {
            if (spiderImage != null) {
                if (player.isGravityReversed()) {
                    g2d.drawImage(spiderImage, 0, 50, 50, -50, null);
                } else {
                    g2d.drawImage(spiderImage, 0, 0, 50, 50, null);
                }
            } else {
                g2d.setColor(Color.ORANGE);
                g2d.fillRect(0, 0, 50, 50);
            }
        } else {
            if (playerImage != null) {
                if (player.isPlatformer()) {
                    g2d.drawImage(playerImage, 50, 0, -50, 50, null);
                } else {
                    g2d.drawImage(playerImage, 0, 0, 50, 50, null);
                }
            } else {
                g2d.setColor(Color.BLUE);
                g2d.fillRect(0, 0, 50, 50);
            }
        }

        g2d.setTransform(originalTransform);

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Attempt " + mainWindow.getAttempts(), 10, 20);
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(Color.GRAY);
        int panelWidth = getWidth() * 5;
        int panelHeight = getHeight() * 5;

        for (int x = 0; x < panelWidth + cameraOffsetX; x += 50) {
            g2d.drawLine(x, cameraOffsetY, x, panelHeight + cameraOffsetY);
        }
        for (int x = -50; x > cameraOffsetX; x -= 50) {
            g2d.drawLine(x, cameraOffsetY, x, panelHeight + cameraOffsetY);
        }

        for (int y = 0; y < panelHeight + cameraOffsetY; y += 50) {
            g2d.drawLine(cameraOffsetX, y, panelWidth + cameraOffsetX, y);
        }
        for (int y = -50; y > cameraOffsetY; y -= 50) {
            g2d.drawLine(cameraOffsetX, y, panelWidth + cameraOffsetX, y);
        }
    }

    private void drawWorld(Graphics2D g2d) {
        Rectangle cameraBounds = new Rectangle(cameraOffsetX, cameraOffsetY, getWidth(), getHeight());

        for (Tile tile : world.getTiles()) drawTile(g2d, tile, cameraBounds);
        for (Spike spike : world.getSpikes()) drawSpike(g2d, spike);
        for (Orb orb : world.getOrbs()) drawOrb(g2d, orb);
        for (Pad pad : World.getPads()) drawPad(g2d, pad);
        for (Portal portal : world.getPortals()) drawPortal(g2d, portal);
        for (SpeedPortal speedPortal : world.getSpeedPortals()) drawSpeedPortal(g2d, speedPortal);
    }

    private void drawPortal(Graphics2D g2d, Portal portal) {
        double x = portal.getX() * 50;
        double y = portal.getY() * 50;

        Image portalImage = portalImages.get(portal.getTargetGameMode());
        if (portalImage != null) {
            g2d.drawImage(portalImage, (int)x, (int)y, 50, 100, null);
        } else {
            switch (portal.getTargetGameMode()) {
                case CUBE:
                    g2d.setColor(Color.GREEN);
                    break;
                case SHIP:
                    g2d.setColor(Color.BLUE);
                    break;
                case BALL:
                    g2d.setColor(Color.YELLOW);
                    break;
                case UFO:
                    g2d.setColor(Color.MAGENTA);
                    break;
                case WAVE:
                    g2d.setColor(Color.CYAN);
                    break;
                case ROBOT:
                    g2d.setColor(Color.PINK);
                    break;
                case SPIDER:
                    g2d.setColor(Color.ORANGE);
                    break;
                default:
                    g2d.setColor(Color.GRAY);
            }
            g2d.fillRect((int)x, (int)y, 50, 100);
        }
    }

    private void drawSpeedPortal(Graphics2D g2d, SpeedPortal speedPortal) {
        double x = speedPortal.getX() * 50;
        double y = speedPortal.getY() * 50;

        double speedMultiplier = speedPortal.getSpeedMultiplier();

        Image portalImage = null;
        if (speedMultiplier == 0.807) {
            portalImage = portalImages.get(GameMode.SLOW);
        } else if (speedMultiplier == 1.0) {
            portalImage = portalImages.get(GameMode.NORMAL);
        } else if (speedMultiplier == 1.243) {
            portalImage = portalImages.get(GameMode.FAST);
        } else if (speedMultiplier == 1.502) {
            portalImage = portalImages.get(GameMode.VERY_FAST);
        } else if (speedMultiplier == 1.849) {
            portalImage = portalImages.get(GameMode.EXTREMELY_FAST);
        }

        if (portalImage != null) {
            g2d.drawImage(portalImage, (int)x, (int)y, 50, 100, null);
        } else {
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect((int)x, (int)y, 50, 100);
        }
    }

    private void drawTile(Graphics2D g2d, Tile tile, Rectangle cameraBounds) {
        Rectangle tileBounds = new Rectangle(tile.getX() * 50, tile.getY() * 50, 50, 50);
        if (!cameraBounds.intersects(tileBounds)) return;

        if (tileImage != null) {
            g2d.drawImage(tileImage, tileBounds.x, tileBounds.y, tileBounds.width, tileBounds.height, null);
        } else {
            g2d.setColor(Color.GREEN);
            g2d.fillRect(tileBounds.x, tileBounds.y, tileBounds.width, tileBounds.height);
        }

        if (showHitboxes) {
            g2d.setColor(Color.RED);
            g2d.drawRect(tileBounds.x, tileBounds.y, tileBounds.width, tileBounds.height);
        }
    }

    private void drawSpike(Graphics2D g2d, Spike spike) {
        int x = spike.x() * 50;
        int y = spike.y() * 50;

        if (spikeImage != null) {
            g2d.drawImage(spikeImage, x, y, 50, 50, null);
        } else {
            g2d.setColor(Color.RED);
            g2d.fillRect(x, y, 50, 50);
        }

        if (showHitboxes) {
            g2d.setColor(Color.GREEN);
            g2d.drawPolygon(new int[]{x, x + 25, x + 50}, new int[]{y + 50, y, y + 50}, 3);
            g2d.setColor(Color.RED);
            g2d.drawRect(x + 19, y + 15, 12, 20);
        }

    }
    private void drawOrb(Graphics2D g2d, Orb orb) {
        int x = orb.getX() * 50;
        int y = orb.getY() * 50;
        String orbKey = orb.getColor().toLowerCase();
        if ("spider".equals(orb.getColor())) orbKey += "_" + orb.getDirection();

        Image orbImage = orbImages.get(orbKey);
        if (orbImage != null) {
            g2d.drawImage(orbImage, x + 7, y + 7, 36, 36, null);
        } else {
            g2d.setColor(Color.YELLOW);
            g2d.fillOval(x + 12, y + 12, 36, 36);
        }

        if (showHitboxes) {
            g2d.setColor(Color.RED);
            g2d.drawRect(x, y, 50, 50);
        }
    }
    private void drawPad(Graphics2D g2d, Pad pad) {
        int x = pad.getX() * 50;
        int y = pad.getY() * 50;
        String padKey = pad.getColor().toLowerCase();

        Image padImage = padImages.get(padKey);
        if (padImage != null) {
            if ("bottom".equals(pad.getPosition())) {
                g2d.drawImage(padImage, x, y + 20, 50, -20, null);
            } else {
                g2d.drawImage(padImage, x, y + 30, 50, 20, null);
            }
        } else {
            g2d.setColor(Color.MAGENTA);
            g2d.fillRect(x + 12, y + 14, 36, 36);
        }

        if (showHitboxes) {
            g2d.setColor(Color.RED);
            g2d.drawRect(x, y, 50, 50);
        }
    }

    public int getCameraOffsetY() {
        return cameraOffsetY;
    }

    public void setCameraOffsetX(int offset) {
        this.cameraOffsetX = offset;
        repaint();
    }

    public Orb getActivatedOrb(Player player) {
        for (Orb orb : world.getOrbs()) {
            if (Math.abs(player.getX() - orb.getX() * 50) < 50 && Math.abs(player.getY() - orb.getY() * 50) < 50) {
                return orb;
            }
        }
        return null;
    }

    public void setCameraOffsetY(int offset) {
        this.cameraOffsetY = offset;
        repaint();
    }

    public void setSelectedOrbColor(String selectedOrbColor) {
        this.selectedOrbColor = selectedOrbColor;
    }

    public void setSelectedPadColor(String selectedPadColor) {
        this.selectedPadColor = selectedPadColor;
    }



    public World getWorld() {
        return world;
    }

    public boolean isCollision(double x, double y) {

        for (Tile tile : world.getTiles()) {
            if (tile.isSolid() && x < tile.getX() * 50 + 50 && x + 50 > tile.getX() * 50 &&
                    y < tile.getY() * 50 + 50 && y + 50 > tile.getY() * 50) {
                return true;
            }
        }
        return false;
    }
    public boolean isCollisionWithCeilingOrFloor(double x, double y, boolean isGravityReversed) {
        double checkY = isGravityReversed ? y : y + 50;
        for (Tile tile : world.getTiles()) {
            if (tile.isSolid() && x < tile.getX() * 50 + 50 && x + 50 > tile.getX() * 50 &&
                    checkY >= tile.getY() * 50 && checkY <= tile.getY() * 50 + 50) {
                return true;
            }
        }
        return false;
    }

    public boolean isCollision(double playerX, double playerY, int spikeX, int spikeY) {
        int playerWidth = 50;
        int playerHeight = 50;
        double rectX = spikeX * 50 + 19;
        double rectY = spikeY * 50 + 15;
        int rectWidth = 12;
        int rectHeight = 20;

        return playerX < rectX + rectWidth &&
                playerX + playerWidth > rectX &&
                playerY < rectY + rectHeight &&
                playerY + playerHeight > rectY;
    }

    public Player getPlayer() {
        return player;
    }

    public void setShowHitboxes(boolean showHitboxes) {
        this.showHitboxes = showHitboxes;
        repaint();
    }



    public void setPortalSpeedMultiplier(double speedMultiplier) {
        this.portalSpeedMultiplier = speedMultiplier;
    }

    public void setSelectedPadPosition(String selectedPadPosition) {
        this.selectedPadPosition = selectedPadPosition;
    }

    public Pad getActivatedPad(Player player) {
        for (Pad pad : World.getPads()) {
            if (Math.abs(player.getX() - pad.getX() * 50) < 50 && Math.abs(player.getY() - pad.getY() * 50) < 50) {
                return pad;
            }
        }
        return null;
    }
}