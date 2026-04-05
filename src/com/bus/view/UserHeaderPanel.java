package com.bus.view;

import com.bus.model.User;
import javax.swing.*;
import java.awt.*;

/**
 * UserHeaderPanel – with dark mode toggle and dynamic theme integration.
 */
public class UserHeaderPanel extends JPanel {
    private MainFrame frame;
    private JLabel userLabel, logo;
    private JButton loginBtn, signupBtn, logoutBtn, myBookingsBtn, profileBtn, adminBtn;
    private JToggleButton darkToggle;

    private java.util.Map<String, JLabel> navLinks = new java.util.HashMap<>();
    private String activeTarget = "home";

    public UserHeaderPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(ThemeManager.surface());
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.border()));
        setPreferredSize(new Dimension(0, 80));

        // LEFT - BRANDING
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 20));
        logoPanel.setOpaque(false);
        logo = new JLabel("<html><font color='#2563EB'><b>Quick</b></font><font color='" 
                + (ThemeManager.isDark()?"#F3F4F6":"#1F2937") + "'>Bus</font></html>");
        logo.setFont(new Font("SansSerif", Font.BOLD, 28));
        logoPanel.add(logo);
        add(logoPanel, BorderLayout.WEST);

        // CENTER - NAVIGATION
        JPanel navCenter = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 25));
        navCenter.setOpaque(false);
        if (AppModules.HOME)         addNav("BOOK TICKET",   "home", navCenter);
        if (AppModules.ROUTES)       addNav("ALL ROUTES",    "routes", navCenter);
        if (AppModules.AVAILABILITY) addNav("AVAILABILITY",  "availability", navCenter);
        if (AppModules.CANCELLATION) addNav("CANCELLATION",  "cancel", navCenter);
        if (AppModules.HELP)         addNav("HELP/SUPPORT",  "help", navCenter);
        add(navCenter, BorderLayout.CENTER);
        setActiveLink("home");

        // RIGHT - ACCOUNT + TOOLS
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 20));
        userPanel.setOpaque(false);

        userLabel = new JLabel("");
        userLabel.setFont(StyleConfig.LABEL_FONT);
        userLabel.setForeground(ThemeManager.text());
        userPanel.add(userLabel);

        // Dark mode toggle
        darkToggle = new JToggleButton(ThemeManager.isDark() ? "\u2600\uFE0F" : "\uD83C\uDF19");
        darkToggle.setFocusPainted(false);
        darkToggle.setContentAreaFilled(false);
        darkToggle.setFont(new Font("SansSerif", Font.PLAIN, 18));
        darkToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        darkToggle.setSelected(ThemeManager.isDark());
        darkToggle.addActionListener(e -> {
            boolean isDark = darkToggle.isSelected();
            ThemeManager.applyTheme(isDark);
            darkToggle.setText(isDark ? "\u2600\uFE0F" : "\uD83C\uDF19");
            
            // Rebuild the logo text
            logo.setText("<html><font color='#2563EB'><b>Quick</b></font><font color='" 
                + (isDark ? "#F3F4F6" : "#1F2937") + "'>Bus</font></html>");
            
            SwingUtilities.updateComponentTreeUI(frame);
            ToastNotification.show(this, isDark ? "Dark Vision Active" : "Light Mode On", ToastNotification.Type.INFO);
        });
        userPanel.add(darkToggle);

        loginBtn = new JButton("Login");
        signupBtn = new JButton("Register");
        logoutBtn = new JButton("Logout");
        myBookingsBtn = new JButton("\uD83C\uDFAB Bookings");
        profileBtn = new JButton("\uD83D\uDC64 Profile");
        adminBtn = new JButton("\uD83D\uDCCA Admin");

        styleNavBtn(loginBtn, false);
        styleNavBtn(signupBtn, true);
        styleNavBtn(logoutBtn, false);
        styleNavBtn(myBookingsBtn, false);
        styleNavBtn(profileBtn, false);
        StyleConfig.styleButton(adminBtn, new Color(139, 92, 246), Color.WHITE);
        adminBtn.setFont(new Font("SansSerif", Font.BOLD, 12));

        loginBtn.addActionListener(e -> frame.showPanel("login"));
        signupBtn.addActionListener(e -> frame.showPanel("signup"));
        logoutBtn.addActionListener(e -> frame.logout());
        myBookingsBtn.addActionListener(e -> frame.showPanel("mybookings"));
        profileBtn.addActionListener(e -> frame.showProfile());
        adminBtn.addActionListener(e -> frame.showPanel("admin"));

        userPanel.add(loginBtn);
        userPanel.add(signupBtn);
        userPanel.add(myBookingsBtn); myBookingsBtn.setVisible(false);
        userPanel.add(profileBtn);   profileBtn.setVisible(false);
        userPanel.add(adminBtn);     adminBtn.setVisible(false);
        userPanel.add(logoutBtn);    logoutBtn.setVisible(false);

        add(userPanel, BorderLayout.EAST);
    }

    private void addNav(String text, String target, JPanel parent) {
        JLabel l = createNavLink(text, target);
        navLinks.put(target, l);
        parent.add(l);
    }

    private JLabel createNavLink(String text, String target) {
        JLabel l = new JLabel(text);
        l.setFont(StyleConfig.LABEL_FONT);
        l.setForeground(ThemeManager.muted());
        l.setCursor(new Cursor(Cursor.HAND_CURSOR));
        l.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { frame.showPanel(target); }
            public void mouseEntered(java.awt.event.MouseEvent e) { 
                if(!target.equals(activeTarget)) l.setForeground(StyleConfig.PRIMARY); 
            }
            public void mouseExited(java.awt.event.MouseEvent e)  { 
                if(!target.equals(activeTarget)) l.setForeground(ThemeManager.muted()); 
            }
        });
        return l;
    }

    public void setActiveLink(String name) {
        this.activeTarget = name;
        navLinks.forEach((target, label) -> {
            boolean active = target.equals(name);
            label.setForeground(active ? StyleConfig.PRIMARY : ThemeManager.muted());
            label.setBorder(active ? BorderFactory.createMatteBorder(0, 0, 2, 0, StyleConfig.PRIMARY) : null);
        });
    }

    private void styleNavBtn(JButton btn, boolean isPrimary) {
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (isPrimary) {
            btn.setForeground(Color.WHITE);
            btn.setBackground(StyleConfig.PRIMARY);
            btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        } else {
            btn.setForeground(ThemeManager.text());
            btn.setContentAreaFilled(false);
            btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }
    }

    public void updateStatus(User user) {
        boolean loggedIn = (user != null);
        loginBtn.setVisible(!loggedIn);
        signupBtn.setVisible(!loggedIn);
        logoutBtn.setVisible(loggedIn);
        myBookingsBtn.setVisible(loggedIn && AppModules.MY_BOOKINGS);
        profileBtn.setVisible(loggedIn && AppModules.PROFILE);
        adminBtn.setVisible(loggedIn && "admin".equalsIgnoreCase(user.getRole()));
        userLabel.setText(loggedIn ? "\uD83D\uDC64 " + user.getName().toUpperCase() : "");
        revalidate(); repaint();
    }
}
