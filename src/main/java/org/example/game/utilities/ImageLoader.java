package org.example.game.utilities;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class ImageLoader {
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public static void loadImageAsync(String path, Consumer<Image> callback) {
        executor.submit(() -> {
            ImageIcon icon = new ImageIcon(path);
            Image image = icon.getImage();
            SwingUtilities.invokeLater(() -> callback.accept(image));
        });
    }
}