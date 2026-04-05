package com.bus.view;

import com.bus.dao.BookingDAO;
import com.bus.model.Booking;
import com.bus.model.User;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;

/**
 * MyBookingsPanel – premium boarding-pass style booking history.
 */
public class MyBookingsPanel extends JPanel {
    private MainFrame frame;
    private JLabel greetingLbl, countLbl;
    private JPanel cardContainer;

    public MyBookingsPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(StyleConfig.BACKGROUND);

        // ── HERO HEADER BANNER ────────────────────────────────────────────────
        JPanel banner = new JPanel(new BorderLayout()) {
            private Image img;
            {
                URL u = getClass().getResource("/com/bus/resources/hero-bg.jpg");
                if (u != null) img = new ImageIcon(u).getImage();
            }
            @Override protected void paintComponent(Graphics g) {
                if (img != null) {
                    g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(new Color(10, 20, 60, 185));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    g.setColor(StyleConfig.SECONDARY);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        banner.setOpaque(false);
        banner.setPreferredSize(new Dimension(0, 150));

        JPanel bannerContent = new JPanel(new GridBagLayout());
        bannerContent.setOpaque(false);
        bannerContent.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));

        GridBagConstraints bc = new GridBagConstraints();
        bc.fill = GridBagConstraints.HORIZONTAL;
        bc.weightx = 1.0;
        bc.gridy = 0;

        greetingLbl = new JLabel("\uD83C\uDFAB  MY BOOKINGS");
        greetingLbl.setFont(new Font("SansSerif", Font.BOLD, 28));
        greetingLbl.setForeground(Color.WHITE);
        bannerContent.add(greetingLbl, bc);

        bc.gridy = 1;
        bc.insets = new Insets(8, 0, 0, 0);
        countLbl = new JLabel("Login to view your booking history");
        countLbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
        countLbl.setForeground(new Color(180, 210, 255));
        bannerContent.add(countLbl, bc);

        banner.add(bannerContent, BorderLayout.CENTER);

        // Refresh + Home inside banner right
        JPanel bannerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        bannerRight.setOpaque(false);
        bannerRight.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 50));

        JButton refreshBtn = new JButton("\u21BB  Refresh");
        StyleConfig.styleButton(refreshBtn, StyleConfig.ACCENT, Color.WHITE);
        refreshBtn.addActionListener(e -> refreshData());

        JButton homeBtn = new JButton("Back to Home");
        StyleConfig.styleButton(homeBtn, new Color(255, 255, 255, 40), Color.WHITE);
        homeBtn.setOpaque(false);
        homeBtn.setContentAreaFilled(false);
        homeBtn.setForeground(Color.WHITE);
        homeBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 1),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)));
        homeBtn.addActionListener(e -> frame.showPanel("home"));

        bannerRight.add(homeBtn);
        bannerRight.add(refreshBtn);
        banner.add(bannerRight, BorderLayout.SOUTH);

        add(banner, BorderLayout.NORTH);

        // ── MAIN CONTENT — Card list ──────────────────────────────────────────
        cardContainer = new JPanel();
        cardContainer.setLayout(new BoxLayout(cardContainer, BoxLayout.Y_AXIS));
        cardContainer.setOpaque(false);
        cardContainer.setBorder(BorderFactory.createEmptyBorder(24, 45, 24, 45));

        // Placeholder
        JLabel emptyMsg = new JLabel("<html><center><br/><br/>\uD83C\uDFAB<br/><b>No bookings yet</b><br/>"
                + "<font color='#9CA3AF'>Your confirmed tickets will appear here.</font></center></html>");
        emptyMsg.setFont(new Font("SansSerif", Font.PLAIN, 16));
        emptyMsg.setForeground(StyleConfig.TEXT_GRAY);
        emptyMsg.setHorizontalAlignment(SwingConstants.CENTER);
        emptyMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardContainer.add(Box.createRigidArea(new Dimension(0, 60)));
        cardContainer.add(emptyMsg);

        JScrollPane scroll = new JScrollPane(cardContainer);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    public void refreshData() {
        User user = frame.getCurrentUser();
        if (user == null) {
            JOptionPane.showMessageDialog(this,
                    "Please log in to view your bookings.", "QuickBus",
                    JOptionPane.WARNING_MESSAGE);
            frame.showPanel("login");
            return;
        }

        greetingLbl.setText("\uD83C\uDFAB  MY BOOKINGS");

        BookingDAO dao = new BookingDAO();
        List<Booking> bookings = dao.getUserBookings(user.getUserId());

        cardContainer.removeAll();
        cardContainer.setBorder(BorderFactory.createEmptyBorder(24, 45, 24, 45));

        if (bookings.isEmpty()) {
            countLbl.setText("Logged in as: " + user.getName().toUpperCase() + "  |  No bookings yet.");
            JLabel emptyMsg = new JLabel("<html><center><br/><br/>"
                    + "\uD83D\uDE8C<br/><b style='font-size:16px'>No tickets found</b><br/><br/>"
                    + "<font color='" + (ThemeManager.isDark() ? "#94a3b8" : "#9CA3AF") + "'>You haven't made any bookings yet.<br/>Book your first ride today!</font></center></html>");
            emptyMsg.setFont(new Font("SansSerif", Font.PLAIN, 15));
            emptyMsg.setForeground(StyleConfig.TEXT_GRAY);
            emptyMsg.setHorizontalAlignment(SwingConstants.CENTER);
            emptyMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
            cardContainer.add(Box.createRigidArea(new Dimension(0, 50)));
            cardContainer.add(emptyMsg);
        } else {
            countLbl.setText("Welcome back, " + user.getName().toUpperCase()
                    + "  |  " + bookings.size() + " ticket" + (bookings.size() == 1 ? "" : "s") + " found");

            for (Booking b : bookings) {
                cardContainer.add(buildBookingCard(b));
                cardContainer.add(Box.createRigidArea(new Dimension(0, 16)));
            }
        }

        cardContainer.revalidate();
        cardContainer.repaint();
    }

    private JPanel buildBookingCard(Booking b) {
        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setBackground(ThemeManager.surface());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 135));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, StyleConfig.PRIMARY),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ThemeManager.border()),
                        BorderFactory.createEmptyBorder(20, 28, 20, 28))));

        // Left: PNR + route info
        JPanel leftInfo = new JPanel();
        leftInfo.setLayout(new BoxLayout(leftInfo, BoxLayout.Y_AXIS));
        leftInfo.setOpaque(false);

        JLabel pnrLbl = new JLabel("PNR: QB-" + b.getBookingId());
        pnrLbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        pnrLbl.setForeground(ThemeManager.text());

        JLabel routeLbl = new JLabel("<html><font color='#6B7280'>Route: </font><b>BUS-"
                + b.getRouteId() + "</b></html>");
        routeLbl.setFont(StyleConfig.BODY_FONT);

        JLabel passengerLbl = new JLabel("<html><font color='#6B7280'>Passenger: </font><b>"
                + b.getPassengerName().toUpperCase() + "</b></html>");
        passengerLbl.setFont(StyleConfig.BODY_FONT);

        leftInfo.add(pnrLbl);
        leftInfo.add(Box.createRigidArea(new Dimension(0, 6)));
        leftInfo.add(routeLbl);
        leftInfo.add(Box.createRigidArea(new Dimension(0, 4)));
        leftInfo.add(passengerLbl);

        // Center: Date + Seat
        JPanel centerInfo = new JPanel(new GridLayout(2, 1, 0, 8));
        centerInfo.setOpaque(false);

        JLabel dateLbl = new JLabel("<html><font color='#9CA3AF' style='font-size:10px'>TRAVEL DATE</font>"
                + "<br/><b>" + b.getTravelDate() + "</b></html>");
        dateLbl.setFont(StyleConfig.BODY_FONT);
        dateLbl.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel seatLbl = new JLabel("<html><font color='#9CA3AF' style='font-size:10px'>SEAT</font>"
                + "<br/><b>No. " + b.getSeatNumber() + "</b></html>");
        seatLbl.setFont(StyleConfig.BODY_FONT);
        seatLbl.setHorizontalAlignment(SwingConstants.CENTER);

        centerInfo.add(dateLbl);
        centerInfo.add(seatLbl);

        // Right: Price badge + Status
        JPanel rightInfo = new JPanel();
        rightInfo.setLayout(new BoxLayout(rightInfo, BoxLayout.Y_AXIS));
        rightInfo.setOpaque(false);
        rightInfo.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel priceLbl = new JLabel("\u20B9 " + String.format("%.0f", b.getPrice()));
        priceLbl.setFont(new Font("SansSerif", Font.BOLD, 22));
        priceLbl.setForeground(StyleConfig.PRIMARY);
        priceLbl.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel statusBadge = new JLabel(b.getStatus().toUpperCase());
        statusBadge.setOpaque(true);
        boolean isCancelled = "CANCELLED".equalsIgnoreCase(b.getStatus());
        
        if (isCancelled) {
            statusBadge.setBackground(ThemeManager.isDark() ? new Color(127, 29, 29) : new Color(254, 226, 226));
            statusBadge.setForeground(ThemeManager.isDark() ? new Color(248, 113, 113) : new Color(220, 38, 38));
        } else {
            statusBadge.setBackground(ThemeManager.isDark() ? new Color(6, 78, 59) : new Color(220, 252, 231));
            statusBadge.setForeground(ThemeManager.isDark() ? new Color(52, 211, 153) : new Color(22, 163, 74));
        }
        statusBadge.setFont(new Font("SansSerif", Font.BOLD, 11));
        statusBadge.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        statusBadge.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JButton cancelBtn = null;
        if (!isCancelled) {
            cancelBtn = new JButton("Cancel Ticket");
            cancelBtn.setFont(new Font("SansSerif", Font.BOLD, 10));
            cancelBtn.setForeground(StyleConfig.DANGER);
            cancelBtn.setBorder(BorderFactory.createLineBorder(StyleConfig.DANGER, 1));
            cancelBtn.setContentAreaFilled(false);
            cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            cancelBtn.setFocusPainted(false);
            cancelBtn.setPreferredSize(new Dimension(100, 24));
            cancelBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
            
            final int bid = b.getBookingId();
            cancelBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Are you sure you want to cancel booking QB-" + bid + "?",
                    "Cancel Ticket", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (new BookingDAO().cancelTicket(bid)) {
                        ToastNotification.show(this, "Ticket QB-" + bid + " cancelled successfully.", ToastNotification.Type.SUCCESS);
                        refreshData();
                    } else {
                        ToastNotification.show(this, "Failed to cancel ticket.", ToastNotification.Type.ERROR);
                    }
                }
            });
        }

        JLabel contactLbl = new JLabel(b.getContactDetails());
        contactLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        contactLbl.setForeground(StyleConfig.TEXT_GRAY);
        contactLbl.setAlignmentX(Component.RIGHT_ALIGNMENT);

        rightInfo.add(priceLbl);
        rightInfo.add(Box.createRigidArea(new Dimension(0, 6)));
        rightInfo.add(statusBadge);
        if (cancelBtn != null) {
            rightInfo.add(Box.createRigidArea(new Dimension(0, 8)));
            rightInfo.add(cancelBtn);
        }
        rightInfo.add(Box.createRigidArea(new Dimension(0, 4)));
        rightInfo.add(contactLbl);

        // Dashed separator (vertical-ish visual) — use BorderLayout gaps
        card.add(leftInfo, BorderLayout.WEST);
        card.add(centerInfo, BorderLayout.CENTER);
        card.add(rightInfo, BorderLayout.EAST);

        // Styling for cancelled card
        if (isCancelled) {
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, StyleConfig.DANGER),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ThemeManager.border()),
                        BorderFactory.createEmptyBorder(20, 28, 20, 28))));
        }

        // Subtle hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(ThemeManager.surface2());
                card.repaint();
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(ThemeManager.surface());
                card.repaint();
            }
        });

        return card;
    }
}
