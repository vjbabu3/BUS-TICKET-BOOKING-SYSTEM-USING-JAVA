package com.bus.view;

import com.bus.dao.BookingDAO;
import com.bus.model.Booking;
import com.bus.model.Route;
import com.bus.model.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PaymentPanel – Refined for multiple seat bookings and full dark mode.
 */
public class PaymentPanel extends JPanel {
    private MainFrame frame;
    private Route currentRoute;
    private java.util.List<Integer> selectedSeats = new java.util.ArrayList<>();
    private String travelDate;
    private JLabel totalLbl;
    private JTextField cardField, expiryField, cvvField;
    private double basePrice = 0;

    public PaymentPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(ThemeManager.bg());

        JPanel main = new JPanel(new GridBagLayout());
        main.setOpaque(false);
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(ThemeManager.surface());
        card.setPreferredSize(new Dimension(420, 520));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.border()),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.weightx = 1.0; 
        gbc.insets = new Insets(10, 0, 10, 0);

        // 1. Title
        JLabel title = new JLabel("Secure Payment", JLabel.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(new Color(37, 99, 235)); // Blue color from image
        gbc.gridy = 0; card.add(title, gbc);

        // 2. Amount
        totalLbl = new JLabel("Total Amount: \u20B90.00", JLabel.CENTER);
        totalLbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalLbl.setForeground(ThemeManager.text());
        gbc.gridy = 1; gbc.insets = new Insets(15, 0, 30, 0);
        card.add(totalLbl, gbc);

        // 3. Card Number
        gbc.insets = new Insets(8, 0, 4, 0);
        gbc.gridy = 2; card.add(new JLabel("Card Number") {{ setFont(StyleConfig.LABEL_FONT); setForeground(ThemeManager.muted()); }}, gbc);
        cardField = new JTextField(20); StyleConfig.styleTextField(cardField);
        StyleConfig.setupPlaceholder(cardField, "XXXX XXXX XXXX XXXX");
        gbc.insets = new Insets(0, 0, 12, 0);
        gbc.gridy = 3; card.add(cardField, gbc);

        // 4. Expiry and CVV Row
        JPanel row = new JPanel(new GridLayout(1, 2, 20, 0));
        row.setOpaque(false);
        
        JPanel expCol = new JPanel(new BorderLayout(0, 4)); expCol.setOpaque(false);
        expCol.add(new JLabel("Expiry (MM/YY)") {{ setFont(StyleConfig.LABEL_FONT); setForeground(ThemeManager.muted()); }}, BorderLayout.NORTH);
        expiryField = new JTextField(); StyleConfig.styleTextField(expiryField);
        StyleConfig.setupPlaceholder(expiryField, "12/26");
        expCol.add(expiryField, BorderLayout.CENTER);

        JPanel cvvCol = new JPanel(new BorderLayout(0, 4)); cvvCol.setOpaque(false);
        cvvCol.add(new JLabel("CVV") {{ setFont(StyleConfig.LABEL_FONT); setForeground(ThemeManager.muted()); }}, BorderLayout.NORTH);
        cvvField = new JPasswordField(); StyleConfig.styleTextField((JTextField)cvvField);
        StyleConfig.setupPlaceholder((JTextField)cvvField, "***");
        cvvCol.add(cvvField, BorderLayout.CENTER);

        row.add(expCol); row.add(cvvCol);
        gbc.gridy = 4; gbc.insets = new Insets(5, 0, 30, 0);
        card.add(row, gbc);

        // 5. Pay Button
        JButton payBtn = new JButton("Pay & Confirm Booking");
        StyleConfig.styleButton(payBtn, new Color(34, 197, 94), Color.WHITE); // Bright Green
        payBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        payBtn.addActionListener(e -> processFinalPayment());
        gbc.gridy = 5; gbc.insets = new Insets(20, 0, 10, 0);
        card.add(payBtn, gbc);

        // 6. Cancel Link
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        cancelBtn.setForeground(new Color(239, 68, 68)); // Red
        cancelBtn.setBorder(null);
        cancelBtn.setContentAreaFilled(false);
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelBtn.addActionListener(e -> frame.showPanel("bookticket"));
        gbc.gridy = 6; gbc.insets = new Insets(5, 0, 0, 0);
        card.add(cancelBtn, gbc);

        main.add(card);
        add(main, BorderLayout.CENTER);
    }

    public void setBookingData(Route route, List<Integer> seats, String travelDate) {
        this.currentRoute = route;
        this.selectedSeats = seats;
        this.travelDate = travelDate;
        this.basePrice = route.getPrice() * seats.size();
        totalLbl.setText("Total Amount: \u20B9 " + String.format("%.2f", basePrice));
    }

    private void processFinalPayment() {
        if (cardField.getText().isEmpty() || cardField.getText().startsWith("XXXX")) {
            ToastNotification.show(this, "Valid card details required.", ToastNotification.Type.WARNING); return;
        }
        User user = frame.getCurrentUser();
        BookingDAO dao = new BookingDAO();
        List<Integer> bookedIds = new ArrayList<>();
        
        for (int seat : selectedSeats) {
            Booking b = new Booking();
            b.setRouteId(currentRoute.getRouteId());
            b.setUserId(user.getUserId());
            b.setPassengerName(user.getName());
            b.setContactDetails(user.getEmail());
            b.setSeatNumber(seat);
            b.setTravelDate(travelDate != null ? travelDate : java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            
            try {
                int id = dao.bookTicket(b);
                if (id > 0) bookedIds.add(id);
            } catch (java.sql.SQLException ex) {
                ex.printStackTrace();
            }
        }

        if (!bookedIds.isEmpty()) {
            frame.showSuccessPanel(bookedIds);
        } else {
            ToastNotification.show(this, "Payment processing failed.", ToastNotification.Type.ERROR);
        }
    }
}
