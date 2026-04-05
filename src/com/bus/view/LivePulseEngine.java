package com.bus.view;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * LivePulseEngine simulates real-time activity on the platform.
 * It broadcasts events to subscribed UI components to create a "Living App" atmosphere.
 */
public class LivePulseEngine {
    private static LivePulseEngine instance;
    private final Random random = new Random();
    private final List<PulseListener> listeners = new ArrayList<>();
    
    public interface PulseListener {
        /**
         * Triggered when a new real-time event is generated.
         * @param message The activity description (e.g., "Someone just booked a seat")
         */
        void onPulse(String message);
    }

    private LivePulseEngine() {
        // Trigger a new real-time event every 8-12 seconds
        Timer timer = new Timer(10000, e -> triggerPulse());
        timer.start();
    }

    public static synchronized LivePulseEngine getInstance() {
        if (instance == null) {
            instance = new LivePulseEngine();
        }
        return instance;
    }

    public void addListener(PulseListener l) {
        listeners.add(l);
    }

    private void triggerPulse() {
        String[] events = {
            "\u26A1 Flash Sale! 5% extra off for the next 10 minutes.",
            "\uD83D\uDC64 Rahul from Pune just booked a seat to Mumbai.",
            "\uD83D\uDD25 12 people are currently searching for Delhi to Jaipur.",
            "\u2705 Priya just confirmed a premium booking to Bangalore.",
            "\u23F3 Hurrying! Only 3 seats left on the 10:00 PM Express.",
            "\uD83C\uDF1F New High-Rated Bus added to the Hyderabad-Vizag route.",
            "\uD83D\uDCCA Demand is high for Mumbai - Pune. Book now to save!",
            "\u2728 Amit just saved \u20B9150 using a 'QuickBus' special coupon."
        };
        
        String msg = events[random.nextInt(events.length)];
        for (PulseListener l : listeners) {
            SwingUtilities.invokeLater(() -> l.onPulse(msg));
        }
    }
    
    /**
     * Simulates a "Live Seating" viewer count for a specific route.
     */
    public int getMockViewerCount() {
        return 5 + random.nextInt(20);
    }
}
