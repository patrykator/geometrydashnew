package org.example;

import org.example.game.engine.GameEngine;
import org.example.game.engine.InputHandler;
import org.example.game.entities.GameMode;
import org.example.game.ui.MainWindow;
import org.example.game.entities.Player;
import org.example.game.world.World;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int gameWidth = 1600;
            int gameHeight = 900;

            Player player = MainWindow.createPlayer();

            MainWindow mainWindow = new MainWindow(player, new World()); // Przekazujemy pusty świat
            MainWindow.configureMainWindow(mainWindow, gameWidth, gameHeight);
            mainWindow.setVisible(false);

            MainWindow.setPlayerPosition(player, gameHeight);



            // Utworzenie GameEngine
            GameEngine gameEngine = new GameEngine(mainWindow, player);
            gameEngine.setCurrentGameMode(GameMode.SHIP);
            mainWindow.setGameEngine(gameEngine);

            // Ustawienie GameEngine w MainWindow
            gameEngine.setMainWindow(mainWindow);



            // Pokaż menu główne
            mainWindow.showMainMenu();
        });
    }
}