package com.bus.view;

import com.bus.dao.BookingDAO;
import com.bus.dao.RouteDAO;
import com.bus.model.Route;
import com.bus.model.User;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * BookingPanel – Refined for multiple seat selection and full dark mode.
 */
public class BookingPanel extends JPanel {
    private MainFrame frame;
    private JTextField sourceField, destField, dateField;
    private JComboBox<Route> routeCombo;
    private JPanel seatGrid;
    private List<Integer> selectedSeats = new ArrayList<>();
    private List<JButton> seatButtons = new ArrayList<>();
    private List<Integer> bookedSeats = new ArrayList<>();

    public BookingPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(ThemeManager.bg());

        JPanel mainCard = new JPanel(new BorderLayout(0, 30));
        mainCard.setBackground(ThemeManager.surface());
        mainCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.border()),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        // Header
        JLabel titleLabel = new JLabel("CHOOSE YOUR SEATS");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(ThemeManager.text());
        mainCard.add(titleLabel, BorderLayout.NORTH);

        // Form Section (Simplified for display)
        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(8, 10, 8, 10); gbc.weightx = 1.0;

        sourceField = new JTextField(15); sourceField.setEditable(false); StyleConfig.styleTextField(sourceField);
        destField = new JTextField(15); destField.setEditable(false); StyleConfig.styleTextField(destField);
        dateField = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), 15);
        StyleConfig.styleTextField(dateField);

        routeCombo = new JComboBox<>();
        routeCombo.setBackground(ThemeManager.surface()); routeCombo.setFont(StyleConfig.BODY_FONT);

        gbc.gridx = 0; gbc.gridy = 0; formGrid.add(new JLabel("FROM") {{ setFont(StyleConfig.LABEL_FONT); setForeground(ThemeManager.muted()); }}, gbc);
        gbc.gridx = 1; gbc.gridy = 0; formGrid.add(new JLabel("TO") {{ setFont(StyleConfig.LABEL_FONT); setForeground(ThemeManager.muted()); }}, gbc);
        
        gbc.gridy = 1; gbc.gridx = 0; formGrid.add(sourceField, gbc);
        gbc.gridx = 1; formGrid.add(destField, gbc);
        
        gbc.gridy = 2; gbc.gridx = 0; formGrid.add(new JLabel("DATE") {{ setFont(StyleConfig.LABEL_FONT); setForeground(ThemeManager.muted()); }}, gbc);
        gbc.gridx = 1; formGrid.add(new JLabel("SERVICE") {{ setFont(StyleConfig.LABEL_FONT); setForeground(ThemeManager.muted()); }}, gbc);
        
        gbc.gridy = 3; gbc.gridx = 0; formGrid.add(dateField, gbc);
        gbc.gridx = 1; formGrid.add(routeCombo, gbc);

        // Center Content
        JPanel center = new JPanel(new BorderLayout(50, 0));
        center.setOpaque(false);
        center.add(formGrid, BorderLayout.NORTH);

        // Bus Layout Container
        JPanel busBox = new JPanel(new BorderLayout());
        busBox.setBackground(ThemeManager.surface());
        busBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.border(), 2, true),
            BorderFactory.createEmptyBorder(20, 30, 30, 30)
        ));
        
        seatGrid = new JPanel();
        busBox.add(seatGrid, BorderLayout.CENTER);
        center.add(busBox, BorderLayout.CENTER);
        mainCard.add(center, BorderLayout.CENTER);

        // Footer
        JButton payBtn = new JButton("Proceed to Pay");
        StyleConfig.styleButton(payBtn, StyleConfig.PRIMARY, Color.WHITE);
        payBtn.addActionListener(e -> processBooking());
        mainCard.add(payBtn, BorderLayout.SOUTH);

        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);
        outer.add(mainCard);
        add(new JScrollPane(outer) {{ setBorder(null); setOpaque(false); getViewport().setOpaque(false); }}, BorderLayout.CENTER);

        routeCombo.addActionListener(e -> {
            Route r = (Route) routeCombo.getSelectedItem();
            if (r != null) {
                sourceField.setText(r.getSource());
                destField.setText(r.getDestination());
                initializeSeats(r.getTotalSeats());
                refreshSeatGrid(r.getRouteId());
            }
        });
        loadRoutes();
    }

    private void initializeSeats(int total) {
        seatGrid.removeAll();
        seatGrid.setOpaque(false);
        seatButtons.clear();
        int rows = (int) Math.ceil(total / 4.0);
        seatGrid.setLayout(new GridLayout(rows, 5, 10, 15));
        
        for (int i = 0; i < rows * 5; i++) {
            if (i % 5 == 2) { seatGrid.add(new JLabel()); continue; }
            int row = i / 5, col = i % 5, effCol = (col > 2) ? col - 1 : col, seatNo = (row * 4) + effCol + 1;
            if (seatNo <= total) {
                AvailabilityPanel.SeatButton seat = new AvailabilityPanel.SeatButton(seatNo);
                seat.addActionListener(e -> toggleSeat(seat, seatNo));
                seatButtons.add(seat);
                seatGrid.add(seat);
            } else { seatGrid.add(new JLabel()); }
        }
        seatGrid.revalidate(); seatGrid.repaint();
    }

    private void toggleSeat(AvailabilityPanel.SeatButton btn, int num) {
        if (btn.status == AvailabilityPanel.SeatStatus.BOOKED) return;
        if (selectedSeats.contains((Integer)num)) {
            selectedSeats.remove((Integer)num);
            btn.setStatus(AvailabilityPanel.SeatStatus.AVAILABLE);
        }
        else {
            selectedSeats.add(num);
            btn.setStatus(AvailabilityPanel.SeatStatus.SELECTED);
        }
    }

    private void updateSeatUI() {
        for (JButton b : seatButtons) {
            AvailabilityPanel.SeatButton btn = (AvailabilityPanel.SeatButton) b;
            if (bookedSeats.contains(btn.number)) {
                btn.setStatus(AvailabilityPanel.SeatStatus.BOOKED);
            } else if (selectedSeats.contains(btn.number)) {
                btn.setStatus(AvailabilityPanel.SeatStatus.SELECTED);
            } else {
                btn.setStatus(AvailabilityPanel.SeatStatus.AVAILABLE);
            }
        }
    }

    public void setSelectedData(Route r, List<Integer> seats) {
        if (r == null) return;
        loadRoutes();
        for (int i = 0; i < routeCombo.getItemCount(); i++) {
            Route item = routeCombo.getItemAt(i);
            if (item != null && item.getRouteId() == r.getRouteId()) {
                routeCombo.setSelectedIndex(i);
                break;
            }
        }
        this.selectedSeats = new ArrayList<>(seats != null ? seats : new ArrayList<>());
        updateSeatUI();
    }

    private void loadRoutes() {
        List<Route> routes = new RouteDAO().getAllRoutes();
        Route current = (Route) routeCombo.getSelectedItem();
        routeCombo.removeAllItems();
        routeCombo.addItem(null);
        for (Route r : routes) routeCombo.addItem(r);
        if (current != null) routeCombo.setSelectedItem(current);
    }

    private void refreshSeatGrid(int rid) {
        bookedSeats = new BookingDAO().getBookedSeats(rid, dateField.getText());
        selectedSeats.clear();
        updateSeatUI();
    }

    private void processBooking() {
        User user = frame.getCurrentUser();
        Route r = (Route) routeCombo.getSelectedItem();
        if (user == null) { 
            ToastNotification.show(this, "Please login to proceed with booking.", ToastNotification.Type.INFO);
            frame.showPanel("login"); 
            return; 
        }
        if (r == null || selectedSeats.isEmpty()) {
            ToastNotification.show(this, "Please select a route and at least one seat.", ToastNotification.Type.WARNING);
            return;
        }
        frame.showPayment(r, selectedSeats, dateField.getText());
    }

    public void refreshData() { loadRoutes(); updateSeatUI(); }
}
