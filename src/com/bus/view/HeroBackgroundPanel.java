package com.bus.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Full-width hero with dynamic theme-aware fade and high-end overlays.
 */
public class HeroBackgroundPanel extends JPanel {
    private BufferedImage image;

    public HeroBackgroundPanel() {
        setOpaque(false);
        setLayout(new GridBagLayout());
        loadImage();
    }

    private void loadImage() {
        // Try multiple locations for the high-end background asset
        Path[] paths = {
            Paths.get(System.getProperty("user.dir"), "src", "com", "bus", "resources", "hero-bg.jpg"),
            Paths.get(System.getProperty("user.dir"), "bin", "com", "bus", "resources", "hero-bg.jpg"),
        };
        for (Path p : paths) {
            try {
                if (Files.isRegularFile(p)) {
                    image = ImageIO.read(p.toFile());
                    return;
                }
            } catch (Exception ignored) {}
        }
        try (InputStream in = HeroBackgroundPanel.class.getResourceAsStream("/com/bus/resources/hero-bg.jpg")) {
            if (in != null) image = ImageIO.read(in);
        } catch (Exception ignored) {}
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Always call super first
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth();
        int h = getHeight();
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Image Background / Base Gradient
        if (image != null) {
            g2.drawImage(image, 0, 0, w, h, null);
        } else {
            GradientPaint gp = new GradientPaint(0, 0, new Color(15, 23, 42), w, h, 
                ThemeManager.isDark() ? new Color(30, 41, 59) : new Color(31, 41, 55));
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);
        }

        // 2. Optimized Theme-aware overlay for high-contrast text readability (Clearer Image)
        Color overlayColor = ThemeManager.isDark() ? new Color(15, 23, 42, 175) : new Color(255, 255, 255, 155); 
        g2.setColor(overlayColor);
        g2.fillRect(0, 0, w, h);

        // 2.5 Central Radial Gradient to specifically DARKEN the area behind text
        if (ThemeManager.isDark()) {
            float[] dist = {0.0f, 0.8f};
            Color[] colors = {new Color(0, 0, 0, 100), new Color(0, 0, 0, 0)};
            RadialGradientPaint rgp = new RadialGradientPaint(new Point2D.Float(w/2, h/2), w/2, dist, colors);
            g2.setPaint(rgp);
            g2.fillRect(0, 0, w, h);
        }

        // 3. Premium Vertical Gradient Fade into Page Background
        Color bg = ThemeManager.bg();
        GradientPaint fade = new GradientPaint(0, h - 140, new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 0), 
                                                0, h, bg);
        g2.setPaint(fade);
        g2.fillRect(0, h - 140, w, 140);

        g2.dispose(); // Final disposal
    }

    @Override public Dimension getPreferredSize() { return new Dimension(0, 440); }
    @Override public Dimension getMaximumSize()   { return new Dimension(Integer.MAX_VALUE, 460); }
}
