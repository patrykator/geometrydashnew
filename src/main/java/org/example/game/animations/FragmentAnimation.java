package org.example.game.animations;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class FragmentAnimation {
    private final List<Fragment> fragments;
    private boolean animationFinished;
    private static final int TARGET_WIDTH = 50;
    private static final int TARGET_HEIGHT = 50;

    public FragmentAnimation(double x, double y, BufferedImage playerImage, int numFragmentsX, int numFragmentsY, double initialSpeed, double gravity) {
        this.fragments = new ArrayList<>();
        this.animationFinished = false;

        double scaleX = (double) TARGET_WIDTH / playerImage.getWidth(null);
        double scaleY = (double) TARGET_HEIGHT / playerImage.getHeight(null);

        int fragmentWidth = playerImage.getWidth(null) / numFragmentsX;
        int fragmentHeight = playerImage.getHeight(null) / numFragmentsY;

        for (int i = 0; i < numFragmentsX; i++) {
            for (int j = 0; j < numFragmentsY; j++) {
                BufferedImage fragmentImage = playerImage.getSubimage(i * fragmentWidth, j * fragmentHeight, fragmentWidth, fragmentHeight);
                BufferedImage scaledFragmentImage = getScaledFragmentImage(fragmentImage, scaleX, scaleY);

                double angle = Math.random() * 2 * Math.PI;
                double speed = initialSpeed * (1 + Math.random() * 0.5);
                double vx = speed * Math.cos(angle);
                double vy = speed * Math.sin(angle);

                fragments.add(new Fragment(x + i * fragmentWidth * scaleX + (fragmentWidth * scaleX) / 2.0, y + j * fragmentHeight * scaleY + (fragmentHeight * scaleY) / 2.0, vx, vy, gravity, scaledFragmentImage));
            }
        }
    }

    private BufferedImage getScaledFragmentImage(BufferedImage fragmentImage, double scaleX, double scaleY) {
        BufferedImage scaledFragmentImage = new BufferedImage((int) (fragmentImage.getWidth() * scaleX), (int) (fragmentImage.getHeight() * scaleY), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledFragmentImage.createGraphics();
        g2d.drawImage(fragmentImage, 0, 0, (int) (fragmentImage.getWidth() * scaleX), (int) (fragmentImage.getHeight() * scaleY), null);
        g2d.dispose();
        return scaledFragmentImage;
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
        private final double gravity;
        private final BufferedImage image;
        private boolean finished;
        private double rotation;
        private final double rotationSpeed;
        private static final double SPEED_DECREASE_FACTOR = 0.995;
        private static final int DISAPPEAR_Y_THRESHOLD = 900;

        public Fragment(double x, double y, double vx, double vy, double gravity, BufferedImage image) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.gravity = gravity;
            this.image = image;
            this.finished = false;
            this.rotation = 0;
            this.rotationSpeed = (Math.random() - 0.5) * 0.2;
        }

        public void update() {
            if (!finished) {
                x += vx;
                y += vy;
                vy += gravity;
                rotation += rotationSpeed;

                vx *= SPEED_DECREASE_FACTOR;
                vy *= SPEED_DECREASE_FACTOR;

                if (y > DISAPPEAR_Y_THRESHOLD) {
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