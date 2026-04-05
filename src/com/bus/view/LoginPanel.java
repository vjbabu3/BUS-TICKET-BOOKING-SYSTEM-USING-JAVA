package com.bus.view;

import com.bus.dao.UserDAO;
import com.bus.model.User;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

/**
 * Premium LoginPanel UI – Simplified, Clean, and Theme-aware.
 */
public class LoginPanel extends JPanel {

    public LoginPanel(MainFrame frame) {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.bg());

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        // --- LOGIN CARD ---
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(ThemeManager.surface());
        card.setPreferredSize(new Dimension(380, 520));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.border()),
                BorderFactory.createEmptyBorder(35, 45, 35, 45)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; gbc.insets = new Insets(8, 0, 8, 0);

        JLabel logo = new JLabel("<html><span style='color:#2563EB;'><b>Quick</b></span>"
                + "<span style='color:" + (ThemeManager.isDark()?"#F3F4F6":"#1F2937") + ";'>Bus</span></html>", JLabel.CENTER);
        logo.setFont(new Font("SansSerif", Font.BOLD, 30));
        gbc.gridy = 0; card.add(logo, gbc);

        JLabel sub = new JLabel("Secure Account Login", JLabel.CENTER);
        sub.setFont(StyleConfig.BODY_FONT); sub.setForeground(ThemeManager.muted());
        gbc.gridy = 1; card.add(sub, gbc);

        gbc.insets = new Insets(30, 0, 5, 0); gbc.gridy = 2;
        card.add(new JLabel("EMAIL ADDRESS") {{ setFont(StyleConfig.LABEL_FONT); setForeground(ThemeManager.text()); }}, gbc);

        JTextField emailField = new JTextField(20); StyleConfig.styleTextField(emailField);
        gbc.insets = new Insets(0, 0, 15, 0); gbc.gridy = 3; card.add(emailField, gbc);

        gbc.insets = new Insets(10, 0, 5, 0); gbc.gridy = 4;
        card.add(new JLabel("PASSWORD") {{ setFont(StyleConfig.LABEL_FONT); setForeground(ThemeManager.text()); }}, gbc);

        JPasswordField passField = new JPasswordField(20); StyleConfig.styleTextField(passField);
        gbc.insets = new Insets(0, 0, 10, 0); gbc.gridy = 5; card.add(passField, gbc);

        JCheckBox rememberMe = new JCheckBox("Stay Logged In");
        rememberMe.setFont(new Font("SansSerif", Font.PLAIN, 12)); rememberMe.setOpaque(false); rememberMe.setForeground(ThemeManager.muted());
        gbc.gridy = 6; card.add(rememberMe, gbc);

        JButton loginBtn = new JButton("LOGIN");
        StyleConfig.styleButton(loginBtn, StyleConfig.PRIMARY, Color.WHITE);
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        gbc.gridy = 7; gbc.insets = new Insets(20, 0, 0, 0); card.add(loginBtn, gbc);

        JButton signUpBtn = new JButton("<html>Don't have an account? <font color='#2563EB'>SIGN UP</font></html>");
        signUpBtn.setFont(new Font("SansSerif", Font.PLAIN, 12)); signUpBtn.setBorder(null); signUpBtn.setContentAreaFilled(false);
        gbc.gridy = 8; gbc.insets = new Insets(15, 0, 0, 0); card.add(signUpBtn, gbc);

        center.add(card);
        add(center, BorderLayout.CENTER);

        // Pre-fill
        Preferences prefs = Preferences.userNodeForPackage(LoginPanel.class);
        emailField.setText(prefs.get("saved_email", ""));
        rememberMe.setSelected(!emailField.getText().isEmpty());

        // Actions
        loginBtn.addActionListener(e -> {
            String email = emailField.getText();
            String pwd = new String(passField.getPassword());
            if (email.isEmpty() || pwd.isEmpty()) {
                ToastNotification.show(this, "Fields cannot be empty.", ToastNotification.Type.WARNING); return;
            }
            User user = new UserDAO().login(email, pwd);
            if (user != null) {
                if (rememberMe.isSelected()) prefs.put("saved_email", email); else prefs.remove("saved_email");
                ToastNotification.show(this, "Welcome " + user.getName(), ToastNotification.Type.SUCCESS);
                frame.loginUser(user);
            } else {
                ToastNotification.show(this, "Account invalid.", ToastNotification.Type.ERROR);
            }
        });

        signUpBtn.addActionListener(e -> frame.showPanel("signup"));
    }
}
