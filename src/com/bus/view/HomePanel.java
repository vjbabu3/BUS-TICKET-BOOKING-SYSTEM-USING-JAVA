package com.bus.view;

import com.bus.dao.RouteDAO;
import com.bus.model.Route;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Landing screen with high-end Hero background and Glassmorphism Search UI.
 */
public class HomePanel extends JPanel {
    private final MainFrame frame;
    private JTextField srcInput, destInput;

    public HomePanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(ThemeManager.bg());

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);

        // --- 1. HERO SECTION ---
        HeroBackgroundPanel hero = new HeroBackgroundPanel();
        hero.setAlignmentX(Component.CENTER_ALIGNMENT);
        hero.setLayout(new GridBagLayout());

        StyleConfig.ShadowLabel heroTitle = new StyleConfig.ShadowLabel("Smart Way to Book Your Bus", SwingConstants.CENTER);
        heroTitle.setFont(new Font("SansSerif", Font.BOLD, 48)); 
        heroTitle.setForeground(Color.WHITE); 
        heroTitle.setShadow(new Color(0,0,0,180), 3, 3);

        StyleConfig.ShadowLabel heroSub = new StyleConfig.ShadowLabel(
                "Experience online bus ticket booking with real-time seat selection and secure tracking.", SwingConstants.CENTER);
        heroSub.setFont(new Font("SansSerif", Font.BOLD, 18)); 
        heroSub.setForeground(new Color(241, 245, 249)); 
        heroSub.setShadow(new Color(0,0,0,150), 2, 2);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        btnRow.setOpaque(false);

        JButton bookBtn = new JButton("Book Ticket");
        StyleConfig.styleHeroSolidButton(bookBtn);
        bookBtn.addActionListener(e -> frame.showPanel("bookticket"));

        JButton availBtn = new JButton("Check Availability");
        StyleConfig.styleHeroOutlineButton(availBtn);
        availBtn.addActionListener(e -> frame.showPanel("availability"));

        btnRow.add(bookBtn);
        btnRow.add(availBtn);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(40, 40, 10, 40);
        hero.add(heroTitle, c);
        c.gridy = 1;
        c.insets = new Insets(0, 40, 28, 40);
        hero.add(heroSub, c);
        c.gridy = 2;
        c.insets = new Insets(0, 40, 60, 40);
        hero.add(btnRow, c);

        mainContent.add(hero);
        mainContent.add(Box.createRigidArea(new Dimension(0, -75)));

        // --- 2. GLASS SEARCH BAR ---
        JPanel searchWrap = new JPanel(new GridBagLayout());
        searchWrap.setOpaque(false);
        searchWrap.setMaximumSize(new Dimension(1060, 220));
        searchWrap.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel searchBox = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Frosted Glass Effect
                Color glass = ThemeManager.isDark() ? new Color(15, 23, 42, 170) : new Color(255, 255, 255, 170);
                g2.setColor(glass);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 32, 32);
                
                // Glossy Edge / Border
                g2.setStroke(new BasicStroke(1.5f));
                g2.setColor(new Color(255, 255, 255, 45));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 32, 32);
                
                // Drop shadow
                g2.setColor(new Color(0, 0, 0, 50));
                g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 32, 32);
            }
        };
        searchBox.setOpaque(false);
        searchBox.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        srcInput = new JTextField(18);
        StyleConfig.styleTextField(srcInput);
        StyleConfig.setupPlaceholder(srcInput, "Leaving From");

        destInput = new JTextField(18);
        StyleConfig.styleTextField(destInput);
        StyleConfig.setupPlaceholder(destInput, "Going To");

        JButton searchBtn = new JButton("Search buses");
        StyleConfig.styleButton(searchBtn, StyleConfig.PRIMARY, Color.WHITE);
        searchBtn.setPreferredSize(new Dimension(210, 48));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel fromL = new JLabel("FROM");
        fromL.setFont(StyleConfig.LABEL_FONT);
        fromL.setForeground(ThemeManager.muted());
        JLabel toL = new JLabel("TO");
        toL.setFont(StyleConfig.LABEL_FONT);
        toL.setForeground(ThemeManager.muted());

        gbc.gridx = 0;
        gbc.gridy = 0;
        searchBox.add(fromL, gbc);
        gbc.gridx = 1;
        searchBox.add(toL, gbc);
        gbc.gridy = 1;
        gbc.gridx = 0;
        searchBox.add(srcInput, gbc);
        gbc.gridx = 1;
        searchBox.add(destInput, gbc);
        gbc.gridx = 2;
        searchBox.add(searchBtn, gbc);

        searchWrap.add(searchBox);
        mainContent.add(searchWrap);
        mainContent.add(Box.createRigidArea(new Dimension(0, 50)));

        // --- 3. FEATURE CARDS ---
        JPanel cards = new JPanel(new GridLayout(1, 3, 26, 0));
        cards.setOpaque(false);
        cards.setMaximumSize(new Dimension(1060, 230));
        cards.setAlignmentX(Component.CENTER_ALIGNMENT);

        cards.add(StyleConfig.createCard("Online Booking", "Book your tickets anywhere, with high-speed performance.",
                StyleConfig.PRIMARY));
        cards.add(StyleConfig.createCard("Real-time Tracking", "Visual mapping and GPS-grade tracking for every bus.",
                StyleConfig.ACCENT));
        cards.add(StyleConfig.createCard("Secure Payments",
                "Fully encrypted transactions and instant receipt generation.", StyleConfig.SECONDARY));

        mainContent.add(cards);
        mainContent.add(Box.createRigidArea(new Dimension(0, 50)));

        JScrollPane scroll = new JScrollPane(mainContent);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        searchBtn.addActionListener(e -> performSearch());
    }

    private void performSearch() {
        String s = srcInput.getText().trim();
        String d = destInput.getText().trim();
        if (s.isEmpty() || s.equals("Leaving From") || d.isEmpty() || d.equals("Going To")) {
            ToastNotification.show(this, "Leaving From/Going To fields are required.", ToastNotification.Type.WARNING);
            return;
        }

        List<Route> allRoutes = new RouteDAO().getAllRoutes();
        List<Route> filtered = allRoutes.stream()
                .filter(r -> r.getSource().equalsIgnoreCase(s) && r.getDestination().equalsIgnoreCase(d))
                .toList();

        if (!filtered.isEmpty()) {
            frame.showSearchResults(filtered);
        } else {
            ToastNotification.show(this, "No buses found on this route.", ToastNotification.Type.INFO);
        }
    }
}
