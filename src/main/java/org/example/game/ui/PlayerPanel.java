package org.example.game.ui;

import org.example.game.entities.*;
import org.example.game.utilities.ImageLoader;
import org.example.game.world.Tile;
import org.example.game.world.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
    private String selectedPadPosition = "top";
    private final int MIN_BUILD_Y = -4450;
    private final int MAX_BUILD_Y = 4450;
    private final int MIN_BUILD_X = -200;
    private int targetX = -1;
    private int targetY = -1;
    private boolean isSettingTeleportTarget = false;
    private Orb selectedTeleportOrb = null;
    private boolean showTeleportTarget = false;
    private boolean drawPlayer = true;
    private final Map<GameMode, Image> speedPortalImages = new HashMap<>();
    private String selectedSpikePosition = "top";
    private Image checkpointImage;
    private Image activatedCheckpointImage;





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

    public void stopDrawingPlayer() {
        drawPlayer = false;
    }

    public void resumeDrawingPlayer() {
        drawPlayer = true;
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
        if (mainWindow.getInputHandler().isEditingMode() && dragStartPoint == null) {
            repaint();
        }
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
        if (!mainWindow.getInputHandler().isEditingMode()) {
            return;
        }

        int gridX = (e.getX() + cameraOffsetX) / 50;
        int gridY = (e.getY() + cameraOffsetY) / 50;

        if (isSettingTeleportTarget) {
            targetX = gridX * 50;
            targetY = gridY * 50;

            if (selectedTeleportOrb != null) {
                setTeleportTargetForLastOrb(targetX, targetY);
                showTeleportTarget = true;
            }
            isSettingTeleportTarget = false;
            selectedTeleportOrb = null;
            setCursor(Cursor.getDefaultCursor());
            repaint();
        } else {
            Orb clickedOrb = null;
            if (selectedTool == 3 && "teleport".equals(selectedOrbColor)) {
                for (Orb orb : world.getOrbs()) {
                    if (e.getX() + cameraOffsetX >= orb.getX() * 50 &&
                            e.getX() + cameraOffsetX < (orb.getX() + 1) * 50 &&
                            e.getY() + cameraOffsetY >= orb.getY() * 50 &&
                            e.getY() + cameraOffsetY < (orb.getY() + 1) * 50) {
                        clickedOrb = orb;
                        break;
                    }
                }
            }

            if (clickedOrb != null) {
                if (selectedTeleportOrb == clickedOrb) {
                    showTeleportTarget = !showTeleportTarget;
                } else {
                    selectedTeleportOrb = clickedOrb;
                    showTeleportTarget = true;
                }
                targetX = selectedTeleportOrb.getTeleportX() != null ? selectedTeleportOrb.getTeleportX() : -1;
                targetY = selectedTeleportOrb.getTeleportY() != null ? selectedTeleportOrb.getTeleportY() : -1;
                repaint();
            } else {
                showTeleportTarget = false;
                selectedTeleportOrb = null;
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
                        placePad(gridX, gridY);
                        break;
                    case 5:
                        placeCheckpoint(gridX, gridY);
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
                    case 12:
                        placeLevelEnd(gridX, gridY);
                        break;
                }
            }
        }
    }

    private void placeLevelEnd(int gridX, int gridY) {
        if (mainWindow.getGameEngine().isGamePaused()) {
            return;
        }

        if (isWithinBuildLimits(gridX, gridY)) {
            return;
        }

        int adjustedGridX = adjustGridX(gridX, mouseX, cameraOffsetX);
        int adjustedGridY = adjustGridY(gridY, mouseY, cameraOffsetY);

        world.getLevelEnds().removeIf(le -> le.getX() == adjustedGridX && le.getY() == adjustedGridY);

        world.addLevelEnd(new LevelEnd(adjustedGridX, adjustedGridY));

        repaint();
    }



    private void setTeleportTargetForLastOrb(int x, int y) {
        if (selectedTeleportOrb != null) {
            selectedTeleportOrb.setTeleportX(x);
            selectedTeleportOrb.setTeleportY(y);
        }
    }

    private void deleteObject(int gridX, int gridY) {

        int adjustedGridX = adjustGridX(gridX, mouseX, cameraOffsetX);
        int adjustedGridY = adjustGridY(gridY, mouseY, cameraOffsetY);


        world.getTiles().removeIf(tile -> tile.getX() == adjustedGridX && tile.getY() == adjustedGridY);

        world.getSpikes().removeIf(spike -> spike.x() == adjustedGridX && spike.y() == adjustedGridY);

        world.getOrbs().removeIf(orb -> orb.getX() == adjustedGridX && orb.getY() == adjustedGridY);

        World.getPads().removeIf(pad -> pad.getX() == adjustedGridX && pad.getY() == adjustedGridY);

        world.getPortals().removeIf(portal -> portal.getX() == adjustedGridX && portal.getY() == adjustedGridY);

        world.getSpeedPortals().removeIf(speedPortal -> speedPortal.getX() == adjustedGridX && speedPortal.getY() == adjustedGridY);

        world.getLevelEnds().removeIf(levelEnd -> levelEnd.getX() == adjustedGridX && levelEnd.getY() == adjustedGridY);

        world.getCheckpoints().removeIf(checkpoint -> checkpoint.getX() == adjustedGridX && checkpoint.getY() == adjustedGridY);

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

        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/Checkpoint.png", img -> {
            checkpointImage = img;
            repaint();
        });

        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/Checkpoint_Activated.png", img -> {
            activatedCheckpointImage = img;
            repaint();
        });


        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/portal_cube.png", img -> portalImages.put(GameMode.CUBE, img));
        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/portal_ship.png", img -> portalImages.put(GameMode.SHIP, img));
        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/portal_ball.png", img -> portalImages.put(GameMode.BALL, img));
        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/portal_ufo.png", img -> portalImages.put(GameMode.UFO, img));
        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/portal_wave.png", img -> portalImages.put(GameMode.WAVE, img));
        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/portal_robot.png", img -> portalImages.put(GameMode.ROBOT, img));
        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/portal_spider.png", img -> portalImages.put(GameMode.SPIDER, img));

        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/portal_slow.png", img -> speedPortalImages.put(GameMode.SLOW, img));
        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/portal_normal.png", img -> speedPortalImages.put(GameMode.NORMAL, img));
        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/portal_fast.png", img -> speedPortalImages.put(GameMode.FAST, img));
        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/portal_very_fast.png", img -> speedPortalImages.put(GameMode.VERY_FAST, img));
        ImageLoader.loadImageAsync("src/main/java/org/example/game/img/portal_extremely_fast.png", img -> speedPortalImages.put(GameMode.EXTREMELY_FAST, img));

        loadEntityImages("Orb", orbImages, "Yellow", "Purple", "Red", "Blue", "Green", "Black", "Spider_Up", "Spider_Down", "Teleport");
        loadEntityImages("Pad", padImages, "Yellow", "Purple", "Red", "Blue", "Spider");
    }

    public Image getCheckpointImage() {
        return checkpointImage;
    }

    public Image getActivatedCheckpointImage() {
        return activatedCheckpointImage;
    }


    public Image getTileImage() {
        return tileImage;
    }

    public Image getPlayerImage() {
        return playerImage;
    }

    public Image getShipImage() {
        return shipImage;
    }

    public Image getShipImagePlatformer() {
        return shipImagePlatformer;
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

    public Image getSpikeImage() {
        return spikeImage;
    }

    public Map<String, Image> getOrbImages() {
        return orbImages;
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
            world.addSpike(new Spike(adjustedGridX, adjustedGridY, selectedSpikePosition));
            repaint();
        }
    }

    public void setSelectedSpikePosition(String selectedSpikePosition) {
        this.selectedSpikePosition = selectedSpikePosition;
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

        Orb existingOrb = null;
        for (Orb orb : world.getOrbs()) {
            if (orb.getX() == adjustedGridX && orb.getY() == adjustedGridY) {
                existingOrb = orb;
                break;
            }
        }

        if (existingOrb != null) {
            world.getOrbs().remove(existingOrb);
        }

        if ("spider".equals(selectedOrbColor)) {
            world.addOrb(new Orb(adjustedGridX, adjustedGridY, selectedOrbColor, selectedOrbDirection));
        } else if ("teleport".equals(selectedOrbColor)) {
            Orb newOrb = new Orb(adjustedGridX, adjustedGridY, selectedOrbColor, selectedOrbDirection);
            world.addOrb(newOrb);
            isSettingTeleportTarget = true;
            showTeleportTarget = false;
            selectedTeleportOrb = newOrb;
            targetX = -1;
            targetY = -1;
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        } else {
            world.addOrb(new Orb(adjustedGridX, adjustedGridY, selectedOrbColor));
        }
        repaint();
    }

    private void placePad(int gridX, int gridY) {
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
        world.addPad(new Pad(adjustedGridX, adjustedGridY, selectedPadColor, selectedPadPosition));
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
            if (!isSettingTeleportTarget && isDraggingCamera()) {
                highlightGridSquare(g2d);
            }
        }

        if (drawPlayer) {
            player.draw(g2d, this);
        }

        for (Tile tile : world.getTiles()) {
            tile.draw(g2d, this);
        }
        for (Spike spike : world.getSpikes()) {
            spike.draw(g2d, this);
        }
        for (Orb orb : world.getOrbs()) {
            orb.draw(g2d, this);
        }
        for (Pad pad : World.getPads()) {
            pad.draw(g2d, this);
        }
        for (Portal portal : world.getPortals()) {
            portal.draw(g2d, this);
        }
        for (SpeedPortal speedPortal : world.getSpeedPortals()) {
            speedPortal.draw(g2d, this);
        }
        for (LevelEnd levelEnd : world.getLevelEnds()) {
            levelEnd.draw(g2d, this);
        }

        for (Checkpoint checkpoint : world.getCheckpoints()) {
            checkpoint.draw(g2d, this);
        }

        if (!player.isPlatformer()) {
            drawLevelEnd(g2d);
        }




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

        if (mainWindow.getInputHandler().isEditingMode() && showTeleportTarget && selectedTeleportOrb != null && targetX != -1 && targetY != -1) {
            g2d = (Graphics2D) g.create();
            g2d.translate(-cameraOffsetX, -cameraOffsetY);
            g2d.setColor(Color.RED);
            g2d.fillRect(targetX, targetY, 50, 50);

            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(selectedTeleportOrb.getX() * 50 + 25, selectedTeleportOrb.getY() * 50 + 25, targetX + 25, targetY + 25);

            g2d.dispose();
        }
    }

    private void drawLevelEnd(Graphics2D g2d) {
        if ( !getPlayer().isPlatformer() && !getMainWindow().getInputHandler().isEditingMode() ) {
            double endX = getMainWindow().getGameEngine().getLevelEndX();

            int x = (int) (endX);


            g2d.setColor(Color.GREEN);
            g2d.setStroke(new BasicStroke(3));

            g2d.drawLine(x, -10000, x, 10000);
        }
    }


    private boolean isDraggingCamera() {
        return dragStartPoint == null;
    }

    private void highlightGridSquare(Graphics2D g2d) {
        int gridX = (mouseX + cameraOffsetX) / 50;
        int gridY = (mouseY + cameraOffsetY) / 50;

        int adjustedGridX = adjustGridX(gridX, mouseX, cameraOffsetX);
        int adjustedGridY = adjustGridY(gridY, mouseY, cameraOffsetY);

        g2d.setColor(new Color(255, 0, 0, 100));
        g2d.fillRect(adjustedGridX * 50, adjustedGridY * 50, 50, 50);
    }

    private void placeCheckpoint(int gridX, int gridY) {
        if (mainWindow.getGameEngine().isGamePaused()) {
            return;
        }

        if (isWithinBuildLimits(gridX, gridY)) {
            return;
        }

        int adjustedGridX = adjustGridX(gridX, mouseX, cameraOffsetX);
        int adjustedGridY = adjustGridY(gridY, mouseY, cameraOffsetY);

        Checkpoint existingCheckpoint = null;
        for (Checkpoint checkpoint : world.getCheckpoints()) {
            if (checkpoint.getX() == adjustedGridX && checkpoint.getY() == adjustedGridY) {
                existingCheckpoint = checkpoint;
                break;
            }
        }

        if (existingCheckpoint != null) {
            world.getCheckpoints().remove(existingCheckpoint);
        } else {
            Checkpoint newCheckpoint = new Checkpoint(adjustedGridX, adjustedGridY);
            world.addCheckpoint(newCheckpoint);
        }

        repaint();
    }

    public String getToolName() {
        return switch (selectedTool) {
            case 0 -> "None";
            case 1 -> "Tile";
            case 2 -> "Spike";
            case 3 -> "Orb";
            case 4 -> "Pad";
            case 5 -> "Checkpoint";
            case 9 -> "Portal";
            case 10 -> "Speed Portal";
            case 11 -> "Delete";
            case 12 -> "Level End";
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
            if (tile.isSolid() &&  x < tile.getX() * 50 + 50 &&  x + 50 > tile.getX() * 50 &&
                     y < tile.getY() * 50 + 50 &&  y + 50 > tile.getY() * 50) {
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


    public boolean isShowHitboxes() {
        return showHitboxes;
    }

    public Map<String, Image> getPadImages() {
        return padImages;
    }

    public Map<GameMode, Image> getPortalImages() {
        return portalImages;
    }

    public Map<GameMode, Image> getSpeedPortalImages() {
        return speedPortalImages;
    }

    public MainWindow getMainWindow() {
        return mainWindow;
    }
}