package com.bus.view;

import com.bus.model.Route;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * SearchResultsPanel designed to match QuickBus results list.
 */
public class SearchResultsPanel extends JPanel {
    private MainFrame frame;
    private JPanel resultsContainer;
    private JLabel summaryLbl;

    public SearchResultsPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(ThemeManager.bg());

        // Sidebar (Filters)
        add(createSidebar(), BorderLayout.WEST);

        // Main content area
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setOpaque(false);
        mainContent.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 60));

        // Top Summary Bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(ThemeManager.surface());
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.border()),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));

        summaryLbl = new JLabel("0 Buses Discovered");
        summaryLbl.setFont(new Font("SansSerif", Font.BOLD, 22));
        summaryLbl.setForeground(ThemeManager.text());
        topBar.add(summaryLbl, BorderLayout.WEST);

        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 0));
        sortPanel.setOpaque(false);
        sortPanel.add(new JLabel("SORT BY: ") {{ setForeground(ThemeManager.muted()); }});
        sortPanel.add(createSortLink("Ratings"));
        sortPanel.add(createSortLink("Departure"));
        sortPanel.add(createSortLink("Price"));
        topBar.add(sortPanel, BorderLayout.EAST);

        mainContent.add(topBar, BorderLayout.NORTH);

        // Results Container
        resultsContainer = new JPanel();
        resultsContainer.setLayout(new BoxLayout(resultsContainer, BoxLayout.Y_AXIS));
        resultsContainer.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(resultsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        mainContent.add(scrollPane, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    public void setResults(List<Route> routes) {
        resultsContainer.removeAll();
        summaryLbl.setText(routes.size() + " BUSES DISCOVERED");
        
        for (Route r : routes) {
            resultsContainer.add(new BusResultCard(r));
            resultsContainer.add(Box.createRigidArea(new Dimension(0, 20)));
        }
        
        resultsContainer.revalidate();
        resultsContainer.repaint();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(ThemeManager.surface());
        sidebar.setPreferredSize(new Dimension(300, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ThemeManager.border()));
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));

        JLabel filterTitle = new JLabel("FILTER BY");
        filterTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        filterTitle.setForeground(ThemeManager.text());
        filterTitle.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));
        sidebar.add(filterTitle);

        sidebar.add(createFilterPanel("BUS TYPE", new String[]{"AC", "Non-AC", "Sleeper", "Seater"}));
        sidebar.add(createFilterPanel("DEPARTURE TIME", new String[]{"Before 6 AM", "6 AM to 12 PM", "After 12 PM"}));
        
        sidebar.add(Box.createVerticalGlue());
        
        JButton backBtn = new JButton("EDIT ROUTE");
        StyleConfig.styleButton(backBtn, StyleConfig.SECONDARY, Color.WHITE);
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> frame.showPanel("home"));
        
        sidebar.add(backBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 40)));

        return sidebar;
    }

    private JPanel createFilterPanel(String title, String[] options) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));
        
        JLabel l = new JLabel(title);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setForeground(ThemeManager.muted());
        p.add(l);
        p.add(Box.createRigidArea(new Dimension(0, 10)));
        
        for (String opt : options) {
            JCheckBox cb = new JCheckBox(opt);
            cb.setFont(new Font("SansSerif", Font.PLAIN, 13));
            cb.setOpaque(false);
            cb.setForeground(ThemeManager.text());
            p.add(cb);
        }
        return p;
    }

    private JLabel createSortLink(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        l.setForeground(ThemeManager.text());
        l.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return l;
    }

    private class BusResultCard extends JPanel {
        public BusResultCard(Route r) {
            setLayout(new BorderLayout());
            setBackground(ThemeManager.surface());
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.border()),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
            ));
            setMaximumSize(new Dimension(1000, 170));
            setPreferredSize(new Dimension(900, 160));

            JPanel inner = new JPanel(new GridBagLayout());
            inner.setOpaque(false);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 1.0;

            // 1. Operator & Basic Info
            JPanel infoSection = new JPanel(new GridLayout(3, 1, 0, 5));
            infoSection.setOpaque(false);
            
            JLabel nameLbl = new JLabel(r.getBusNumber() + " SuperFast Express");
            nameLbl.setFont(new Font("SansSerif", Font.BOLD, 20));
            nameLbl.setForeground(ThemeManager.text());
            
            JLabel typeLbl = new JLabel("Premium A/C Sleeper (2+2)");
            typeLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
            typeLbl.setForeground(ThemeManager.muted());
            
            JPanel ratingRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            ratingRow.setOpaque(false);
            JLabel ratingBadge = new JLabel("\u2605 4.5");
            ratingBadge.setOpaque(true);
            ratingBadge.setBackground(StyleConfig.SUCCESS);
            ratingBadge.setForeground(Color.WHITE);
            ratingBadge.setFont(new Font("SansSerif", Font.BOLD, 12));
            ratingBadge.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            ratingRow.add(ratingBadge);
            
            infoSection.add(nameLbl); infoSection.add(typeLbl); infoSection.add(ratingRow);

            gbc.weightx = 0.4; gbc.gridx = 0;
            inner.add(infoSection, gbc);

            // 2. Schedule & Duration
            JPanel timeSection = new JPanel(new GridLayout(2, 1, 0, 5));
            timeSection.setOpaque(false);
            
            JLabel timeRange = new JLabel(r.getDepartureTime() + " — Arrival");
            timeRange.setFont(new Font("SansSerif", Font.BOLD, 20));
            timeRange.setForeground(ThemeManager.text());
            
            JLabel durationLbl = new JLabel("08h 45m Duration");
            durationLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
            durationLbl.setForeground(StyleConfig.TEXT_GRAY);

            timeSection.add(timeRange); timeSection.add(durationLbl);

            gbc.weightx = 0.3; gbc.gridx = 1;
            inner.add(timeSection, gbc);

            // 3. Price & Select
            JPanel priceSection = new JPanel(new GridLayout(2, 1, 0, 10));
            priceSection.setOpaque(false);
            
            JLabel priceLbl = new JLabel("\u20B9" + String.format("%.0f", r.getPrice()));
            priceLbl.setFont(new Font("SansSerif", Font.BOLD, 26));
            priceLbl.setForeground(StyleConfig.PRIMARY);
            priceLbl.setHorizontalAlignment(SwingConstants.RIGHT);
            
            JButton bookBtn = new JButton("Select Seats");
            StyleConfig.styleButton(bookBtn, StyleConfig.PRIMARY, Color.WHITE);
            bookBtn.setPreferredSize(new Dimension(160, 45));
            bookBtn.addActionListener(e -> frame.showBookingWithRoute(r, new java.util.ArrayList<>()));

            priceSection.add(priceLbl); priceSection.add(bookBtn);

            gbc.weightx = 0.3; gbc.gridx = 2;
            inner.add(priceSection, gbc);

            add(inner, BorderLayout.CENTER);
        }
    }
}
