package com.bus.view;

import com.bus.dao.BookingDAO;
import com.bus.dao.RouteDAO;
import com.bus.model.Route;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * AdminDashboardPanel – summary stats, revenue bar chart, and top routes for admin users.
 * Optimized for TRUE dark mode.
 */
public class AdminDashboardPanel extends JPanel {

    private MainFrame frame;
    private JPanel content;

    public AdminDashboardPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(ThemeManager.bg());

        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(StyleConfig.SECONDARY);
        header.setBorder(BorderFactory.createEmptyBorder(22, 45, 22, 45));

        JLabel title = new JLabel("\uD83D\uDCCA  ADMIN DASHBOARD");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(StyleConfig.TEXT_LIGHT);
        header.add(title, BorderLayout.WEST);

        JPanel headerBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        headerBtns.setOpaque(false);

        JButton refreshBtn = new JButton("\u21BB Refresh");
        StyleConfig.styleButton(refreshBtn, StyleConfig.ACCENT, Color.WHITE);
        refreshBtn.addActionListener(e -> refreshData());

        JButton homeBtn = new JButton("BACK TO HOME");
        StyleConfig.styleButton(homeBtn, StyleConfig.TEXT_LIGHT, StyleConfig.SECONDARY);
        homeBtn.addActionListener(e -> this.frame.showPanel("home"));

        headerBtns.add(refreshBtn);
        headerBtns.add(homeBtn);
        header.add(headerBtns, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // --- BODY SCROLL ---
        content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(25, 45, 25, 45));

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null); scroll.setOpaque(false); scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        refreshData();
    }

    public void refreshData() {
        content.removeAll();

        BookingDAO bDao = new BookingDAO();
        RouteDAO rDao = new RouteDAO();
        List<com.bus.model.Route> routes = rDao.getAllRoutes();
        
        // 1. Stats Row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 20, 0));
        statsRow.setOpaque(false);
        statsRow.add(makeStatCard("\uD83D\uDCCB", "Bookings", String.valueOf(bDao.getTotalBookings()), new Color(37, 99, 235)));
        statsRow.add(makeStatCard("\uD83D\uDCB0", "Revenue", "\u20B9" + String.format("%.0f", bDao.getTotalRevenue()), new Color(16, 185, 129)));
        statsRow.add(makeStatCard("\uD83D\uDE8C", "Routes", String.valueOf(routes.size()), new Color(245, 158, 11)));
        statsRow.add(makeStatCard("\uD83D\uDC65", "Users", String.valueOf(bDao.getActiveUserCount()), new Color(139, 92, 246)));
        content.add(statsRow);
        content.add(Box.createRigidArea(new Dimension(0, 25)));

        // 2. Charts & Tables Row
        JPanel bodyRow = new JPanel(new GridLayout(1, 2, 20, 0));
        bodyRow.setOpaque(false);
        bodyRow.add(makeChartCard(routes));
        bodyRow.add(makeTableCard(routes));
        content.add(bodyRow);

        content.revalidate();
        content.repaint();
    }

    private JPanel makeStatCard(String icon, String label, String val, Color accent) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(ThemeManager.surface());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.border()),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel i = new JLabel(icon); i.setFont(new Font("SansSerif", Font.PLAIN, 28));
        
        JPanel texts = new JPanel(new GridLayout(2, 1));
        texts.setOpaque(false);
        JLabel v = new JLabel(val); v.setFont(new Font("SansSerif", Font.BOLD, 22)); v.setForeground(accent);
        JLabel l = new JLabel(label.toUpperCase()); l.setFont(new Font("SansSerif", Font.BOLD, 10)); l.setForeground(ThemeManager.muted());
        texts.add(v); texts.add(l);

        card.add(i, BorderLayout.WEST); card.add(texts, BorderLayout.CENTER);
        return card;
    }

    private JPanel makeChartCard(List<Route> routes) {
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setBackground(ThemeManager.surface());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.border()),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));

        JLabel tit = new JLabel("REVENUE BY ROUTE"); tit.setFont(StyleConfig.LABEL_FONT); tit.setForeground(ThemeManager.text());
        card.add(tit, BorderLayout.NORTH);

        JPanel chart = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int n = Math.min(routes.size(), 6);
                if (n == 0) return;
                int w = getWidth(), h = getHeight() - 40;
                int barW = (w / n) - 20;
                for (int i = 0; i < n; i++) {
                    int barH = (int)((routes.get(i).getPrice() / 2000.0) * h);
                    int x = 10 + i * (barW + 20);
                    int y = h - barH + 10;
                    g2.setColor(new Color(37, 99, 235, 180));
                    g2.fillRoundRect(x, y, barW, barH, 8, 8);
                    g2.setColor(ThemeManager.text());
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
                    g2.drawString(routes.get(i).getSource().substring(0, 3), x + 5, h + 25);
                }
            }
        };
        chart.setOpaque(false);
        card.add(chart, BorderLayout.CENTER);
        return card;
    }

    private JPanel makeTableCard(List<Route> routes) {
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setBackground(ThemeManager.surface());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.border()),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));

        JLabel tit = new JLabel("RECENT SERVICES"); tit.setFont(StyleConfig.LABEL_FONT); tit.setForeground(ThemeManager.text());
        card.add(tit, BorderLayout.NORTH);

        String[] cols = {"Route", "Bus No", "Fare"};
        Object[][] data = new Object[Math.min(routes.size(), 10)][3];
        for(int i=0; i<data.length; i++) {
            Route r = routes.get(i);
            data[i] = new Object[]{ r.getSource() + "-" + r.getDestination(), r.getBusNumber(), "\u20B9" + r.getPrice() };
        }

        JTable table = new JTable(data, cols);
        table.setRowHeight(32);
        table.setBackground(ThemeManager.surface());
        table.setForeground(ThemeManager.text());
        table.setGridColor(ThemeManager.border());
        table.setShowVerticalLines(false);
        table.getTableHeader().setBackground(ThemeManager.isDark() ? new Color(31, 41, 55) : new Color(243, 244, 246));
        table.getTableHeader().setForeground(ThemeManager.text());

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(null); sp.getViewport().setBackground(ThemeManager.surface());
        card.add(sp, BorderLayout.CENTER);
        return card;
    }
}
