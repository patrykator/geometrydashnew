package org.example;

import org.example.game.engine.GameEngine;
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

            MainWindow mainWindow = new MainWindow(player, new World());

            MainWindow.configureMainWindow(mainWindow, gameWidth, gameHeight);
            mainWindow.setVisible(false);


            MainWindow.setPlayerPosition(player, gameHeight);

            GameEngine gameEngine = new GameEngine(mainWindow, player);
            gameEngine.setCurrentGameMode(GameMode.CUBE);
            mainWindow.setGameEngine(gameEngine);

            gameEngine.setMainWindow(mainWindow);

            mainWindow.showMainMenu();
        });
    }
}