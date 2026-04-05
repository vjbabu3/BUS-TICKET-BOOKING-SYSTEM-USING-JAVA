package com.bus.view;

import com.bus.model.Route;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * LiveTrackerPanel – GPS Tracking + Real-Time Route Schedule in IST.
 */
public class LiveTrackerPanel extends JPanel {
    private Route route;
    private double progress = 0.05;
    private Timer timer;
    private JPanel scheduleList;

    public LiveTrackerPanel(MainFrame frame) {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.bg());

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);

        // --- TRACKER HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(StyleConfig.PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(18, 40, 18, 40));

        JLabel title = new JLabel("LIVE BUS TRACKER");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        JButton back = new JButton("BACK");
        StyleConfig.styleButton(back, Color.WHITE, StyleConfig.PRIMARY);
        back.addActionListener(e -> { stopTracking(); frame.showPanel("home"); });
        header.add(back, BorderLayout.EAST);
        content.add(header, BorderLayout.NORTH);

        // --- CENTRAL MONITORING ---
        JPanel monitoring = new JPanel(new BorderLayout(25, 0));
        monitoring.setOpaque(false);
        monitoring.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        // 1. Live Map Card
        JPanel mapCard = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth(), h = getHeight();
                int trackY = h / 2 + 10;
                int startX = 60, endX = w - 60;
                
                g2.setColor(ThemeManager.surface());
                g2.fillRoundRect(20, 20, w-40, h-40, 15, 15);
                g2.setColor(ThemeManager.border());
                g2.drawRoundRect(20, 20, w-40, h-40, 15, 15);

                // Track
                g2.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(ThemeManager.isDark() ? new Color(55,65,81) : new Color(229,231,235));
                g2.drawLine(startX, trackY, endX, trackY);
                
                // Progress
                g2.setColor(StyleConfig.SUCCESS);
                int currentX = startX + (int)((endX - startX) * progress);
                g2.drawLine(startX, trackY, currentX, trackY);

                // Stops
                drawPin(g2, startX, trackY, route != null ? route.getSource() : "Source");
                drawPin(g2, endX, trackY, route != null ? route.getDestination() : "End");

                // Bus Icon
                drawBusAt(g2, currentX, trackY - 45);
            }

            private void drawPin(Graphics2D g2, int x, int y, String name) {
                g2.setColor(ThemeManager.text());
                g2.fillOval(x-8, y-8, 16, 16);
                g2.setColor(ThemeManager.surface());
                g2.fillOval(x-4, y-4, 8, 8);
                g2.setColor(ThemeManager.text());
                g2.setFont(StyleConfig.LABEL_FONT);
                g2.drawString(name, x - (g2.getFontMetrics().stringWidth(name)/2), y + 35);
            }

            private void drawBusAt(Graphics2D g2, int x, int y) {
                g2.setColor(StyleConfig.PRIMARY);
                g2.fillRoundRect(x-35, y, 70, 35, 10, 10);
                g2.setColor(Color.WHITE); g2.fillRect(x+10, y+8, 15, 12); // Window
                g2.setColor(Color.BLACK); g2.fillOval(x-25, y+30, 12, 12); g2.fillOval(x+13, y+30, 12, 12);
            }
        };
        mapCard.setPreferredSize(new Dimension(0, 300));
        mapCard.setOpaque(false);
        monitoring.add(mapCard, BorderLayout.CENTER);

        // 2. Schedule Sidebar
        JPanel schedulePanel = new JPanel(new BorderLayout());
        schedulePanel.setBackground(ThemeManager.surface());
        schedulePanel.setPreferredSize(new Dimension(280, 0));
        schedulePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.border()),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)));

        JLabel schedTitle = new JLabel("ROUTE SCHEDULE (IST)");
        schedTitle.setFont(StyleConfig.LABEL_FONT);
        schedTitle.setForeground(StyleConfig.PRIMARY);
        schedulePanel.add(schedTitle, BorderLayout.NORTH);

        scheduleList = new JPanel();
        scheduleList.setLayout(new BoxLayout(scheduleList, BoxLayout.Y_AXIS));
        scheduleList.setOpaque(false);
        schedulePanel.add(new JScrollPane(scheduleList) {{ setBorder(null); setOpaque(false); getViewport().setOpaque(false); }}, BorderLayout.CENTER);

        monitoring.add(schedulePanel, BorderLayout.EAST);
        content.add(monitoring, BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);

        timer = new Timer(50, e -> {
            if (progress < 1.0) { progress += 0.001; repaint(); }
            else { timer.stop(); }
        });
    }

    public void startTracking(Route r) {
        this.route = r;
        this.progress = 0.05 + Math.random() * 0.4;
        updateSchedule();
        timer.start();
    }

    private void updateSchedule() {
        scheduleList.removeAll();
        if (route == null) return;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime nowIST = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        
        scheduleList.add(Box.createRigidArea(new Dimension(0, 15)));
        scheduleList.add(createSchedItem("TODAY", nowIST.format(DateTimeFormatter.ofPattern("dd MMM")), true));
        scheduleList.add(Box.createRigidArea(new Dimension(0, 10)));
        scheduleList.add(createSchedItem(route.getSource(), nowIST.minusHours(1).format(dtf), false));
        scheduleList.add(createSchedItem("Current Point", nowIST.format(dtf), false));
        scheduleList.add(createSchedItem(route.getDestination(), nowIST.plusHours(route.getRouteId() % 5 + 3).format(dtf), false));
        
        scheduleList.revalidate();
    }

    private JPanel createSchedItem(String stop, String time, boolean isDate) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(240, 45));
        
        JLabel s = new JLabel(stop);
        s.setFont(isDate ? new Font("SansSerif", Font.BOLD, 12) : StyleConfig.BODY_FONT);
        s.setForeground(isDate ? ThemeManager.muted() : ThemeManager.text());

        JLabel t = new JLabel(time);
        t.setFont(new Font("Monospaced", Font.BOLD, 13));
        t.setForeground(isDate ? ThemeManager.muted() : StyleConfig.SUCCESS);

        p.add(s, BorderLayout.WEST);
        p.add(t, BorderLayout.EAST);
        return p;
    }

    public void stopTracking() { if (timer != null) timer.stop(); }
}
