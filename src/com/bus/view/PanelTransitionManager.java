package com.bus.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * PanelTransitionManager – implements a quick Fade-to-Color-and-Back transition.
 * Optimized with a custom glass pane for smooth alpha-blended transitions.
 */
public class PanelTransitionManager {

    private static class GlassFadePanel extends JPanel {
        private float alphaValue = 0f;
        public void setAlpha(float a) { this.alphaValue = a; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            Color bg = ThemeManager.bg();
            g2.setColor(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), (int)(alphaValue * 255)));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    public static void showWithFade(JPanel container, String name, CardLayout cardLayout) {
        Window win = SwingUtilities.getWindowAncestor(container);
        if (!(win instanceof JFrame)) {
            cardLayout.show(container, name);
            return;
        }

        JFrame frame = (JFrame) win;
        final GlassFadePanel glass = new GlassFadePanel();
        glass.setOpaque(false);
        frame.setGlassPane(glass);
        glass.setVisible(true);

        Timer timer = new Timer(15, null);
        timer.addActionListener(new ActionListener() {
            boolean phaseTwo = false;
            float currentAlpha = 0f;

            @Override public void actionPerformed(ActionEvent e) {
                if (!phaseTwo) {
                    currentAlpha += 0.25f;
                    if (currentAlpha >= 1.0f) {
                        currentAlpha = 1.0f; phaseTwo = true;
                        cardLayout.show(container, name);
                    }
                } else {
                    currentAlpha -= 0.2f;
                    if (currentAlpha <= 0f) {
                        currentAlpha = 0f; timer.stop();
                        glass.setVisible(false);
                    }
                }
                glass.setAlpha(currentAlpha);
            }
        });
        timer.start();
    }
}
