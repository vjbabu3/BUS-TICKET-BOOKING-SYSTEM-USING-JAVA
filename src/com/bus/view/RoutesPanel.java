package com.bus.view;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RoutesPanel – premium redesign with banner, stat cards, search filter and styled table.
 */
public class RoutesPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private List<com.bus.model.Route> allRoutes;
    private JTextField searchField;
    private MainFrame frame;

    public RoutesPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(ThemeManager.bg());
        refreshData();
    }

    public void refreshData() {
        removeAll();
        allRoutes = new com.bus.dao.RouteDAO().getAllRoutes();

        // ── TOP BANNER ──────────────────────────────────────────────────────
        JPanel bannerWrapper = new JPanel(new BorderLayout()) {
            private Image bannerImg;
            {
                URL url = getClass().getResource("/com/bus/resources/route-map.png");
                if (url != null) bannerImg = new ImageIcon(url).getImage();
            }
            @Override protected void paintComponent(Graphics g) {
                if (bannerImg != null) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.drawImage(bannerImg, 0, 0, getWidth(), getHeight(), this);
                    // Dark overlay for readability
                    g2.setColor(new Color(15, 30, 70, 170));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    g.setColor(StyleConfig.SECONDARY);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        bannerWrapper.setPreferredSize(new Dimension(0, 160));
        bannerWrapper.setOpaque(false);

        JPanel bannerContent = new JPanel(new GridBagLayout());
        bannerContent.setOpaque(false);
        bannerContent.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));

        GridBagConstraints bc = new GridBagConstraints();
        bc.fill = GridBagConstraints.HORIZONTAL;
        bc.weightx = 1.0;
        bc.gridy = 0;

        JLabel bannerTitle = new JLabel("\uD83D\uDE8C  ALL BUS ROUTES");
        bannerTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        bannerTitle.setForeground(Color.WHITE);
        bannerContent.add(bannerTitle, bc);

        bc.gridy = 1;
        bc.insets = new Insets(8, 0, 0, 0);
        JLabel bannerSub = new JLabel("Browse all available routes. Select any route to book your ticket.");
        bannerSub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        bannerSub.setForeground(new Color(180, 210, 255));
        bannerContent.add(bannerSub, bc);

        bannerWrapper.add(bannerContent, BorderLayout.CENTER);

        // Search bar inside banner bottom-right
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        searchBar.setOpaque(false);
        searchBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 50));
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(260, 38));
        StyleConfig.styleTextField(searchField);
        StyleConfig.setupPlaceholder(searchField, "Search city or route...");
        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { filterTable(searchField.getText()); }
        });
        JButton searchBtn = new JButton("Search");
        StyleConfig.styleButton(searchBtn, StyleConfig.PRIMARY, Color.WHITE);
        searchBar.add(searchField);
        searchBar.add(searchBtn);
        bannerWrapper.add(searchBar, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> filterTable(searchField.getText()));

        add(bannerWrapper, BorderLayout.NORTH);

        // ── STAT CARDS ───────────────────────────────────────────────────────
        int totalRoutes = allRoutes.size();
        long totalCities = allRoutes.stream()
                .flatMap(r -> java.util.Arrays.stream(new String[]{r.getSource(), r.getDestination()}))
                .distinct().count();
        double minFare = allRoutes.stream().mapToDouble(com.bus.model.Route::getPrice).min().orElse(0);
        double maxFare = allRoutes.stream().mapToDouble(com.bus.model.Route::getPrice).max().orElse(0);

        JPanel statsRow = new JPanel(new GridLayout(1, 4, 20, 0));
        statsRow.setOpaque(false);
        statsRow.setBorder(BorderFactory.createEmptyBorder(20, 45, 10, 45));
        statsRow.add(makeStatCard(String.valueOf(totalRoutes), "Total Routes", StyleConfig.PRIMARY));
        statsRow.add(makeStatCard(String.valueOf(totalCities), "Cities Covered", StyleConfig.ACCENT));
        statsRow.add(makeStatCard("\u20B9" + String.format("%.0f", minFare), "Lowest Fare", new Color(16, 185, 129)));
        statsRow.add(makeStatCard("\u20B9" + String.format("%.0f", maxFare), "Highest Fare", new Color(245, 158, 11)));

        // ── TABLE AREA ───────────────────────────────────────────────────────
        String[] columns = {"#", "From", "To", "Bus No.", "Departure", "Capacity", "Fare (\u20B9)", "Book"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return col == 7; }
        };
        buildTableRows(allRoutes);

        table = new JTable(tableModel);
        table.setRowHeight(52);
        table.setFont(StyleConfig.BODY_FONT);
        table.setGridColor(ThemeManager.border());
        table.setShowVerticalLines(false);
        table.setSelectionBackground(ThemeManager.isDark() ? new Color(30, 64, 175) : new Color(219, 234, 254));
        table.setSelectionForeground(ThemeManager.text());
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);

        // Column widths
        int[] widths = {55, 160, 160, 130, 100, 90, 100, 110};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Stripe renderer
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, focus, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
                setBackground(sel ? (ThemeManager.isDark() ? new Color(30, 64, 175) : new Color(219, 234, 254)) 
                                  : (row % 2 == 0 ? ThemeManager.surface() : ThemeManager.surface2()));
                setForeground(ThemeManager.text());
                if (col == 6) { // Fare column
                    setForeground(StyleConfig.PRIMARY);
                    setFont(new Font("SansSerif", Font.BOLD, 14));
                }
                if (col == 0) {
                    setForeground(ThemeManager.muted());
                    setHorizontalAlignment(CENTER);
                } else {
                    setHorizontalAlignment(LEFT);
                }
                return this;
            }
        });

        // Book button column
        table.getColumnModel().getColumn(7).setCellRenderer(new TableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable tbl, Object value,
                    boolean isSel, boolean hasFocus, int row, int col) {
                JButton btn = new JButton("Book Now");
                StyleConfig.styleButton(btn, StyleConfig.PRIMARY, Color.WHITE);
                btn.setFont(new Font("SansSerif", Font.BOLD, 12));
                return btn;
            }
        });
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == 7 && row >= 0) {
                    String from = (String) tableModel.getValueAt(row, 1);
                    String to   = (String) tableModel.getValueAt(row, 2);
                    // Find matching route and open booking
                    allRoutes.stream()
                        .filter(r -> r.getSource().equals(from) && r.getDestination().equals(to))
                        .findFirst()
                        .ifPresent(r -> frame.showBookingWithRoute(r, new java.util.ArrayList<>()));
                }
            }
        });

        JTableHeader header = table.getTableHeader();
        header.setBackground(StyleConfig.SECONDARY);
        header.setForeground(Color.WHITE);
        header.setFont(StyleConfig.LABEL_FONT);
        header.setPreferredSize(new Dimension(0, 46));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(8, 45, 0, 45),
                BorderFactory.createLineBorder(ThemeManager.border())));
        scroll.getViewport().setBackground(ThemeManager.bg());
        scroll.setOpaque(false);

        // ── FOOTER ──────────────────────────────────────────────────────────
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 45, 16));
        footer.setBackground(ThemeManager.bg());
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeManager.border()));

        JButton backBtn = new JButton("Back to Home");
        StyleConfig.styleButton(backBtn, StyleConfig.SECONDARY, Color.WHITE);
        backBtn.addActionListener(e -> frame.showPanel("home"));
        footer.add(backBtn);

        // ── ASSEMBLE ────────────────────────────────────────────────────────
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(statsRow, BorderLayout.NORTH);
        centerWrapper.add(scroll, BorderLayout.CENTER);
        centerWrapper.add(footer, BorderLayout.SOUTH);

        add(centerWrapper, BorderLayout.CENTER);
    }

    private JPanel makeStatCard(String value, String label, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(ThemeManager.surface());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ThemeManager.border()),
                        BorderFactory.createEmptyBorder(18, 20, 18, 20))));

        JLabel val = new JLabel(value);
        val.setFont(new Font("SansSerif", Font.BOLD, 26));
        val.setForeground(accent);

        JLabel lbl = new JLabel(label);
        lbl.setFont(StyleConfig.LABEL_FONT);
        lbl.setForeground(ThemeManager.muted());

        card.add(val, BorderLayout.CENTER);
        card.add(lbl, BorderLayout.SOUTH);
        return card;
    }

    private void buildTableRows(List<com.bus.model.Route> routes) {
        tableModel.setRowCount(0);
        int idx = 1;
        for (com.bus.model.Route r : routes) {
            tableModel.addRow(new Object[]{
                    idx++,
                    r.getSource(),
                    r.getDestination(),
                    r.getBusNumber(),
                    r.getDepartureTime(),
                    r.getTotalSeats() + " seats",
                    "\u20B9 " + String.format("%.0f", r.getPrice()),
                    "Book Now"
            });
        }
    }

    private void filterTable(String query) {
        String q = query.trim().toLowerCase();
        if (q.isEmpty() || q.equals("search city or route...")) {
            buildTableRows(allRoutes);
            return;
        }
        List<com.bus.model.Route> filtered = allRoutes.stream()
                .filter(r -> r.getSource().toLowerCase().contains(q)
                        || r.getDestination().toLowerCase().contains(q)
                        || r.getBusNumber().toLowerCase().contains(q))
                .collect(Collectors.toList());
        buildTableRows(filtered);
    }
}
