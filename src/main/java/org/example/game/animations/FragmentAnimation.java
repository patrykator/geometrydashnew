package org.example.game.animations;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class FragmentAnimation {
    private List<Fragment> fragments;
    private boolean animationFinished;

    public FragmentAnimation(double x, double y, BufferedImage playerImage, int numFragmentsX, int numFragmentsY, double initialSpeed, double gravity) {
        this.fragments = new ArrayList<>();
        this.animationFinished = false;

        // Docelowy rozmiar animacji
        int targetWidth = 50;
        int targetHeight = 50;

        // Skalowanie fragmentów
        double scaleX = (double) targetWidth / playerImage.getWidth(null);
        double scaleY = (double) targetHeight / playerImage.getHeight(null);

        // Podziel obraz na fragmenty
        int fragmentWidth = playerImage.getWidth(null) / numFragmentsX;
        int fragmentHeight = playerImage.getHeight(null) / numFragmentsY;

        for (int i = 0; i < numFragmentsX; i++) {
            for (int j = 0; j < numFragmentsY; j++) {
                int startX = i * fragmentWidth;
                int startY = j * fragmentHeight;
                BufferedImage fragmentImage = playerImage.getSubimage(startX, startY, fragmentWidth, fragmentHeight);

                // Skaluj fragment
                BufferedImage scaledFragmentImage = new BufferedImage((int) (fragmentWidth * scaleX), (int) (fragmentHeight * scaleY), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = scaledFragmentImage.createGraphics();
                g2d.drawImage(fragmentImage, 0, 0, (int) (fragmentWidth * scaleX), (int) (fragmentHeight * scaleY), null);
                g2d.dispose();

                double angle = Math.random() * 2 * Math.PI; // Losowy kierunek
                double speed = initialSpeed * (1 + Math.random() * 0.5); // Losowa prędkość
                double vx = speed * Math.cos(angle);
                double vy = speed * Math.sin(angle);

                // Pozycjonowanie fragmentów w odniesieniu do docelowego rozmiaru
                fragments.add(new Fragment(x + startX * scaleX + (fragmentWidth * scaleX) / 2.0, y + startY * scaleY + (fragmentHeight * scaleY) / 2.0, vx, vy, gravity, scaledFragmentImage));
            }
        }
    }

    public void update() {
        boolean allFragmentsFinished = true;
        for (Fragment fragment : fragments) {
            fragment.update();
            if (!fragment.isFinished()) {
                allFragmentsFinished = false;
            }
        }
        animationFinished = allFragmentsFinished;
    }

    public void draw(Graphics2D g2d) {
        for (Fragment fragment : fragments) {
            fragment.draw(g2d);
        }
    }

    public boolean isAnimationFinished() {
        return animationFinished;
    }

    private static class Fragment {
        private double x;
        private double y;
        private double vx;
        private double vy;
        private double gravity;
        private BufferedImage image;
        private boolean finished;
        private double rotation; // Dodane pole na rotację
        private double rotationSpeed; // Dodane pole na prędkość rotacji

        public Fragment(double x, double y, double vx, double vy, double gravity, BufferedImage image) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.gravity = gravity;
            this.image = image;
            this.finished = false;
            this.rotation = 0;
            this.rotationSpeed = (Math.random() - 0.5) * 0.2; // Losowa prędkość rotacji
        }

        public void update() {
            if (!finished) {
                x += vx;
                y += vy;
                vy += gravity;
                rotation += rotationSpeed;

                // Zmniejsz prędkość w czasie - ale wolniej
                vx *= 0.995; // Było 0.99
                vy *= 0.995; // Było 0.99

                // Szybsze znikanie
                if (y > 900) { // Było 1000
                    finished = true;
                }
            }
        }

        public void draw(Graphics2D g2d) {
            if (!finished) {
                AffineTransform oldTransform = g2d.getTransform();
                g2d.translate(x, y);
                g2d.rotate(rotation, image.getWidth() / 2.0, image.getHeight() / 2.0);
                g2d.drawImage(image, -image.getWidth() / 2, -image.getHeight() / 2, null);
                g2d.setTransform(oldTransform);
            }
        }

        public boolean isFinished() {
            return finished;
        }
    }
}