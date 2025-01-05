package org.example.game.engine;

import org.example.game.entities.Orb;
import org.example.game.entities.Player;
import org.example.game.ui.PlayerPanel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class InputHandler extends KeyAdapter {
    private Player player;
    private PlayerPanel playerPanel;
    private final Set<Integer> pressedKeys = new HashSet<>();
    private boolean orbActivated = false;
    private boolean teleportActivated = false;
    private boolean spiderOrbKeyPressed = false;
    private boolean editingMode = false;
    private final GameEngine gameEngine;
    private boolean escPressed = false;

    public InputHandler(Player player, PlayerPanel playerPanel, GameEngine gameEngine) {
        this.player = player;
        this.playerPanel = playerPanel;
        this.gameEngine = gameEngine;
    }

    public void setEditingMode(boolean editingMode) {
        this.editingMode = editingMode;
        this.pressedKeys.clear();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setPlayerPanel(PlayerPanel playerPanel) {
        this.playerPanel = playerPanel;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (editingMode) return;

        int keyCode = e.getKeyCode();
        pressedKeys.add(keyCode);

        if (isEscapeKey(keyCode)) {
            handleEscapeKeyPressed();
        } else if (isJumpKey(keyCode)) {
            handleJumpKeyPressed();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        pressedKeys.remove(keyCode);

        if (isEscapeKey(keyCode)) {
            handleEscapeKeyReleased();
        } else if (isJumpKey(keyCode)) {
            handleJumpKeyReleased();
        }
    }

    private boolean isEscapeKey(int keyCode) {
        return keyCode == KeyEvent.VK_ESCAPE;
    }

    private boolean isJumpKey(int keyCode) {
        return keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_UP;
    }

    private void handleEscapeKeyPressed() {
        if (!escPressed) {
            escPressed = true;
            togglePauseGame();
        }
    }

    private void handleEscapeKeyReleased() {
        escPressed = false;
    }

    private void togglePauseGame() {
        if (gameEngine.isGamePaused()) {
            gameEngine.resumeGame();
        } else {
            gameEngine.pauseGame();
        }
    }

    private void handleJumpKeyPressed() {
        if (!orbActivated && !teleportActivated) {
            checkOrbActivation();
        }
    }

    private void handleJumpKeyReleased() {
        orbActivated = false;
        teleportActivated = false;
        spiderOrbKeyPressed = false;
    }

    private void checkOrbActivation() {
        for (Orb orb : playerPanel.getWorld().getOrbs()) {
            if (isPlayerNearOrb(orb)) {
                activateOrb(orb);
                break;
            }
        }
    }

    private boolean isPlayerNearOrb(Orb orb) {
        return Math.abs(player.getX() - orb.getX() * 50) < 50 &&
                Math.abs(player.getY() - orb.getY() * 50) < 50;
    }

    private void activateOrb(Orb orb) {
        System.out.println("Orb activated: " + orb.getColor() + ", Ship mode: " + player.isShipMode());
        System.out.println("Player velocityY: " + player.getVelocityY());
        orb.activate(player);
        orbActivated = true;

        if ("spider".equals(orb.getColor())) {
            teleportActivated = true;
            spiderOrbKeyPressed = true;
        }
    }

    public Set<Integer> getPressedKeys() {
        return pressedKeys;
    }

    public boolean isSpiderOrbKeyPressed() {
        return spiderOrbKeyPressed;
    }

    public boolean isEditingMode() {
        return editingMode;
    }
}