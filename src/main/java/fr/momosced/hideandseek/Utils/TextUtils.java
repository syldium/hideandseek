package fr.momosced.hideandseek.Utils;

import fr.momosced.hideandseek.LocaleManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class TextUtils {

    public static void sendMessage(CommandSender to, String message) {
        if (to != null && message != null)
            to.sendMessage(LocaleManager.PREFIX + " " + message);
    }

    public static void sendInfoMessage(CommandSender to, String message) {
        sendMessage(to, ChatColor.DARK_AQUA + message);
    }
    public static void sendVerboseMessage(CommandSender to, String message) {
        sendMessage(to, ChatColor.BLUE + message);
    }
    public static void sendSuccessMessage(CommandSender to, String message) {
        sendMessage(to, ChatColor.DARK_GREEN + message);
    }
    public static void sendErrorMessage(CommandSender to, String message) {
        sendMessage(to, ChatColor.RED + message);
    }

    public static void sendConsoleMessage(String message) {
        if (message != null)
            Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "HS" + ChatColor.GRAY + "] " + ChatColor.DARK_GREEN + message);
    }
}