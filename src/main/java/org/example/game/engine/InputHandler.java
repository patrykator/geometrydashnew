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
    private int jumpStrength;
    private Set<Integer> pressedKeys = new HashSet<>();
    private boolean orbActivated = false; // Flaga śledząca aktywację orba
    private boolean teleportActivated = false; // Flaga śledząca aktywację teleportacji
    private boolean spiderOrbKeyPressed = false; // Flaga śledząca trzymanie klawisza spider orba
    private boolean editingMode = false;
    private GameEngine gameEngine;
    private boolean spaceOrUpPressed = false;

    private boolean escPressed = false;

    public InputHandler(Player player, PlayerPanel playerPanel, int jumpStrength, GameEngine gameEngine) {
        this.player = player;
        this.playerPanel = playerPanel;
        this.jumpStrength = jumpStrength;
        this.gameEngine = gameEngine;
    }

    public void setEditingMode(boolean editingMode){
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
        if (editingMode) return; // Dodano: Ignoruj input klawiatury w trybie edycji (poza ESC)
        pressedKeys.add(e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (!escPressed) {
                escPressed = true;
                if (gameEngine.isGamePaused()) {
                    gameEngine.resumeGame();
                } else {
                    gameEngine.pauseGame("");
                }
            }
        } else {
            pressedKeys.add(e.getKeyCode());
            if ((e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP) && !orbActivated && !teleportActivated) {
                orbActivated = true;
                for (Orb orb : playerPanel.getWorld().getOrbs()) {
                    if (Math.abs(player.getX() - orb.getX() * 50) < 50 && Math.abs(player.getY() - orb.getY() * 50) < 50) {
                        System.out.println("Orb activated: " + ", Ship mode: " + player.isShipMode());
                        System.out.println("Player velocityY: " + player.getVelocityY());
                        orb.activate(player);
                        orbActivated = true; // Ustawienie flagi po aktywacji orba
                        if ("spider".equals(orb.getColor())) {
                            teleportActivated = true; // Ustawienie flagi po aktywacji teleportacji
                            spiderOrbKeyPressed = true; // Ustawienie flagi po aktywacji spider orba
                        }
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            escPressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP) {
            orbActivated = false; // Resetowanie flagi po zwolnieniu klawisza
            teleportActivated = false; // Resetowanie flagi po zwolnieniu klawisza
            spiderOrbKeyPressed = false; // Resetowanie flagi po zwolnieniu klawisza spider orba
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