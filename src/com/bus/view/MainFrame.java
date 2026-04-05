package com.bus.view;

import com.bus.model.User;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private UserHeaderPanel headerPanel;
    private User currentUser;

    private BookingPanel bookingPanel;
    private MyBookingsPanel myBookingsPanel;
    private AvailabilityPanel availabilityPanel;
    private BookingSuccessPanel successPanel;
    private LiveTrackerPanel liveTrackerPanel;
    private PaymentPanel paymentPanel;
    private SearchResultsPanel searchResultsPanel;
    private ProfilePanel profilePanel;
    private CancelPanel cancelPanel;
    private AdminDashboardPanel adminPanel;
    private RoutesPanel routesPanel;

    public MainFrame() {
        ThemeManager.applyTheme(true); // Default to Dark theme as requested
        setTitle("QuickBus - Smart Way to Book Your Bus");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(ThemeManager.bg());

        setLayout(new BorderLayout());

        headerPanel = new UserHeaderPanel(this);
        add(headerPanel, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        if (AppModules.HOME)            mainPanel.add(new HomePanel(this),           "home");
        if (AppModules.LOGIN)           mainPanel.add(new LoginPanel(this),          "login");
        if (AppModules.SIGNUP)          mainPanel.add(new SignUpPanel(this),         "signup");
        if (AppModules.BOOKING) {
            bookingPanel = new BookingPanel(this);
            mainPanel.add(bookingPanel, "bookticket");
        }
        if (AppModules.AVAILABILITY) {
            availabilityPanel = new AvailabilityPanel(this);
            mainPanel.add(availabilityPanel, "availability");
        }
        if (AppModules.ROUTES) {
            routesPanel = new RoutesPanel(this);
            mainPanel.add(routesPanel, "routes");
        }
        if (AppModules.CANCELLATION) {
            cancelPanel = new CancelPanel(this);
            mainPanel.add(cancelPanel, "cancel");
        }
        if (AppModules.MY_BOOKINGS) {
            myBookingsPanel = new MyBookingsPanel(this);
            mainPanel.add(myBookingsPanel, "mybookings");
        }
        if (AppModules.ABOUT_PROJECT)   mainPanel.add(new AboutProjectPanel(this),   "aboutproject");
        if (AppModules.BOOKING_SUCCESS) {
            successPanel = new BookingSuccessPanel(this);
            mainPanel.add(successPanel, "success");
        }
        if (AppModules.LIVE_TRACKER) {
            liveTrackerPanel = new LiveTrackerPanel(this);
            mainPanel.add(liveTrackerPanel, "livetracker");
        }
        if (AppModules.SEARCH_RESULTS) {
            searchResultsPanel = new SearchResultsPanel(this);
            mainPanel.add(searchResultsPanel, "results");
        }
        if (AppModules.PAYMENT) {
            paymentPanel = new PaymentPanel(this);
            mainPanel.add(paymentPanel, "payment");
        }
        if (AppModules.ADMIN_DASHBOARD) {
            adminPanel = new AdminDashboardPanel(this);
            mainPanel.add(adminPanel, "admin");
        }
        if (AppModules.PROFILE) {
            profilePanel = new ProfilePanel(this);
            mainPanel.add(profilePanel, "profile");
        }
        if (AppModules.HELP) {
            mainPanel.add(new HelpPanel(this), "help");
        }

        add(mainPanel, BorderLayout.CENTER);
        showPanel(AppModules.HOME ? "home" : firstRegisteredCard());
    }

    private String firstRegisteredCard() {
        if (AppModules.LOGIN)  return "login";
        if (AppModules.SIGNUP) return "signup";
        return "home";
    }

    public void showPanel(String name) {
        if (!AppModules.isCardRegistered(name)) return;
        if ("mybookings".equals(name) && currentUser != null && myBookingsPanel != null) {
            myBookingsPanel.refreshData();
        } else if ("bookticket".equals(name) && bookingPanel != null) {
            bookingPanel.refreshData();
        } else if ("routes".equals(name) && routesPanel != null) {
            routesPanel.refreshData();
        } else if ("availability".equals(name) && availabilityPanel != null) {
            availabilityPanel.refreshData();
        } else if ("cancel".equals(name) && cancelPanel != null) {
            cancelPanel.resetPanel();
        } else if ("admin".equals(name) && adminPanel != null) {
            adminPanel.refreshData();
        }
        
        // Update Header selection
        headerPanel.setActiveLink(name);
        
        // Smooth transition
        PanelTransitionManager.showWithFade(mainPanel, name, cardLayout);
    }

    public void showProfile() {
        if (profilePanel == null) return;
        profilePanel.refreshData();
        cardLayout.show(mainPanel, "profile");
    }

    public void showBookingWithRoute(com.bus.model.Route route, java.util.List<Integer> seats) {
        if (bookingPanel == null) return;
        bookingPanel.setSelectedData(route, seats);
        showPanel("bookticket");
    }

    public void showSuccessPanel(java.util.List<Integer> bookingIds) {
        if (successPanel == null) return;
        successPanel.setTicketIds(bookingIds);
        cardLayout.show(mainPanel, "success");
    }

    public void showSearchResults(java.util.List<com.bus.model.Route> routes) {
        if (searchResultsPanel == null) return;
        searchResultsPanel.setResults(routes);
        showPanel("results");
    }

    public void showLiveTracker(com.bus.model.Route route) {
        if (!AppModules.LIVE_TRACKER || liveTrackerPanel == null) return;
        liveTrackerPanel.startTracking(route);
        cardLayout.show(mainPanel, "livetracker");
    }

    public void showPayment(com.bus.model.Route route, java.util.List<Integer> seats, String travelDate) {
        if (paymentPanel == null) return;
        paymentPanel.setBookingData(route, seats, travelDate);
        cardLayout.show(mainPanel, "payment");
    }

    public void loginUser(User user) {
        this.currentUser = user;
        headerPanel.updateStatus(user);
        showPanel(AppModules.HOME ? "home" : firstRegisteredCard());
    }

    public User getCurrentUser() { return currentUser; }

    public void logout() {
        this.currentUser = null;
        headerPanel.updateStatus(null);
        showPanel(AppModules.HOME ? "home" : firstRegisteredCard());
    }

    public static void main(String[] args) {
        // Initialize Database and Tables directly from Java code as requested
        com.bus.util.DBInitializer.initialize();
        
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
