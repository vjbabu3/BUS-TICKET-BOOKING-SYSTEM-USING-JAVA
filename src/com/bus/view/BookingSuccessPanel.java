package com.bus.view;

import com.bus.dao.BookingDAO;
import com.bus.dao.RouteDAO;
import com.bus.model.Booking;
import com.bus.model.Route;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Success Panel matching Image 1: Clean, centered with green accents.
 */
public class BookingSuccessPanel extends JPanel {
    private JPanel detailsCard;
    private JLabel titleLbl, subTitleLbl, pnrLbl, passengerLbl, routeLbl, timeLbl, seatLbl, dateLbl, logoLbl;

    public BookingSuccessPanel(MainFrame frame) {
        setLayout(new GridBagLayout());
        setBackground(ThemeManager.bg());

        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.fill = GridBagConstraints.HORIZONTAL;

        // 0. BUS LOGO
        try {
            ImageIcon icon = new ImageIcon("src/com/bus/resources/bus_logo.png");
            Image img = icon.getImage().getScaledInstance(120, 80, Image.SCALE_SMOOTH);
            logoLbl = new JLabel(new ImageIcon(img), SwingConstants.CENTER);
        } catch (Exception e) {
            logoLbl = new JLabel("\uD83D\uDE8C", SwingConstants.CENTER); // Fallback emoji
            logoLbl.setFont(new Font("SansSerif", Font.PLAIN, 48));
        }
        logoLbl.setForeground(StyleConfig.PRIMARY);
        c.gridy = 0; c.insets = new Insets(0, 0, 15, 0);
        container.add(logoLbl, c);

        // 1. Success Message
        titleLbl = new JLabel("ORDER CONFIRMED!", SwingConstants.CENTER);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 32));
        titleLbl.setForeground(new Color(22, 101, 52)); // Dark green
        c.gridy = 1; c.insets = new Insets(0, 0, 10, 0);
        container.add(titleLbl, c);

        subTitleLbl = new JLabel("Thank you for choosing QuickBus.", SwingConstants.CENTER);
        subTitleLbl.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subTitleLbl.setForeground(ThemeManager.muted());
        c.gridy = 2; c.insets = new Insets(0, 0, 30, 0);
        container.add(subTitleLbl, c);

        // 2. The Ticket Info Card
        detailsCard = new JPanel(new GridBagLayout());
        detailsCard.setBackground(ThemeManager.surface());
        detailsCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(22, 101, 52), 2), // Stronger green border
            BorderFactory.createEmptyBorder(30, 50, 30, 50)
        ));
        
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.fill = GridBagConstraints.CENTER; gc.insets = new Insets(8, 0, 8, 0);

        pnrLbl = new JLabel("TICKET ID: QB-000");
        pnrLbl.setFont(new Font("SansSerif", Font.BOLD, 24));
        pnrLbl.setForeground(ThemeManager.text());
        gc.gridy = 0; detailsCard.add(pnrLbl, gc);

        passengerLbl = new JLabel("PASSENGER: -");
        passengerLbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        passengerLbl.setForeground(ThemeManager.text());
        gc.gridy = 1; detailsCard.add(passengerLbl, gc);

        JSeparator sep2 = new JSeparator();
        sep2.setForeground(ThemeManager.border());
        gc.gridy = 2; gc.fill = GridBagConstraints.HORIZONTAL; gc.insets = new Insets(10, 0, 10, 0);
        detailsCard.add(sep2, gc);

        gc.fill = GridBagConstraints.CENTER; gc.insets = new Insets(5, 0, 5, 0);

        routeLbl = new JLabel("ROUTE: - \u2192 -");
        routeLbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        routeLbl.setForeground(ThemeManager.text());
        gc.gridy = 3; detailsCard.add(routeLbl, gc);

        timeLbl = new JLabel("DEP. TIME: 22:15 PM");
        timeLbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        timeLbl.setForeground(ThemeManager.text());
        gc.gridy = 4; detailsCard.add(timeLbl, gc);

        seatLbl = new JLabel("CONFIRMED SEAT: -");
        seatLbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        seatLbl.setForeground(ThemeManager.text());
        gc.gridy = 5; detailsCard.add(seatLbl, gc);

        dateLbl = new JLabel("TRAVEL DATE: -");
        dateLbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        dateLbl.setForeground(ThemeManager.text());
        gc.gridy = 6; detailsCard.add(dateLbl, gc);

        c.gridy = 3; c.insets = new Insets(0, 0, 40, 0);
        container.add(detailsCard, c);

        // 3. Footer Area
        JLabel footerTip = new JLabel("Please keep this digital copy for boarding.", SwingConstants.CENTER);
        footerTip.setFont(new Font("SansSerif", Font.ITALIC, 12));
        footerTip.setForeground(ThemeManager.muted());
        c.gridy = 4; c.insets = new Insets(-20, 0, 30, 0);
        container.add(footerTip, c);

        // 4. Back Button
        JButton backBtn = new JButton("BACK TO HOME");
        StyleConfig.styleButton(backBtn, new Color(21, 128, 61), Color.WHITE);
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        backBtn.setPreferredSize(new Dimension(240, 50));
        backBtn.addActionListener(e -> frame.showPanel("home"));
        c.gridy = 5; 
        container.add(backBtn, c);

        add(container);
    }

    public void setTicketIds(List<Integer> bookingIds) {
        if (bookingIds == null || bookingIds.isEmpty()) return;
        
        BookingDAO dao = new BookingDAO();
        List<String> seats = new ArrayList<>();
        Booking first = null;
        
        for (int id : bookingIds) {
            Booking b = dao.getBookingById(id);
            if (b != null) {
                if (first == null) first = b;
                seats.add(String.valueOf(b.getSeatNumber()));
            }
        }
        
        if (first != null) {
            Route r = new RouteDAO().getRouteById(first.getRouteId());
            if (bookingIds.size() > 1) {
                StringBuilder sb = new StringBuilder();
                for (int bid : bookingIds) sb.append(bid).append(", ");
                String ids = sb.toString();
                pnrLbl.setText("TICKET IDS: QB-" + ids.substring(0, ids.length() - 2));
            } else {
                pnrLbl.setText("TICKET ID: QB-" + first.getBookingId());
            }
            passengerLbl.setText("PASSENGER: " + first.getPassengerName().toUpperCase());
            if (r != null) {
                routeLbl.setText("ROUTE: " + r.getSource() + " \u2192 " + r.getDestination());
                timeLbl.setText("DEP. TIME: " + r.getDepartureTime() + " | BUS: " + r.getBusNumber()); 
            }
            seatLbl.setText("CONFIRMED SEATS: " + String.join(", ", seats));
            dateLbl.setText("TRAVEL DATE: " + first.getTravelDate());
        }
        revalidate(); repaint();
    }
}

