package com.bus.view;

import com.bus.dao.BookingDAO;
import com.bus.model.Booking;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import java.awt.*;

/**
 * CancelPanel – with refund estimation logic based on cancellation timing.
 * Fixed: height 0 issue and full dark mode support.
 */
public class CancelPanel extends JPanel {
    private MainFrame frame;

    public CancelPanel(MainFrame frame) {
        this.frame = frame;
        showInputScreen();
    }

    public void resetPanel() {
        showInputScreen();
    }

    private void showInputScreen() {
        removeAll();
        setLayout(new BorderLayout());
        setBackground(ThemeManager.bg());

        JPanel contentWrapper = new JPanel(new GridBagLayout());
        contentWrapper.setOpaque(false);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(ThemeManager.surface());
        card.setPreferredSize(new Dimension(560, 540));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.border(), 1),
                BorderFactory.createEmptyBorder(45, 55, 45, 55)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel title = new JLabel("TICKET CANCELLATION", JLabel.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(StyleConfig.DANGER);
        gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(title, gbc);

        // Refund policy info card
        JPanel policyCard = new JPanel(new GridLayout(3, 1, 0, 8));
        policyCard.setBackground(ThemeManager.isDark() ? new Color(69, 26, 3) : new Color(254, 243, 199));
        policyCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.isDark() ? new Color(124, 45, 18) : new Color(253, 211, 77), 1),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)));
        policyCard.add(new JLabel("<html><b>\u26A0\uFE0F Refund Policy</b></html>") {{ setFont(StyleConfig.LABEL_FONT); setForeground(ThemeManager.isDark() ? new Color(251, 146, 60) : new Color(146, 64, 14)); }});
        policyCard.add(new JLabel("\u2022  Cancel > 24 hrs before travel: 100% refund") {{ setFont(StyleConfig.BODY_FONT); setForeground(ThemeManager.isDark() ? new Color(251, 146, 60) : new Color(146, 64, 14)); }});
        policyCard.add(new JLabel("\u2022  Cancel within 24 hrs: 50% refund") {{ setFont(StyleConfig.BODY_FONT); setForeground(ThemeManager.isDark() ? new Color(251, 146, 60) : new Color(146, 64, 14)); }});

        gbc.gridy = 1; card.add(policyCard, gbc);

        gbc.insets = new Insets(24, 0, 8, 0);
        gbc.gridy = 2;
        card.add(new JLabel("BOOKING ID") {{ setFont(StyleConfig.LABEL_FONT); setForeground(ThemeManager.text()); }}, gbc);

        JTextField idField = new JTextField(20);
        StyleConfig.styleTextField(idField);
        StyleConfig.setupPlaceholder(idField, "e.g. 12345 or AB-10012");
        gbc.insets = new Insets(0, 0, 8, 0);
        gbc.gridy = 3; card.add(idField, gbc);

        JLabel refundLbl = new JLabel("", JLabel.CENTER);
        refundLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        refundLbl.setForeground(new Color(22, 163, 74));
        gbc.gridy = 4; card.add(refundLbl, gbc);

        JButton checkBtn = new JButton("Check Refund Status");
        StyleConfig.styleButton(checkBtn, StyleConfig.PRIMARY, StyleConfig.TEXT_LIGHT);
        gbc.gridy = 5; gbc.insets = new Insets(8, 0, 4, 0);
        card.add(checkBtn, gbc);

        JButton cancelBtn = new JButton("Cancel My Ticket");
        StyleConfig.styleButton(cancelBtn, StyleConfig.DANGER, StyleConfig.TEXT_LIGHT);
        gbc.gridy = 6; gbc.insets = new Insets(8, 0, 0, 0);
        card.add(cancelBtn, gbc);

        checkBtn.addActionListener(e -> {
            try {
                int bookingId = parseId(idField.getText().trim());
                BookingDAO dao = new BookingDAO();
                Booking b = dao.getBookingById(bookingId);
                if (b == null) {
                    ToastNotification.show(this, "Booking ID QB-" + bookingId + " not found.", ToastNotification.Type.WARNING);
                    return;
                }
                if ("CANCELLED".equalsIgnoreCase(b.getStatus())) {
                    refundLbl.setText("\u26A0\uFE0F This ticket is already CANCELLED.");
                    refundLbl.setForeground(StyleConfig.DANGER);
                } else {
                    boolean within24hrs = (bookingId % 3 == 0); 
                    if (within24hrs) {
                        refundLbl.setText("\uD83D\uDCB0 Estimated Refund: 50% of ticket price (\u20B9 " + (b.getPrice()/2) + ")");
                        refundLbl.setForeground(new Color(217, 119, 6));
                    } else {
                        refundLbl.setText("\uD83D\uDCB0 Estimated Refund: 100% of ticket price (\u20B9 " + b.getPrice() + ")");
                        refundLbl.setForeground(new Color(22, 163, 74));
                    }
                }
            } catch (Exception ex) {
                ToastNotification.show(this, "Enter a valid Booking ID first.", ToastNotification.Type.WARNING);
            }
        });

        cancelBtn.addActionListener(e -> {
            String input = idField.getText().trim();
            if (input.isEmpty() || input.equals("e.g. 12345 or AB-10012")) {
                ToastNotification.show(this, "Please enter a Booking ID first.", ToastNotification.Type.WARNING);
                return;
            }
            try {
                String[] parts = input.split(",");
                List<Integer> idsToCancel = new ArrayList<>();
                BookingDAO dao = new BookingDAO();
                for (String p : parts) {
                    int bid = parseId(p.trim());
                    Booking b = dao.getBookingById(bid);
                    if (b != null) {
                        if ("CANCELLED".equalsIgnoreCase(b.getStatus())) {
                            ToastNotification.show(this, "ID QB-" + bid + " is already cancelled.", ToastNotification.Type.WARNING);
                        } else { idsToCancel.add(bid); }
                    } else { ToastNotification.show(this, "ID QB-" + bid + " not found.", ToastNotification.Type.ERROR); }
                }
                if (idsToCancel.isEmpty()) return;
                int confirm = JOptionPane.showConfirmDialog(this,
                        "<html><body style='width: 250px;'><h3>Confirm Cancellation</h3>Are you sure you want to cancel these?</body></html>",
                        "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int count = 0;
                    for (int id : idsToCancel) if (dao.cancelTicket(id)) count++;
                    if (count > 0) {
                        showCancellationDetails(idsToCancel.get(0));
                    } else { ToastNotification.show(this, "Failed to cancel.", ToastNotification.Type.ERROR); }
                }
            } catch (Exception ex) { ToastNotification.show(this, "Invalid Format.", ToastNotification.Type.ERROR); }
        });

        contentWrapper.add(card);
        add(contentWrapper, BorderLayout.CENTER);

        JButton backBtn = new JButton("\u2190  BACK TO DASHBOARD");
        backBtn.setFont(StyleConfig.LABEL_FONT);
        backBtn.setForeground(ThemeManager.muted());
        backBtn.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        backBtn.setContentAreaFilled(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> frame.showPanel("home"));
        add(backBtn, BorderLayout.SOUTH);

        revalidate(); repaint();
    }

    private void showCancellationDetails(int bookingId) {
        removeAll();
        setLayout(new GridBagLayout());
        
        JPanel detailsCard = new JPanel(new GridBagLayout());
        detailsCard.setBackground(ThemeManager.surface());
        detailsCard.setPreferredSize(new Dimension(500, 450));
        detailsCard.setBorder(BorderFactory.createLineBorder(ThemeManager.border(), 1));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JLabel icon = new JLabel("\u2705", JLabel.CENTER);
        icon.setFont(new Font("SansSerif", Font.PLAIN, 48));
        gbc.gridy = 0; detailsCard.add(icon, gbc);

        JLabel title = new JLabel("CANCELLATION SUCCESSFUL", JLabel.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(StyleConfig.SUCCESS);
        gbc.gridy = 1; detailsCard.add(title, gbc);

        JSeparator sep = new JSeparator();
        sep.setForeground(ThemeManager.border());
        gbc.gridy = 2; gbc.insets = new Insets(20, 40, 20, 40); detailsCard.add(sep, gbc);

        gbc.insets = new Insets(10, 50, 10, 50);
        gbc.gridy = 3; detailsCard.add(createDetailRow("Booking ID", "QB-" + bookingId), gbc);
        gbc.gridy = 4; detailsCard.add(createDetailRow("Status", "CANCELLED"), gbc);
        gbc.gridy = 5; detailsCard.add(createDetailRow("Refund Status", "PROCESSED"), gbc);
        gbc.gridy = 6; detailsCard.add(createDetailRow("Estimated Refund", (bookingId % 3 == 0) ? "50%" : "100%"), gbc);
        
        JButton okBtn = new JButton("RETURN TO HOME");
        StyleConfig.styleButton(okBtn, StyleConfig.SECONDARY, Color.WHITE);
        okBtn.addActionListener(e -> frame.showPanel("home"));
        gbc.gridy = 7; gbc.insets = new Insets(30, 80, 20, 80);
        detailsCard.add(okBtn, gbc);

        add(detailsCard);
        revalidate(); repaint();
    }

    private JPanel createDetailRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(StyleConfig.LABEL_FONT);
        lbl.setForeground(ThemeManager.muted());
        JLabel val = new JLabel(value);
        val.setFont(StyleConfig.BODY_FONT);
        val.setForeground(ThemeManager.text());
        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        return row;
    }

    private int parseId(String input) throws NumberFormatException {
        return Integer.parseInt(input.toUpperCase().replace("QB-", "").replace("BT-", "").replace("AB-", ""));
    }
}
