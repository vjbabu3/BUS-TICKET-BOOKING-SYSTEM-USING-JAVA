package com.bus.view;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;

/**
 * ThemeManager – Global application theme controller using UIManager.
 */
public class ThemeManager {
    private static boolean dark = false;

    public static boolean isDark() { return dark; }

    public static void applyTheme(boolean isDark) {
        ThemeManager.dark = isDark;

        Color BG        = new ColorUIResource(isDark ? new Color(10, 14, 23)  : Color.WHITE);
        Color SURFACE   = new ColorUIResource(isDark ? new Color(22, 28, 45)  : Color.WHITE);
        Color SURFACE2  = new ColorUIResource(isDark ? new Color(30, 41, 59)  : new Color(241, 245, 249));
        Color TEXT      = new ColorUIResource(isDark ? new Color(226, 232, 240) : new Color(15, 23, 42));
        Color MUTED     = new ColorUIResource(isDark ? new Color(148, 163, 184) : new Color(71, 85, 105));
        Color BORDER    = new ColorUIResource(isDark ? new Color(30, 41, 59)  : new Color(226, 226, 226));
        Color INPUT_BG  = new ColorUIResource(isDark ? new Color(30, 41, 59)  : new Color(250, 250, 250));
        Color SEL_BG    = new ColorUIResource(isDark ? new Color(37, 99, 235)  : new Color(219, 234, 254));
        Color SEL_FG    = new ColorUIResource(isDark ? new Color(248, 250, 252) : new Color(30, 58, 138));

        // Core Components
        UIManager.put("Panel.background",        BG);
        UIManager.put("Panel.foreground",        TEXT);
        UIManager.put("Viewport.background",     BG);
        UIManager.put("ScrollPane.background",   BG);
        UIManager.put("Label.foreground",        TEXT);
        UIManager.put("Label.background",        BG);
        UIManager.put("Label.disabledForeground", MUTED);

        // Inputs
        UIManager.put("TextField.background",    INPUT_BG);
        UIManager.put("TextField.foreground",    TEXT);
        UIManager.put("TextField.caretForeground", isDark ? Color.WHITE : BG);
        UIManager.put("TextField.selectionBackground", SEL_BG);
        UIManager.put("TextField.selectionForeground", SEL_FG);
        UIManager.put("TextField.inactiveForeground", MUTED);
        UIManager.put("TextField.border", BorderFactory.createLineBorder(BORDER));
        
        UIManager.put("PasswordField.background", INPUT_BG);
        UIManager.put("PasswordField.foreground",  TEXT);
        UIManager.put("PasswordField.caretForeground", isDark ? Color.WHITE : BG);
        UIManager.put("PasswordField.selectionBackground", SEL_BG);
        UIManager.put("PasswordField.selectionForeground", SEL_FG);
        UIManager.put("PasswordField.border", BorderFactory.createLineBorder(BORDER));

        // Buttons & Selectables
        UIManager.put("Button.background",       SURFACE2);
        UIManager.put("Button.foreground",       TEXT);
        UIManager.put("Button.border",           BorderFactory.createEmptyBorder(8, 16, 8, 16));
        UIManager.put("CheckBox.background",     BG);
        UIManager.put("CheckBox.foreground",     TEXT);
        UIManager.put("ComboBox.background",     INPUT_BG);
        UIManager.put("ComboBox.foreground",     TEXT);
        UIManager.put("ComboBox.selectionBackground", SEL_BG);
        UIManager.put("ComboBox.selectionForeground", SEL_FG);

        // Tables
        UIManager.put("Table.background",        SURFACE);
        UIManager.put("Table.foreground",        TEXT);
        UIManager.put("Table.gridColor",         BORDER);
        UIManager.put("Table.selectionBackground", SEL_BG);
        UIManager.put("Table.selectionForeground", SEL_FG);
        UIManager.put("TableHeader.background",  isDark ? new Color(15, 23, 42) : new Color(31,41,55));
        UIManager.put("TableHeader.foreground",  Color.WHITE);

        // Others
        UIManager.put("OptionPane.background",   SURFACE);
        UIManager.put("OptionPane.messageForeground",  TEXT);
        UIManager.put("ToolTip.background",      SURFACE);
        UIManager.put("ToolTip.foreground",      TEXT);
        
        UIManager.put("List.background", SURFACE);
        UIManager.put("List.foreground", TEXT);
        UIManager.put("List.selectionBackground", SEL_BG);
        UIManager.put("List.selectionForeground", SEL_FG);
        
        StyleConfig.refresh();
    }

    public static Color bg()      { return new ColorUIResource(dark ? new Color(10, 14, 23) : Color.WHITE); }
    public static Color surface() { return new ColorUIResource(dark ? new Color(22, 28, 45) : Color.WHITE); }
    public static Color surface2(){ return new ColorUIResource(dark ? new Color(30, 41, 59) : new Color(249, 250, 251)); }
    public static Color text()    { return new ColorUIResource(dark ? new Color(226, 232, 240) : new Color(31, 41, 55)); }
    public static Color muted()   { return new ColorUIResource(dark ? new Color(148, 163, 184) : new Color(107, 114, 128)); }
    public static Color border()  { return new ColorUIResource(dark ? new Color(30, 41, 59) : new Color(226, 226, 226)); }
    public static Color input()   { return new ColorUIResource(dark ? new Color(30, 41, 59) : Color.WHITE); }
}

