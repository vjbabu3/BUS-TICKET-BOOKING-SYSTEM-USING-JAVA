package com.bus.view;

import javax.swing.*;
import java.awt.*;

/**
 * HelpPanel - Provides detailed guides on how to use QuickBus.
 */
public class HelpPanel extends JPanel {

    public HelpPanel(MainFrame frame) {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.bg());

        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(StyleConfig.PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(22, 45, 22, 45));

        JLabel title = new JLabel("\u2753 QUICKBUS HELP CENTER");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(StyleConfig.TEXT_LIGHT);
        header.add(title, BorderLayout.WEST);

        JButton homeBtn = new JButton("BACK TO HOME");
        StyleConfig.styleButton(homeBtn, StyleConfig.TEXT_LIGHT, StyleConfig.PRIMARY);
        homeBtn.addActionListener(e -> frame.showPanel("home"));
        header.add(homeBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // --- BODY ---
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        body.add(createHelpSection("How to Book a Ticket", 
            "1. Navigate to 'Book Ticket' section.\n" +
            "2. Select your desired Route from the dropdown.\n" +
            "3. Choose an available travel date.\n" +
            "4. Select your preferred seats on the interactive bus grid.\n" +
            "5. Click 'Proceed to Payment' to finalize your booking."));

        body.add(Box.createRigidArea(new Dimension(0, 20)));

        body.add(createHelpSection("Checking Seat Availability", 
            "1. Visit the 'Availability' dashboard.\n" +
            "2. Filter by service or route number.\n" +
            "3. View real-time status of available vs. booked seats.\n" +
            "4. Use the visual map to see which seats are currently occupied."));

        body.add(Box.createRigidArea(new Dimension(0, 20)));

        body.add(createHelpSection("Payment Process", 
            "1. Once you select seats, you are redirected to the 'Secure Payment' gateway.\n" +
            "2. Enter your 16-digit Card Number, Expiry, and CVV.\n" +
            "3. Click 'Pay & Confirm Booking'.\n" +
            "4. Upon successful payment, your digital ticket will be generated instantly."));

        body.add(Box.createRigidArea(new Dimension(0, 20)));

        body.add(createHelpSection("Ticket Cancellation", 
            "1. Go to the 'Cancellation' module.\n" +
            "2. Enter your unique Booking ID (e.g., QB-1023).\n" +
            "3. Review the refund estimation based on timing.\n" +
            "4. Confirm the cancellation. Refunds are processed to the original payment source."));

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null); scroll.setOpaque(false); scroll.getViewport().setOpaque(false);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel createHelpSection(String title, String content) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(ThemeManager.surface());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.border()),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));

        JLabel t = new JLabel(title.toUpperCase());
        t.setFont(new Font("SansSerif", Font.BOLD, 16));
        t.setForeground(StyleConfig.PRIMARY);
        panel.add(t, BorderLayout.NORTH);

        JTextArea area = new JTextArea(content);
        area.setFont(new Font("SansSerif", Font.PLAIN, 14));
        area.setForeground(ThemeManager.text());
        area.setBackground(ThemeManager.surface());
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        panel.add(area, BorderLayout.CENTER);

        return panel;
    }
}
