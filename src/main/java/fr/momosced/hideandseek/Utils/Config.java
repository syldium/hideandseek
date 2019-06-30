package fr.momosced.hideandseek.Utils;

import fr.momosced.hideandseek.Main;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    private static FileConfiguration config;

    public static String LOCALE = "en";

    public static double FALL_REDUCER = 2;

    public static int COUNTDOWN = 5;
    public static int HIDE_TIMER = 15;
    public static int GAME_DURATION = 300;

    public static boolean GIVE_TRIDENT = false;
    public static boolean BECOME_HUNTER = true;
    public static boolean SAVE_INVENTORY = true;

    public static int NAMETAG_HIDE = 5;

    public static void load() {
        Main.getInstance().reloadConfig();
        config = Main.getInstance().getConfig();

        LOCALE = getString("locale", "en");
        FALL_REDUCER = getPositiveDouble("fall-reducer", 2);
        COUNTDOWN = getPositiveInt("countdown", 5);
        HIDE_TIMER = getPositiveInt("hide-timer", 15);
        GAME_DURATION = getPositiveInt("game-duration", 300);
        GIVE_TRIDENT = getBoolean("give-trident", false);
        BECOME_HUNTER = getBoolean("become-hunter", true);
        SAVE_INVENTORY = getBoolean("save-inventory", true);
        NAMETAG_HIDE = getPositiveInt("nametag-hide", 5);

        Main.getInstance().saveConfig();
    }

    private static String getString(String path, String defaultValue) {
        try {
            return config.getString(path);
        } catch (NullPointerException|IllegalArgumentException e) {
            TextUtils.sendConsoleMessage(ChatColor.DARK_RED + "Unable to find or interpret " + path + " in config.yml.");
            config.set(path, defaultValue);
            return defaultValue;
        }
    }
    private static int getPositiveInt(String path, int defaultValue) {
        try {
            int nb = config.getInt(path);
            if (nb < 1) {
                config.set(path, defaultValue);
                return defaultValue;
            } else {
                return nb;
            }
        } catch (NullPointerException|IllegalArgumentException e) {
            TextUtils.sendConsoleMessage(ChatColor.DARK_RED + "Unable to find or interpret " + path + " in config.yml.");
            config.set(path, defaultValue);
            return defaultValue;
        }
    }

    private static double getPositiveDouble(String path, double defaultValue) {
        try {
            double nb = config.getDouble(path);
            if (nb < 1) {
                config.set(path, defaultValue);
                return defaultValue;
            } else {
                return nb;
            }
        } catch (NullPointerException|IllegalArgumentException e) {
            TextUtils.sendConsoleMessage(ChatColor.DARK_RED + "Unable to find or interpret " + path + " in config.yml.");
            config.set(path, defaultValue);
            return defaultValue;
        }
    }

    private static boolean getBoolean(String path, boolean defaultValue) {
        try {
            return config.getBoolean(path);
        } catch (NullPointerException|IllegalArgumentException e) {
            TextUtils.sendConsoleMessage(ChatColor.DARK_RED + "Unable to find or interpret " + path + " in config.yml.");
            config.set(path, defaultValue);
            return defaultValue;
        }
    }

}
