package com.github.syldium.hideseek.utils;

import com.github.syldium.hideseek.HSPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigUtil {

    public static void setup(HSPlugin plugin) {
        plugin.saveDefaultConfig();

        FileConfiguration config = plugin.getConfig();
        config.addDefault("lang", "en");
        config.addDefault("fall-reducer", 2);
        config.addDefault("countdown", 5);
        config.addDefault("hide-timer", 15);
        config.addDefault("game-duration", 300);

        config.addDefault("give-trident", false);
        config.addDefault("become-hunter", true);
        config.addDefault("save-inventory", true);
        config.addDefault("nametag-hide", 5);
    }
}
