package com.bus.view;

import javax.swing.*;
import java.awt.*;

/**
 * AboutProjectPanel redesigned for the QuickBus brand experience.
 */
public class AboutProjectPanel extends JPanel {
    public AboutProjectPanel(MainFrame frame) {
        setLayout(new BorderLayout());
        setBackground(StyleConfig.BACKGROUND);

        JPanel mainCard = new JPanel(new BorderLayout(0, 20));
        mainCard.setBackground(ThemeManager.surface());
        mainCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.border()),
                BorderFactory.createEmptyBorder(45, 65, 45, 65)));

        JLabel title = new JLabel("QUICKBUS - PROJECT SHOWCASE");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(StyleConfig.PRIMARY);
        mainCard.add(title, BorderLayout.NORTH);

        String info = "<html><body style='width: 550px; font-family: SansSerif; font-size: 11pt; line-height: 1.6; color: " + (ThemeManager.isDark() ? "#cbd5e1" : "#1F2937") + ";'>"
                + "<p>QuickBus is a professional-grade Bus Reservation System built using Java Swing and MySQL. "
                + "This project demonstrates the power of clean UI design synchronized with robust data management protocols.</p>"
                + "<br><b><font color='#2563EB'>ENGINEERING HIGHLIGHTS:</font></b>"
                + "<ul>"
                + "<li>High-Fidelity UI/UX Following Industry Leaders</li>"
                + "<li>Dynamic Route & Availability Engine</li>"
                + "<li>Real-time Seat Selection with Occupancy Logic</li>"
                + "<li>Boarding Pass Generation for Bookings</li>"
                + "<li>Secure Ticket Cancellation & History</li>"
                + "</ul>"
                + "<br><p>\u00A9 2026 QuickBus Project. Created for Advanced Data Science Study.</p>"
                + "</body></html>";

        JLabel content = new JLabel(info);
        mainCard.add(content, BorderLayout.CENTER);

        JButton backBtn = new JButton("BACK TO DASHBOARD");
        StyleConfig.styleButton(backBtn, StyleConfig.SECONDARY, Color.WHITE);
        backBtn.addActionListener(e -> frame.showPanel("home"));

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        footer.add(backBtn);
        mainCard.add(footer, BorderLayout.SOUTH);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(mainCard);
        add(wrapper, BorderLayout.CENTER);
    }
}
