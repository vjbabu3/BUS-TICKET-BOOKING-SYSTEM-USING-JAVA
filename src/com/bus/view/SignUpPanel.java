package com.bus.view;

import com.bus.dao.UserDAO;
import com.bus.model.User;

import javax.swing.*;
import java.awt.*;

/**
 * SignUpPanel redesigned to match the QuickBus brand experience.
 */
public class SignUpPanel extends JPanel {
    public SignUpPanel(MainFrame frame) {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.bg());

        JPanel contentWrapper = new JPanel(new GridBagLayout());
        contentWrapper.setOpaque(false);

        // --- The SignUp Card ---
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(ThemeManager.surface());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.border(), 1),
                BorderFactory.createEmptyBorder(40, 50, 40, 50)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel logoBrand = new JLabel("<html><font color='#2563EB'><b>Quick</b></font><font color='" + (ThemeManager.isDark()? "#e2e8f0":"#1f2937") + "'>Bus</font></html>", JLabel.CENTER);
        logoBrand.setFont(new Font("SansSerif", Font.BOLD, 30));
        gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(logoBrand, gbc);

        JLabel subTitle = new JLabel("Create your free account", JLabel.CENTER);
        subTitle.setFont(StyleConfig.BODY_FONT);
        subTitle.setForeground(ThemeManager.muted());
        gbc.gridy = 1; card.add(subTitle, gbc);

        // Name
        gbc.insets = new Insets(20, 0, 5, 0);
        gbc.gridy = 2; card.add(new JLabel("FULL NAME") {{ setFont(StyleConfig.LABEL_FONT); }}, gbc);
        JTextField nameField = new JTextField(25);
        StyleConfig.styleTextField(nameField);
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.gridy = 3; card.add(nameField, gbc);

        // Email
        gbc.insets = new Insets(10, 0, 5, 0);
        gbc.gridy = 4; card.add(new JLabel("EMAIL ADDRESS") {{ setFont(StyleConfig.LABEL_FONT); }}, gbc);
        JTextField emailField = new JTextField(25);
        StyleConfig.styleTextField(emailField);
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.gridy = 5; card.add(emailField, gbc);

        // Password
        gbc.insets = new Insets(10, 0, 5, 0);
        gbc.gridy = 6; card.add(new JLabel("CREATE PASSWORD") {{ setFont(StyleConfig.LABEL_FONT); }}, gbc);
        JPasswordField passField = new JPasswordField(25);
        StyleConfig.styleTextField(passField);
        gbc.insets = new Insets(0, 0, 20, 0);
        gbc.gridy = 7; card.add(passField, gbc);

        // Action Buttons
        JButton signUpBtn = new JButton("Register Now");
        StyleConfig.styleButton(signUpBtn, StyleConfig.PRIMARY, Color.WHITE);
        gbc.gridy = 8; card.add(signUpBtn, gbc);

        JButton backBtn = new JButton("Already have an account? Login");
        backBtn.setFont(StyleConfig.BODY_FONT);
        backBtn.setForeground(ThemeManager.text());
        backBtn.setContentAreaFilled(false);
        backBtn.setBorder(null);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.insets = new Insets(20, 0, 0, 0);
        gbc.gridy = 9; card.add(backBtn, gbc);

        // Actions
        signUpBtn.addActionListener(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = new String(passField.getPassword());
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete all fields to register.", "QuickBus", JOptionPane.WARNING_MESSAGE);
                return;
            }
            User u = new User();
            u.setName(name);
            u.setEmail(email);
            u.setPassword(password);
            u.setRole("user");
            
            if (new UserDAO().register(u)) {
                JOptionPane.showMessageDialog(this, "Registration successful! Welcome to QuickBus.");
                frame.showPanel("login");
            } else {
                JOptionPane.showMessageDialog(this, "Email already exists or registration failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backBtn.addActionListener(e -> frame.showPanel("login"));

        contentWrapper.add(card);
        add(contentWrapper, BorderLayout.CENTER);
        
        JButton homeBtn = new JButton("\u2190 BACK TO DASHBOARD");
        homeBtn.setFont(StyleConfig.LABEL_FONT);
        homeBtn.setForeground(ThemeManager.muted());
        homeBtn.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        homeBtn.setContentAreaFilled(false);
        homeBtn.addActionListener(e -> frame.showPanel("home"));
        add(homeBtn, BorderLayout.SOUTH);
    }
}
