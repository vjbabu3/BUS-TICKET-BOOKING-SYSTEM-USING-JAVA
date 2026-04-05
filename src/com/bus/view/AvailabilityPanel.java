package com.bus.view;

import com.bus.dao.BookingDAO;
import com.bus.dao.RouteDAO;
import com.bus.model.Route;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AvailabilityPanel – Refined for multiple seat selection, dark mode, and premium UX.
 */
public class AvailabilityPanel extends JPanel {
    private MainFrame frame;
    private JComboBox<Route> routeCombo;
    private JPanel seatGrid;
    private JLabel totalSeatsLbl, availableSeatsLbl, bookedSeatsLbl, priceLbl, selectedSeatsLbl;
    private JLabel routeInfoLbl;
    private JButton trackBtn, confirmBtn;
    private JTextField dateField;
    private List<SeatButton> seatButtons = new ArrayList<>();
    private List<Integer> bookedSeatNumbers = new ArrayList<>();
    private List<Integer> selectedSeatNumbers = new ArrayList<>();

    public AvailabilityPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(ThemeManager.bg());

        // --- HERO STRIP ---
        JPanel heroStrip = new JPanel(new GridBagLayout());
        heroStrip.setBackground(StyleConfig.PRIMARY);
        heroStrip.setPreferredSize(new Dimension(0, 110));
        
        JLabel heroTitle = new JLabel("\uD83D\uDCCA  PICK YOUR SEATS");
        heroTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        heroTitle.setForeground(Color.WHITE);
        
        routeInfoLbl = new JLabel("Select Service & Date to Begin");
        routeInfoLbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        routeInfoLbl.setForeground(new Color(191, 219, 254));
        
        GridBagConstraints hgbc = new GridBagConstraints();
        hgbc.gridy = 0; heroStrip.add(heroTitle, hgbc);
        hgbc.gridy = 1; hgbc.insets = new Insets(5,0,0,0); heroStrip.add(routeInfoLbl, hgbc);
        add(heroStrip, BorderLayout.NORTH);

        // --- MAIN CONTENT ---
        JPanel main = new JPanel(new BorderLayout(0, 20));
        main.setOpaque(false);
        main.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // 1. Controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        controls.setBackground(ThemeManager.surface());
        controls.setBorder(BorderFactory.createLineBorder(ThemeManager.border()));

        routeCombo = new JComboBox<>();
        routeCombo.setPreferredSize(new Dimension(240, 38));
        
        dateField = new JTextField(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")), 10);
        StyleConfig.styleTextField(dateField);

        JButton checkBtn = new JButton("Fetch Seats");
        StyleConfig.styleButton(checkBtn, StyleConfig.PRIMARY, Color.WHITE);
        checkBtn.addActionListener(e -> refreshCurrentRoute());

        trackBtn = new JButton("\uD83D\uDCCD Track Bus");
        StyleConfig.styleButton(trackBtn, StyleConfig.SUCCESS, Color.WHITE);
        trackBtn.setVisible(false);
        trackBtn.addActionListener(e -> {
            Route r = (Route) routeCombo.getSelectedItem();
            if (r != null) frame.showLiveTracker(r);
        });

        controls.add(new JLabel("SERVICE:") {{ setFont(StyleConfig.LABEL_FONT); }});
        controls.add(routeCombo);
        controls.add(new JLabel("DATE:") {{ setFont(StyleConfig.LABEL_FONT); }});
        controls.add(dateField);
        controls.add(checkBtn);
        controls.add(trackBtn);
        main.add(controls, BorderLayout.NORTH);

        // 2. Body (Map + Sidebar)
        JPanel body = new JPanel(new BorderLayout(25, 0));
        body.setOpaque(false);

        // Seat Map Card
        JPanel mapCard = new JPanel(new BorderLayout());
        mapCard.setBackground(ThemeManager.surface());
        mapCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.border()),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));

        JPanel busHeader = new JPanel(new BorderLayout());
        busHeader.setOpaque(false);
        busHeader.add(new JLabel("REAR") {{ setForeground(ThemeManager.muted()); }}, BorderLayout.WEST);
        busHeader.add(new JLabel("FRONT [ DRIVER ]") {{ setForeground(ThemeManager.muted()); }}, BorderLayout.EAST);
        mapCard.add(busHeader, BorderLayout.NORTH);

        seatGrid = new JPanel();
        seatGrid.setOpaque(false);
        JScrollPane scroll = new JScrollPane(seatGrid);
        scroll.setBorder(null); scroll.setOpaque(false); scroll.getViewport().setOpaque(false);
        mapCard.add(scroll, BorderLayout.CENTER);
        body.add(mapCard, BorderLayout.CENTER);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(240, 0));

        JPanel stats = new JPanel(new GridLayout(5, 1, 0, 15));
        stats.setBackground(ThemeManager.surface());
        stats.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.border()),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        totalSeatsLbl = createStatLbl("Total Capacity", "—");
        availableSeatsLbl = createStatLbl("Available", "—");
        bookedSeatsLbl = createStatLbl("Booked", "—");
        selectedSeatsLbl = createStatLbl("Selected", "0 Seats");
        priceLbl = createStatLbl("Fare / Seat", "—");

        stats.add(totalSeatsLbl); stats.add(availableSeatsLbl); 
        stats.add(bookedSeatsLbl); stats.add(selectedSeatsLbl); stats.add(priceLbl);

        confirmBtn = new JButton("Proceed to Book");
        StyleConfig.styleButton(confirmBtn, StyleConfig.PRIMARY, Color.WHITE);
        confirmBtn.setEnabled(false);
        confirmBtn.addActionListener(e -> proceedToBooking());

        sidebar.add(stats);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(confirmBtn);
        body.add(sidebar, BorderLayout.EAST);

        main.add(body, BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);

        loadRoutes();
        routeCombo.addActionListener(e -> refreshCurrentRoute());
    }

    private JLabel createStatLbl(String label, String value) {
        return new JLabel("<html><font color='#9CA3AF' size='2'>" + label + "</font><br/>"
                + "<b style='color:" + (ThemeManager.isDark() ? "#F3F4F6":"#1F2937") + "; font-size:14px;'>" + value + "</b></html>");
    }

    private void loadRoutes() {
        List<Route> list = new RouteDAO().getAllRoutes();
        routeCombo.removeAllItems();
        routeCombo.addItem(null);
        for (Route r : list) routeCombo.addItem(r);
    }

    private void refreshCurrentRoute() {
        Route r = (Route) routeCombo.getSelectedItem();
        if (r == null) return;

        routeInfoLbl.setText(r.getSource() + " \u2192 " + r.getDestination() + " | Bus: " + r.getBusNumber());
        trackBtn.setVisible(true);
        selectedSeatNumbers.clear();
        confirmBtn.setEnabled(false);
        selectedSeatsLbl.setText(createStatLbl("Selected", "0 Seats").getText());
        
        initializeSeatGrid(r.getTotalSeats());
        bookedSeatNumbers = new BookingDAO().getBookedSeats(r.getRouteId(), dateField.getText());
        
        for (SeatButton btn : seatButtons) {
            btn.setStatus(bookedSeatNumbers.contains(btn.number) ? SeatStatus.BOOKED : SeatStatus.AVAILABLE);
        }

        totalSeatsLbl.setText(createStatLbl("Total Capacity", r.getTotalSeats() + "").getText());
        availableSeatsLbl.setText(createStatLbl("Available", (r.getTotalSeats() - bookedSeatNumbers.size()) + "").getText());
        bookedSeatsLbl.setText(createStatLbl("Booked", bookedSeatNumbers.size() + "").getText());
        priceLbl.setText(createStatLbl("Fare / Seat", "\u20B9" + r.getPrice()).getText());
    }

    private void initializeSeatGrid(int total) {
        seatGrid.removeAll();
        seatButtons.clear();
        int rows = (int)Math.ceil(total / 4.0);
        seatGrid.setLayout(new GridLayout(rows, 5, 10, 12));

        for (int i = 0; i < rows * 5; i++) {
            if (i % 5 == 2) { seatGrid.add(new JLabel()); continue; } // Aisle
            int row = i / 5, col = i % 5;
            int effCol = col > 2 ? col - 1 : col;
            int seatNo = (row * 4) + effCol + 1;

            if (seatNo <= total) {
                SeatButton btn = new SeatButton(seatNo);
                btn.addActionListener(e -> toggleSeat(btn));
                seatButtons.add(btn);
                seatGrid.add(btn);
            } else {
                seatGrid.add(new JLabel());
            }
        }
        seatGrid.revalidate(); seatGrid.repaint();
    }

    private void toggleSeat(SeatButton btn) {
        if (btn.status == SeatStatus.BOOKED) return;
        
        if (btn.status == SeatStatus.SELECTED) {
            btn.setStatus(SeatStatus.AVAILABLE);
            selectedSeatNumbers.remove((Integer)btn.number);
        } else {
            btn.setStatus(SeatStatus.SELECTED);
            selectedSeatNumbers.add(btn.number);
        }
        
        int count = selectedSeatNumbers.size();
        selectedSeatsLbl.setText(createStatLbl("Selected", count + " Seats").getText());
        confirmBtn.setEnabled(count > 0);
    }

    private void proceedToBooking() {
        Route r = (Route) routeCombo.getSelectedItem();
        if (r == null || selectedSeatNumbers.isEmpty()) return;
        
        // Pass MULTIPLE seats to booking panel
        // For now, it passes the first one, but we should update BookingPanel to join them
        StringBuilder sb = new StringBuilder();
        for(int s : selectedSeatNumbers) sb.append(s).append(", ");
        String seatsStr = sb.toString().substring(0, sb.length()-2);
        
        ToastNotification.show(this, "Selected Seats: " + seatsStr, ToastNotification.Type.SUCCESS);
        // Navigate with selected seats list
        frame.showBookingWithRoute(r, selectedSeatNumbers); 
    }

    public void refreshData() { loadRoutes(); }

    enum SeatStatus { AVAILABLE, BOOKED, SELECTED }

    static class SeatButton extends JButton {
        int number;
        SeatStatus status = SeatStatus.AVAILABLE;

        SeatButton(int num) {
            this.number = num;
            setPreferredSize(new Dimension(50, 60));
            setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        void setStatus(SeatStatus s) { this.status = s; repaint(); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            Color mainColor, borderColor, textColor, shadowColor;
            
            if (status == SeatStatus.BOOKED) {
                mainColor = ThemeManager.isDark() ? new Color(30, 41, 59) : new Color(229, 231, 235);
                borderColor = ThemeManager.isDark() ? new Color(51, 65, 85) : new Color(209, 213, 219);
                textColor = ThemeManager.isDark() ? new Color(71, 85, 105) : new Color(156, 163, 175);
                shadowColor = new Color(0, 0, 0, 40);
            } else if (status == SeatStatus.SELECTED) {
                mainColor = new Color(37, 99, 235);
                borderColor = new Color(29, 78, 216);
                textColor = Color.WHITE;
                shadowColor = new Color(37, 99, 235, 60);
            } else {
                mainColor = ThemeManager.isDark() ? new Color(15, 23, 42) : new Color(240, 253, 244);
                borderColor = ThemeManager.isDark() ? new Color(34, 197, 94, 180) : new Color(34, 197, 94);
                textColor = ThemeManager.isDark() ? new Color(34, 197, 94) : new Color(22, 163, 74);
                shadowColor = new Color(0, 0, 0, 20);
            }

            // Draw Seat Base (Realistic Shape)
            int w = getWidth(), h = getHeight();
            
            // Subtle Shadow
            g2.setColor(shadowColor);
            g2.fillRoundRect(4, 6, w-8, h-10, 10, 10);
            
            // Seat Body
            GradientPaint gp = new GradientPaint(0, 0, mainColor, 0, h, mainColor.darker());
            g2.setPaint(gp);
            g2.fillRoundRect(4, 4, w-8, h-12, 10, 10);
            
            // Seat Border
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(4, 4, w-8, h-12, 10, 10);
            
            // Seat Cushion Detail (The "Image Style" part)
            g2.setColor(new Color(borderColor.getRed(), borderColor.getGreen(), borderColor.getBlue(), 80));
            g2.drawRoundRect(8, 8, w-16, h-24, 6, 6);
            
            // Seat handle or headrest
            g2.fillRoundRect(w/2 - 10, 6, 20, 6, 3, 3);

            // Seat Number
            g2.setColor(textColor);
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            String lbl = status == SeatStatus.BOOKED ? "\u2715" : String.valueOf(number);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(lbl, (w-fm.stringWidth(lbl))/2, (h+fm.getAscent())/2 - 4);
        }
    }
}
