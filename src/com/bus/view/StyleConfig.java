package com.bus.view;

import javax.swing.*;
import java.awt.*;

/**
 * StyleConfig – centralized BRANDING and STYLING methods.
 * Restored all legacy methods and constants for maximum compatibility.
 */
public class StyleConfig {
    // Brand Palette
    public static final Color PRIMARY   = new Color(37, 99, 235);
    public static final Color SECONDARY = new Color(31, 41, 55);
    public static final Color SUCCESS   = new Color(16, 185, 129);
    public static final Color DANGER    = new Color(239, 68, 68);
    public static final Color ACCENT    = SUCCESS;

    // Dynamic Getters
    public static Color background() { return ThemeManager.bg();      }
    public static Color surface()    { return ThemeManager.surface(); }
    public static Color text()       { return ThemeManager.text();    }
    public static Color muted()      { return ThemeManager.muted();   }
    public static Color border()     { return ThemeManager.border();  }

    // Compatibility Constants (Dynamic)
    public static Color BACKGROUND = background(); 
    public static Color SURFACE    = surface();
    public static Color TEXT_DARK  = text();
    public static Color TEXT_GRAY  = muted();
    public static Color TEXT_LIGHT = new Color(248, 250, 252);

    public static void refresh() {
        BACKGROUND = background();
        SURFACE    = surface();
        TEXT_DARK  = text();
        TEXT_GRAY  = muted();
        TEXT_LIGHT = new Color(248, 250, 252);
    }

    // Fonts
    public static final Font TITLE_FONT    = new Font("SansSerif", Font.BOLD, 32);
    public static final Font SUBTITLE_FONT = new Font("SansSerif", Font.BOLD, 18);
    public static final Font LABEL_FONT    = new Font("SansSerif", Font.BOLD, 12);
    public static final Font BODY_FONT     = new Font("SansSerif", Font.PLAIN, 14);

    /** Stylish standard button */
    public static void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 32, 12, 32));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void styleHeroSolidButton(JButton btn) {
        styleButton(btn, PRIMARY, TEXT_LIGHT);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
    }

    public static void styleHeroOutlineButton(JButton btn) {
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setForeground(TEXT_LIGHT);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 120), 2),
                BorderFactory.createEmptyBorder(12, 30, 12, 30)));
    }

    public static void styleTextField(JTextField field) {
        field.setBackground(ThemeManager.input());
        field.setForeground(ThemeManager.text());
        field.setCaretColor(PRIMARY);
        field.setFont(BODY_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.border()),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
    }

    public static void setupPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(ThemeManager.muted());
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(ThemeManager.text());
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(ThemeManager.muted());
                    field.setText(placeholder);
                }
            }
        });
    }

    public static JPanel createCard(String title, String subtitle, Color accentStripe) {
        JPanel card = new JPanel(new BorderLayout(0, 12)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.surface());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                
                // Theme-aware glass border
                g2.setColor(ThemeManager.isDark() ? new Color(255, 255, 255, 25) : new Color(0, 0, 0, 15));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 24, 24);
                
                // Accent Stripe
                g2.setColor(accentStripe);
                g2.fillRoundRect(0, 0, 6, getHeight(), 6, 6);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(22, 28, 26, 22));

        JLabel t = new JLabel(title.toUpperCase());
        t.setFont(new Font("SansSerif", Font.BOLD, 15));
        t.setForeground(UIManager.getColor("Label.foreground"));

        JLabel st = new JLabel("<html><div style='width:220px'>" + subtitle + "</div></html>");
        st.setFont(new Font("SansSerif", Font.PLAIN, 12));
        st.setForeground(UIManager.getColor("Label.disabledForeground"));

        card.add(t, BorderLayout.NORTH);
        card.add(st, BorderLayout.CENTER);
        return card;
    }

    /**
     * Custom Label that paints a drop shadow for better visibility on bright backgrounds.
     */
    public static class ShadowLabel extends JLabel {
        private Color shadowColor = new Color(0, 0, 0, 180);
        private int xOffset = 2;
        private int yOffset = 2;

        public ShadowLabel(String text) { super(text); }
        public ShadowLabel(String text, int align) { super(text, align); }

        public void setShadow(Color color, int x, int y) {
            this.shadowColor = color;
            this.xOffset = x;
            this.yOffset = y;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            // Paint Shadow
            g2.setColor(shadowColor);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            String text = getText();
            
            // Handle centering
            int x = 0;
            if (getHorizontalAlignment() == CENTER) {
                x = (getWidth() - fm.stringWidth(text)) / 2;
            } else if (getHorizontalAlignment() == RIGHT) {
                x = getWidth() - fm.stringWidth(text);
            }
            
            // Draw shadow with offset
            g2.drawString(text, x + xOffset, fm.getAscent() + yOffset);
            
            // Paint Main Text
            g2.setColor(getForeground());
            g2.drawString(text, x, fm.getAscent());
            g2.dispose();
        }
    }
}
