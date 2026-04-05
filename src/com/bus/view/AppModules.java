package com.bus.view;

/**
 * Which UI modules are registered and reachable.
 */
public final class AppModules {
    private AppModules() {}

    public static final boolean HOME            = true;
    public static final boolean LOGIN           = true;
    public static final boolean SIGNUP          = true;
    public static final boolean BOOKING         = true;
    public static final boolean SEARCH_RESULTS  = true;
    public static final boolean PAYMENT         = true;
    public static final boolean BOOKING_SUCCESS = true;
    public static final boolean MY_BOOKINGS     = true;
    public static final boolean ROUTES          = true;
    public static final boolean AVAILABILITY    = true;
    public static final boolean CANCELLATION    = true;
    public static final boolean LIVE_TRACKER    = true;
    public static final boolean ABOUT_PROJECT   = true;
    public static final boolean ADMIN_DASHBOARD = true;
    public static final boolean PROFILE         = true;
    public static final boolean HELP            = true;

    public static boolean isCardRegistered(String cardName) {
        switch (cardName) {
            case "home":           return HOME;
            case "login":          return LOGIN;
            case "signup":         return SIGNUP;
            case "bookticket":     return BOOKING;
            case "availability":   return AVAILABILITY;
            case "routes":         return ROUTES;
            case "cancel":         return CANCELLATION;
            case "mybookings":     return MY_BOOKINGS;
            case "aboutproject":   return ABOUT_PROJECT;
            case "success":        return BOOKING_SUCCESS;
            case "livetracker":    return LIVE_TRACKER;
            case "results":        return SEARCH_RESULTS;
            case "payment":        return PAYMENT;
            case "admin":          return ADMIN_DASHBOARD;
            case "profile":        return PROFILE;
            case "help":           return HELP;
            default:               return false;
        }
    }
}
