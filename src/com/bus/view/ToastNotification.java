package com.bus.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

/**
 * Toast notification system – shows a sliding bottom-right popup message.
 */
public class ToastNotification {

    public enum Type { SUCCESS, ERROR, WARNING, INFO }

    public static void show(Component parent, String message, Type type) {
        JWindow toast = new JWindow(SwingUtilities.getWindowAncestor(parent));

        Color bg, fg, border;
        String icon;
        switch (type) {
            case SUCCESS: bg = new Color(220, 252, 231); fg = new Color(22, 163, 74);
                border = new Color(134, 239, 172); icon = "\u2705 "; break;
            case ERROR:   bg = new Color(254, 226, 226); fg = new Color(220, 38, 38);
                border = new Color(252, 165, 165); icon = "\u274C "; break;
            case WARNING: bg = new Color(254, 243, 199); fg = new Color(217, 119, 6);
                border = new Color(253, 211, 77); icon = "\u26A0\uFE0F "; break;
            default:      bg = new Color(219, 234, 254); fg = new Color(37, 99, 235);
                border = new Color(147, 197, 253); icon = "\u2139\uFE0F "; break;
        }

        JPanel panel = new JPanel(new BorderLayout(12, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(border);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 12, 12));
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel msgLabel = new JLabel(icon + message);
        msgLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        msgLabel.setForeground(fg);
        panel.add(msgLabel, BorderLayout.CENTER);

        toast.add(panel);
        toast.pack();
        toast.setOpacity(0f);

        // Position bottom-right of parent
        Window win = SwingUtilities.getWindowAncestor(parent);
        if (win != null) {
            int x = win.getX() + win.getWidth() - toast.getWidth() - 30;
            int y = win.getY() + win.getHeight() - toast.getHeight() - 60;
            toast.setLocation(x, y);
        }
        toast.setVisible(true);

        // Fade in → hold → fade out
        final float[] opacity = {0f};
        final boolean[] fadingOut = {false};
        Timer timer = new Timer(16, null);
        timer.addActionListener(new ActionListener() {
            int holdFrames = 0;
            @Override public void actionPerformed(ActionEvent e) {
                if (!fadingOut[0]) {
                    opacity[0] = Math.min(1f, opacity[0] + 0.07f);
                    toast.setOpacity(opacity[0]);
                    if (opacity[0] >= 1f) { holdFrames++; if (holdFrames > 150) fadingOut[0] = true; }
                } else {
                    opacity[0] = Math.max(0f, opacity[0] - 0.05f);
                    toast.setOpacity(opacity[0]);
                    if (opacity[0] <= 0f) { timer.stop(); toast.dispose(); }
                }
            }
        });
        timer.start();
    }
}
