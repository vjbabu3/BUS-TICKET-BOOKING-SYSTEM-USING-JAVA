package com.bus.view;

import com.bus.dao.BookingDAO;
import com.bus.dao.UserDAO;
import com.bus.model.Booking;
import com.bus.model.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * ProfilePanel – shows user info, stats, and password management.
 * Refined for TRUE dark mode and multi-seat spending.
 */
public class ProfilePanel extends JPanel {
    private final MainFrame frame;
    private JLabel nameLbl, emailLbl, totalTripsLbl, totalSpendLbl;
    private JPasswordField curPass, newPass, confirmPass;

    public ProfilePanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(ThemeManager.bg());

        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(StyleConfig.PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(22, 45, 22, 45));
        
        JLabel title = new JLabel("\uD83D\uDC64  MY PROFILE");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(StyleConfig.TEXT_LIGHT);
        header.add(title, BorderLayout.WEST);

        JButton homeBtn = new JButton("BACK TO HOME");
        StyleConfig.styleButton(homeBtn, StyleConfig.TEXT_LIGHT, StyleConfig.PRIMARY);
        homeBtn.addActionListener(e -> frame.showPanel("home"));
        header.add(homeBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // --- BODY ---
        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(10, 10, 10, 10);

        // 1. Info Card
        JPanel infoCard = new JPanel(new BorderLayout(0, 15));
        infoCard.setBackground(ThemeManager.surface());
        infoCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.border()),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)));

        JLabel avatar = new JLabel("\uD83D\uDC64", SwingConstants.CENTER);
        avatar.setFont(new Font("SansSerif", Font.PLAIN, 64));
        avatar.setForeground(ThemeManager.muted());

        nameLbl = new JLabel("USER NAME", SwingConstants.CENTER);
        nameLbl.setFont(new Font("SansSerif", Font.BOLD, 20));
        nameLbl.setForeground(ThemeManager.text());

        emailLbl = new JLabel("user@example.com", SwingConstants.CENTER);
        emailLbl.setFont(StyleConfig.BODY_FONT);
        emailLbl.setForeground(ThemeManager.muted());

        JPanel statGrid = new JPanel(new GridLayout(1, 2, 20, 0));
        statGrid.setOpaque(false);
        totalTripsLbl = createStatItem("0", "Bookings");
        totalSpendLbl = createStatItem("\u20B90", "Total Spent");
        statGrid.add(totalTripsLbl); statGrid.add(totalSpendLbl);

        infoCard.add(avatar, BorderLayout.NORTH);
        JPanel mid = new JPanel(new GridLayout(2, 1));
        mid.setOpaque(false); mid.add(nameLbl); mid.add(emailLbl);
        infoCard.add(mid, BorderLayout.CENTER);
        infoCard.add(statGrid, BorderLayout.SOUTH);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.4;
        body.add(infoCard, gbc);

        // 2. Security Card
        JPanel pwCard = new JPanel();
        pwCard.setLayout(new BoxLayout(pwCard, BoxLayout.Y_AXIS));
        pwCard.setBackground(ThemeManager.surface());
        pwCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.border()),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)));

        JLabel pwTitle = new JLabel("\uD83D\uDD12  ACCOUNT SECURITY");
        pwTitle.setFont(StyleConfig.LABEL_FONT);
        pwTitle.setForeground(StyleConfig.PRIMARY);

        curPass = new JPasswordField(); StyleConfig.styleTextField(curPass);
        newPass = new JPasswordField(); StyleConfig.styleTextField(newPass);
        confirmPass = new JPasswordField(); StyleConfig.styleTextField(confirmPass);

        JButton updateBtn = new JButton("UPDATE PASSWORD");
        StyleConfig.styleButton(updateBtn, StyleConfig.PRIMARY, Color.WHITE);
        updateBtn.addActionListener(e -> handlePasswordUpdate());

        pwCard.add(pwTitle);
        pwCard.add(Box.createRigidArea(new Dimension(0, 20)));
        addInput(pwCard, "Current Password", curPass);
        addInput(pwCard, "New Password", newPass);
        addInput(pwCard, "Confirm New Password", confirmPass);
        pwCard.add(Box.createRigidArea(new Dimension(0, 15)));
        pwCard.add(updateBtn);

        gbc.gridx = 1; gbc.weightx = 0.6;
        body.add(pwCard, gbc);

        add(new JScrollPane(body) {{ setBorder(null); setOpaque(false); getViewport().setOpaque(false); }}, BorderLayout.CENTER);
    }

    private void addInput(JPanel p, String lbl, JPasswordField f) {
        JLabel l = new JLabel(lbl.toUpperCase());
        l.setFont(new Font("SansSerif", Font.BOLD, 10)); l.setForeground(ThemeManager.muted());
        p.add(l); p.add(Box.createRigidArea(new Dimension(0, 4)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        p.add(f); p.add(Box.createRigidArea(new Dimension(0, 15)));
    }

    private JLabel createStatItem(String val, String lbl) {
        return new JLabel("<html><center><b style='font-size:16px;color:" + (ThemeManager.isDark()?"#F3F4F6":"#1F2937") + "'>" + val + "</b><br/>"
                + "<font color='#9CA3AF' size='2'>" + lbl + "</font></center></html>", SwingConstants.CENTER);
    }

    private void handlePasswordUpdate() {
        User u = frame.getCurrentUser();
        if (u == null) return;
        String cur = new String(curPass.getPassword());
        String nw = new String(newPass.getPassword());
        String cf = new String(confirmPass.getPassword());

        if (nw.isEmpty() || nw.length() < 5) {
            ToastNotification.show(this, "Password too short.", ToastNotification.Type.WARNING); return;
        }
        if (!nw.equals(cf)) {
            ToastNotification.show(this, "Passwords don't match.", ToastNotification.Type.ERROR); return;
        }
        if (new UserDAO().changePassword(u.getUserId(), cur, nw)) {
            ToastNotification.show(this, "Security updated successfully!", ToastNotification.Type.SUCCESS);
            curPass.setText(""); newPass.setText(""); confirmPass.setText("");
        } else {
            ToastNotification.show(this, "Verification failed. Check current password.", ToastNotification.Type.ERROR);
        }
    }

    public void refreshData() {
        User u = frame.getCurrentUser();
        if (u == null) return;
        nameLbl.setText(u.getName().toUpperCase());
        emailLbl.setText(u.getEmail());
        List<Booking> list = new BookingDAO().getUserBookings(u.getUserId());
        double total = list.stream().mapToDouble(Booking::getPrice).sum();
        
        totalTripsLbl.setText(createStatItem(list.size() + "", "Bookings").getText());
        totalSpendLbl.setText(createStatItem("\u20B9" + String.format("%.0f", total), "Total Spent").getText());
    }
}
